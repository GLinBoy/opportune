<template>
  <v-autocomplete
    v-model="selectedItem"
    v-model:search="searchQuery"
    :items="searchResults"
    :loading="isSearching"
    item-title="name"
    item-value="id"
    flat
    clearable
    hide-details
    single-line
    max-width="100%"
    rounded="lg"
    label="Search..."
    density="compact"
    variant="solo-filled"
    prepend-inner-icon="mdi-magnify"
    no-filter
    return-object
    @update:search="handleSearchInput"
    @update:model-value="handleItemSelect"
  >
    <template v-slot:item="{ props, item }">
      <v-list-item
        v-bind="props"
        :title="item.name"
        :subtitle="`${item.type} - ${item.status}`"
      >
        <template v-slot:prepend>
          <v-icon
            :icon="getIconForType(item.type)"
            :color="getStatusColor(item.status, item.type)"
          />
        </template>
      </v-list-item>
    </template>
    <template v-slot:no-data>
      <v-list-item>
        <v-list-item-title>
          {{ searchQuery ? 'No results found' : 'Start typing to search...' }}
        </v-list-item-title>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<script setup lang="ts">
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import SearchService from '../../services/search.service'
import {
  getApplicationStatusColor,
  getCompanyStatusColor,
  type ApplicationStatus,
  type CompanyStatus
} from '../../models'

const searchService = inject('searchService', () => new SearchService())
const router = useRouter()

// Reactive state
const searchQuery = ref<string>('')
const searchResults = ref<Array<{ id: string; name: string; status: string; type: string }>>([])
const isSearching = ref(false)
const selectedItem = ref<{ id: string; name: string; status: string; type: string } | null>(null)

// Search function
const searchGlobal = async (query: string) => {
  if (!query || query.trim().length < 2) {
    searchResults.value = []
    return
  }

  isSearching.value = true
  try {
    const response = await searchService().search(query.trim(), {
      page: 0,
      size: 15, // Limit global search results
    })
    searchResults.value = response.data
  } catch (error) {
    console.error('Error searching:', error)
    searchResults.value = []
  } finally {
    isSearching.value = false
  }
}

// Debounced search handler
let searchTimeout: ReturnType<typeof setTimeout> | null = null
const handleSearchInput = (value: string) => {
  searchQuery.value = value || ''

  // Clear existing timeout
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  // Debounce the search
  searchTimeout = setTimeout(async () => {
    await searchGlobal(searchQuery.value)
  }, 300) // Wait 300ms after user stops typing
}

// Handle item selection
const handleItemSelect = (selected: { id: string; name: string; status: string; type: string } | null) => {
  console.log('Item selected:', selected)
  if (selected) {
    const itemType = selected.type.toUpperCase()
    // Navigate based on type
    if (itemType === 'APPLICATION') {
      console.log('Navigating to application:', `/applications/${selected.id}`)
      router.push(`/applications/${selected.id}`)
    } else if (itemType === 'COMPANY') {
      console.log('Navigating to company:', `/companies/${selected.id}`)
      router.push(`/companies/${selected.id}`)
    }
    // Clear selection after a small delay to allow navigation
    setTimeout(() => {
      selectedItem.value = null
      searchQuery.value = ''
    }, 100)
  }
}

// Helper functions
const getIconForType = (type: string): string => {
  const upperType = type.toUpperCase()
  switch (upperType) {
    case 'APPLICATION':
      return 'mdi-briefcase'
    case 'COMPANY':
      return 'mdi-office-building'
    default:
      return 'mdi-file-document'
  }
}

const getStatusColor = (status: string, type: string): string => {
  const upperType = type.toUpperCase()
  if (upperType === 'APPLICATION') {
    return getApplicationStatusColor(status as ApplicationStatus)
  } else if (upperType === 'COMPANY') {
    return getCompanyStatusColor(status as CompanyStatus)
  }
  return 'grey'
}
</script>

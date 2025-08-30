<template>
  <div>
    <!-- Page Header -->
    <div class="d-flex justify-space-between align-center mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold">Companies</h1>
        <p class="text-subtitle-1 text-medium-emphasis">Manage companies and your interest level</p>
      </div>
      <div class="d-flex align-center" style="gap: 16px">
        <v-text-field
          v-model="searchQuery"
          label="Search companies..."
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          density="compact"
          style="min-width: 300px"
          clearable
          @input="handleSearch"
        />
        <v-tooltip text="Add new company">
          <template #activator="{ props }">
            <v-btn v-bind="props" color="primary" prepend-icon="mdi-plus" @click="addNewCompany">
              Add Company
            </v-btn>
          </template>
        </v-tooltip>
      </div>
    </div>

    <!-- Data Table -->
    <v-card>
      <v-data-table
        v-model:items-per-page="itemsPerPage"
        v-model:page="currentPage"
        :headers="headers"
        :items="filteredCompanies"
        :loading="loading"
        item-value="id"
        class="elevation-1"
        @click:row="handleRowClick"
      >
        <!-- Company Column with Logo -->
        <template #[`item.company`]="{ item }">
          <div class="d-flex align-center py-2">
            <v-avatar size="40" class="mr-3">
              <v-img :src="item.logo" :alt="`${item.company} logo`" />
            </v-avatar>
            <div>
              <div class="font-weight-medium">{{ item.company }}</div>
              <div class="text-caption text-medium-emphasis">{{ item.website }}</div>
            </div>
          </div>
        </template>

        <!-- Industry Column -->
        <template #[`item.industry`]="{ item }">
          <v-chip size="small" variant="tonal" color="primary">
            {{ item.industry }}
          </v-chip>
        </template>

        <!-- Status Column with Editable Select -->
        <template #[`item.status`]="{ item }">
          <v-select
            v-model="item.status"
            :items="statusOptions"
            variant="outlined"
            density="compact"
            hide-details
            @update:model-value="updateCompanyStatus(item.id, $event)"
            @click.stop
          >
            <template #selection="{ item: statusItem }">
              <v-chip :color="getStatusColor(statusItem.value)" size="small" variant="flat">
                {{ statusItem.title }}
              </v-chip>
            </template>
            <template #item="{ props, item: statusItem }">
              <v-list-item v-bind="props">
                <template #prepend>
                  <v-chip :color="getStatusColor(statusItem.value)" size="x-small" variant="flat" />
                </template>
              </v-list-item>
            </template>
          </v-select>
        </template>

        <!-- Footer with pagination info -->
        <template #bottom>
          <div class="text-center pt-2">
            <v-pagination v-model="currentPage" :length="pageCount" :total-visible="7" />
            <div class="text-caption text-medium-emphasis mt-2">
              Showing {{ startItem }} to {{ endItem }} of {{ totalItems }} companies
            </div>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <!-- Snackbar for status updates -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn color="white" variant="text" @click="snackbar.show = false"> Close </v-btn>
      </template>
    </v-snackbar>

    <!-- Add Company Dialog -->
    <v-dialog v-model="addDialog.show" max-width="600">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-plus" class="mr-2" />
          Add New Company
        </v-card-title>
        <v-card-text>
          <v-form ref="addForm" v-model="addDialog.valid">
            <v-text-field
              v-model="addDialog.companyName"
              label="Company Name"
              placeholder="Enter company name"
              variant="outlined"
              prepend-inner-icon="mdi-domain"
              :rules="[rules.required]"
              class="mb-3"
            />
            <v-text-field
              v-model="addDialog.website"
              label="Company Website"
              placeholder="https://company.com"
              variant="outlined"
              prepend-inner-icon="mdi-web"
              :rules="[rules.url]"
              class="mb-3"
            />
            <v-select
              v-model="addDialog.industry"
              label="Industry"
              :items="industryOptions"
              variant="outlined"
              prepend-inner-icon="mdi-factory"
              :rules="[rules.required]"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="closeAddDialog"> Cancel </v-btn>
          <v-btn
            color="primary"
            :loading="addDialog.loading"
            :disabled="!addDialog.valid"
            @click="addCompany"
          >
            Add Company
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// Types
interface Company {
  id: number
  company: string
  logo: string
  website: string
  industry: string
  status: string
}

interface StatusOption {
  title: string
  value: string
}

interface Snackbar {
  show: boolean
  message: string
  color: string
}

interface AddDialog {
  show: boolean
  valid: boolean
  loading: boolean
  companyName: string
  website: string
  industry: string
}

// Router
const router = useRouter()

// Reactive data
const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const itemsPerPage = ref(25)

// Snackbar for notifications
const snackbar = ref<Snackbar>({
  show: false,
  message: '',
  color: 'success',
})

// Add company dialog
const addDialog = ref<AddDialog>({
  show: false,
  valid: false,
  loading: false,
  companyName: '',
  website: '',
  industry: '',
})

const addForm = ref()

// Form validation rules
const rules = {
  required: (value: string) => !!value || 'This field is required',
  url: (value: string) => {
    if (!value) return true
    const pattern = /^https?:\/\/.+/
    return pattern.test(value) || 'Please enter a valid URL starting with http:// or https://'
  },
}

// Table headers
const headers = [
  { title: 'Company', key: 'company', sortable: true, width: '300px' },
  { title: 'Industry', key: 'industry', sortable: true },
  { title: 'Status', key: 'status', sortable: true, width: '200px' },
]

// Status options
const statusOptions: StatusOption[] = [
  { title: 'Interested', value: 'interested' },
  { title: 'Not Interested', value: 'not_interested' },
  { title: 'Ignored', value: 'ignored' },
]

// Industry options
const industryOptions = [
  'Technology',
  'Finance',
  'Healthcare',
  'Education',
  'E-commerce',
  'Manufacturing',
  'Consulting',
  'Marketing',
  'Media',
  'Energy',
  'Automotive',
  'Aerospace',
  'Real Estate',
  'Telecommunications',
  'Gaming',
  'Biotechnology',
  'Startups',
  'Government',
  'Non-profit',
  'Other',
]

// Sample data - Replace with real API call in the future
const companies = ref<Company[]>([
  {
    id: 1,
    company: 'Google',
    logo: 'https://logo.clearbit.com/google.com',
    website: 'google.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 2,
    company: 'Microsoft',
    logo: 'https://logo.clearbit.com/microsoft.com',
    website: 'microsoft.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 3,
    company: 'Apple',
    logo: 'https://logo.clearbit.com/apple.com',
    website: 'apple.com',
    industry: 'Technology',
    status: 'not_interested',
  },
  {
    id: 4,
    company: 'Amazon',
    logo: 'https://logo.clearbit.com/amazon.com',
    website: 'amazon.com',
    industry: 'E-commerce',
    status: 'interested',
  },
  {
    id: 5,
    company: 'Meta',
    logo: 'https://logo.clearbit.com/meta.com',
    website: 'meta.com',
    industry: 'Technology',
    status: 'ignored',
  },
  {
    id: 6,
    company: 'Tesla',
    logo: 'https://logo.clearbit.com/tesla.com',
    website: 'tesla.com',
    industry: 'Automotive',
    status: 'interested',
  },
  {
    id: 7,
    company: 'Netflix',
    logo: 'https://logo.clearbit.com/netflix.com',
    website: 'netflix.com',
    industry: 'Media',
    status: 'interested',
  },
  {
    id: 8,
    company: 'Spotify',
    logo: 'https://logo.clearbit.com/spotify.com',
    website: 'spotify.com',
    industry: 'Media',
    status: 'not_interested',
  },
  {
    id: 9,
    company: 'Adobe',
    logo: 'https://logo.clearbit.com/adobe.com',
    website: 'adobe.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 10,
    company: 'Salesforce',
    logo: 'https://logo.clearbit.com/salesforce.com',
    website: 'salesforce.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 11,
    company: 'IBM',
    logo: 'https://logo.clearbit.com/ibm.com',
    website: 'ibm.com',
    industry: 'Technology',
    status: 'ignored',
  },
  {
    id: 12,
    company: 'Oracle',
    logo: 'https://logo.clearbit.com/oracle.com',
    website: 'oracle.com',
    industry: 'Technology',
    status: 'not_interested',
  },
  {
    id: 13,
    company: 'Uber',
    logo: 'https://logo.clearbit.com/uber.com',
    website: 'uber.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 14,
    company: 'Airbnb',
    logo: 'https://logo.clearbit.com/airbnb.com',
    website: 'airbnb.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 15,
    company: 'LinkedIn',
    logo: 'https://logo.clearbit.com/linkedin.com',
    website: 'linkedin.com',
    industry: 'Technology',
    status: 'not_interested',
  },
  {
    id: 16,
    company: 'Twitter',
    logo: 'https://logo.clearbit.com/twitter.com',
    website: 'twitter.com',
    industry: 'Technology',
    status: 'ignored',
  },
  {
    id: 17,
    company: 'Stripe',
    logo: 'https://logo.clearbit.com/stripe.com',
    website: 'stripe.com',
    industry: 'Finance',
    status: 'interested',
  },
  {
    id: 18,
    company: 'PayPal',
    logo: 'https://logo.clearbit.com/paypal.com',
    website: 'paypal.com',
    industry: 'Finance',
    status: 'interested',
  },
  {
    id: 19,
    company: 'Square',
    logo: 'https://logo.clearbit.com/squareup.com',
    website: 'squareup.com',
    industry: 'Finance',
    status: 'not_interested',
  },
  {
    id: 20,
    company: 'Shopify',
    logo: 'https://logo.clearbit.com/shopify.com',
    website: 'shopify.com',
    industry: 'E-commerce',
    status: 'interested',
  },
  {
    id: 21,
    company: 'Zoom',
    logo: 'https://logo.clearbit.com/zoom.us',
    website: 'zoom.us',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 22,
    company: 'Slack',
    logo: 'https://logo.clearbit.com/slack.com',
    website: 'slack.com',
    industry: 'Technology',
    status: 'not_interested',
  },
  {
    id: 23,
    company: 'GitHub',
    logo: 'https://logo.clearbit.com/github.com',
    website: 'github.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 24,
    company: 'Atlassian',
    logo: 'https://logo.clearbit.com/atlassian.com',
    website: 'atlassian.com',
    industry: 'Technology',
    status: 'interested',
  },
  {
    id: 25,
    company: 'Dropbox',
    logo: 'https://logo.clearbit.com/dropbox.com',
    website: 'dropbox.com',
    industry: 'Technology',
    status: 'ignored',
  },
])

// Computed properties
const filteredCompanies = computed(() => {
  if (!searchQuery.value) {
    return companies.value
  }

  const query = searchQuery.value.toLowerCase()
  return companies.value.filter(
    (company) =>
      company.company.toLowerCase().includes(query) ||
      company.industry.toLowerCase().includes(query) ||
      company.status.toLowerCase().includes(query)
  )
})

const totalItems = computed(() => filteredCompanies.value.length)
const pageCount = computed(() => Math.ceil(totalItems.value / itemsPerPage.value))
const startItem = computed(() => (currentPage.value - 1) * itemsPerPage.value + 1)
const endItem = computed(() => Math.min(currentPage.value * itemsPerPage.value, totalItems.value))

// Methods
const getStatusColor = (status: string): string => {
  const colorMap: Record<string, string> = {
    interested: 'success',
    not_interested: 'error',
    ignored: 'grey',
  }
  return colorMap[status] || 'default'
}

const updateCompanyStatus = async (companyId: number, newStatus: string) => {
  try {
    loading.value = true

    // Replace with actual API call when backend is ready
    // await updateCompanyStatusAPI(companyId, newStatus)

    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 500))

    // Update local data
    const company = companies.value.find((comp) => comp.id === companyId)
    if (company) {
      company.status = newStatus
    }

    // Show success message
    snackbar.value = {
      show: true,
      message: 'Company status updated successfully!',
      color: 'success',
    }
  } catch (error) {
    console.error('Failed to update company status:', error)
    snackbar.value = {
      show: true,
      message: 'Failed to update company status. Please try again.',
      color: 'error',
    }
  } finally {
    loading.value = false
  }
}

const goToCompanyDetail = (event: Event | null, { item }: { item: Company }) => {
  router.push(`/companies/${item.id}`)
}

const handleRowClick = (event: Event, { item }: { item: Company }) => {
  goToCompanyDetail(event, { item })
}

const addNewCompany = () => {
  // Open dialog to get company details from user
  addDialog.value.show = true
  addDialog.value.companyName = ''
  addDialog.value.website = ''
  addDialog.value.industry = ''
  addDialog.value.valid = false
}

const closeAddDialog = () => {
  addDialog.value.show = false
  addDialog.value.companyName = ''
  addDialog.value.website = ''
  addDialog.value.industry = ''
  addDialog.value.valid = false
}

const addCompany = async () => {
  if (!addForm.value || !addDialog.value.valid) return

  try {
    addDialog.value.loading = true

    // Replace with actual API call when backend is ready
    // const newCompany = await createCompanyAPI(addDialog.value)

    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1000))

    // Add new company to local data
    const newCompany: Company = {
      id: Math.max(...companies.value.map((c) => c.id)) + 1,
      company: addDialog.value.companyName,
      logo: `https://logo.clearbit.com/${
        addDialog.value.website.replace('https://', '').replace('http://', '').split('/')[0]
      }`,
      website: addDialog.value.website.replace('https://', '').replace('http://', '').split('/')[0],
      industry: addDialog.value.industry,
      status: 'interested', // Default status
    }

    companies.value.unshift(newCompany)

    // Close dialog
    closeAddDialog()

    snackbar.value = {
      show: true,
      message: 'Company added successfully!',
      color: 'success',
    }
  } catch (error) {
    console.error('Failed to add company:', error)
    snackbar.value = {
      show: true,
      message: 'Failed to add company. Please try again.',
      color: 'error',
    }
  } finally {
    addDialog.value.loading = false
  }
}

const handleSearch = () => {
  // Reset to first page when searching
  currentPage.value = 1
}

// Lifecycle
onMounted(() => {
  // Replace with actual API call to fetch companies when backend is ready
  // loadCompanies()
})
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.hover\:bg-grey-lighten-5:hover {
  background-color: rgb(var(--v-theme-grey-lighten-5)) !important;
}

/* Custom styling for the data table */
:deep(.v-data-table__tr:hover) {
  background-color: rgb(var(--v-theme-grey-lighten-5)) !important;
}

:deep(.v-data-table__tr) {
  cursor: pointer;
}

/* Ensure status column doesn't interfere with row clicks */
:deep(.v-data-table__td:last-child) {
  position: relative;
}

/* Company logo styling */
:deep(.v-avatar img) {
  object-fit: contain;
  background-color: white;
  padding: 2px;
}
</style>

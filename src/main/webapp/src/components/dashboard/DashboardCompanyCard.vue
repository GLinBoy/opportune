<script setup lang="ts">
import DashboardMainCard from './DashboardMainCard.vue'

interface Company {
  id: number
  name: string
  logo: string
}

// Mock data - replace with API call later
const companies: Company[] = [
  {
    id: 1,
    name: 'Tech Corp',
    logo: '🏢',
  },
  {
    id: 2,
    name: 'Startup Inc',
    logo: '🚀',
  },
  {
    id: 3,
    name: 'Digital Agency',
    logo: '💻',
  },
  {
    id: 4,
    name: 'Enterprise Solutions',
    logo: '⚡',
  },
  {
    id: 5,
    name: 'Creative Studio',
    logo: '🎨',
  },
]

const handleCompanyClick = (company: Company) => {
  // Navigation to company page will be implemented later
  console.log('Navigate to company:', company.id)
}
</script>

<template>
  <DashboardMainCard title="Recent Companies" subtitle="Companies you've recently applied to">
    <v-list class="py-0">
      <v-list-item
        v-for="(company, index) in companies"
        :key="company.id"
        @click="handleCompanyClick(company)"
        class="cursor-pointer"
        :class="{ 'border-b': index < companies.length - 1 }"
      >
        <template v-slot:prepend>
          <v-avatar size="40" class="company-logo">
            {{ company.logo }}
          </v-avatar>
        </template>

        <v-list-item-title class="font-weight-medium">
          {{ company.name }}
        </v-list-item-title>

        <template v-slot:append>
          <v-icon icon="mdi-chevron-right" color="medium-emphasis"></v-icon>
        </template>
      </v-list-item>
    </v-list>
  </DashboardMainCard>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  background-color: rgba(var(--v-theme-on-surface), 0.04);
}

.company-logo {
  font-size: 20px;
}

.border-b {
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.12);
}
</style>

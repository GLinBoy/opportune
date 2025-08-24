<script setup lang="ts">
import DashboardMainCard from './DashboardMainCard.vue'

interface Application {
  id: number
  position: string
  company: string
  status: string
}

// Mock data - replace with API call later
const applications: Application[] = [
  {
    id: 1,
    position: 'Senior Frontend Developer',
    company: 'Tech Corp',
    status: 'Interview Scheduled',
  },
  {
    id: 2,
    position: 'Full Stack Engineer',
    company: 'Startup Inc',
    status: 'Applied',
  },
  {
    id: 3,
    position: 'Vue.js Developer',
    company: 'Digital Agency',
    status: 'Phone Screen',
  },
  {
    id: 4,
    position: 'Software Engineer',
    company: 'Enterprise Solutions',
    status: 'Offer Received',
  },
  {
    id: 5,
    position: 'Web Developer',
    company: 'Creative Studio',
    status: 'Rejected',
  },
]

const getStatusColor = (status: string): string => {
  switch (status.toLowerCase()) {
    case 'offer received':
      return 'success'
    case 'interview scheduled':
    case 'phone screen':
      return 'primary'
    case 'applied':
      return 'info'
    case 'rejected':
      return 'error'
    default:
      return 'default'
  }
}

const handleApplicationClick = (application: Application) => {
  // Navigation to application detail page will be implemented later
  console.log('Navigate to application:', application.id)
}
</script>

<template>
  <DashboardMainCard title="Recent Applications" subtitle="Your latest job applications">
    <v-table hover>
      <thead>
        <tr>
          <th scope="col" class="text-left">Position</th>
          <th scope="col" class="text-left">Company</th>
          <th scope="col" class="text-left">Status</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="application in applications"
          :key="application.id"
          @click="handleApplicationClick(application)"
          class="cursor-pointer"
        >
          <td class="font-weight-medium">{{ application.position }}</td>
          <td>{{ application.company }}</td>
          <td>
            <v-chip :color="getStatusColor(application.status)" size="small" variant="tonal">
              {{ application.status }}
            </v-chip>
          </td>
        </tr>
      </tbody>
    </v-table>
  </DashboardMainCard>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  background-color: rgba(var(--v-theme-on-surface), 0.04);
}
</style>

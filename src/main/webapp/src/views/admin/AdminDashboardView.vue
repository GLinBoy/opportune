<template>
  <FormCard :collapsible="false">
    <template #title>
      <v-icon icon="mdi-view-dashboard-outline" class="mr-2" />
      Dashboard
    </template>

    <!-- Row 1: KPI cards -->
    <DashboardKpiCards :cards="kpiCards" :loading="loading" class="mb-4" />

    <!-- Row 2: User trend (full width) -->
    <v-row class="mb-4">
      <v-col cols="12">
        <AdminUserTrend :dates="userTrendDates" :counts="userTrendCounts" :loading="loading" />
      </v-col>
    </v-row>

    <!-- Row 3: Account status donut + App status donut + AI queue -->
    <v-row class="mb-4">
      <v-col cols="12" md="4">
        <AdminUserStatusDonut :distribution="accountStatusDistribution" :loading="loading" />
      </v-col>
      <v-col cols="12" md="4">
        <AdminAppStatusDonut :distribution="applicationStatusDistribution" :loading="loading" />
      </v-col>
      <v-col cols="12" md="4">
        <AdminAiQueueWidget :items="aiQueueItems" :loading="loading" />
      </v-col>
    </v-row>
  </FormCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAdminDashboard } from '../../composables/useAdminDashboard'
import DashboardKpiCards from '../../components/dashboard/DashboardKpiCards.vue'
import AdminUserTrend from '../../components/admin/dashboard/AdminUserTrend.vue'
import AdminUserStatusDonut from '../../components/admin/dashboard/AdminUserStatusDonut.vue'
import AdminAppStatusDonut from '../../components/admin/dashboard/AdminAppStatusDonut.vue'
import AdminAiQueueWidget from '../../components/admin/dashboard/AdminAiQueueWidget.vue'
import FormCard from '../../components/forms/FormCard.vue'

const {
  kpis,
  userTrendDates,
  userTrendCounts,
  accountStatusDistribution,
  applicationStatusDistribution,
  aiQueueItems,
  loading,
} = useAdminDashboard()

const kpiCards = computed(() => [
  { label: 'Total Users', value: kpis.value.totalUsers, icon: 'mdi-account-group-outline', color: 'primary' },
  { label: 'Total Companies', value: kpis.value.totalCompanies, icon: 'mdi-domain', color: 'info' },
  { label: 'Active Sessions', value: kpis.value.activeSessions, icon: 'mdi-shield-check-outline', color: 'success' },
  {
    label: 'AI Queue',
    value: kpis.value.aiQueueCount,
    icon: 'mdi-robot-outline',
    color: kpis.value.aiQueueCount > 0 ? 'warning' : 'secondary',
  },
])
</script>

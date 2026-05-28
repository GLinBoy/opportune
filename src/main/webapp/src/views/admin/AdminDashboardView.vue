<template>
  <v-container fluid class="pa-4">
    <!-- Row 1: KPI cards -->
    <AdminKpiCards :kpis="kpis" :loading="loading" class="mb-4" />

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
  </v-container>
</template>

<script setup lang="ts">
import { useAdminDashboard } from '../../composables/useAdminDashboard'
import AdminKpiCards from '../../components/admin/dashboard/AdminKpiCards.vue'
import AdminUserTrend from '../../components/admin/dashboard/AdminUserTrend.vue'
import AdminUserStatusDonut from '../../components/admin/dashboard/AdminUserStatusDonut.vue'
import AdminAppStatusDonut from '../../components/admin/dashboard/AdminAppStatusDonut.vue'
import AdminAiQueueWidget from '../../components/admin/dashboard/AdminAiQueueWidget.vue'

const {
  kpis,
  userTrendDates,
  userTrendCounts,
  accountStatusDistribution,
  applicationStatusDistribution,
  aiQueueItems,
  loading,
} = useAdminDashboard()
</script>

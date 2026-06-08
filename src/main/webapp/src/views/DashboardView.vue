<template>
  <v-container fluid class="pa-4">
    <!-- Row 1: KPI Cards -->
    <DashboardKpiCards :cards="kpiCards" :loading="loading" class="mb-4" />

    <!-- Row 2: Applications Over Time — full width -->
    <v-row class="mb-4">
      <v-col cols="12">
        <DashboardApplicationTrend
          :dates="trendStats.dates"
          :series="trendStats.series"
          :loading="loading"
          :summary-days="summary?.summaryDays"
        />
      </v-col>
    </v-row>

    <!-- Row 3: Status Donut + AI Score Radar + Pipeline Funnel -->
    <v-row>
      <v-col cols="12" md="4">
        <DashboardActivityHeatmap
          :stats="summary?.stats ?? []"
          :loading="loading"
          :summary-days="summary?.summaryDays"
        />
      </v-col>
      <v-col cols="12" md="4">
        <DashboardAiScoreRadar :scores="scores" :loading="loading" />
      </v-col>
      <v-col cols="12" md="4">
        <DashboardStatusDonut :status-counts="statusCounts" :loading="loading" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useDashboard } from '../composables/useDashboard'
import DashboardKpiCards from '../components/dashboard/DashboardKpiCards.vue'
import DashboardStatusDonut from '../components/dashboard/DashboardStatusDonut.vue'
import DashboardApplicationTrend from '../components/dashboard/DashboardApplicationTrend.vue'
import DashboardAiScoreRadar from '../components/dashboard/DashboardAiScoreRadar.vue'
import DashboardActivityHeatmap from '../components/dashboard/DashboardActivityHeatmap.vue'

const { summary, kpis, statusCounts, trendData, trendStats, scores, loading, error, reload } =
  useDashboard()

const kpiCards = computed(() => {
  const k = kpis.value
  return [
    { label: 'Total Applications', value: k.totalApplications, icon: 'mdi-briefcase-outline', color: 'primary' },
    { label: 'Active Pipeline', value: k.activePipeline, icon: 'mdi-progress-clock', color: 'info' },
    { label: 'Response Rate', value: `${k.responseRate}%`, icon: 'mdi-reply-outline', color: 'success' },
    { label: 'Offer Rate', value: `${k.offerRate}%`, icon: 'mdi-trophy-outline', color: 'warning' },
    { label: 'Rejection Rate', value: `${k.rejectionRate}%`, icon: 'mdi-close-circle-outline', color: 'error' },
  ]
})
</script>

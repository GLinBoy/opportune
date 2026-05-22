<template>
  <v-container fluid class="pa-4">
    <!-- Row 1: KPI Cards -->
    <DashboardKpiCards :kpis="kpis" :loading="loading" class="mb-4" />

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

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
      {{ snackbar.message }}
      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">Close</v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Snackbar } from '../views/application/ApplicationListView'
import { useDashboard } from '../composables/useDashboard'
import DashboardKpiCards from '../components/dashboard/DashboardKpiCards.vue'
import DashboardStatusDonut from '../components/dashboard/DashboardStatusDonut.vue'
import DashboardApplicationTrend from '../components/dashboard/DashboardApplicationTrend.vue'
import DashboardAiScoreRadar from '../components/dashboard/DashboardAiScoreRadar.vue'
import DashboardActivityHeatmap from '../components/dashboard/DashboardActivityHeatmap.vue'

const snackbar = ref<Snackbar>({ show: false, message: '', color: 'success' })

const { summary, kpis, statusCounts, trendData, trendStats, scores, loading, error, reload } =
  useDashboard()
</script>

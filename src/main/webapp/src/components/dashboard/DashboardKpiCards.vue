<template>
  <v-row>
    <v-col v-for="card in cards" :key="card.label" cols="12" sm="6" md="4" lg>
      <v-card :loading="loading" rounded="lg" elevation="1">
        <v-card-text class="d-flex flex-column align-center pa-4">
          <v-icon :color="card.color" size="28" class="mb-2">{{ card.icon }}</v-icon>
          <div class="text-h5 font-weight-bold">
            <template v-if="loading">—</template>
            <template v-else>{{ card.value }}</template>
          </div>
          <div class="text-caption text-medium-emphasis text-center">{{ card.label }}</div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { DashboardKpis } from '../../models'

const props = defineProps<{
  kpis: DashboardKpis
  loading: boolean
}>()

const cards = computed(() => [
  {
    label: 'Total Applications',
    value: props.kpis.totalApplications,
    icon: 'mdi-briefcase-outline',
    color: 'primary',
  },
  {
    label: 'Active Pipeline',
    value: props.kpis.activePipeline,
    icon: 'mdi-progress-clock',
    color: 'info',
  },
  {
    label: 'Response Rate',
    value: `${props.kpis.responseRate}%`,
    icon: 'mdi-reply-outline',
    color: 'success',
  },
  {
    label: 'Offer Rate',
    value: `${props.kpis.offerRate}%`,
    icon: 'mdi-trophy-outline',
    color: 'warning',
  },
  {
    label: 'Rejection Rate',
    value: `${props.kpis.rejectionRate}%`,
    icon: 'mdi-close-circle-outline',
    color: 'error',
  },
])
</script>

<template>
  <v-row>
    <v-col v-for="card in cards" :key="card.label" cols="12" sm="6" md="3">
      <v-card :loading="loading" rounded="lg" elevation="1" class="kpi-card">
        <v-card-text class="kpi-card__body pa-4">
          <div class="kpi-card__icon-wrap">
            <v-icon :color="card.color" size="32">{{ card.icon }}</v-icon>
          </div>
          <div class="kpi-card__content">
            <div class="kpi-card__value">
              <template v-if="loading">—</template>
              <template v-else>{{ card.value }}</template>
            </div>
            <div class="kpi-card__label text-medium-emphasis">{{ card.label }}</div>
          </div>
        </v-card-text>
        <div
          class="kpi-card__accent"
          :style="{ background: `rgb(var(--v-theme-${card.color}))` }"
        />
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { AdminKpis } from '../../../models'

const props = defineProps<{
  kpis: AdminKpis
  loading: boolean
}>()

const cards = computed(() => [
  {
    label: 'Total Users',
    value: props.kpis.totalUsers,
    icon: 'mdi-account-group-outline',
    color: 'primary',
  },
  {
    label: 'Total Companies',
    value: props.kpis.totalCompanies,
    icon: 'mdi-domain',
    color: 'info',
  },
  {
    label: 'Active Sessions',
    value: props.kpis.activeSessions,
    icon: 'mdi-shield-check-outline',
    color: 'success',
  },
  {
    label: 'AI Queue',
    value: props.kpis.aiQueueCount,
    icon: 'mdi-robot-outline',
    color: props.kpis.aiQueueCount > 0 ? 'warning' : 'secondary',
  },
])
</script>

<style scoped>
.kpi-card {
  position: relative;
  overflow: hidden;
  min-height: 90px;
}
.kpi-card__body {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  min-height: 82px;
  padding-bottom: 10px !important;
}
.kpi-card__icon-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.kpi-card__content {
  flex: 3;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}
.kpi-card__value {
  font-size: 1.75rem;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.5px;
}
.kpi-card__label {
  font-size: 0.78rem;
  font-weight: 500;
  margin-top: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.kpi-card__accent {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  opacity: 0.6;
}
</style>

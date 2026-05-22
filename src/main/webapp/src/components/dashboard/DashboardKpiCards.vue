<template>
  <v-row>
    <v-col v-for="card in cards" :key="card.label" cols="12" sm="6" md="4" lg>
      <v-card :loading="loading" rounded="lg" elevation="1" class="kpi-card">
        <v-card-text class="kpi-card__body pa-4">
          <!-- Left: icon occupies 1/4 of card width -->
          <div class="kpi-card__icon-wrap">
            <v-icon :color="card.color" size="32">{{ card.icon }}</v-icon>
          </div>

          <!-- Right: value (2/3 height) + label (1/3 height) -->
          <div class="kpi-card__content">
            <div class="kpi-card__value">
              <template v-if="loading">—</template>
              <template v-else>{{ card.value }}</template>
            </div>
            <div class="kpi-card__label text-medium-emphasis">{{ card.label }}</div>
          </div>
        </v-card-text>

        <!-- Subtle bottom accent line -->
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

<style scoped>
.kpi-card {
  position: relative;
  overflow: hidden;
  min-height: 90px;
}

/* Card body: flex row, full height */
.kpi-card__body {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  min-height: 82px;
  padding-bottom: 10px !important;
}

/* Left side: stacked value on top, label below — 3/4 of width */
.kpi-card__content {
  flex: 3;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

/* Value takes ~2/3 of the card's vertical space */
.kpi-card__value {
  font-size: 1.75rem;
  font-weight: 700;
  line-height: 1.15;
  flex: 2;
  display: flex;
  align-items: flex-end;
  padding-bottom: 2px;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.01em;
}

/* Label takes ~1/3 of the card's vertical space */
.kpi-card__label {
  font-size: 0.75rem;
  font-weight: 500;
  flex: 1;
  display: flex;
  align-items: flex-start;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  line-height: 1.3;
}

/* Right side: icon in 1/4 of the card width, vertically centered */
.kpi-card__icon-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-right: 20px;
}

.kpi-card__icon-wrap .v-icon {
  opacity: 0.85;
  padding: 24px; /* was 10px — gives ~64px total circle at 32px icon */
  border-radius: 50%;
  background: color-mix(in oklch, currentColor 12%, transparent);
}

/* Thin colored accent bar at the bottom */
.kpi-card__accent {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  opacity: 0.7;
  border-radius: 0 0 8px 8px;
}
</style>

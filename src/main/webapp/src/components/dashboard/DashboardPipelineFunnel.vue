<template>
  <v-card rounded="lg" elevation="1" height="100%">
    <v-card-title class="text-body-1 font-weight-medium pa-4 pb-0"> Pipeline Funnel </v-card-title>
    <v-card-text>
      <v-skeleton-loader v-if="loading" type="image" height="260" />

      <div
        v-else-if="isEmpty"
        class="d-flex flex-column align-center justify-center"
        style="height: 260px"
      >
        <v-icon size="40" color="grey-lighten-1">mdi-filter-outline</v-icon>
        <p class="text-body-2 text-medium-emphasis mt-2">No pipeline data yet</p>
      </div>

      <v-chart v-else :option="option" autoresize style="height: 260px" />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'

const props = defineProps<{
  statusCounts: { status: string; total: number }[]
  loading: boolean
}>()

// Pipeline stage order — READY_TO_APPLY and AI_PROCESSING are pre-apply stages
const PIPELINE_ORDER = [
  'AI_PROCESSING',
  'READY_TO_APPLY',
  'INITIATED',
  'APPLIED',
  'IN_PROGRESS',
  'OFFER_RECEIVED',
  'REJECTED',
  'WITHDRAWN',
]

const STATUS_LABELS: Record<string, string> = {
  AI_PROCESSING: 'AI Processing',
  READY_TO_APPLY: 'Ready to Apply',
  INITIATED: 'Initiated',
  APPLIED: 'Applied',
  IN_PROGRESS: 'In Progress',
  OFFER_RECEIVED: 'Offer Received',
  REJECTED: 'Rejected',
  WITHDRAWN: 'Withdrawn',
}

const STATUS_COLORS: Record<string, string> = {
  AI_PROCESSING: '#AB47BC',
  READY_TO_APPLY: '#26C6DA',
  INITIATED: '#FFA726',
  APPLIED: '#5C6BC0',
  IN_PROGRESS: '#42A5F5',
  OFFER_RECEIVED: '#66BB6A',
  REJECTED: '#EF5350',
  WITHDRAWN: '#BDBDBD',
}

// Build ordered list — only include statuses that have data
const orderedStages = computed(() => {
  const countMap = Object.fromEntries(props.statusCounts.map((s) => [s.status, s.total]))
  return PIPELINE_ORDER.filter((s) => countMap[s] !== undefined).map((s) => ({
    status: s,
    total: countMap[s],
  }))
})

const isEmpty = computed(() => orderedStages.value.length === 0)

const total = computed(() => orderedStages.value.reduce((sum, s) => sum + (s.total ?? 0), 0))

const option = computed<EChartsOption>(() => {
  const stages = orderedStages.value
  const labels = stages.map((s) => STATUS_LABELS[s.status] ?? s.status)
  const values = stages.map((s) => s.total)
  const colors = stages.map((s) => STATUS_COLORS[s.status] ?? '#78909C')
  const t = total.value || 1

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'none' },
      formatter: (params: any) => {
        const p = Array.isArray(params) ? params[0] : params
        const pct = ((p.value / t) * 100).toFixed(1)
        return `${p.name}: <strong>${p.value}</strong> (${pct}%)`
      },
    },
    grid: { left: 100, right: 48, top: 8, bottom: 8, containLabel: false },
    xAxis: {
      type: 'value',
      show: false,
      max: total.value,
    },
    yAxis: {
      type: 'category',
      data: labels,
      inverse: false,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        fontSize: 12,
        color: '#666',
        width: 90,
        overflow: 'truncate',
      },
    },
    series: [
      {
        type: 'bar',
        data: values.map((v, i) => ({
          value: v,
          itemStyle: { color: colors[i], borderRadius: [0, 4, 4, 0] },
        })),
        barMaxWidth: 28,
        label: {
          show: true,
          position: 'right',
          formatter: (params: any) => {
            const pct = ((params.value / t) * 100).toFixed(0)
            return `${params.value}  ${pct}%`
          },
          fontSize: 11,
          color: '#888',
        },
      },
    ],
  }
})
</script>

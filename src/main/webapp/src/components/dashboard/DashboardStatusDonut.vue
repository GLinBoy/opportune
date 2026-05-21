<template>
  <v-card rounded="lg" elevation="1" height="100%">
    <v-card-title class="text-body-1 font-weight-medium pa-4 pb-0">
      Applications by Status
    </v-card-title>
    <v-card-text>
      <v-skeleton-loader v-if="loading" type="image" height="260" />
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

const STATUS_COLORS: Record<string, string> = {
  APPLIED: '#5C6BC0',
  IN_PROGRESS: '#42A5F5',
  OFFER_RECEIVED: '#66BB6A',
  REJECTED: '#EF5350',
  WITHDRAWN: '#BDBDBD',
  INITIATED: '#FFA726',
  AI_PROCESSING: '#AB47BC',
  READY_TO_APPLY: '#26C6DA',
}

const option = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: 0, type: 'scroll' },
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: props.statusCounts.map((s) => ({
        name: s.status.replace(/_/g, ' '),
        value: s.total,
        itemStyle: { color: STATUS_COLORS[s.status] ?? '#78909C' },
      })),
    },
  ],
}))
</script>

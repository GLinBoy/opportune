<template>
  <v-card rounded="lg" elevation="1" height="100%">
    <v-card-title class="text-body-1 font-weight-medium pa-4 pb-0">
      Applications Over Time
    </v-card-title>
    <v-card-subtitle v-if="summaryDays" class="px-4 pb-0 text-caption">
      Last {{ summaryDays }} days
    </v-card-subtitle>
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
  dates: string[]
  counts: number[]
  loading: boolean
  summaryDays?: number // optional — used for subtitle only
}>()

const option = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 16, top: 16, bottom: 40 },
  xAxis: {
    type: 'category',
    data: props.dates,
    axisLabel: { formatter: (v: string) => v.slice(5) }, // MM-DD
  },
  yAxis: { type: 'value', minInterval: 1 },
  series: [
    {
      type: 'line',
      data: props.counts,
      smooth: true,
      areaStyle: { opacity: 0.15 },
      lineStyle: { width: 2 },
      itemStyle: { color: '#5C6BC0' },
    },
  ],
}))
</script>

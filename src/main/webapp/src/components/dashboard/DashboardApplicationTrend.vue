<template>
  <FormCard :collapsible="false" style="height: 100%">
    <template #title>
      Applications Over Time
      <span v-if="summaryDays" class="text-caption font-weight-regular text-medium-emphasis ms-2">
        · Last {{ summaryDays }} days
      </span>
    </template>
    <v-skeleton-loader v-if="loading" type="image" height="260" />
    <v-chart v-else :option="option" autoresize style="height: 260px" />
  </FormCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import FormCard from '../forms/FormCard.vue'
import {
  getApplicationStatusDisplay,
  getApplicationStatusChartColor,
  APPLICATION_STATUS_ORDER,
} from '../../models/enumerations/application-status.model'

const props = defineProps<{
  dates: string[]
  series: Record<string, number[]>
  loading: boolean
  summaryDays?: number
}>()

const option = computed<EChartsOption>(() => {
  const seriesEntries = Object.entries(props.series).sort(
    ([a], [b]) =>
      (APPLICATION_STATUS_ORDER.indexOf(a) + 1 || Infinity) -
      (APPLICATION_STATUS_ORDER.indexOf(b) + 1 || Infinity)
  )

  const chartSeries = seriesEntries.map(([status, data]) => ({
    name: getApplicationStatusDisplay(status),
    type: 'line' as const,
    data,
    smooth: true,
    areaStyle: { opacity: 0.08 },
    lineStyle: { width: 2 },
    itemStyle: {
      color: getApplicationStatusChartColor(status),
    },
    symbol: 'circle',
    symbolSize: 5,
    showSymbol: false, // only show dots on hover
  }))

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross', label: { backgroundColor: '#6a7985' } },
    },
    legend: {
      bottom: 0,
      textStyle: { fontSize: 11 },
      icon: 'roundRect',
      itemWidth: 12,
      itemHeight: 6,
    },
    grid: { left: 40, right: 16, top: 16, bottom: 48 },
    xAxis: {
      type: 'category',
      data: props.dates,
      boundaryGap: false,
      axisLabel: { formatter: (v: string) => v.slice(5) }, // MM-DD
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
    },
    series: chartSeries,
  }
})
</script>

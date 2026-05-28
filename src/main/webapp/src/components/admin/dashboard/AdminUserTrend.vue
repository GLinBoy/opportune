<template>
  <FormCard :collapsible="false" style="height: 100%">
    <template #title>
      User Registrations
      <span class="text-caption font-weight-regular text-medium-emphasis ms-2">· Last 30 days</span>
    </template>
    <v-skeleton-loader v-if="loading" type="image" height="260" />
    <v-chart v-else :option="option" autoresize style="height: 260px" />
  </FormCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import FormCard from '../../forms/FormCard.vue'

const props = defineProps<{
  dates: string[]
  counts: number[]
  loading: boolean
}>()

function resolveVuetifyColor(variable: string): string {
  const raw = getComputedStyle(document.documentElement).getPropertyValue(variable).trim()
  return raw ? `rgb(${raw})` : '#1976D2'
}

const option = computed<EChartsOption>(() => {
  const primaryColor = resolveVuetifyColor('--v-theme-primary')
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
    },
    grid: { left: 40, right: 16, top: 16, bottom: 32 },
    xAxis: {
      type: 'category',
      data: props.dates,
      axisLabel: { formatter: (v: string) => v.slice(5) }, // MM-DD
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
    },
    series: [
      {
        name: 'Registrations',
        type: 'bar',
        data: props.counts,
        itemStyle: { color: primaryColor, borderRadius: [4, 4, 0, 0] },
        emphasis: { itemStyle: { color: primaryColor, opacity: 0.75 } },
      },
    ],
  }
})
</script>

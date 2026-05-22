<template>
  <FormCard :collapsible="false" style="height: 100%">
    <template #title>
      Activity Heatmap
      <span class="text-caption font-weight-regular text-medium-emphasis ms-2">
        · Daily application activity
      </span>
    </template>
    <v-skeleton-loader v-if="loading" type="image" height="160" />
    <div
      v-else-if="isEmpty"
      class="d-flex flex-column align-center justify-center"
      style="height: 160px"
    >
      <v-icon size="40" color="grey-lighten-1">mdi-calendar-blank-outline</v-icon>
      <p class="text-body-2 text-medium-emphasis mt-2">No activity yet</p>
    </div>
    <v-chart v-else :option="option" autoresize style="height: 160px" />
  </FormCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import FormCard from '../forms/FormCard.vue'

const props = defineProps<{
  stats: { createdDay: string; status: string; total: number }[]
  loading: boolean
  summaryDays?: number
}>()

// Aggregate all statuses per day into a single total
const dailyTotals = computed(() => {
  const map: Record<string, number> = {}
  for (const s of props.stats) {
    map[s.createdDay] = (map[s.createdDay] ?? 0) + s.total
  }
  return map
})

const isEmpty = computed(() => Object.keys(dailyTotals.value).length === 0)

// Build [date, value] pairs for the full period
const heatmapData = computed(() => {
  const days = props.summaryDays ?? 90
  const today = new Date()
  const result: [string, number][] = []
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(today)
    d.setDate(today.getDate() - i)
    const key = d.toISOString().slice(0, 10)
    result.push([key, dailyTotals.value[key] ?? 0])
  }
  return result
})

const maxValue = computed(() => Math.max(1, ...Object.values(dailyTotals.value)))

// Date range for the calendar
const rangeStart = computed(() => {
  const days = props.summaryDays ?? 90
  const d = new Date()
  d.setDate(d.getDate() - days + 1)
  return d.toISOString().slice(0, 10)
})
const rangeEnd = computed(() => new Date().toISOString().slice(0, 10))

const option = computed<EChartsOption>(() => ({
  tooltip: {
    formatter: (params: any) =>
      `${params.value[0]}: <strong>${params.value[1]}</strong> application${
        params.value[1] !== 1 ? 's' : ''
      }`,
  },
  visualMap: {
    min: 0,
    max: maxValue.value,
    show: false,
    inRange: {
      color: ['#ebedf0', '#c6e48b', '#7bc96f', '#239a3b', '#196127'],
    },
  },
  calendar: {
    top: 20,
    left: 30,
    right: 10,
    bottom: 10,
    range: [rangeStart.value, rangeEnd.value],
    cellSize: ['auto', 13],
    splitLine: { show: false },
    itemStyle: {
      borderWidth: 2,
      borderColor: '#fff',
      borderRadius: 2,
    },
    yearLabel: { show: false },
    monthLabel: { fontSize: 10, color: '#999' },
    dayLabel: { fontSize: 9, color: '#bbb', firstDay: 1 },
  },
  series: [
    {
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: heatmapData.value,
    },
  ],
}))
</script>

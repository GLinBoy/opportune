<template>
  <FormCard :collapsible="false" style="height: 100%">
    <template #title> Applications by Status </template>
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
  statusCounts: { status: string; total: number }[]
  loading: boolean
}>()

const option = computed<EChartsOption>(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)',
  },
  legend: {
    bottom: 0,
    type: 'scroll',
    textStyle: { fontSize: 11 },
    icon: 'circle',
    itemWidth: 8,
    itemHeight: 8,
  },
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      padAngle: 4, // ← actual angular gap between slices (degrees)
      itemStyle: {
        borderRadius: 8, // rounded corners on each slice
      },
      label: {
        show: false,
      },
      labelLine: {
        show: false,
      },
      emphasis: {
        label: {
          show: false,
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.2)',
        },
      },
      data: [...props.statusCounts]
        .sort(
          (a, b) =>
            (APPLICATION_STATUS_ORDER.indexOf(a.status) + 1 || Infinity) -
            (APPLICATION_STATUS_ORDER.indexOf(b.status) + 1 || Infinity)
        )
        .map((s) => ({
          name: getApplicationStatusDisplay(s.status),
          value: s.total,
          itemStyle: { color: getApplicationStatusChartColor(s.status) },
        })),
    },
  ],
}))
</script>

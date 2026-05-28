<template>
  <FormCard :collapsible="false" style="height: 100%">
    <template #title>Account Status</template>
    <v-skeleton-loader v-if="loading" type="image" height="260" />
    <v-chart v-else :option="option" autoresize style="height: 260px" />
  </FormCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { EChartsOption } from 'echarts'
import FormCard from '../../forms/FormCard.vue'
import type { AccountStatusCount } from '../../../models'

const STATUS_COLORS: Record<string, string> = {
  ACTIVE: '#4CAF50',
  SUSPENDED: '#F44336',
  PENDING_VERIFICATION: '#FF9800',
}

const STATUS_LABELS: Record<string, string> = {
  ACTIVE: 'Active',
  SUSPENDED: 'Suspended',
  PENDING_VERIFICATION: 'Pending Verification',
}

const props = defineProps<{
  distribution: AccountStatusCount[]
  loading: boolean
}>()

const option = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
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
      padAngle: 4,
      itemStyle: { borderRadius: 8 },
      label: { show: false },
      labelLine: { show: false },
      emphasis: {
        label: { show: false },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' },
      },
      data: props.distribution.map((s) => ({
        name: STATUS_LABELS[s.status] ?? s.status,
        value: s.count,
        itemStyle: { color: STATUS_COLORS[s.status] ?? '#9E9E9E' },
      })),
    },
  ],
}))
</script>

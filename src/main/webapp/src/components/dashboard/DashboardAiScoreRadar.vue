<template>
  <FormCard :collapsible="false" style="height: 100%">
    <template #title> AI Score Overview </template>
    <v-skeleton-loader v-if="loading" type="image" height="220" />
    <div
      v-else-if="!hasScores"
      class="d-flex flex-column align-center justify-center"
      style="height: 280px"
    >
      <v-icon size="40" color="grey-lighten-1">mdi-robot-outline</v-icon>
      <p class="text-body-2 text-medium-emphasis mt-2">No AI scores yet</p>
    </div>
    <template v-else>
      <v-chart :option="option" autoresize style="height: 220px" />

      <!-- Score chips -->
      <v-divider class="mb-3" />
      <div class="d-flex flex-wrap justify-space-around gap-2">
        <v-chip
          v-for="score in scoreList"
          :key="score.label"
          size="small"
          variant="tonal"
          color="success"
          rounded="sm"
          class="ma-1 px-2"
        >
          <span class="score-chip__value mr-2">{{ score.value ?? '—' }}</span>
          {{ score.label }}
        </v-chip>
      </div>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import FormCard from '../forms/FormCard.vue'
import type { ScoreSummaryProjection } from '../../models'
import type { EChartsOption } from 'echarts'

const props = defineProps<{
  scores: ScoreSummaryProjection | null
  loading: boolean
}>()

const hasScores = computed(
  () => props.scores !== null && Object.values(props.scores).some((v) => v !== null)
)

const fmt = (v: number | null | undefined) => (v == null ? null : +v.toFixed(1))

const scoreList = computed(() => {
  const s = props.scores
  if (!s) return []
  return [
    { label: 'Overall', value: fmt(s.avgResumeScore) },
    { label: 'Skill', value: fmt(s.avgSkillScore) },
    { label: 'Experience', value: fmt(s.avgExperienceScore) },
    { label: 'Education', value: fmt(s.avgEducationScore) },
    { label: 'Keywords', value: fmt(s.avgKeywordScore) },
  ]
})

const option = computed<EChartsOption>(() => {
  const s = props.scores
  if (!s) return {}
  return {
    radar: {
      indicator: [
        { name: 'Overall', max: 100 },
        { name: 'Skill', max: 100 },
        { name: 'Experience', max: 100 },
        { name: 'Education', max: 100 },
        { name: 'Keywords', max: 100 },
      ],
      radius: '65%',
      axisName: { fontSize: 11 },
      splitNumber: 4,
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: [
              fmt(s.avgResumeScore),
              fmt(s.avgSkillScore),
              fmt(s.avgExperienceScore),
              fmt(s.avgEducationScore),
              fmt(s.avgKeywordScore),
            ],
            name: 'Avg Scores',
            areaStyle: { opacity: 0.2 },
            itemStyle: { color: '#66BB6A' },
            label: {
              show: false, // hidden by default
            },
            emphasis: {
              label: {
                show: true,
                formatter: (params: any) => `${params.value ?? '—'}`,
                fontSize: 13,
                fontWeight: 'bold',
                color: '#66BB6A',
              },
            },
          },
        ],
      },
    ],
  }
})
</script>

<style scoped>
.score-chip__value {
  font-weight: 700;
  background: rgba(102, 187, 106, 0.25);
  border-radius: 3px;
  padding: 1px 5px;
  font-variant-numeric: tabular-nums;
}
</style>

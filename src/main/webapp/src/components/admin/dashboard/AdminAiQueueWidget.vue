<template>
  <FormCard :collapsible="false" style="height: 100%">
    <template #title>
      AI Processing Queue
      <v-chip v-if="!loading && items.length > 0" color="warning" size="x-small" class="ms-2">{{
        items.length
      }}</v-chip>
    </template>

    <v-skeleton-loader v-if="loading" type="list-item-two-line@4" />

    <template v-else-if="items.length === 0">
      <div class="text-center text-medium-emphasis pa-6">
        <v-icon size="40" class="mb-2">mdi-check-circle-outline</v-icon>
        <div class="text-body-2">No jobs in queue</div>
      </div>
    </template>

    <v-list v-else lines="two" density="compact">
      <v-list-item
        v-for="item in items"
        :key="item.id"
        :subtitle="item.companyName ?? 'Unknown company'"
      >
        <template #title>
          <span class="text-body-2 font-weight-medium">
            {{ item.title ?? 'Untitled application' }}
          </span>
        </template>
        <template #append>
          <v-chip size="x-small" :color="waitColor(item.waitMinutes)">
            {{ formatWait(item.waitMinutes) }}
          </v-chip>
        </template>
      </v-list-item>
    </v-list>
  </FormCard>
</template>

<script setup lang="ts">
import FormCard from '../../forms/FormCard.vue'
import type { AiQueueItem } from '../../../models'

defineProps<{
  items: AiQueueItem[]
  loading: boolean
}>()

function formatWait(minutes: number): string {
  if (minutes < 60) return `${minutes}m`
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return m > 0 ? `${h}h ${m}m` : `${h}h`
}

function waitColor(minutes: number): string {
  if (minutes > 60) return 'error'
  if (minutes > 15) return 'warning'
  return 'info'
}
</script>

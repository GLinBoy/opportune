<template>
  <div class="bullet-editor">
    <div v-for="(bullet, index) in bullets" :key="index" class="d-flex align-start mb-1">
      <span class="bullet-number text-caption text-medium-emphasis mr-1 flex-shrink-0"
        >{{ index + 1 }}.</span
      >
      <span class="flex-grow-1 text-body-2 text-justify">{{ bullet.content }}</span>
      <v-btn
        icon="mdi-close"
        variant="text"
        size="x-small"
        color="error"
        class="flex-shrink-0 ml-1"
        @click="removeBullet(index)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { IWorkExperienceBullet } from '../../../models/resume-data.model'

const props = defineProps<{
  bullets: IWorkExperienceBullet[]
}>()

const emit = defineEmits<{
  'update:bullets': [value: IWorkExperienceBullet[]]
}>()

function removeBullet(index: number) {
  const updated = props.bullets.filter((_, i) => i !== index)
  emit(
    'update:bullets',
    updated.map((b, i) => ({ ...b, displayOrder: i }))
  )
}
</script>

<style scoped>
.bullet-number {
  line-height: 1.5;
}
</style>

<template>
  <v-card elevation="0" border rounded="lg" class="mb-4">
    <v-card-title class="pa-4 pb-0 d-flex align-center">
      <v-icon icon="mdi-text-box-edit" color="primary" class="mr-2" />
      <span class="text-body-1 font-weight-semibold">Professional Summary</span>
    </v-card-title>
    <v-card-text class="pa-4">
      <v-textarea
        :model-value="profile.professionalSummary"
        label="Professional Summary"
        variant="outlined"
        density="compact"
        placeholder="Write a brief summary of your professional background, key achievements, and career goals..."
        rows="5"
        auto-grow
        hint="This summary will be used by AI to tailor your cover letters and interview introductions."
        persistent-hint
        @update:model-value="updateField('professionalSummary', $event as string | undefined)"
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { IProfile } from '../../../models'

defineProps<{
  profile: IProfile
}>()

const emit = defineEmits<{
  'update': [field: string, value: string | undefined | boolean]
}>()

function updateField(field: string, value: string | undefined) {
  emit('update', field, value)
}
</script>

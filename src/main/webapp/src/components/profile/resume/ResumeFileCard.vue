<template>
  <FormCard collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-file-account" color="primary" class="mr-2" />
      <span class="text-body-1 font-weight-semibold">Resume File</span>
    </template>
    <template #default>
      <ProfileResumeField
        :profile-id="profileId"
        :resume-id="resumeId"
        @update:resume-id="$emit('update:resumeId', $event)"
      />
      <v-divider class="my-4" />
      <div
        v-if="resumeId && !extracting"
        class="d-flex align-center"
      >
        <div class="flex-grow-1">
          <div class="text-body-2 font-weight-medium">Extract Structured Data</div>
          <div class="text-caption text-medium-emphasis">
            Use AI to automatically extract work experience, education, and skills from your resume.
          </div>
        </div>
        <v-btn
          color="primary"
          variant="flat"
          prepend-icon="mdi-magic-staff"
          :loading="extracting"
          @click="$emit('extract')"
        >
          Extract from Resume
        </v-btn>
      </div>
      <div v-else-if="extracting" class="d-flex align-center">
        <v-progress-circular indeterminate size="24" class="mr-3" />
        <span class="text-body-2">Extracting data from your resume...</span>
      </div>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import FormCard from '../../forms/FormCard.vue'
import ProfileResumeField from '../ProfileResumeField.vue'

defineProps<{
  profileId?: string
  resumeId?: string
  extracting: boolean
}>()

defineEmits<{
  'update:resumeId': [value: string | undefined]
  'extract': []
}>()
</script>

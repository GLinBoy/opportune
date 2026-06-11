<template>
  <v-dialog v-model="isOpen" max-width="900" persistent>
    <v-card rounded="lg">
      <div class="form-dialog__status" style="height: 4px; border-radius: 8px 8px 0 0; background-color: rgb(var(--v-theme-primary))" />

      <v-card-title class="d-flex align-center pt-5 px-6 pb-3">
        <v-icon icon="mdi-magic-staff" color="primary" size="36" class="mr-3 flex-shrink-0" />
        <div>
          <div class="text-h6 font-weight-bold">Resume Extraction Preview</div>
          <div class="text-body-2 text-medium-emphasis font-weight-regular mt-1">
            Review the extracted data before saving. You can edit any information after saving.
          </div>
        </div>
      </v-card-title>

      <v-divider />

      <v-card-text class="px-6 pt-5 pb-3" style="max-height: 60vh; overflow-y: auto;">
        <div v-if="!result" class="text-center py-8 text-medium-emphasis">
          <v-progress-circular indeterminate color="primary" size="40" />
          <p class="mt-3">Loading extraction result...</p>
        </div>

        <template v-else>
          <!-- Work Experiences -->
          <div v-if="result.workExperiences && result.workExperiences.length" class="mb-6">
            <h3 class="text-subtitle-1 font-weight-bold mb-3 d-flex align-center">
              <v-icon icon="mdi-briefcase" class="mr-2" size="small" />
              Work Experience ({{ result.workExperiences.length }})
            </h3>
            <v-card
              v-for="(we, i) in result.workExperiences"
              :key="i"
              variant="outlined"
              class="mb-3"
            >
              <v-card-text>
                <div class="font-weight-medium">{{ we.jobTitle }}</div>
                <div class="text-caption text-medium-emphasis">{{ we.company }}{{ we.location ? ` — ${we.location}` : '' }}</div>
                <v-list v-if="we.bullets && we.bullets.length" density="compact" class="bg-transparent pa-0 mt-1">
                  <v-list-item v-for="(b, bi) in we.bullets" :key="bi" class="pa-0 text-body-2">
                    <template #prepend>
                      <v-icon icon="mdi-circle-small" size="x-small" class="mr-1" />
                    </template>
                    {{ b.content }}
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
          </div>

          <!-- Education -->
          <div v-if="result.education && result.education.length" class="mb-6">
            <h3 class="text-subtitle-1 font-weight-bold mb-3 d-flex align-center">
              <v-icon icon="mdi-school" class="mr-2" size="small" />
              Education ({{ result.education.length }})
            </h3>
            <v-card
              v-for="(edu, i) in result.education"
              :key="i"
              variant="outlined"
              class="mb-3"
            >
              <v-card-text>
                <div class="font-weight-medium">{{ edu.degree }} in {{ edu.fieldOfStudy }}</div>
                <div class="text-caption text-medium-emphasis">{{ edu.school }}</div>
                <div v-if="edu.courses && edu.courses.length" class="mt-1">
                  <v-chip v-for="(c, ci) in edu.courses" :key="ci" size="x-small" variant="outlined" class="mr-1 mb-1">
                    {{ c }}
                  </v-chip>
                </div>
              </v-card-text>
            </v-card>
          </div>

          <!-- Skill Groups -->
          <div v-if="result.skillGroups && result.skillGroups.length" class="mb-3">
            <h3 class="text-subtitle-1 font-weight-bold mb-3 d-flex align-center">
              <v-icon icon="mdi-lightning-bolt" class="mr-2" size="small" />
              Skills ({{ result.skillGroups.length }} groups)
            </h3>
            <v-card
              v-for="(sg, i) in result.skillGroups"
              :key="i"
              variant="outlined"
              class="mb-3"
            >
              <v-card-text>
                <div class="font-weight-medium mb-1">{{ sg.category }}</div>
                <v-chip
                  v-for="(skill, si) in sg.skills"
                  :key="si"
                  size="x-small"
                  color="primary"
                  variant="outlined"
                  class="mr-1 mb-1"
                >
                  {{ skill }}
                </v-chip>
              </v-card-text>
            </v-card>
          </div>

          <div
            v-if="(!result.workExperiences || !result.workExperiences.length) && (!result.education || !result.education.length) && (!result.skillGroups || !result.skillGroups.length)"
            class="text-center py-4 text-medium-emphasis"
          >
            <v-icon icon="mdi-alert-circle-outline" size="48" class="mb-2" />
            <p>No structured data could be extracted from your resume. The AI may not have been able to parse the content.</p>
          </div>
        </template>
      </v-card-text>

      <v-divider />

      <v-card-actions class="px-6 py-4">
        <v-spacer />
        <v-btn
          text="Cancel"
          variant="text"
          color="secondary"
          :disabled="saving"
          min-width="120"
          @click="onCancel"
        />
        <v-btn
          text="Save All"
          color="primary"
          variant="flat"
          rounded="md"
          :loading="saving"
          :disabled="!result"
          min-width="120"
          @click="onSave"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { IResumeExtractionResult } from '../../../models/resume-data.model'

const props = defineProps<{
  modelValue: boolean
  result: IResumeExtractionResult | null
  saving: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  save: [result: IResumeExtractionResult]
  cancel: []
}>()

const isOpen = ref(props.modelValue)

watch(
  () => props.modelValue,
  (newValue) => {
    isOpen.value = newValue
  }
)

watch(isOpen, (newValue) => {
  emit('update:modelValue', newValue)
})

function onCancel() {
  emit('cancel')
  isOpen.value = false
}

function onSave() {
  if (props.result) {
    emit('save', props.result)
  }
}
</script>

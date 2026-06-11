<template>
  <div>
    <ContactSectionCard :profile="profile" @update="onContactUpdate" />
    <SummaryCard :profile="profile" @update="onContactUpdate" />
    <ResumeFileCard
      :profile-id="profile.id"
      :resume-id="profile.resumeId"
      :extracting="store.extracting"
      @update:resume-id="onResumeIdUpdate"
      @extract="onExtract"
    />
    <SkillsCard />
    <div v-for="we in store.workExperiences" :key="we.id">
      <WorkExperienceCard :work-experience="we" />
    </div>
    <div v-for="edu in store.education" :key="edu.id">
      <EducationCard :education-item="edu" />
    </div>

    <!-- Placeholder for Phase 5: Optional sections (Projects, Certifications, Languages, etc.) -->

    <ExtractionPreviewDialog
      v-model="showPreview"
      :result="store.extractionResult"
      :saving="savingExtracted"
      @save="onSaveAllExtracted"
      @cancel="showPreview = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import type { IProfile } from '../../../models'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import ContactSectionCard from './ContactSectionCard.vue'
import SummaryCard from './SummaryCard.vue'
import ResumeFileCard from './ResumeFileCard.vue'
import SkillsCard from './SkillsCard.vue'
import WorkExperienceCard from './WorkExperienceCard.vue'
import EducationCard from './EducationCard.vue'
import ExtractionPreviewDialog from './ExtractionPreviewDialog.vue'
import type { IResumeExtractionResult } from '../../../models/resume-data.model'

const props = defineProps<{
  profile: IProfile
}>()

const emit = defineEmits<{
  'update:profile': [value: IProfile]
  change: []
}>()

const store = useResumeDataStore()
const showPreview = ref(false)
const savingExtracted = ref(false)

onMounted(() => {
  store.fetchAggregate()
})

watch(() => store.aggregate, () => {}, { deep: true })

async function onExtract() {
  const result = await store.extractFromResume()
  if (result) {
    showPreview.value = true
  }
}

async function onSaveAllExtracted(result: IResumeExtractionResult) {
  savingExtracted.value = true
  try {
    await store.saveAllExtracted(result)
    showPreview.value = false
  } finally {
    savingExtracted.value = false
  }
}

function onResumeIdUpdate(value: string | undefined) {
  emit('update:profile', { ...props.profile, resumeId: value })
  emit('change')
}

function onContactUpdate(field: string, value: string | undefined | boolean) {
  emit('update:profile', { ...props.profile, [field]: value })
  emit('change')
}
</script>

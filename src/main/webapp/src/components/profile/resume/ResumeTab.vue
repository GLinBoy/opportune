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

    <template v-if="store.projects && store.projects.length > 0">
      <ProjectCard />
    </template>
    <template v-if="store.certifications && store.certifications.length > 0">
      <CertificationCard />
    </template>
    <template v-if="store.languages && store.languages.length > 0">
      <LanguageCard />
    </template>
    <template v-if="store.volunteerWork && store.volunteerWork.length > 0">
      <VolunteerCard />
    </template>
    <template v-if="store.publications && store.publications.length > 0">
      <PublicationCard />
    </template>
    <template v-if="store.awards && store.awards.length > 0">
      <AwardCard />
    </template>
    <template v-if="store.affiliations && store.affiliations.length > 0">
      <AffiliationCard />
    </template>

    <v-card
      v-if="addableSections.length > 0"
      elevation="0"
      border
      rounded="lg"
      class="mb-4"
    >
      <div class="d-flex align-center pa-4 pb-0 mb-2">
        <v-icon icon="mdi-puzzle-plus" color="primary" size="28" class="mr-3" />
        <div class="text-body-1 font-weight-medium">Add Section</div>
      </div>
      <v-card-text class="pt-0">
        <v-chip-group column>
          <v-chip
            v-for="section in addableSections"
            :key="section.key"
            :prepend-icon="section.icon"
            color="primary"
            variant="outlined"
            @click="onAddSection(section.key)"
          >
            {{ section.label }}
          </v-chip>
        </v-chip-group>
      </v-card-text>
    </v-card>

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
import { ref, computed, onMounted, watch } from 'vue'
import type { IProfile } from '../../../models'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import ContactSectionCard from './ContactSectionCard.vue'
import SummaryCard from './SummaryCard.vue'
import ResumeFileCard from './ResumeFileCard.vue'
import SkillsCard from './SkillsCard.vue'
import WorkExperienceCard from './WorkExperienceCard.vue'
import EducationCard from './EducationCard.vue'
import ProjectCard from './ProjectCard.vue'
import CertificationCard from './CertificationCard.vue'
import LanguageCard from './LanguageCard.vue'
import VolunteerCard from './VolunteerCard.vue'
import PublicationCard from './PublicationCard.vue'
import AwardCard from './AwardCard.vue'
import AffiliationCard from './AffiliationCard.vue'
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

interface AddableSection {
  key: string
  label: string
  icon: string
}

const addableSections = computed<AddableSection[]>(() => {
  const sections: AddableSection[] = []
  if (!store.projects || store.projects.length === 0) sections.push({ key: 'projects', label: 'Projects', icon: 'mdi-code-braces' })
  if (!store.certifications || store.certifications.length === 0) sections.push({ key: 'certifications', label: 'Certifications', icon: 'mdi-certificate' })
  if (!store.languages || store.languages.length === 0) sections.push({ key: 'languages', label: 'Languages', icon: 'mdi-translate' })
  if (!store.volunteerWork || store.volunteerWork.length === 0) sections.push({ key: 'volunteerWork', label: 'Volunteer Work', icon: 'mdi-hand-heart' })
  if (!store.publications || store.publications.length === 0) sections.push({ key: 'publications', label: 'Publications', icon: 'mdi-book-open-page-variant' })
  if (!store.awards || store.awards.length === 0) sections.push({ key: 'awards', label: 'Awards', icon: 'mdi-trophy' })
  if (!store.affiliations || store.affiliations.length === 0) sections.push({ key: 'affiliations', label: 'Affiliations', icon: 'mdi-account-group' })
  return sections
})

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

function onAddSection(sectionKey: string) {
  showPreview.value = false
}
</script>

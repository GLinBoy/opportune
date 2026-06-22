<template>
  <div v-if="profile">
    <ResumeFileCard
      :profile-id="profile.id"
      :resume-id="profile.resumeId"
      :extracting="store.extracting"
      @update:resume-id="onResumeIdUpdate"
      @extract="onExtract"
    />
    <SkillsCard />
    <ExperienceCard />
    <EducationCard />

    <ProjectCard />
    <CertificationCard />
    <LanguageCard />
    <VolunteerCard />
    <PublicationCard />
    <AwardCard />
    <AffiliationCard />

    <Teleport to="body">
      <v-menu>
        <template #activator="{ props: menuProps }">
          <v-btn
            v-bind="menuProps"
            icon="mdi-plus"
            color="primary"
            size="large"
            class="add-section-fab"
            elevation="4"
          />
        </template>
        <v-list>
          <v-list-item
            v-for="section in addableSections"
            :key="section.key"
            :prepend-icon="section.icon"
            :title="section.label"
            @click="onAddSection(section.key)"
          />
        </v-list>
      </v-menu>
    </Teleport>

    <ExtractionPreviewDialog
      v-model="showPreview"
      :result="store.extractionResult"
      :saving="savingExtracted"
      @save="onSaveAllExtracted"
      @cancel="showPreview = false"
    />
  </div>
  <div v-else class="text-center py-8 text-medium-emphasis">
    <v-icon icon="mdi-file-account-off" size="48" class="mb-2" />
    <p>Unable to load resume data.</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import type { IProfile } from '../../../models'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import ResumeFileCard from './ResumeFileCard.vue'
import SkillsCard from './SkillsCard.vue'
import ExperienceCard from './ExperienceCard.vue'
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
  if (!store.education || store.education.length === 0) sections.push({ key: 'education', label: 'Education', icon: 'mdi-school' })
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

function onAddSection(sectionKey: string) {
  if (sectionKey === 'education') {
    return
  }
  showPreview.value = false
}
</script>

<style scoped>
.add-section-fab {
  position: fixed;
  bottom: 80px;
  right: 20px;
  z-index: 999;
}
</style>

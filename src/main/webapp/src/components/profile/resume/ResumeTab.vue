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
    <div v-for="we in store.workExperiences" :key="we.id">
      <WorkExperienceCard :work-experience="we" />
    </div>
    <EducationCard />

    <ProjectCard />
    <CertificationCard />
    <LanguageCard />
    <VolunteerCard />
    <PublicationCard />
    <AwardCard />
    <AffiliationCard />

    <FormDialog
      v-model="showAddWorkExperience"
      title="Add Work Experience"
      icon="mdi-briefcase-plus"
      :loading="savingNew"
      :valid="workFormValid"
      @confirm="saveNewWorkExperience"
      @cancel="showAddWorkExperience = false"
    >
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="workForm.jobTitle"
            label="Job Title"
            variant="outlined"
            density="compact"
            :rules="[rules.required]"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="workForm.company"
            label="Company"
            variant="outlined"
            density="compact"
            :rules="[rules.required]"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="workForm.location" label="Location" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="12" md="6">
          <v-switch v-model="workForm.isCurrent" label="I currently work here" color="primary" hide-details />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="workForm.startMonth" label="Start Month" type="number" variant="outlined" density="compact" min="1" max="12" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="workForm.startYear" label="Start Year" type="number" variant="outlined" density="compact" min="1900" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="workForm.endMonth" label="End Month" type="number" variant="outlined" density="compact" min="1" max="12" :disabled="workForm.isCurrent" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="workForm.endYear" label="End Year" type="number" variant="outlined" density="compact" min="1900" :disabled="workForm.isCurrent" />
        </v-col>
      </v-row>
    </FormDialog>

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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import type { IProfile } from '../../../models'
import { useResumeDataStore } from '../../../stores/resume-data.store'
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
import FormDialog from '../../FormDialog.vue'
import type { IResumeExtractionResult, IWorkExperience } from '../../../models/resume-data.model'

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
const showAddWorkExperience = ref(false)
const savingNew = ref(false)

const workForm = reactive({
  jobTitle: '',
  company: '',
  location: '',
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
})

const rules = {
  required: (v: string) => !!v || 'This field is required',
}

const workFormValid = computed(() => !!workForm.jobTitle && !!workForm.company)

watch(showAddWorkExperience, (open) => {
  if (open) {
    workForm.jobTitle = ''
    workForm.company = ''
    workForm.location = ''
    workForm.startMonth = undefined
    workForm.startYear = undefined
    workForm.endMonth = undefined
    workForm.endYear = undefined
    workForm.isCurrent = false
  }
})

interface AddableSection {
  key: string
  label: string
  icon: string
}

const addableSections = computed<AddableSection[]>(() => {
  const sections: AddableSection[] = []
    sections.push({ key: 'work-experience', label: 'Work Experience', icon: 'mdi-briefcase' })
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
  if (sectionKey === 'work-experience') {
    showAddWorkExperience.value = true
    return
  }
  if (sectionKey === 'education') {
    return
  }
  showPreview.value = false
}

async function saveNewWorkExperience() {
  savingNew.value = true
  try {
    const dto: IWorkExperience = { ...workForm }
    await store.createWorkExperience(dto)
    showAddWorkExperience.value = false
  } finally {
    savingNew.value = false
  }
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

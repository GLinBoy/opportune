<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-briefcase" color="primary" size="28" class="mr-3" />
      Experience
      <v-spacer />
      <v-btn
        v-if="cardOpen"
        icon="mdi-plus"
        variant="text"
        color="primary"
        size="small"
        @click.stop="openAdd"
      />
    </template>

    <template #default>
      <div v-if="store.workExperiences.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-briefcase-outline" size="40" class="mb-2" />
        <p class="text-body-2">No experience added yet.</p>
      </div>

      <div v-for="we in store.workExperiences" :key="we.id" class="mb-3 pa-2 rounded">
        <div class="d-flex align-start">
          <v-icon icon="mdi-briefcase-outline" size="small" color="primary" class="mr-2 mt-1" />
          <div class="flex-grow-1">
            <div class="text-subtitle-1 font-weight-medium">{{ we.jobTitle }}</div>
            <div class="text-body-2">
              {{ we.company }}<template v-if="we.industry"> ({{ we.industry }})</template><template v-if="we.employmentType"> &middot; {{ employmentTypeLabel(we.employmentType) }}</template>
            </div>
            <div class="text-caption text-medium-emphasis mt-1">
              {{ formatDate(we.startMonth, we.startYear, we.endMonth, we.endYear, we.isCurrent) }}
            </div>
            <div v-if="we.location || we.locationType" class="text-caption text-medium-emphasis">
              <template v-if="we.location">{{ we.location }}</template>
              <template v-if="we.locationType">
                <template v-if="we.location"> &middot; </template>
                {{ locationTypeLabel(we.locationType) }}
              </template>
            </div>
          </div>
          <div class="d-flex flex-shrink-0 ml-2">
            <v-tooltip text="Add bullet" location="top">
              <template #activator="{ props: tp }">
                <v-btn
                  v-bind="tp"
                  icon="mdi-plus-circle-outline"
                  variant="text"
                  size="x-small"
                  color="primary"
                  class="mr-1"
                  @click="openAddBullet(we)"
                />
              </template>
            </v-tooltip>
            <v-tooltip text="Edit" location="top">
              <template #activator="{ props: tp }">
                <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editExperience(we)" />
              </template>
            </v-tooltip>
            <v-tooltip text="Delete" location="top">
              <template #activator="{ props: tp }">
                <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="confirmDelete(we)" />
              </template>
            </v-tooltip>
          </div>
        </div>

        <div v-if="we.id" class="ml-8 mt-2">
          <div v-if="we.description" class="text-body-2 text-medium-emphasis mb-2 text-justify" style="white-space: pre-wrap">
            {{ we.description }}
          </div>
          <BulletEditor
            :bullets="we.bullets || []"
            @update:bullets="(b) => onUpdateBullets(we.id!, b)"
          />
        </div>
      </div>

      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Work Experience' : 'Add Work Experience'"
        icon="mdi-briefcase-plus"
        :loading="saving"
        :valid="formValid"
        @confirm="saveExperience"
        @cancel="revertForm"
      >
        <v-row>
          <v-col cols="12">
            <v-text-field v-model="form.jobTitle" label="Job Title" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.company" label="Company" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.industry" label="Industry" variant="outlined" density="compact" placeholder="e.g. GPS/IoT" />
          </v-col>
          <v-col cols="12">
            <v-select
              v-model="form.employmentType"
              label="Employment Type"
              :items="employmentTypeOptions"
              variant="outlined"
              density="compact"
              clearable
            />
          </v-col>
          <v-col cols="6">
            <v-select v-model="form.startMonth" label="Start Month" :items="monthOptions" variant="outlined" density="compact" clearable />
          </v-col>
          <v-col cols="6">
            <v-text-field v-model="form.startYear" label="Start Year" type="number" variant="outlined" density="compact" min="1900" />
          </v-col>
          <v-col cols="6">
            <v-select v-model="form.endMonth" label="End Month" :items="monthOptions" variant="outlined" density="compact" clearable :disabled="form.isCurrent" />
          </v-col>
          <v-col cols="6">
            <v-text-field
              v-model="form.endYear"
              label="End Year"
              type="number"
              variant="outlined"
              density="compact"
              min="1900"
              :disabled="form.isCurrent"
            />
          </v-col>
          <v-col cols="12">
            <v-switch v-model="form.isCurrent" label="I currently work here" color="primary" hide-details />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.location" label="Location" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12">
            <v-select
              v-model="form.locationType"
              label="Location Type"
              :items="locationTypeOptions"
              variant="outlined"
              density="compact"
              clearable
            />
          </v-col>
          <v-col cols="12">
            <v-textarea
              v-model="form.description"
              label="Description"
              variant="outlined"
              density="compact"
              rows="4"
              placeholder="Describe your role, responsibilities, and accomplishments..."
            />
          </v-col>
        </v-row>
      </FormDialog>

      <FormDialog
        v-model="showBulletDialog"
        title="Add Bullet Point"
        icon="mdi-plus-circle-outline"
        :loading="savingBullet"
        :valid="!!bulletText"
        @confirm="saveBullet"
        @cancel="showBulletDialog = false"
      >
        <v-textarea
          v-model="bulletText"
          label="Bullet point"
          variant="outlined"
          density="compact"
          rows="3"
          placeholder="Describe an accomplishment or responsibility..."
          :rules="[rules.required]"
          hide-details
        />
      </FormDialog>

      <ConfirmDialog
        v-model="deleteConfirm"
        title="Delete Work Experience"
        variant="error"
        confirm-text="Delete"
        :loading="deleting"
        @confirm="doDelete"
      >
        Are you sure you want to delete "{{ deleteTarget?.jobTitle }}" at
        {{ deleteTarget?.company }}?
      </ConfirmDialog>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IWorkExperience, IWorkExperienceBullet, EmploymentType, LocationType } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormCard from '../../forms/FormCard.vue'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'
import BulletEditor from './BulletEditor.vue'

const store = useResumeDataStore()

const cardOpen = ref(true)
const showDialog = ref(false)
const saving = ref(false)
const editingId = ref<string | null>(null)
const deleteConfirm = ref(false)
const deleting = ref(false)
const deleteTarget = ref<IWorkExperience | null>(null)
const showBulletDialog = ref(false)
const savingBullet = ref(false)
const bulletText = ref('')
const bulletTarget = ref<IWorkExperience | null>(null)

const monthOptions = [
  { title: 'January', value: 1 },
  { title: 'February', value: 2 },
  { title: 'March', value: 3 },
  { title: 'April', value: 4 },
  { title: 'May', value: 5 },
  { title: 'June', value: 6 },
  { title: 'July', value: 7 },
  { title: 'August', value: 8 },
  { title: 'September', value: 9 },
  { title: 'October', value: 10 },
  { title: 'November', value: 11 },
  { title: 'December', value: 12 },
]

const employmentTypeOptions = [
  { title: 'Full-time', value: 'FULL_TIME' },
  { title: 'Part-time', value: 'PART_TIME' },
  { title: 'Self-employed', value: 'SELF_EMPLOYED' },
  { title: 'Freelance', value: 'FREELANCE' },
  { title: 'Contract', value: 'CONTRACT' },
  { title: 'Internship', value: 'INTERNSHIP' },
  { title: 'Apprenticeship', value: 'APPRENTICESHIP' },
  { title: 'Co-op', value: 'CO_OP' },
  { title: 'Lifetime civil servant', value: 'LIFETIME_CIVIL_SERVANT' },
  { title: 'Civil service internship', value: 'CIVIL_SERVICE_INTERNSHIP' },
  { title: 'Work study', value: 'WORK_STUDY' },
]

const locationTypeOptions = [
  { title: 'On-site', value: 'ON_SITE' },
  { title: 'Remote', value: 'REMOTE' },
  { title: 'Hybrid', value: 'HYBRID' },
]

function employmentTypeLabel(type: EmploymentType): string {
  const opt = employmentTypeOptions.find((o) => o.value === type)
  return opt?.title ?? type
}

function locationTypeLabel(type: LocationType): string {
  const opt = locationTypeOptions.find((o) => o.value === type)
  return opt?.title ?? type
}

const form = reactive({
  jobTitle: '',
  company: '',
  industry: '',
  employmentType: undefined as EmploymentType | undefined,
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  location: '',
  locationType: undefined as LocationType | undefined,
  description: '',
})

const formSnapshot = reactive({
  jobTitle: '',
  company: '',
  industry: '',
  employmentType: undefined as EmploymentType | undefined,
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  location: '',
  locationType: undefined as LocationType | undefined,
  description: '',
})

const rules = {
  required: (v: string) => !!v || 'This field is required',
}

const formValid = computed(() => !!form.jobTitle && !!form.company)

function formatDatePart(month?: number | null, year?: number | null): string {
  if (month && year) {
    const names = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    return `${names[month - 1]} ${year}`
  }
  if (year) return String(year)
  return ''
}

function formatDate(startMonth?: number | null, startYear?: number | null, endMonth?: number | null, endYear?: number | null, isCurrent?: boolean | null): string {
  const start = formatDatePart(startMonth, startYear)
  const end = isCurrent ? 'Present' : formatDatePart(endMonth, endYear)
  if (!start && !end) return ''
  const duration = computeDuration(startMonth, startYear, endMonth, endYear, isCurrent)
  const suffix = duration ? ` \u00b7 ${duration}` : ''
  return `${start || '?'} \u2014 ${end || '?'}${suffix}`
}

function computeDuration(startMonth?: number | null, startYear?: number | null, endMonth?: number | null, endYear?: number | null, isCurrent?: boolean | null): string {
  if (!startYear) return ''
  const now = new Date()
  const sy = startYear ?? 0
  const sm = startMonth ?? 1
  const ey = isCurrent ? now.getFullYear() : (endYear ?? 0)
  const em = isCurrent ? now.getMonth() + 1 : (endMonth ?? 1)
  const totalMonths = (ey - sy) * 12 + (em - sm) + 1
  if (totalMonths < 1) return ''
  const years = Math.floor(totalMonths / 12)
  const months = totalMonths % 12
  const parts: string[] = []
  if (years > 0) parts.push(`${years} ${years === 1 ? 'yr' : 'yrs'}`)
  if (months > 0) parts.push(`${months} ${months === 1 ? 'mo' : 'mos'}`)
  return parts.join(' ')
}

function resetForm() {
  form.jobTitle = ''
  form.company = ''
  form.industry = ''
  form.employmentType = undefined
  form.startMonth = undefined
  form.startYear = undefined
  form.endMonth = undefined
  form.endYear = undefined
  form.isCurrent = false
  form.location = ''
  form.locationType = undefined
  form.description = ''
}

function takeSnapshot() {
  formSnapshot.jobTitle = form.jobTitle
  formSnapshot.company = form.company
  formSnapshot.industry = form.industry
  formSnapshot.employmentType = form.employmentType
  formSnapshot.startMonth = form.startMonth
  formSnapshot.startYear = form.startYear
  formSnapshot.endMonth = form.endMonth
  formSnapshot.endYear = form.endYear
  formSnapshot.isCurrent = form.isCurrent
  formSnapshot.location = form.location
  formSnapshot.locationType = form.locationType
  formSnapshot.description = form.description
}

function openAdd() {
  editingId.value = null
  resetForm()
  takeSnapshot()
  showDialog.value = true
}

function editExperience(we: IWorkExperience) {
  editingId.value = we.id || null
  form.jobTitle = we.jobTitle || ''
  form.company = we.company || ''
  form.industry = we.industry || ''
  form.employmentType = we.employmentType ?? undefined
  form.startMonth = we.startMonth ?? undefined
  form.startYear = we.startYear ?? undefined
  form.endMonth = we.endMonth ?? undefined
  form.endYear = we.endYear ?? undefined
  form.isCurrent = we.isCurrent ?? false
  form.location = we.location || ''
  form.locationType = we.locationType ?? undefined
  form.description = we.description || ''
  takeSnapshot()
  showDialog.value = true
}

function revertForm() {
  form.jobTitle = formSnapshot.jobTitle
  form.company = formSnapshot.company
  form.industry = formSnapshot.industry
  form.employmentType = formSnapshot.employmentType
  form.startMonth = formSnapshot.startMonth
  form.startYear = formSnapshot.startYear
  form.endMonth = formSnapshot.endMonth
  form.endYear = formSnapshot.endYear
  form.isCurrent = formSnapshot.isCurrent
  form.location = formSnapshot.location
  form.locationType = formSnapshot.locationType
  form.description = formSnapshot.description
  if (!editingId.value) {
    showDialog.value = false
  }
}

async function saveExperience() {
  saving.value = true
  try {
    const dto: IWorkExperience = {
      jobTitle: form.jobTitle,
      company: form.company,
      industry: form.industry || undefined,
      employmentType: form.employmentType,
      startMonth: form.startMonth,
      startYear: form.startYear,
      endMonth: form.endMonth,
      endYear: form.endYear,
      isCurrent: form.isCurrent,
      location: form.location || undefined,
      locationType: form.locationType,
      description: form.description || undefined,
    }
    if (editingId.value) {
      await store.updateWorkExperience(editingId.value, dto)
    } else {
      await store.createWorkExperience(dto)
    }
    takeSnapshot()
    showDialog.value = false
  } finally {
    saving.value = false
  }
}

function confirmDelete(we: IWorkExperience) {
  deleteTarget.value = we
  deleteConfirm.value = true
}

async function doDelete() {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await store.deleteWorkExperience(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}

async function onUpdateBullets(workExperienceId: string, bullets: IWorkExperienceBullet[]) {
  await store.replaceBullets(workExperienceId, bullets)
}

function openAddBullet(we: IWorkExperience) {
  bulletTarget.value = we
  bulletText.value = ''
  showBulletDialog.value = true
}

async function saveBullet() {
  if (!bulletTarget.value?.id || !bulletText.value.trim()) return
  savingBullet.value = true
  try {
    const current = bulletTarget.value.bullets ?? []
    const newBullet: IWorkExperienceBullet = {
      content: bulletText.value.trim(),
      displayOrder: current.length,
    }
    await store.replaceBullets(bulletTarget.value.id, [...current, newBullet])
    showBulletDialog.value = false
    bulletTarget.value = null
  } finally {
    savingBullet.value = false
  }
}
</script>

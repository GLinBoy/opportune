<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-hand-heart" color="primary" size="28" class="mr-3" />
      Volunteer Work
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
      <div v-if="store.volunteerWork.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-hand-heart-outline" size="40" class="mb-2" />
        <p class="text-body-2">No volunteer work added yet.</p>
      </div>

      <div v-for="vw in store.volunteerWork" :key="vw.id" class="mb-3 pa-2 rounded">
        <div class="d-flex align-start">
          <v-icon icon="mdi-hand-heart-outline" size="small" color="primary" class="mr-2 mt-1" />
          <div class="flex-grow-1">
            <div class="text-subtitle-1 font-weight-medium">{{ vw.role }}</div>
            <div class="text-body-2">
              {{ vw.organization }}<template v-if="vw.location"> &middot; {{ vw.location }}</template>
            </div>
            <div class="text-caption text-medium-emphasis mt-1">
              {{ formatDate(vw.startMonth, vw.startYear, vw.endMonth, vw.endYear, vw.isCurrent) }}
            </div>
            <div v-if="vw.description" class="text-body-2 mt-1 text-justify" style="white-space: pre-wrap">
              {{ vw.description }}
            </div>
          </div>
          <div class="d-flex flex-shrink-0 ml-2">
            <v-tooltip text="Edit" location="top">
              <template #activator="{ props: tp }">
                <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editVolunteerWork(vw)" />
              </template>
            </v-tooltip>
            <v-tooltip text="Delete" location="top">
              <template #activator="{ props: tp }">
                <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="confirmDelete(vw)" />
              </template>
            </v-tooltip>
          </div>
        </div>
      </div>

      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Volunteer Work' : 'Add Volunteer Work'"
        icon="mdi-hand-heart"
        :loading="saving"
        :valid="formValid"
        @confirm="saveVolunteerWork"
        @cancel="revertForm"
      >
        <v-row>
          <v-col cols="12">
            <v-text-field v-model="form.organization" label="Organization" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.role" label="Role" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.location" label="Location" variant="outlined" density="compact" />
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
            <v-text-field v-model="form.endYear" label="End Year" type="number" variant="outlined" density="compact" min="1900" :disabled="form.isCurrent" />
          </v-col>
          <v-col cols="12">
            <v-switch v-model="form.isCurrent" label="Currently volunteering" color="primary" hide-details />
          </v-col>
          <v-col cols="12">
            <v-textarea v-model="form.description" label="Description" variant="outlined" density="compact" rows="4" />
          </v-col>
        </v-row>
      </FormDialog>

      <ConfirmDialog
        v-model="deleteConfirm"
        title="Delete Volunteer Work"
        variant="error"
        confirm-text="Delete"
        :loading="deleting"
        @confirm="doDelete"
      >
        Are you sure you want to delete "{{ deleteTarget?.role }}" at {{ deleteTarget?.organization }}?
      </ConfirmDialog>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IVolunteerWork } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormCard from '../../forms/FormCard.vue'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'

const store = useResumeDataStore()

const cardOpen = ref(true)
const showDialog = ref(false)
const saving = ref(false)
const editingId = ref<string | null>(null)
const deleteConfirm = ref(false)
const deleting = ref(false)
const deleteTarget = ref<IVolunteerWork | null>(null)

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

const form = reactive({
  role: '',
  organization: '',
  location: '',
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  description: '',
})

const formSnapshot = reactive({
  role: '',
  organization: '',
  location: '',
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  description: '',
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.role && !!form.organization)

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
  form.role = ''
  form.organization = ''
  form.location = ''
  form.startMonth = undefined
  form.startYear = undefined
  form.endMonth = undefined
  form.endYear = undefined
  form.isCurrent = false
  form.description = ''
}

function takeSnapshot() {
  formSnapshot.role = form.role
  formSnapshot.organization = form.organization
  formSnapshot.location = form.location
  formSnapshot.startMonth = form.startMonth
  formSnapshot.startYear = form.startYear
  formSnapshot.endMonth = form.endMonth
  formSnapshot.endYear = form.endYear
  formSnapshot.isCurrent = form.isCurrent
  formSnapshot.description = form.description
}

function openAdd() {
  editingId.value = null
  resetForm()
  takeSnapshot()
  showDialog.value = true
}

function editVolunteerWork(vw: IVolunteerWork) {
  editingId.value = vw.id || null
  form.role = vw.role || ''
  form.organization = vw.organization || ''
  form.location = vw.location || ''
  form.startMonth = vw.startMonth ?? undefined
  form.startYear = vw.startYear ?? undefined
  form.endMonth = vw.endMonth ?? undefined
  form.endYear = vw.endYear ?? undefined
  form.isCurrent = vw.isCurrent ?? false
  form.description = vw.description || ''
  takeSnapshot()
  showDialog.value = true
}

function revertForm() {
  form.role = formSnapshot.role
  form.organization = formSnapshot.organization
  form.location = formSnapshot.location
  form.startMonth = formSnapshot.startMonth
  form.startYear = formSnapshot.startYear
  form.endMonth = formSnapshot.endMonth
  form.endYear = formSnapshot.endYear
  form.isCurrent = formSnapshot.isCurrent
  form.description = formSnapshot.description
  if (!editingId.value) {
    showDialog.value = false
  }
}

async function saveVolunteerWork() {
  saving.value = true
  try {
    const dto: IVolunteerWork = {
      role: form.role,
      organization: form.organization,
      location: form.location || undefined,
      startMonth: form.startMonth,
      startYear: form.startYear,
      endMonth: form.endMonth,
      endYear: form.endYear,
      isCurrent: form.isCurrent,
      description: form.description || undefined,
    }
    if (editingId.value) {
      await store.updateVolunteerWork(editingId.value, dto)
    } else {
      await store.createVolunteerWork(dto)
    }
    takeSnapshot()
    showDialog.value = false
  } finally {
    saving.value = false
  }
}

function confirmDelete(vw: IVolunteerWork) {
  deleteTarget.value = vw
  deleteConfirm.value = true
}

async function doDelete() {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await store.deleteVolunteerWork(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

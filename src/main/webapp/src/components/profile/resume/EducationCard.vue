<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-school" color="primary" size="28" class="mr-3" />
      Education
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

      <div v-if="store.education.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-school-outline" size="40" class="mb-2" />
        <p class="text-body-2">No education added yet.</p>
      </div>

      <div v-for="edu in store.education" :key="edu.id" class="mb-3 pa-2 rounded">
        <div class="d-flex align-start">
          <v-icon icon="mdi-school-outline" size="small" color="primary" class="mr-2 mt-1" />
          <div class="flex-grow-1">
            <div class="text-subtitle-1 font-weight-medium">{{ edu.school }}</div>
            <div class="text-body-2">
              {{ edu.degree }}<span v-if="edu.fieldOfStudy"> &middot; {{ edu.fieldOfStudy }}</span>
            </div>
            <div class="text-caption text-medium-emphasis">
              {{ formatDate(edu.startMonth, edu.startYear, edu.endMonth, edu.endYear, edu.isCurrent) }}
            </div>
            <div v-if="edu.gpa" class="text-caption text-medium-emphasis">
              GPA: {{ edu.gpa }}
            </div>
            <div v-if="edu.honors" class="text-caption text-medium-emphasis mb-1">
              {{ edu.honors }}
            </div>
            <div v-if="edu.courses?.length" class="text-caption text-medium-emphasis">
              <span class="font-weight-medium">Relevant Courses:</span>
              {{ edu.courses.join(', ') }}
            </div>
          </div>
          <div class="d-flex flex-shrink-0 ml-2">
            <v-tooltip text="Edit" location="top">
              <template #activator="{ props: tp }">
                <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editEducation(edu)" />
              </template>
            </v-tooltip>
            <v-tooltip text="Delete" location="top">
              <template #activator="{ props: tp }">
                <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="confirmDelete(edu)" />
              </template>
            </v-tooltip>
          </div>
        </div>
      </div>

      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Education' : 'Add Education'"
        icon="mdi-school"
        :loading="saving"
        :valid="formValid"
        @confirm="saveEducation"
        @cancel="revertForm"
      >
        <v-row>
          <v-col cols="12">
            <v-text-field v-model="form.school" label="School" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.degree" label="Degree" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.fieldOfStudy" label="Field of Study" variant="outlined" density="compact" :rules="[rules.required]" />
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
            <v-switch v-model="form.isCurrent" label="Currently enrolled" color="primary" hide-details />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.gpa" label="GPA" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12">
            <v-textarea v-model="form.honors" label="Honors" variant="outlined" density="compact" rows="4" />
          </v-col>
          <v-col cols="12">
            <v-combobox
              v-model="form.courses"
              label="Relevant Courses"
              variant="outlined"
              density="compact"
              multiple
              chips
              deletable-chips
              small-chips
              hint="Type a course name and press Enter"
              persistent-hint
            />
          </v-col>
        </v-row>
      </FormDialog>

      <ConfirmDialog
        v-model="deleteConfirm"
        title="Delete Education"
        variant="error"
        confirm-text="Delete"
        :loading="deleting"
        @confirm="doDelete"
      >
        Are you sure you want to delete "{{ deleteTarget?.degree }}" at {{ deleteTarget?.school }}?
      </ConfirmDialog>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IEducation } from '../../../models/resume-data.model'
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
const deleteTarget = ref<IEducation | null>(null)

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
  school: '',
  degree: '',
  fieldOfStudy: '',
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  gpa: '',
  honors: '',
  courses: [] as string[],
})

const formSnapshot = reactive({
  school: '',
  degree: '',
  fieldOfStudy: '',
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  gpa: '',
  honors: '',
  courses: [] as string[],
})

const rules = {
  required: (v: string) => !!v || 'This field is required',
}

const formValid = computed(() => !!form.school && !!form.degree && !!form.fieldOfStudy)

const dirty = computed(() => {
  if (!editingId.value) return true
  return form.school !== formSnapshot.school
    || form.degree !== formSnapshot.degree
    || form.fieldOfStudy !== formSnapshot.fieldOfStudy
    || form.startMonth !== formSnapshot.startMonth
    || form.startYear !== formSnapshot.startYear
    || form.endMonth !== formSnapshot.endMonth
    || form.endYear !== formSnapshot.endYear
    || form.isCurrent !== formSnapshot.isCurrent
    || form.gpa !== formSnapshot.gpa
    || form.honors !== formSnapshot.honors
    || JSON.stringify(form.courses) !== JSON.stringify(formSnapshot.courses)
})

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
  return `${start || '?'} \u2014 ${end || '?'}`
}

function resetForm() {
  form.school = ''
  form.degree = ''
  form.fieldOfStudy = ''
  form.startMonth = undefined
  form.startYear = undefined
  form.endMonth = undefined
  form.endYear = undefined
  form.isCurrent = false
  form.gpa = ''
  form.honors = ''
  form.courses = []
}

function takeSnapshot() {
  formSnapshot.school = form.school
  formSnapshot.degree = form.degree
  formSnapshot.fieldOfStudy = form.fieldOfStudy
  formSnapshot.startMonth = form.startMonth
  formSnapshot.startYear = form.startYear
  formSnapshot.endMonth = form.endMonth
  formSnapshot.endYear = form.endYear
  formSnapshot.isCurrent = form.isCurrent
  formSnapshot.gpa = form.gpa
  formSnapshot.honors = form.honors
  formSnapshot.courses = [...form.courses]
}

function openAdd() {
  editingId.value = null
  resetForm()
  takeSnapshot()
  showDialog.value = true
}

function editEducation(edu: IEducation) {
  editingId.value = edu.id || null
  form.school = edu.school || ''
  form.degree = edu.degree || ''
  form.fieldOfStudy = edu.fieldOfStudy || ''
  form.startMonth = edu.startMonth ?? undefined
  form.startYear = edu.startYear ?? undefined
  form.endMonth = edu.endMonth ?? undefined
  form.endYear = edu.endYear ?? undefined
  form.isCurrent = edu.isCurrent ?? false
  form.gpa = edu.gpa || ''
  form.honors = edu.honors || ''
  form.courses = edu.courses ? [...edu.courses] : []
  takeSnapshot()
  showDialog.value = true
}

function revertForm() {
  form.school = formSnapshot.school
  form.degree = formSnapshot.degree
  form.fieldOfStudy = formSnapshot.fieldOfStudy
  form.startMonth = formSnapshot.startMonth
  form.startYear = formSnapshot.startYear
  form.endMonth = formSnapshot.endMonth
  form.endYear = formSnapshot.endYear
  form.isCurrent = formSnapshot.isCurrent
  form.gpa = formSnapshot.gpa
  form.honors = formSnapshot.honors
  form.courses = [...formSnapshot.courses]
  if (!editingId.value) {
    showDialog.value = false
  }
}

async function saveEducation() {
  saving.value = true
  try {
    const dto: IEducation = {
      school: form.school,
      degree: form.degree,
      fieldOfStudy: form.fieldOfStudy,
      startMonth: form.startMonth,
      startYear: form.startYear,
      endMonth: form.endMonth,
      endYear: form.endYear,
      isCurrent: form.isCurrent,
      gpa: form.gpa || undefined,
      honors: form.honors || undefined,
      courses: form.courses,
    }
    if (editingId.value) {
      await store.updateEducation(editingId.value, dto)
    } else {
      await store.createEducation(dto)
    }
    takeSnapshot()
    showDialog.value = false
  } finally {
    saving.value = false
  }
}

function confirmDelete(edu: IEducation) {
  deleteTarget.value = edu
  deleteConfirm.value = true
}

async function doDelete() {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await store.deleteEducation(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

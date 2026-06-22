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
        <div class="d-flex align-center mb-1">
          <v-icon icon="mdi-school-outline" size="small" color="primary" class="mr-2" />
          <div class="flex-grow-1">
            <span class="text-subtitle-2 font-weight-medium">{{ edu.degree }}</span>
            <span v-if="edu.school" class="text-caption text-medium-emphasis ml-2">
              &mdash; {{ edu.school }}
            </span>
          </div>
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editEducation(edu)" />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteTarget = edu" />
            </template>
          </v-tooltip>
        </div>
        <div v-if="edu.fieldOfStudy" class="text-caption text-medium-emphasis ml-8">
          {{ edu.fieldOfStudy }}
        </div>
        <div v-if="edu.startYear || edu.endYear" class="text-caption text-medium-emphasis ml-8">
          {{ formatDate(edu.startYear, edu.endYear, edu.isCurrent) }}
        </div>
        <div v-if="edu.gpa" class="text-caption text-medium-emphasis ml-8">
          GPA: {{ edu.gpa }}
        </div>
      </div>

      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Education' : 'Add Education'"
        icon="mdi-school"
        :loading="saving"
        :valid="formValid"
        hide-confirm
        @confirm="saveEducation"
        @cancel="revertForm"
      >
        <v-row>
          <v-col cols="12" class="d-flex ga-2 pb-0">
            <v-btn
              v-if="dirty"
              color="success"
              prepend-icon="mdi-content-save"
              @click="saveEducation"
            >
              Save
            </v-btn>
            <v-btn
              color="secondary"
              variant="outlined"
              prepend-icon="mdi-undo"
              @click="revertForm"
            >
              Cancel
            </v-btn>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.school" label="School" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.degree" label="Degree" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.fieldOfStudy" label="Field of Study" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.startYear" label="Start Year" type="number" variant="outlined" density="compact" min="1900" />
          </v-col>
          <v-col cols="6" md="3">
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
          <v-col cols="12" md="6">
            <v-switch v-model="form.isCurrent" label="Currently enrolled" color="primary" hide-details />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.gpa" label="GPA" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.honors" label="Honors" variant="outlined" density="compact" />
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
              append-icon="mdi-plus"
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

const form = reactive({
  school: '',
  degree: '',
  fieldOfStudy: '',
  startYear: undefined as number | undefined,
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
  startYear: undefined as number | undefined,
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
    || form.startYear !== formSnapshot.startYear
    || form.endYear !== formSnapshot.endYear
    || form.isCurrent !== formSnapshot.isCurrent
    || form.gpa !== formSnapshot.gpa
    || form.honors !== formSnapshot.honors
    || JSON.stringify(form.courses) !== JSON.stringify(formSnapshot.courses)
})

function formatDate(startYear?: number | null, endYear?: number | null, isCurrent?: boolean | null): string {
  const end = isCurrent ? 'Present' : endYear || ''
  if (!startYear && !end) return ''
  return `${startYear || '?'} — ${end}`
}

function resetForm() {
  form.school = ''
  form.degree = ''
  form.fieldOfStudy = ''
  form.startYear = undefined
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
  formSnapshot.startYear = form.startYear
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
  form.startYear = edu.startYear ?? undefined
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
  form.startYear = formSnapshot.startYear
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
    const dto: IEducation = { ...form }
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

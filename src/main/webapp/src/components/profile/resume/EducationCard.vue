<template>
  <v-card
    v-if="educationItem"
    elevation="0"
    border
    rounded="lg"
    class="mb-4"
  >
    <div class="d-flex align-center pa-4 pb-0">
      <v-icon icon="mdi-school" color="primary" size="28" class="mr-3" />
      <div class="flex-grow-1">
        <div class="text-body-1 font-weight-medium">
          {{ educationItem.degree || 'Untitled' }}
        </div>
        <div class="text-caption text-medium-emphasis">
          {{ educationItem.school }}
          <template v-if="educationItem.fieldOfStudy"> &middot; {{ educationItem.fieldOfStudy }} </template>
        </div>
      </div>
      <div class="d-flex ga-1">
        <v-tooltip text="Edit" location="top">
          <template #activator="{ props: tp }">
            <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="small" color="primary" @click="editDialog = true" />
          </template>
        </v-tooltip>
        <v-tooltip text="Delete" location="top">
          <template #activator="{ props: tp }">
            <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="small" color="error" @click="confirmDelete = true" />
          </template>
        </v-tooltip>
      </div>
    </div>

    <v-card-text class="pt-2">
      <div v-if="educationItem.startYear || educationItem.endYear" class="text-caption text-medium-emphasis mb-1">
        {{ dateRange }}
      </div>
      <div v-if="educationItem.gpa" class="text-caption text-medium-emphasis">
        GPA: {{ educationItem.gpa }}
      </div>
      <div v-if="educationItem.honors" class="text-caption text-medium-emphasis">
        {{ educationItem.honors }}
      </div>
      <v-list v-if="educationItem.courses && educationItem.courses.length" density="compact" class="bg-transparent pa-0 mt-1">
        <v-list-item v-for="(course, i) in educationItem.courses" :key="i" class="pa-0 text-caption">
          <template #prepend>
            <v-icon icon="mdi-book-open-variant" size="x-small" class="mr-1" />
          </template>
          {{ course }}
        </v-list-item>
      </v-list>
    </v-card-text>

    <FormDialog
      v-model="editDialog"
      title="Edit Education"
      icon="mdi-school"
      :loading="saving"
      :valid="formValid"
      @confirm="saveEdit"
      @cancel="editDialog = false"
    >
      <v-row>
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
      v-model="confirmDelete"
      title="Delete Education"
      variant="error"
      confirm-text="Delete"
      :loading="deleting"
      @confirm="doDelete"
    >
      Are you sure you want to delete this education entry? This action cannot be undone.
    </ConfirmDialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import type { IEducation } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'

const props = defineProps<{
  educationItem: IEducation
}>()

const store = useResumeDataStore()
const editDialog = ref(false)
const confirmDelete = ref(false)
const saving = ref(false)
const deleting = ref(false)

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

const rules = {
  required: (v: string) => !!v || 'This field is required',
}

const formValid = computed(() => !!form.school && !!form.degree && !!form.fieldOfStudy)

const dateRange = computed(() => {
  const edu = props.educationItem
  const end = edu.isCurrent ? 'Present' : edu.endYear || ''
  if (!edu.startYear && !end) return ''
  return `${edu.startYear || '?'} — ${end}`
})

watch(editDialog, (open) => {
  if (open) {
    form.school = props.educationItem.school || ''
    form.degree = props.educationItem.degree || ''
    form.fieldOfStudy = props.educationItem.fieldOfStudy || ''
    form.startYear = props.educationItem.startYear ?? undefined
    form.endYear = props.educationItem.endYear ?? undefined
    form.isCurrent = props.educationItem.isCurrent ?? false
    form.gpa = props.educationItem.gpa || ''
    form.honors = props.educationItem.honors || ''
    form.courses = props.educationItem.courses ? [...props.educationItem.courses] : []
  }
})

async function saveEdit() {
  if (!props.educationItem.id) return
  saving.value = true
  try {
    const dto: IEducation = { ...form }
    await store.updateEducation(props.educationItem.id, dto)
    editDialog.value = false
  } finally {
    saving.value = false
  }
}

async function doDelete() {
  if (!props.educationItem.id) return
  deleting.value = true
  try {
    await store.deleteEducation(props.educationItem.id)
    confirmDelete.value = false
  } finally {
    deleting.value = false
  }
}
</script>

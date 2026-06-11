<template>
  <v-card
    v-if="workExperience"
    elevation="0"
    border
    rounded="lg"
    class="mb-4"
  >
    <div class="d-flex align-center pa-4 pb-0">
      <v-icon icon="mdi-briefcase" color="primary" size="28" class="mr-3" />
      <div class="flex-grow-1">
        <div class="text-body-1 font-weight-medium">
          {{ workExperience.jobTitle || 'Untitled' }}
        </div>
        <div class="text-caption text-medium-emphasis">
          {{ workExperience.company }}
          <template v-if="workExperience.location"> &middot; {{ workExperience.location }} </template>
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

    <v-card-text class="pt-2 pb-0">
      <div v-if="workExperience.startYear || workExperience.endYear" class="text-caption text-medium-emphasis mb-2">
        {{ dateRange }}
      </div>
      <BulletEditor
        v-if="workExperience.id"
        :bullets="workExperience.bullets || []"
        @update:bullets="onUpdateBullets"
      />
    </v-card-text>

    <FormDialog
      v-model="editDialog"
      title="Edit Work Experience"
      icon="mdi-briefcase-edit"
      :loading="saving"
      :valid="formValid"
      @confirm="saveEdit"
      @cancel="editDialog = false"
    >
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="form.jobTitle"
            label="Job Title"
            variant="outlined"
            density="compact"
            :rules="[rules.required]"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="form.company"
            label="Company"
            variant="outlined"
            density="compact"
            :rules="[rules.required]"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="form.location" label="Location" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="12" md="6">
          <v-switch v-model="form.isCurrent" label="I currently work here" color="primary" hide-details />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="form.startMonth" label="Start Month" type="number" variant="outlined" density="compact" min="1" max="12" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="form.startYear" label="Start Year" type="number" variant="outlined" density="compact" min="1900" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field
            v-model="form.endMonth"
            label="End Month"
            type="number"
            variant="outlined"
            density="compact"
            min="1"
            max="12"
            :disabled="form.isCurrent"
          />
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
      </v-row>
    </FormDialog>

    <ConfirmDialog
      v-model="confirmDelete"
      title="Delete Work Experience"
      variant="error"
      confirm-text="Delete"
      :loading="deleting"
      @confirm="doDelete"
    >
      Are you sure you want to delete this work experience? This action cannot be undone.
    </ConfirmDialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import type { IWorkExperience, IWorkExperienceBullet } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import BulletEditor from './BulletEditor.vue'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'

const props = defineProps<{
  workExperience: IWorkExperience
}>()

const store = useResumeDataStore()
const editDialog = ref(false)
const confirmDelete = ref(false)
const saving = ref(false)
const deleting = ref(false)

const form = reactive({
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

const formValid = computed(() => !!form.jobTitle && !!form.company)

const dateRange = computed(() => {
  const we = props.workExperience
  const start = formatDatePart(we.startMonth, we.startYear)
  const end = we.isCurrent ? 'Present' : formatDatePart(we.endMonth, we.endYear)
  if (!start && !end) return ''
  return `${start || '?'} — ${end || '?'}`
})

watch(editDialog, (open) => {
  if (open) {
    form.jobTitle = props.workExperience.jobTitle || ''
    form.company = props.workExperience.company || ''
    form.location = props.workExperience.location || ''
    form.startMonth = props.workExperience.startMonth ?? undefined
    form.startYear = props.workExperience.startYear ?? undefined
    form.endMonth = props.workExperience.endMonth ?? undefined
    form.endYear = props.workExperience.endYear ?? undefined
    form.isCurrent = props.workExperience.isCurrent ?? false
  }
})

function formatDatePart(month?: number | null, year?: number | null): string {
  if (month && year) return `${month.toString().padStart(2, '0')}/${year}`
  if (year) return String(year)
  return ''
}

async function saveEdit() {
  if (!props.workExperience.id) return
  saving.value = true
  try {
    const dto: IWorkExperience = { ...form }
    await store.updateWorkExperience(props.workExperience.id, dto)
    editDialog.value = false
  } finally {
    saving.value = false
  }
}

async function doDelete() {
  if (!props.workExperience.id) return
  deleting.value = true
  try {
    await store.deleteWorkExperience(props.workExperience.id)
    confirmDelete.value = false
  } finally {
    deleting.value = false
  }
}

async function onUpdateBullets(bullets: IWorkExperienceBullet[]) {
  if (!props.workExperience.id) return
  await store.replaceBullets(props.workExperience.id, bullets)
}
</script>

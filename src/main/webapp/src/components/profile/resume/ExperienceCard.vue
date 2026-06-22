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
        <div class="d-flex align-start mb-1">
          <v-icon icon="mdi-briefcase-outline" size="small" color="primary" class="mr-2 mt-1" />
          <div class="flex-grow-1">
            <div class="text-subtitle-2 font-weight-medium">
              {{ we.jobTitle || 'Untitled'
              }}<template v-if="we.company"> &middot; {{ we.company }}</template>
            </div>
            <div class="text-caption text-medium-emphasis">
              <template v-if="we.location">{{ we.location }} &middot; </template>
              {{ formatDateRange(we) }}
            </div>
          </div>
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
              <v-btn
                v-bind="tp"
                icon="mdi-pencil"
                variant="text"
                size="x-small"
                color="primary"
                class="mr-1"
                @click="openEdit(we)"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn
                v-bind="tp"
                icon="mdi-delete"
                variant="text"
                size="x-small"
                color="error"
                @click="deleteTarget = we; deleteConfirm = true"
              />
            </template>
          </v-tooltip>
        </div>

        <div v-if="we.id" class="ml-8 mt-2">
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
        @cancel="showDialog = false"
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
            <v-text-field
              v-model="form.location"
              label="Location"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-switch
              v-model="form.isCurrent"
              label="I currently work here"
              color="primary"
              hide-details
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="form.startMonth"
              label="Start Month"
              type="number"
              variant="outlined"
              density="compact"
              min="1"
              max="12"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="form.startYear"
              label="Start Year"
              type="number"
              variant="outlined"
              density="compact"
              min="1900"
            />
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
import type { IWorkExperience, IWorkExperienceBullet } from '../../../models/resume-data.model'
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

function resetForm() {
  form.jobTitle = ''
  form.company = ''
  form.location = ''
  form.startMonth = undefined
  form.startYear = undefined
  form.endMonth = undefined
  form.endYear = undefined
  form.isCurrent = false
}

function openAdd() {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

function openEdit(we: IWorkExperience) {
  editingId.value = we.id || null
  form.jobTitle = we.jobTitle || ''
  form.company = we.company || ''
  form.location = we.location || ''
  form.startMonth = we.startMonth ?? undefined
  form.startYear = we.startYear ?? undefined
  form.endMonth = we.endMonth ?? undefined
  form.endYear = we.endYear ?? undefined
  form.isCurrent = we.isCurrent ?? false
  showDialog.value = true
}

function formatDateRange(we: IWorkExperience): string {
  const start = formatDatePart(we.startMonth, we.startYear)
  const end = we.isCurrent ? 'Present' : formatDatePart(we.endMonth, we.endYear)
  if (!start && !end) return ''
  return `${start || '?'} — ${end || '?'}`
}

function formatDatePart(month?: number | null, year?: number | null): string {
  if (month && year) return `${month.toString().padStart(2, '0')}/${year}`
  if (year) return String(year)
  return ''
}

async function saveExperience() {
  saving.value = true
  try {
    const dto: IWorkExperience = { ...form }
    if (editingId.value) {
      await store.updateWorkExperience(editingId.value, dto)
    } else {
      await store.createWorkExperience(dto)
    }
    showDialog.value = false
  } finally {
    saving.value = false
  }
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

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

      <div v-for="vw in store.volunteerWork" :key="vw.id" class="mb-2 pa-2 rounded">
        <div class="d-flex align-center">
          <v-icon icon="mdi-hand-heart-outline" size="small" color="primary" class="mr-2" />
          <div class="flex-grow-1">
            <div class="text-body-2 font-weight-medium">{{ vw.role }}</div>
            <div class="text-caption text-medium-emphasis">
              {{ vw.organization }}
              <template v-if="vw.location"> &middot; {{ vw.location }} </template>
            </div>
          </div>
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editVolunteerWork(vw)" />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteTarget = vw" />
            </template>
          </v-tooltip>
        </div>
        <div v-if="vw.description" class="text-caption text-medium-emphasis mt-1 ml-8">
          {{ vw.description.substring(0, 120) }}{{ vw.description.length > 120 ? '...' : '' }}
        </div>
      </div>
      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Volunteer Work' : 'Add Volunteer Work'"
        icon="mdi-hand-heart"
        :loading="saving"
        :valid="formValid"
        @confirm="saveVolunteerWork"
        @cancel="showDialog = false"
      >
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.role" label="Role" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.organization" label="Organization" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.location" label="Location" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12" md="6">
            <v-switch v-model="form.isCurrent" label="Currently volunteering" color="primary" hide-details />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.startMonth" label="Start Month" type="number" variant="outlined" density="compact" min="1" max="12" />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.startYear" label="Start Year" type="number" variant="outlined" density="compact" min="1900" />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.endMonth" label="End Month" type="number" variant="outlined" density="compact" min="1" max="12" :disabled="form.isCurrent" />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.endYear" label="End Year" type="number" variant="outlined" density="compact" min="1900" :disabled="form.isCurrent" />
          </v-col>
          <v-col cols="12">
            <v-textarea v-model="form.description" label="Description" variant="outlined" density="compact" rows="3" />
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

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.role && !!form.organization)

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

function openAdd() {
  editingId.value = null
  resetForm()
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
  showDialog.value = true
}

async function saveVolunteerWork() {
  saving.value = true
  try {
    const dto: IVolunteerWork = { ...form }
    if (editingId.value) {
      await store.updateVolunteerWork(editingId.value, dto)
    } else {
      await store.createVolunteerWork(dto)
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
    await store.deleteVolunteerWork(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

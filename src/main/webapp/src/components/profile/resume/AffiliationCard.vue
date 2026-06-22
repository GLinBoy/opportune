<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-account-group" color="primary" size="28" class="mr-3" />
      Professional Affiliations
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
      <div v-if="store.affiliations.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-account-group-outline" size="40" class="mb-2" />
        <p class="text-body-2">No professional affiliations added yet.</p>
      </div>

      <div v-for="aff in store.affiliations" :key="aff.id" class="mb-2 pa-2 rounded">
        <div class="d-flex align-center">
          <v-icon icon="mdi-account-group-outline" size="small" color="primary" class="mr-2" />
          <div class="flex-grow-1">
            <div class="text-body-2 font-weight-medium">{{ aff.organization }}</div>
            <div class="text-caption text-medium-emphasis">
              {{ aff.role }}
              <template v-if="aff.startYear || aff.endYear">
                &middot; {{ aff.startYear || '?' }} &mdash; {{ aff.isCurrent ? 'Present' : aff.endYear || '?' }}
              </template>
            </div>
          </div>
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editAffiliation(aff)" />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteTarget = aff" />
            </template>
          </v-tooltip>
        </div>
        <div v-if="aff.description" class="text-caption text-medium-emphasis mt-1 ml-8">
          {{ aff.description.substring(0, 120) }}{{ aff.description.length > 120 ? '...' : '' }}
        </div>
      </div>
      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Affiliation' : 'Add Affiliation'"
        icon="mdi-account-group"
        :loading="saving"
        :valid="formValid"
        @confirm="saveAffiliation"
        @cancel="showDialog = false"
      >
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.organization" label="Organization" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.role" label="Role" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12" md="6">
            <v-switch v-model="form.isCurrent" label="Currently active" color="primary" hide-details />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.startYear" label="Start Year" type="number" variant="outlined" density="compact" min="1900" />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field v-model="form.endYear" label="End Year" type="number" variant="outlined" density="compact" min="1900" :disabled="form.isCurrent" />
          </v-col>
          <v-col cols="12">
            <v-textarea v-model="form.description" label="Description" variant="outlined" density="compact" rows="2" />
          </v-col>
        </v-row>
      </FormDialog>

      <ConfirmDialog
        v-model="deleteConfirm"
        title="Delete Affiliation"
        variant="error"
        confirm-text="Delete"
        :loading="deleting"
        @confirm="doDelete"
      >
        Are you sure you want to delete "{{ deleteTarget?.organization }}"?
      </ConfirmDialog>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IProfessionalAffiliation } from '../../../models/resume-data.model'
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
const deleteTarget = ref<IProfessionalAffiliation | null>(null)

const form = reactive({
  organization: '',
  role: '',
  startYear: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  description: '',
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.organization)

function resetForm() {
  form.organization = ''
  form.role = ''
  form.startYear = undefined
  form.endYear = undefined
  form.isCurrent = false
  form.description = ''
}

function openAdd() {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

function editAffiliation(aff: IProfessionalAffiliation) {
  editingId.value = aff.id || null
  form.organization = aff.organization || ''
  form.role = aff.role || ''
  form.startYear = aff.startYear ?? undefined
  form.endYear = aff.endYear ?? undefined
  form.isCurrent = aff.isCurrent ?? false
  form.description = aff.description || ''
  showDialog.value = true
}

async function saveAffiliation() {
  saving.value = true
  try {
    const dto: IProfessionalAffiliation = { ...form }
    if (editingId.value) {
      await store.updateAffiliation(editingId.value, dto)
    } else {
      await store.createAffiliation(dto)
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
    await store.deleteAffiliation(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

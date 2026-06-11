<template>
  <v-card elevation="0" border rounded="lg" class="mb-4">
    <div class="d-flex align-center pa-4 pb-0">
      <v-icon icon="mdi-certificate" color="primary" size="28" class="mr-3" />
      <div class="flex-grow-1">
        <div class="text-body-1 font-weight-medium">Certifications</div>
      </div>
      <v-btn icon="mdi-plus" variant="text" color="primary" size="small" @click="openAdd" />
    </div>

    <v-card-text>
      <div v-if="store.certifications.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-certificate-outline" size="40" class="mb-2" />
        <p class="text-body-2">No certifications added yet.</p>
      </div>

      <div v-for="cert in store.certifications" :key="cert.id" class="d-flex align-center mb-2 pa-2 rounded">
        <v-icon icon="mdi-certificate-outline" size="small" color="primary" class="mr-2" />
        <div class="flex-grow-1">
          <div class="text-body-2 font-weight-medium">{{ cert.name }}</div>
          <div class="text-caption text-medium-emphasis">
            {{ cert.issuingOrganization }}
          </div>
        </div>
        <v-tooltip text="Edit" location="top">
          <template #activator="{ props: tp }">
            <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editCertification(cert)" />
          </template>
        </v-tooltip>
        <v-tooltip text="Delete" location="top">
          <template #activator="{ props: tp }">
            <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteTarget = cert" />
          </template>
        </v-tooltip>
      </div>
    </v-card-text>

    <FormDialog
      v-model="showDialog"
      :title="editingId ? 'Edit Certification' : 'Add Certification'"
      icon="mdi-certificate"
      :loading="saving"
      :valid="formValid"
      @confirm="saveCertification"
      @cancel="showDialog = false"
    >
      <v-row>
        <v-col cols="12">
          <v-text-field v-model="form.name" label="Certification Name" variant="outlined" density="compact" :rules="[rules.required]" />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="form.issuingOrganization" label="Issuing Organization" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="form.issueDate" label="Issue Date" type="date" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="form.expirationDate" label="Expiration Date" type="date" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="form.credentialId" label="Credential ID" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="form.credentialUrl" label="Credential URL" variant="outlined" density="compact" />
        </v-col>
      </v-row>
    </FormDialog>

    <ConfirmDialog
      v-model="deleteConfirm"
      title="Delete Certification"
      variant="error"
      confirm-text="Delete"
      :loading="deleting"
      @confirm="doDelete"
    >
      Are you sure you want to delete "{{ deleteTarget?.name }}"?
    </ConfirmDialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IResumeCertification } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'

const store = useResumeDataStore()

const showDialog = ref(false)
const saving = ref(false)
const editingId = ref<string | null>(null)
const deleteConfirm = ref(false)
const deleting = ref(false)
const deleteTarget = ref<IResumeCertification | null>(null)

const form = reactive({
  name: '',
  issuingOrganization: '',
  issueDate: '',
  expirationDate: '',
  credentialId: '',
  credentialUrl: '',
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.name)

function resetForm() {
  form.name = ''
  form.issuingOrganization = ''
  form.issueDate = ''
  form.expirationDate = ''
  form.credentialId = ''
  form.credentialUrl = ''
}

function openAdd() {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

function editCertification(cert: IResumeCertification) {
  editingId.value = cert.id || null
  form.name = cert.name || ''
  form.issuingOrganization = cert.issuingOrganization || ''
  form.issueDate = cert.issueDate || ''
  form.expirationDate = cert.expirationDate || ''
  form.credentialId = cert.credentialId || ''
  form.credentialUrl = cert.credentialUrl || ''
  showDialog.value = true
}

async function saveCertification() {
  saving.value = true
  try {
    const dto: IResumeCertification = { ...form }
    if (editingId.value) {
      await store.updateCertification(editingId.value, dto)
    } else {
      await store.createCertification(dto)
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
    await store.deleteCertification(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

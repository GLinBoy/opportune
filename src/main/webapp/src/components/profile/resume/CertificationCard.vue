<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-certificate" color="primary" size="28" class="mr-3" />
      Certifications
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
      <div v-if="store.certifications.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-certificate-outline" size="40" class="mb-2" />
        <p class="text-body-2">No certifications added yet.</p>
      </div>

      <div v-for="cert in store.certifications" :key="cert.id" class="mb-3 pa-2 rounded">
        <div class="d-flex align-start">
          <v-icon icon="mdi-certificate-outline" size="small" color="primary" class="mr-2 mt-1" />
          <div class="flex-grow-1">
            <div class="text-subtitle-1 font-weight-medium">{{ cert.name }}</div>
            <div class="text-body-2 d-flex align-center">
              <span>{{ cert.issuingOrganization }}</span>
              <a
                v-if="cert.credentialUrl"
                :href="cert.credentialUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="ml-2 text-caption text-primary text-decoration-none d-flex align-center"
              >
                ( Show Credential
                <v-icon icon="mdi-open-in-new" size="14" class="mr-1" /> )
              </a>
            </div>
            <div class="text-caption text-medium-emphasis">
              {{
                formatCertDate(
                  cert.issueMonth,
                  cert.issueYear,
                  cert.expirationMonth,
                  cert.expirationYear
                )
              }}
            </div>
            <div v-if="cert.credentialId" class="text-caption text-medium-emphasis">
              Credential ID: {{ cert.credentialId }}
            </div>
          </div>
          <div class="d-flex flex-shrink-0 ml-2">
            <v-tooltip text="Edit" location="top">
              <template #activator="{ props: tp }">
                <v-btn
                  v-bind="tp"
                  icon="mdi-pencil"
                  variant="text"
                  size="x-small"
                  color="primary"
                  class="mr-1"
                  @click="editCertification(cert)"
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
                  @click="confirmDelete(cert)"
                />
              </template>
            </v-tooltip>
          </div>
        </div>
      </div>

      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Certification' : 'Add Certification'"
        icon="mdi-certificate"
        :loading="saving"
        :valid="formValid"
        @confirm="saveCertification"
        @cancel="revertForm"
      >
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="form.name"
              label="Certification Name"
              variant="outlined"
              density="compact"
              :rules="[rules.required]"
            />
          </v-col>
          <v-col cols="12">
            <v-text-field
              v-model="form.issuingOrganization"
              label="Issuing Organization"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6">
            <v-select
              v-model="form.issueMonth"
              label="Issue Month"
              :items="monthOptions"
              variant="outlined"
              density="compact"
              clearable
            />
          </v-col>
          <v-col cols="6">
            <v-text-field
              v-model="form.issueYear"
              label="Issue Year"
              type="number"
              variant="outlined"
              density="compact"
              min="1900"
            />
          </v-col>
          <v-col cols="6">
            <v-select
              v-model="form.expirationMonth"
              label="Expiration Month"
              :items="monthOptions"
              variant="outlined"
              density="compact"
              clearable
            />
          </v-col>
          <v-col cols="6">
            <v-text-field
              v-model="form.expirationYear"
              label="Expiration Year"
              type="number"
              variant="outlined"
              density="compact"
              min="1900"
            />
          </v-col>
          <v-col cols="12">
            <v-text-field
              v-model="form.credentialId"
              label="Credential ID"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="12">
            <v-text-field
              v-model="form.credentialUrl"
              label="Credential URL"
              variant="outlined"
              density="compact"
            />
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
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IResumeCertification } from '../../../models/resume-data.model'
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
const deleteTarget = ref<IResumeCertification | null>(null)

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
  name: '',
  issuingOrganization: '',
  issueMonth: undefined as number | undefined,
  issueYear: undefined as number | undefined,
  expirationMonth: undefined as number | undefined,
  expirationYear: undefined as number | undefined,
  credentialId: '',
  credentialUrl: '',
})

const formSnapshot = reactive({
  name: '',
  issuingOrganization: '',
  issueMonth: undefined as number | undefined,
  issueYear: undefined as number | undefined,
  expirationMonth: undefined as number | undefined,
  expirationYear: undefined as number | undefined,
  credentialId: '',
  credentialUrl: '',
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.name)

const dirty = computed(() => {
  if (!editingId.value) return true
  return (
    form.name !== formSnapshot.name ||
    form.issuingOrganization !== formSnapshot.issuingOrganization ||
    form.issueMonth !== formSnapshot.issueMonth ||
    form.issueYear !== formSnapshot.issueYear ||
    form.expirationMonth !== formSnapshot.expirationMonth ||
    form.expirationYear !== formSnapshot.expirationYear ||
    form.credentialId !== formSnapshot.credentialId ||
    form.credentialUrl !== formSnapshot.credentialUrl
  )
})

function formatDatePart(month?: number | null, year?: number | null): string {
  if (month && year) {
    const names = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ]
    return `${names[month - 1]} ${year}`
  }
  if (year) return String(year)
  return ''
}

function formatCertDate(
  issueMonth?: number | null,
  issueYear?: number | null,
  expirationMonth?: number | null,
  expirationYear?: number | null
): string {
  const issued = formatDatePart(issueMonth, issueYear)
  const parts: string[] = []
  if (issued) parts.push(`Issued ${issued}`)
  if (expirationYear) {
    parts.push(`Expires ${formatDatePart(expirationMonth, expirationYear)}`)
  }
  return parts.join(' \u2014 ') || ''
}

function resetForm() {
  form.name = ''
  form.issuingOrganization = ''
  form.issueMonth = undefined
  form.issueYear = undefined
  form.expirationMonth = undefined
  form.expirationYear = undefined
  form.credentialId = ''
  form.credentialUrl = ''
}

function takeSnapshot() {
  formSnapshot.name = form.name
  formSnapshot.issuingOrganization = form.issuingOrganization
  formSnapshot.issueMonth = form.issueMonth
  formSnapshot.issueYear = form.issueYear
  formSnapshot.expirationMonth = form.expirationMonth
  formSnapshot.expirationYear = form.expirationYear
  formSnapshot.credentialId = form.credentialId
  formSnapshot.credentialUrl = form.credentialUrl
}

function openAdd() {
  editingId.value = null
  resetForm()
  takeSnapshot()
  showDialog.value = true
}

function editCertification(cert: IResumeCertification) {
  editingId.value = cert.id || null
  form.name = cert.name || ''
  form.issuingOrganization = cert.issuingOrganization || ''
  form.issueMonth = cert.issueMonth ?? undefined
  form.issueYear = cert.issueYear ?? undefined
  form.expirationMonth = cert.expirationMonth ?? undefined
  form.expirationYear = cert.expirationYear ?? undefined
  form.credentialId = cert.credentialId || ''
  form.credentialUrl = cert.credentialUrl || ''
  takeSnapshot()
  showDialog.value = true
}

function revertForm() {
  form.name = formSnapshot.name
  form.issuingOrganization = formSnapshot.issuingOrganization
  form.issueMonth = formSnapshot.issueMonth
  form.issueYear = formSnapshot.issueYear
  form.expirationMonth = formSnapshot.expirationMonth
  form.expirationYear = formSnapshot.expirationYear
  form.credentialId = formSnapshot.credentialId
  form.credentialUrl = formSnapshot.credentialUrl
  if (!editingId.value) {
    showDialog.value = false
  }
}

async function saveCertification() {
  saving.value = true
  try {
    const dto: IResumeCertification = {
      name: form.name,
      issuingOrganization: form.issuingOrganization || undefined,
      issueMonth: form.issueMonth,
      issueYear: form.issueYear,
      expirationMonth: form.expirationMonth,
      expirationYear: form.expirationYear,
      credentialId: form.credentialId || undefined,
      credentialUrl: form.credentialUrl || undefined,
    }
    if (editingId.value) {
      await store.updateCertification(editingId.value, dto)
    } else {
      await store.createCertification(dto)
    }
    takeSnapshot()
    showDialog.value = false
  } finally {
    saving.value = false
  }
}

function confirmDelete(cert: IResumeCertification) {
  deleteTarget.value = cert
  deleteConfirm.value = true
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

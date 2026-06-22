<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-book-open-page-variant" color="primary" size="28" class="mr-3" />
      Publications
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
      <div v-if="store.publications.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-book-open-page-variant-outline" size="40" class="mb-2" />
        <p class="text-body-2">No publications added yet.</p>
      </div>

      <div v-for="pub in store.publications" :key="pub.id" class="mb-2 pa-2 rounded">
        <div class="d-flex align-center">
          <v-icon icon="mdi-book-open-page-variant-outline" size="small" color="primary" class="mr-2" />
          <div class="flex-grow-1">
            <div class="text-body-2 font-weight-medium">{{ pub.title }}</div>
            <div class="text-caption text-medium-emphasis">
              {{ pub.publisher }}
              <template v-if="pub.publicationDate"> &middot; {{ pub.publicationDate }} </template>
            </div>
          </div>
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editPublication(pub)" />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteTarget = pub" />
            </template>
          </v-tooltip>
        </div>
      </div>
      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Publication' : 'Add Publication'"
        icon="mdi-book-open-page-variant"
        :loading="saving"
        :valid="formValid"
        @confirm="savePublication"
        @cancel="showDialog = false"
      >
        <v-row>
          <v-col cols="12">
            <v-text-field v-model="form.title" label="Title" variant="outlined" density="compact" :rules="[rules.required]" />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.publisher" label="Publisher" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field v-model="form.publicationDate" label="Publication Date" type="date" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="form.url" label="URL" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12">
            <v-textarea v-model="form.description" label="Description" variant="outlined" density="compact" rows="2" />
          </v-col>
        </v-row>
      </FormDialog>

      <ConfirmDialog
        v-model="deleteConfirm"
        title="Delete Publication"
        variant="error"
        confirm-text="Delete"
        :loading="deleting"
        @confirm="doDelete"
      >
        Are you sure you want to delete "{{ deleteTarget?.title }}"?
      </ConfirmDialog>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IResumePublication } from '../../../models/resume-data.model'
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
const deleteTarget = ref<IResumePublication | null>(null)

const form = reactive({
  title: '',
  publisher: '',
  publicationDate: '',
  url: '',
  description: '',
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.title)

function resetForm() {
  form.title = ''
  form.publisher = ''
  form.publicationDate = ''
  form.url = ''
  form.description = ''
}

function openAdd() {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

function editPublication(pub: IResumePublication) {
  editingId.value = pub.id || null
  form.title = pub.title || ''
  form.publisher = pub.publisher || ''
  form.publicationDate = pub.publicationDate || ''
  form.url = pub.url || ''
  form.description = pub.description || ''
  showDialog.value = true
}

async function savePublication() {
  saving.value = true
  try {
    const dto: IResumePublication = { ...form }
    if (editingId.value) {
      await store.updatePublication(editingId.value, dto)
    } else {
      await store.createPublication(dto)
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
    await store.deletePublication(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

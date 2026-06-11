<template>
  <v-card elevation="0" border rounded="lg" class="mb-4">
    <div class="d-flex align-center pa-4 pb-0">
      <v-icon icon="mdi-trophy" color="primary" size="28" class="mr-3" />
      <div class="flex-grow-1">
        <div class="text-body-1 font-weight-medium">Awards</div>
      </div>
      <v-btn icon="mdi-plus" variant="text" color="primary" size="small" @click="openAdd" />
    </div>

    <v-card-text>
      <div v-if="store.awards.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-trophy-outline" size="40" class="mb-2" />
        <p class="text-body-2">No awards added yet.</p>
      </div>

      <div v-for="award in store.awards" :key="award.id" class="mb-2 pa-2 rounded">
        <div class="d-flex align-center">
          <v-icon icon="mdi-trophy-outline" size="small" color="primary" class="mr-2" />
          <div class="flex-grow-1">
            <div class="text-body-2 font-weight-medium">{{ award.title }}</div>
            <div class="text-caption text-medium-emphasis">
              {{ award.issuer }}
              <template v-if="award.awardDate"> &middot; {{ award.awardDate }} </template>
            </div>
          </div>
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editAward(award)" />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteTarget = award" />
            </template>
          </v-tooltip>
        </div>
        <div v-if="award.description" class="text-caption text-medium-emphasis mt-1 ml-8">
          {{ award.description.substring(0, 120) }}{{ award.description.length > 120 ? '...' : '' }}
        </div>
      </div>
    </v-card-text>

    <FormDialog
      v-model="showDialog"
      :title="editingId ? 'Edit Award' : 'Add Award'"
      icon="mdi-trophy"
      :loading="saving"
      :valid="formValid"
      @confirm="saveAward"
      @cancel="showDialog = false"
    >
      <v-row>
        <v-col cols="12">
          <v-text-field v-model="form.title" label="Award Title" variant="outlined" density="compact" :rules="[rules.required]" />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="form.issuer" label="Issuer" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="form.awardDate" label="Award Date" type="date" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="12">
          <v-textarea v-model="form.description" label="Description" variant="outlined" density="compact" rows="2" />
        </v-col>
      </v-row>
    </FormDialog>

    <ConfirmDialog
      v-model="deleteConfirm"
      title="Delete Award"
      variant="error"
      confirm-text="Delete"
      :loading="deleting"
      @confirm="doDelete"
    >
      Are you sure you want to delete "{{ deleteTarget?.title }}"?
    </ConfirmDialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IResumeAward } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'

const store = useResumeDataStore()

const showDialog = ref(false)
const saving = ref(false)
const editingId = ref<string | null>(null)
const deleteConfirm = ref(false)
const deleting = ref(false)
const deleteTarget = ref<IResumeAward | null>(null)

const form = reactive({
  title: '',
  issuer: '',
  awardDate: '',
  description: '',
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.title)

function resetForm() {
  form.title = ''
  form.issuer = ''
  form.awardDate = ''
  form.description = ''
}

function openAdd() {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

function editAward(award: IResumeAward) {
  editingId.value = award.id || null
  form.title = award.title || ''
  form.issuer = award.issuer || ''
  form.awardDate = award.awardDate || ''
  form.description = award.description || ''
  showDialog.value = true
}

async function saveAward() {
  saving.value = true
  try {
    const dto: IResumeAward = { ...form }
    if (editingId.value) {
      await store.updateAward(editingId.value, dto)
    } else {
      await store.createAward(dto)
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
    await store.deleteAward(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

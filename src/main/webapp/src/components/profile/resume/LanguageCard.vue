<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-translate" color="primary" size="28" class="mr-3" />
      Languages
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
      <div v-if="store.languages.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-translate" size="40" class="mb-2" />
        <p class="text-body-2">No languages added yet.</p>
      </div>

      <div class="d-flex flex-wrap ga-2">
        <v-chip
          v-for="lang in store.languages"
          :key="lang.id"
          color="primary"
          variant="outlined"
          closable
          @click:close="doDeleteChip(lang)"
          @click="editLanguage(lang)"
        >
          {{ lang.language }} &middot; {{ formatProficiency(lang.proficiency) }}
        </v-chip>
      </div>
      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Language' : 'Add Language'"
        icon="mdi-translate"
        :loading="saving"
        :valid="formValid"
        @confirm="saveLanguage"
        @cancel="showDialog = false"
      >
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="form.language"
              label="Language"
              variant="outlined"
              density="compact"
              :rules="[rules.required]"
              placeholder="e.g., English, Spanish"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12">
            <v-select
              v-model="form.proficiency"
              label="Proficiency"
              variant="outlined"
              density="compact"
              :items="proficiencyItems"
              :rules="[rules.required]"
            />
          </v-col>
        </v-row>
      </FormDialog>

      <ConfirmDialog
        v-model="deleteConfirm"
        title="Delete Language"
        variant="error"
        confirm-text="Delete"
        :loading="deleting"
        @confirm="doDelete"
      >
        Are you sure you want to delete "{{ deleteTarget?.language }}"?
      </ConfirmDialog>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IResumeLanguage, LanguageProficiency } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormCard from '../../forms/FormCard.vue'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'

const store = useResumeDataStore()

const cardOpen = ref(true)

const proficiencyItems = [
  { title: 'Elementary proficiency', value: 'ELEMENTARY' },
  { title: 'Limited working proficiency', value: 'LIMITED_WORKING' },
  { title: 'Professional working proficiency', value: 'PROFESSIONAL_WORKING' },
  { title: 'Full professional proficiency', value: 'FULL_PROFESSIONAL' },
  { title: 'Native or bilingual proficiency', value: 'NATIVE_OR_BILINGUAL' },
]

const showDialog = ref(false)
const saving = ref(false)
const editingId = ref<string | null>(null)
const deleteConfirm = ref(false)
const deleting = ref(false)
const deleteTarget = ref<IResumeLanguage | null>(null)

const form = reactive({
  language: '',
  proficiency: '' as LanguageProficiency | '',
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.language && !!form.proficiency)

const proficiencyDisplay: Record<LanguageProficiency, string> = {
  ELEMENTARY: 'Elementary proficiency',
  LIMITED_WORKING: 'Limited working proficiency',
  PROFESSIONAL_WORKING: 'Professional working proficiency',
  FULL_PROFESSIONAL: 'Full professional proficiency',
  NATIVE_OR_BILINGUAL: 'Native or bilingual proficiency',
}

function formatProficiency(p?: LanguageProficiency): string {
  if (!p) return ''
  return proficiencyDisplay[p]
}

function openAdd() {
  editingId.value = null
  form.language = ''
  form.proficiency = ''
  showDialog.value = true
}

function editLanguage(lang: IResumeLanguage) {
  editingId.value = lang.id || null
  form.language = lang.language || ''
  form.proficiency = lang.proficiency || ''
  showDialog.value = true
}

async function saveLanguage() {
  saving.value = true
  try {
    const dto: IResumeLanguage = { language: form.language, proficiency: form.proficiency as LanguageProficiency }
    if (editingId.value) {
      await store.updateLanguage(editingId.value, dto)
    } else {
      await store.createLanguage(dto)
    }
    showDialog.value = false
  } finally {
    saving.value = false
  }
}

function doDeleteChip(lang: IResumeLanguage) {
  deleteTarget.value = lang
  deleteConfirm.value = true
}

async function doDelete() {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await store.deleteLanguage(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

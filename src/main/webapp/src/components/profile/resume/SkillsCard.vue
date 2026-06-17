<template>
  <FormCard collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-lightning-bolt" color="primary" size="28" class="mr-3" />
      Skills
    </template>

    <template #default>
      <div class="d-flex justify-end mb-2">
        <v-btn icon="mdi-plus" variant="text" color="primary" size="small" @click="showAddForm = true" />
      </div>
      <div v-if="skillGroups.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-lightning-bolt-outline" size="40" class="mb-2" />
        <p class="text-body-2">No skills added yet. Extract from your resume or add manually.</p>
      </div>

      <div v-for="sg in skillGroups" :key="sg.id" class="mb-3">
        <div class="d-flex align-center mb-1">
          <span class="text-subtitle-2 font-weight-medium">{{ sg.category }}</span>
          <v-spacer />
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editSkillGroup(sg)" />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteGroup(sg)" />
            </template>
          </v-tooltip>
        </div>
        <div class="d-flex flex-wrap ga-1">
          <v-chip
            v-for="(skill, i) in sg.skills"
            :key="i"
            size="small"
            color="primary"
            variant="outlined"
          >
            {{ skill }}
          </v-chip>
        </div>
      </div>
      <FormDialog
        v-model="showFormDialog"
        :title="editingId ? 'Edit Skill Group' : 'Add Skill Group'"
        icon="mdi-lightning-bolt"
        :loading="saving"
        :valid="formValid"
        @confirm="saveGroup"
        @cancel="showFormDialog = false"
      >
        <v-text-field
          v-model="form.category"
          label="Category"
          variant="outlined"
          density="compact"
          :rules="[rules.required]"
          placeholder="e.g., Programming Languages"
          class="mb-3"
        />
        <v-combobox
          v-model="form.skills"
          label="Skills"
          variant="outlined"
          density="compact"
          multiple
          chips
          deletable-chips
          small-chips
          append-icon="mdi-plus"
          hint="Type a skill and press Enter"
          persistent-hint
        />
      </FormDialog>
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { ISkillGroup } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormCard from '../../forms/FormCard.vue'
import FormDialog from '../../FormDialog.vue'

const store = useResumeDataStore()
const skillGroups = computed(() => store.skillGroups)

const showFormDialog = ref(false)
const saving = ref(false)
const editingId = ref<string | null>(null)
const showAddForm = computed({
  get: () => showFormDialog.value,
  set: (v) => {
    if (v) {
      editingId.value = null
      form.category = ''
      form.skills = []
    }
    showFormDialog.value = v
  },
})

const form = reactive({
  category: '',
  skills: [] as string[],
})

const rules = {
  required: (v: string) => !!v || 'This field is required',
}

const formValid = computed(() => !!form.category)

function editSkillGroup(sg: ISkillGroup) {
  editingId.value = sg.id || null
  form.category = sg.category || ''
  form.skills = sg.skills ? [...sg.skills] : []
  showFormDialog.value = true
}

async function saveGroup() {
  saving.value = true
  try {
    const dto: ISkillGroup = {
      category: form.category,
      skills: form.skills,
    }
    if (editingId.value) {
      await store.updateSkillGroup(editingId.value, dto)
    } else {
      await store.createSkillGroup(dto)
    }
    showFormDialog.value = false
  } finally {
    saving.value = false
  }
}

async function deleteGroup(sg: ISkillGroup) {
  if (sg.id) {
    await store.deleteSkillGroup(sg.id)
  }
}
</script>

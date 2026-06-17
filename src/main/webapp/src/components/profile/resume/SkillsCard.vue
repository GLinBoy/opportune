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

      <div v-for="(sg, index) in skillGroups" :key="sg.id" class="mb-3">
        <div class="d-flex align-center mb-1">
          <span class="text-subtitle-2 font-weight-medium">{{ sg.category }}</span>
          <v-spacer />
          <v-menu v-model="addSkillMenu[sg.id || index]" :close-on-content-click="false">
            <template #activator="{ props }">
              <v-btn v-bind="props" icon="mdi-plus" variant="text" size="x-small" color="primary" class="mr-1" />
            </template>
            <v-card min-width="250" class="pa-2">
              <v-text-field
                v-model="newSkillText"
                label="New skill"
                variant="outlined"
                density="compact"
                hide-details
                @keyup.enter="confirmAddSkill(sg)"
              />
              <v-btn color="primary" size="small" class="mt-2" @click="confirmAddSkill(sg)">Add</v-btn>
            </v-card>
          </v-menu>
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
            closable
            @click:close="deleteSkill(sg, i)"
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

const addSkillMenu = reactive<Record<string, boolean>>({})
const newSkillText = ref('')

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

async function deleteSkill(sg: ISkillGroup, skillIndex: number) {
  sg.skills?.splice(skillIndex, 1)
  if (sg.id) {
    await store.updateSkillGroup(sg.id, { category: sg.category, skills: sg.skills })
  }
}

function confirmAddSkill(sg: ISkillGroup) {
  const skill = newSkillText.value.trim()
  if (!skill) return
  if (!sg.skills) sg.skills = []
  sg.skills.push(skill)
  newSkillText.value = ''
  if (sg.id) {
    store.updateSkillGroup(sg.id, { category: sg.category, skills: sg.skills })
  }
}
</script>

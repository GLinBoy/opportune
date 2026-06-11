<template>
  <v-card elevation="0" border rounded="lg" class="mb-4">
    <div class="d-flex align-center pa-4 pb-0">
      <v-icon icon="mdi-code-braces" color="primary" size="28" class="mr-3" />
      <div class="flex-grow-1">
        <div class="text-body-1 font-weight-medium">Projects</div>
      </div>
      <v-btn icon="mdi-plus" variant="text" color="primary" size="small" @click="openAdd" />
    </div>

    <v-card-text>
      <div v-if="store.projects.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-code-braces" size="40" class="mb-2" />
        <p class="text-body-2">No projects added yet.</p>
      </div>

      <div v-for="project in store.projects" :key="project.id" class="mb-3">
        <div class="d-flex align-center mb-1">
          <div class="flex-grow-1">
            <span class="text-subtitle-2 font-weight-medium">{{ project.name }}</span>
            <span v-if="project.description" class="text-caption text-medium-emphasis ml-2">
              &mdash; {{ project.description.substring(0, 80) }}{{ project.description.length > 80 ? '...' : '' }}
            </span>
          </div>
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-pencil" variant="text" size="x-small" color="primary" class="mr-1" @click="editProject(project)" />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props: tp }">
              <v-btn v-bind="tp" icon="mdi-delete" variant="text" size="x-small" color="error" @click="deleteTarget = project" />
            </template>
          </v-tooltip>
        </div>
        <div v-if="project.techStack && project.techStack.length" class="d-flex flex-wrap ga-1">
          <v-chip v-for="(tech, i) in project.techStack" :key="i" size="small" color="secondary" variant="outlined">
            {{ tech }}
          </v-chip>
        </div>
        <div v-if="project.url" class="text-caption text-medium-emphasis mt-1">
          <v-icon icon="mdi-link" size="x-small" class="mr-1" />{{ project.url }}
        </div>
      </div>
    </v-card-text>

    <FormDialog
      v-model="showDialog"
      :title="editingId ? 'Edit Project' : 'Add Project'"
      icon="mdi-code-braces"
      :loading="saving"
      :valid="formValid"
      @confirm="saveProject"
      @cancel="showDialog = false"
    >
      <v-row>
        <v-col cols="12">
          <v-text-field v-model="form.name" label="Project Name" variant="outlined" density="compact" :rules="[rules.required]" />
        </v-col>
        <v-col cols="12">
          <v-textarea v-model="form.description" label="Description" variant="outlined" density="compact" rows="2" />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field v-model="form.url" label="URL" variant="outlined" density="compact" />
        </v-col>
        <v-col cols="12" md="6">
          <v-switch v-model="form.isCurrent" label="Currently working on" color="primary" hide-details />
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
          <v-combobox
            v-model="form.techStack"
            label="Technologies"
            variant="outlined"
            density="compact"
            multiple
            chips
            deletable-chips
            small-chips
            append-icon="mdi-plus"
            hint="Type a technology and press Enter"
            persistent-hint
          />
        </v-col>
      </v-row>
    </FormDialog>

    <ConfirmDialog
      v-model="deleteConfirm"
      title="Delete Project"
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
import type { IResumeProject } from '../../../models/resume-data.model'
import { useResumeDataStore } from '../../../stores/resume-data.store'
import FormDialog from '../../FormDialog.vue'
import ConfirmDialog from '../../ConfirmDialog.vue'

const store = useResumeDataStore()

const showDialog = ref(false)
const saving = ref(false)
const editingId = ref<string | null>(null)
const deleteConfirm = ref(false)
const deleting = ref(false)
const deleteTarget = ref<IResumeProject | null>(null)

const form = reactive({
  name: '',
  description: '',
  url: '',
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  techStack: [] as string[],
})

const rules = { required: (v: string) => !!v || 'This field is required' }
const formValid = computed(() => !!form.name)

function resetForm() {
  form.name = ''
  form.description = ''
  form.url = ''
  form.startMonth = undefined
  form.startYear = undefined
  form.endMonth = undefined
  form.endYear = undefined
  form.isCurrent = false
  form.techStack = []
}

function openAdd() {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

function editProject(project: IResumeProject) {
  editingId.value = project.id || null
  form.name = project.name || ''
  form.description = project.description || ''
  form.url = project.url || ''
  form.startMonth = project.startMonth ?? undefined
  form.startYear = project.startYear ?? undefined
  form.endMonth = project.endMonth ?? undefined
  form.endYear = project.endYear ?? undefined
  form.isCurrent = project.isCurrent ?? false
  form.techStack = project.techStack ? [...project.techStack] : []
  showDialog.value = true
}

async function saveProject() {
  saving.value = true
  try {
    const dto: IResumeProject = { ...form }
    if (editingId.value) {
      await store.updateProject(editingId.value, dto)
    } else {
      await store.createProject(dto)
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
    await store.deleteProject(deleteTarget.value.id)
    deleteConfirm.value = false
    deleteTarget.value = null
  } finally {
    deleting.value = false
  }
}
</script>

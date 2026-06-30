<template>
  <FormCard v-model="cardOpen" collapsible default-open class="mb-4">
    <template #title>
      <v-icon icon="mdi-code-braces" color="primary" size="28" class="mr-3" />
      Projects
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
      <div v-if="store.projects.length === 0" class="text-center py-4 text-medium-emphasis">
        <v-icon icon="mdi-code-braces" size="40" class="mb-2" />
        <p class="text-body-2">No projects added yet.</p>
      </div>

      <div v-for="project in store.projects" :key="project.id" class="mb-3 pa-2 rounded">
        <div class="d-flex align-start">
          <v-icon icon="mdi-code-braces" size="small" color="primary" class="mr-2 mt-1" />
          <div class="flex-grow-1">
            <div class="text-subtitle-1 font-weight-medium">
              <a
                v-if="project.url"
                :href="project.url"
                target="_blank"
                rel="noopener noreferrer"
                class="text-decoration-none"
                >{{ project.name }}</a
              >
              <template v-else>{{ project.name }}</template>
            </div>
            <div class="text-caption text-medium-emphasis">
              {{
                formatDate(
                  project.startMonth,
                  project.startYear,
                  project.endMonth,
                  project.endYear,
                  project.isCurrent
                )
              }}
            </div>
            <div v-if="project.description" class="text-body-2 mt-1">
              {{ project.description }}
            </div>
            <div v-if="project.techStack?.length" class="text-caption text-medium-emphasis mt-1">
              <span class="font-weight-medium">Technologies:</span>
              {{ project.techStack.join(', ') }}
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
                  @click="editProject(project)"
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
                  @click="confirmDelete(project)"
                />
              </template>
            </v-tooltip>
          </div>
        </div>
      </div>

      <FormDialog
        v-model="showDialog"
        :title="editingId ? 'Edit Project' : 'Add Project'"
        icon="mdi-code-braces"
        :loading="saving"
        :valid="formValid"
        @confirm="saveProject"
        @cancel="revertForm"
      >
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="form.name"
              label="Project Name"
              variant="outlined"
              density="compact"
              :rules="[rules.required]"
            />
          </v-col>
          <v-col cols="12">
            <v-textarea
              v-model="form.description"
              label="Description"
              variant="outlined"
              density="compact"
              rows="5"
            />
          </v-col>
          <v-col cols="12">
            <v-text-field
              v-model="form.url"
              label="Project URL"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6">
            <v-select
              v-model="form.startMonth"
              label="Start Month"
              :items="monthOptions"
              variant="outlined"
              density="compact"
              clearable
            />
          </v-col>
          <v-col cols="6">
            <v-text-field
              v-model="form.startYear"
              label="Start Year"
              type="number"
              variant="outlined"
              density="compact"
              min="1900"
            />
          </v-col>
          <v-col cols="6">
            <v-select
              v-model="form.endMonth"
              label="End Month"
              :items="monthOptions"
              variant="outlined"
              density="compact"
              clearable
              :disabled="form.isCurrent"
            />
          </v-col>
          <v-col cols="6">
            <v-text-field
              v-model="form.endYear"
              label="End Year"
              type="number"
              variant="outlined"
              density="compact"
              min="1900"
              :disabled="form.isCurrent"
            />
          </v-col>
          <v-col cols="12">
            <v-switch
              v-model="form.isCurrent"
              label="Currently working on"
              color="primary"
              hide-details
            />
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
    </template>
  </FormCard>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { IResumeProject } from '../../../models/resume-data.model'
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
const deleteTarget = ref<IResumeProject | null>(null)

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
  description: '',
  url: '',
  startMonth: undefined as number | undefined,
  startYear: undefined as number | undefined,
  endMonth: undefined as number | undefined,
  endYear: undefined as number | undefined,
  isCurrent: false,
  techStack: [] as string[],
})

const formSnapshot = reactive({
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

const rules = {
  required: (v: string) => !!v || 'This field is required',
}

const formValid = computed(() => !!form.name)

const dirty = computed(() => {
  if (!editingId.value) return true
  return (
    form.name !== formSnapshot.name ||
    form.description !== formSnapshot.description ||
    form.url !== formSnapshot.url ||
    form.startMonth !== formSnapshot.startMonth ||
    form.startYear !== formSnapshot.startYear ||
    form.endMonth !== formSnapshot.endMonth ||
    form.endYear !== formSnapshot.endYear ||
    form.isCurrent !== formSnapshot.isCurrent ||
    JSON.stringify(form.techStack) !== JSON.stringify(formSnapshot.techStack)
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

function formatDate(
  startMonth?: number | null,
  startYear?: number | null,
  endMonth?: number | null,
  endYear?: number | null,
  isCurrent?: boolean | null
): string {
  const start = formatDatePart(startMonth, startYear)
  const end = isCurrent ? 'Present' : formatDatePart(endMonth, endYear)
  if (!start && !end) return ''
  return `${start || '?'} \u2014 ${end || '?'}`
}

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

function takeSnapshot() {
  formSnapshot.name = form.name
  formSnapshot.description = form.description
  formSnapshot.url = form.url
  formSnapshot.startMonth = form.startMonth
  formSnapshot.startYear = form.startYear
  formSnapshot.endMonth = form.endMonth
  formSnapshot.endYear = form.endYear
  formSnapshot.isCurrent = form.isCurrent
  formSnapshot.techStack = [...form.techStack]
}

function openAdd() {
  editingId.value = null
  resetForm()
  takeSnapshot()
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
  takeSnapshot()
  showDialog.value = true
}

function revertForm() {
  form.name = formSnapshot.name
  form.description = formSnapshot.description
  form.url = formSnapshot.url
  form.startMonth = formSnapshot.startMonth
  form.startYear = formSnapshot.startYear
  form.endMonth = formSnapshot.endMonth
  form.endYear = formSnapshot.endYear
  form.isCurrent = formSnapshot.isCurrent
  form.techStack = [...formSnapshot.techStack]
  if (!editingId.value) {
    showDialog.value = false
  }
}

async function saveProject() {
  saving.value = true
  try {
    const dto: IResumeProject = {
      name: form.name,
      description: form.description || undefined,
      url: form.url || undefined,
      startMonth: form.startMonth,
      startYear: form.startYear,
      endMonth: form.endMonth,
      endYear: form.endYear,
      isCurrent: form.isCurrent,
      techStack: form.techStack,
    }
    if (editingId.value) {
      await store.updateProject(editingId.value, dto)
    } else {
      await store.createProject(dto)
    }
    takeSnapshot()
    showDialog.value = false
  } finally {
    saving.value = false
  }
}

function confirmDelete(project: IResumeProject) {
  deleteTarget.value = project
  deleteConfirm.value = true
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

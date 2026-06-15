<template>
  <div class="profile-resume-field">
    <!-- Loading state -->
    <div v-if="loading" class="d-flex align-center justify-center pa-4">
      <v-progress-circular indeterminate size="24" class="mr-2" />
      <span class="text-body-2">Loading resume information...</span>
    </div>

    <!-- Resume info display when file exists -->
    <div v-else-if="resumeInfo" class="resume-info">
      <v-card variant="outlined" class="pa-4">
        <div class="d-flex align-center">
          <v-icon icon="mdi-file-document-check" color="success" size="40" class="mr-3" />
          <div class="flex-grow-1">
            <div class="text-body-1 font-weight-medium">{{ resumeInfo.name }}</div>
            <div class="text-caption text-medium-emphasis">
              {{ formatFileSize(resumeInfo.contentLength) }} • {{ resumeInfo.contentType }}
            </div>
            <div v-if="resumeInfo.lastModifiedDate" class="text-caption text-medium-emphasis">
              Uploaded: {{ formatDate(resumeInfo.lastModifiedDate) }}
            </div>
          </div>
          <div class="d-flex ga-1">
            <v-tooltip text="Download" location="top">
              <template #activator="{ props: tooltipProps }">
                <v-btn
                  v-bind="tooltipProps"
                  color="primary"
                  variant="text"
                  size="small"
                  icon="mdi-download"
                  :loading="downloading"
                  @click="downloadResume"
                />
              </template>
            </v-tooltip>
            <v-tooltip text="Delete" location="top">
              <template #activator="{ props: tooltipProps }">
                <v-btn
                  v-bind="tooltipProps"
                  color="error"
                  variant="text"
                  size="small"
                  icon="mdi-delete"
                  :loading="deleting"
                  @click="confirmDelete"
                />
              </template>
            </v-tooltip>
          </div>
        </div>
      </v-card>
    </div>

    <!-- FilePond uploader when no file exists -->
    <div v-else class="filepond-wrapper">
      <file-pond
        ref="pond"
        name="profile_resume"
        class-name="filepond-resume"
        :label-idle="labelIdle"
        :allow-multiple="false"
        :accepted-file-types="acceptedFileTypes"
        :max-file-size="maxFileSize"
        :label-file-type-not-allowed="'Invalid file type'"
        :file-validate-type-label-expected-types="'Expects {allButLastType} or {lastType}'"
        :label-max-file-size-exceeded="'File is too large'"
        :label-max-file-size="'Maximum file size is {filesize}'"
        :credits="false"
        @addfile="handleAddFile"
        @error="handleError"
      />
    </div>

    <!-- Delete confirmation dialog -->
    <ConfirmDialog
      v-model="showDeleteDialog"
      title="Delete Resume"
      variant="error"
      confirm-text="Delete"
      cancel-text="Cancel"
      :loading="deleting"
      @confirm="deleteResume"
      @cancel="showDeleteDialog = false"
    >
      Are you sure you want to delete this resume? This action cannot be undone.
    </ConfirmDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { type IProfileResume } from '../../models'
import ProfileResumeService from '../../services/profile-resume.service'
import ConfirmDialog from '../ConfirmDialog.vue'
import { useToastStore } from '../../stores/toast'

// Import Vue FilePond
import vueFilePond from 'vue-filepond'

// Import FilePond styles
import 'filepond/dist/filepond.min.css'

// Import FilePond plugins
import FilePondPluginFileValidateType from 'filepond-plugin-file-validate-type'
import FilePondPluginFileValidateSize from 'filepond-plugin-file-validate-size'

// Import FilePond types
import type { FilePondFile, FilePondErrorDescription } from 'filepond'

// Create FilePond component with plugins
const FilePond = vueFilePond(FilePondPluginFileValidateType, FilePondPluginFileValidateSize)

const props = defineProps<{
  profileId?: string
  resumeId?: string
}>()

const emit = defineEmits<{
  'update:resumeId': [value: string | undefined]
}>()

const resumeService = new ProfileResumeService()
const toast = useToastStore()

// FilePond configuration
const acceptedFileTypes = [
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
]
const maxFileSize = '10MB'
const labelIdle = `
  <span class="filepond--label-action">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="32" height="32" fill="currentColor" class="mb-2">
      <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,18V8L14,2M18,20H6V4H13V9H18V20M12,12L16,16H13.5V19H10.5V16H8L12,12Z"/>
    </svg>
  </span>
  <div class="mt-1">Drag & Drop your resume or <span class="filepond--label-action">Browse</span></div>
  <div class="text-caption text-medium-emphasis mt-1">PDF, DOC, DOCX (Max 10MB)</div>
`

// State
const pond = ref()
const resumeInfo = ref<IProfileResume | null>(null)
const loading = ref(false)
const uploading = ref(false)
const downloading = ref(false)
const deleting = ref(false)
const showDeleteDialog = ref(false)

// Methods
const fetchResumeInfo = async (resumeId: string) => {
  loading.value = true
  try {
    resumeInfo.value = await resumeService.find(resumeId)
  } catch (error) {
    console.error('Failed to fetch resume info:', error)
    resumeInfo.value = null
  } finally {
    loading.value = false
  }
}

// Fetch resume info when resumeId changes
watch(
  () => props.resumeId,
  async (newResumeId) => {
    if (newResumeId) {
      await fetchResumeInfo(newResumeId)
    } else {
      resumeInfo.value = null
    }
  },
  { immediate: true }
)

onMounted(async () => {
  if (props.resumeId) {
    await fetchResumeInfo(props.resumeId)
  }
})

const handleAddFile = async (_error: FilePondErrorDescription | null, file: FilePondFile) => {
  if (_error) {
    return
  }

  const actualFile = file.file as File

  if (!actualFile) {
    return
  }

  uploading.value = true

  try {
    const result = await resumeService.upload(actualFile)
    resumeInfo.value = result
    emit('update:resumeId', result.id)
    toast.success('Resume uploaded successfully')

    // Clear FilePond after successful upload
    if (pond.value) {
      pond.value.removeFiles()
    }
  } catch (error) {
    console.error('Failed to upload resume:', error)
    toast.error('Failed to upload resume. Please try again.')

    // Clear FilePond on error
    if (pond.value) {
      pond.value.removeFiles()
    }
  } finally {
    uploading.value = false
  }
}

const handleError = (error: FilePondErrorDescription) => {
  if (error) {
    toast.error(error.body || 'An error occurred')
  }
}

const downloadResume = async () => {
  if (!props.resumeId) return

  downloading.value = true
  try {
    const blob = await resumeService.downloadById(props.resumeId)
    const url = globalThis.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = resumeInfo.value?.name || 'resume'
    document.body.appendChild(link)
    link.click()
    link.remove()
    globalThis.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Failed to download resume:', error)
    toast.error('Failed to download resume. Please try again.')
  } finally {
    downloading.value = false
  }
}

const confirmDelete = () => {
  showDeleteDialog.value = true
}

const deleteResume = async () => {
  if (!props.resumeId) return

  deleting.value = true
  try {
    await resumeService.deleteById(props.resumeId)
    resumeInfo.value = null
    emit('update:resumeId', undefined)
    showDeleteDialog.value = false
    toast.success('Resume deleted successfully')
  } catch (error) {
    console.error('Failed to delete resume:', error)
    toast.error('Failed to delete resume. Please try again.')
  } finally {
    deleting.value = false
  }
}

// Utility functions
const formatFileSize = (bytes?: number): string => {
  if (!bytes) return 'Unknown size'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

const formatDate = (date?: Date): string => {
  if (!date) return ''
  return new Date(date).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

<style scoped>
.profile-resume-field {
  width: 100%;
}

.filepond-wrapper :deep(.filepond--root) {
  margin-bottom: 0;
}

.filepond-wrapper :deep(.filepond--panel-root) {
  border-radius: 8px;
  background-color: rgb(var(--v-theme-surface));
  border: 2px dashed rgba(var(--v-theme-on-surface), 0.2);
}

.filepond-wrapper :deep(.filepond--drop-label) {
  color: rgb(var(--v-theme-on-surface));
  min-height: 120px;
}

.filepond-wrapper :deep(.filepond--drop-label label) {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.filepond-wrapper :deep(.filepond--label-action) {
  color: rgb(var(--v-theme-primary));
  text-decoration: none;
  font-weight: 500;
}

.filepond-wrapper :deep(.filepond--label-action:hover) {
  text-decoration: underline;
}

.filepond-wrapper :deep(.filepond--root[data-hopper-state='waiting'] .filepond--panel-root) {
  border-color: rgb(var(--v-theme-primary));
  background-color: rgba(var(--v-theme-primary), 0.05);
}

.filepond-wrapper :deep(.filepond--item-panel) {
  border-radius: 6px;
}

.filepond-wrapper :deep(.filepond--file-action-button) {
  cursor: pointer;
}

/* Dark mode adjustments */
.v-theme--dark .filepond-wrapper :deep(.filepond--panel-root) {
  border-color: rgba(255, 255, 255, 0.2);
}
</style>

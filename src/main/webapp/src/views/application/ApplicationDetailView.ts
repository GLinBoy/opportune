import { ref, computed, onMounted, defineComponent, inject } from 'vue'
import { useRoute } from 'vue-router'
import type { ApplicationResume, IApplication, IApplicationAttachment, IApplicationMetaData, IApplicationTimeline, IInterviewNote } from '../../models'
import { ApplicationStatus } from '../../models/enumerations/application-status.model'
import { ApplicationAttachmentService, ApplicationMetaDataService, ApplicationResumeService, ApplicationService, ApplicationTimelineService, InterviewNoteService } from '../../services'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}


export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ApplicationDetailView',
  setup() {
    const applicationService = inject('applicationService', () => new ApplicationService())
    const applicationMetaDataService = inject('applicationMetaDataService', () => new ApplicationMetaDataService())
    const applicationAttachmentService = inject('applicationAttachmentService', () => new ApplicationAttachmentService())
    const applicationResumeService = inject('applicationResumeService', () => new ApplicationResumeService())
    const applicationTimelineService = inject('applicationTimelineService', () => new ApplicationTimelineService())
    const interviewNoteService = inject('interviewNoteService', () => new InterviewNoteService())
    const route = useRoute()

    // Main data state
    const application = ref<IApplication | null>(null)
    const applicationMetaData = ref<IApplicationMetaData[]>([])
    const applicationAttachments = ref<IApplicationAttachment[]>([])
    const applicationResume = ref<ApplicationResume | null>(null)
    const applicationTimelines = ref<IApplicationTimeline[]>([])
    const interviewNotes = ref<IInterviewNote[]>([])

    // Loading states
    const loading = ref(false)
    const saving = ref(false)
    const savingMetaData = ref(false)

    // Form state
    const formValid = ref(false)
    const hasModifications = ref(false)
    const applicationForm = ref()
    const activeTab = ref('job-description')

    // Meta Data Dialog state
    const metaDataDialog = ref(false)
    const metaDataForm = ref()
    const metaDataFormValid = ref(false)
    const newMetaData = ref<IApplicationMetaData>({})

    // Snackbar state
    const snackbar = ref<Snackbar>({
      show: false,
      message: '',
      color: 'success',
    })

    // Computed properties
    const applicationId = computed(() => route.params.id as string)

    const breadcrumbs = computed(() => [
      {
        title: 'Dashboard',
        disabled: false,
        href: '/dashboard',
      },
      {
        title: 'Applications',
        disabled: false,
        href: '/applications',
      },
      {
        title: application.value?.title || 'Application Detail',
        disabled: true,
      },
    ])

    // Configuration
    const rules = {
      required: (value: string) => !!value || 'This field is required',
    }

    const workTypeOptions = ['On-site', 'Remote', 'Hybrid']

    const metaDataHeaders = [
      { title: 'Key', value: 'key', width: '200px' },
      { title: 'Value', value: 'value', width: 'auto' },
      { title: 'Actions', value: 'actions', width: '100px', sortable: false, align: 'center' as const },
    ]

    // Utility functions
    const formatDate = (date: Date | undefined): string => {
      if (!date) return ''
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    }

    const getStatusColor = (status: keyof typeof ApplicationStatus | null | undefined): string => {
      if (!status) return 'default'

      const colorMap: Record<keyof typeof ApplicationStatus, string> = {
        INITIATED: 'info',
        APPLIED: 'blue',
        IN_PROGRESS: 'orange',
        REJECTED: 'error',
        OFFER_RECEIVED: 'green',
        ACCEPTED: 'success',
        DECLINED: 'grey',
      }
      return colorMap[status] || 'default'
    }

    const markAsModified = () => {
      hasModifications.value = true
    }

    const saveApplication = async () => {
      if (!application.value || !applicationForm.value) return

      // Validate form
      const { valid } = await applicationForm.value.validate()
      if (!valid) {
        snackbar.value = {
          show: true,
          message: 'Please fix the validation errors before saving.',
          color: 'error',
        }
        return
      }

      try {
        saving.value = true

        // Replace with actual API call when backend is ready
        // await saveApplicationAPI(application.value)

        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 1000))

        hasModifications.value = false

        snackbar.value = {
          show: true,
          message: 'Application saved successfully!',
          color: 'success',
        }
      } catch (error) {
        console.error('Failed to save application:', error)
        snackbar.value = {
          show: true,
          message: 'Failed to save application. Please try again.',
          color: 'error',
        }
      } finally {
        saving.value = false
      }
    }

    const uploadResume = () => {
      // Function to handle resume upload for this specific application
      console.log('Upload resume clicked for application:', application.value?.id)
      snackbar.value = {
        show: true,
        message: 'Upload resume functionality will be implemented soon!',
        color: 'info',
      }
    }

    const showRawContent = () => {
      // Function to show raw content fetched from job posting URL
      console.log('Show raw content clicked for application:', application.value?.id)
      console.log('Job URL:', application.value?.url)
      snackbar.value = {
        show: true,
        message: 'Show raw content functionality will be implemented soon!',
        color: 'info',
      }
    }

    const deleteApplication = () => {
      // Show confirmation dialog and delete when implemented
      snackbar.value = {
        show: true,
        message: 'Delete application feature coming soon!',
        color: 'info',
      }
    }

    // AI Content Generation Methods
    const regenerateJobDescription = () => {
      console.log('Regenerate job description clicked for application:', application.value?.id)
      snackbar.value = {
        show: true,
        message: 'Regenerating job description analysis...',
        color: 'info',
      }
    }

    const regenerateCoverLetter = () => {
      console.log('Regenerate cover letter clicked for application:', application.value?.id)
      snackbar.value = {
        show: true,
        message: 'Regenerating cover letter...',
        color: 'info',
      }
    }

    const regenerateResumeEnhancer = () => {
      console.log('Regenerate resume enhancer clicked for application:', application.value?.id)
      snackbar.value = {
        show: true,
        message: 'Regenerating resume enhancement suggestions...',
        color: 'info',
      }
    }

    const regenerateInterviewDetails = () => {
      console.log('Regenerate interview details clicked for application:', application.value?.id)
      snackbar.value = {
        show: true,
        message: 'Regenerating interview preparation details...',
        color: 'info',
      }
    }

    // Meta Data Management Methods
    const showAddMetaDataDialog = () => {
      newMetaData.value = {}
      metaDataDialog.value = true
    }

    const saveMetaData = async () => {
      if (!metaDataForm.value || !application.value) return

      // Validate form
      const { valid } = await metaDataForm.value.validate()
      if (!valid) {
        return
      }

      try {
        savingMetaData.value = true

        // Simulate API call delay
        await new Promise((resolve) => setTimeout(resolve, 500))

        // Add to application meta data

        markAsModified()
        metaDataDialog.value = false

        snackbar.value = {
          show: true,
          message: 'Meta data added successfully!',
          color: 'success',
        }
      } catch (error) {
        console.error('Failed to add meta data:', error)
        snackbar.value = {
          show: true,
          message: 'Failed to add meta data. Please try again.',
          color: 'error',
        }
      } finally {
        savingMetaData.value = false
      }
    }

    const cancelAddMetaData = () => {
      metaDataDialog.value = false
      newMetaData.value = {}
    }

    const removeMetaData = (index: number) => {
      console.log('Remove meta data at index:', index)
      markAsModified()

      snackbar.value = {
        show: true,
        message: 'Meta data removed successfully!',
        color: 'success',
      }
    }

    const loadApplication = async () => {
      try {
        loading.value = true

        // Replace with actual API call when backend is ready
        // const response = await fetchApplicationById(applicationId.value)
        // application.value = response.data

        // Simulate API call delay
        await applicationService().find(applicationId.value)
          .then(data => {
            application.value = data
          })

        const metadataPaginationQuery = {
          page: 0,
          size: 100,
          sort: `metaName,asc`
        }
        await applicationMetaDataService().retrieve(applicationId.value, metadataPaginationQuery)
          .then(data => {
            applicationMetaData.value = data
          })

        const attachmentPaginationQuery = {
          page: 0,
          size: 100,
          sort: `name,asc`
        }
        await applicationAttachmentService().retrieve(applicationId.value, attachmentPaginationQuery)
          .then(data => {
            applicationAttachments.value = data
          })

        if (application.value?.resumeId) {
          await applicationResumeService().find(applicationId.value, application.value?.resumeId)
            .then(data => {
              applicationResume.value = data
            })
        }

        const timelinePaginationQuery = {
          page: 0,
          size: 100,
          sort: `occurredAt,asc`
        }
        await applicationTimelineService().retrieve(applicationId.value, timelinePaginationQuery)
          .then(response => {
            applicationTimelines.value = response.data
          })

        const interviewNotePaginationQuery = {
          page: 0,
          size: 100,
          sort: `date,asc`
        }
        await interviewNoteService().retrieve(applicationId.value, interviewNotePaginationQuery)
          .then(response => {
            interviewNotes.value = response.data
          })

      } catch (error) {
        console.error('Failed to load application:', error)
        snackbar.value = {
          show: true,
          message: 'Failed to load application details.',
          color: 'error',
        }
        application.value = null
      } finally {
        loading.value = false
      }
    }

    // Lifecycle
    onMounted(() => {
      loadApplication()
    })

    // Return all the reactive properties and methods
    return {
      // Main data
      application,
      applicationMetaData,
      applicationAttachments,
      applicationResume,
      applicationTimelines,
      interviewNotes,

      // Loading states
      loading,
      saving,
      savingMetaData,

      // Form state
      formValid,
      hasModifications,
      applicationForm,
      activeTab,

      // Meta Data Dialog state
      metaDataDialog,
      metaDataForm,
      metaDataFormValid,
      newMetaData,

      // UI state
      snackbar,

      // Computed properties
      applicationId,
      breadcrumbs,

      // Configuration
      rules,
      workTypeOptions,
      metaDataHeaders,

      // Utility methods
      formatDate,
      getStatusColor,
      markAsModified,

      // Main actions
      saveApplication,
      uploadResume,
      showRawContent,
      deleteApplication,

      // AI methods
      regenerateJobDescription,
      regenerateCoverLetter,
      regenerateResumeEnhancer,
      regenerateInterviewDetails,

      // Meta Data methods
      showAddMetaDataDialog,
      saveMetaData,
      cancelAddMetaData,
      removeMetaData,

      // Data methods
      loadApplication,
    }
  }
})

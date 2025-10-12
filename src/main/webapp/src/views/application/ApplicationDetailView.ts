import { ref, computed, onMounted, defineComponent, inject, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Application, type ApplicationResume, type IApplication, type IApplicationAttachment, type IApplicationDetails, type IApplicationMetaData, type IApplicationTimeline, type ICompany, type IInterviewNote } from '../../models'
import { ApplicationStatus, getApplicationStatusDisplay } from '../../models/enumerations/application-status.model'
import { ApplicationService, CompanyService } from '../../services'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}


export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ApplicationDetailView',
  computed: {
    statusOptions() {
      return Object.values(ApplicationStatus).map(status => ({
        title: getApplicationStatusDisplay(status),
        value: status
      }))
    }
  },
  setup() {
    const applicationService = inject('applicationService', () => new ApplicationService())
    const companyService = inject('companyService', () => new CompanyService())
    const route = useRoute()

    // Main data state
    const application = ref<IApplicationDetails | null>(null)
    const companies = ref<ICompany[]>([])

    // Loading states
    const loading = ref(false)
    const saving = ref(false)
    const savingMetaData = ref(false)
    const isCompanyEditing = ref(false)
    const isCompanyLoading = ref(false)

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
      try {
        saving.value = true
        const updateApplication: IApplication = new Application()
        updateApplication.id = application.value?.id
        updateApplication.url = application.value?.url
        updateApplication.title = application.value?.title
        updateApplication.location = application.value?.location
        updateApplication.appliedAt = application.value?.appliedAt
        updateApplication.salary = application.value?.salary
        updateApplication.note = application.value?.note
        updateApplication.rawContent = application.value?.rawContent
        updateApplication.description = application.value?.description
        updateApplication.coverLetter = application.value?.coverLetter
        updateApplication.resumeInsights = application.value?.resumeInsights
        updateApplication.status = application.value?.status
        updateApplication.companyId = application.value?.company?.id
        updateApplication.resumeId = application.value?.resume?.id

        await applicationService().update(updateApplication)

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

    const appliedJob = () => {
      application.value!.appliedAt = new Date()
      application.value!.status = ApplicationStatus.APPLIED
      markAsModified()
      saveApplication()
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

        await applicationService().getApplicationsDetails(applicationId.value)
          .then(data => {
            application.value = data
            companies.value.push(data.company!)
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

    const sort = (): Array<string> => {
      return ['name,asc']
    }

    const search = (): string => {
      return ''
    }

    const loadCompanies = async () => {
      try {
        isCompanyLoading.value = true
        const paginationQuery = {
          page: 0,
          size: 5,
          sort: sort(),
          query: search(),
        }
        const res = await companyService().retrieve(paginationQuery)
        companies.value = [application.value!.company!, ...res.data.filter(c => c.id !== application.value?.company?.id)]
      } catch (err) {
        console.error('Failed to retrieve companies:', err)
      } finally {
        isCompanyLoading.value = false
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
      companies,

      // Loading states
      loading,
      saving,
      savingMetaData,
      isCompanyEditing,
      isCompanyLoading,

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
      appliedJob,

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

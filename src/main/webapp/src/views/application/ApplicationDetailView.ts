import { ref, computed, onMounted, defineComponent, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Application, type IApplication, type IApplicationDetails, type IApplicationMetaData } from '../../models'
import { ApplicationStatus, getApplicationStatusDisplay, getApplicationStatusColor, getApplicationStatusIcon } from '../../models/enumerations/application-status.model'
import { ApplicationService, ApplicationMetaDataService } from '../../services'
import RawContentDialog from '../../components/application/RawContentDialog.vue'
import CompanyAutocomplete from '../../components/company/CompanyAutocomplete.vue'
import ConfirmDialog from '../../components/ConfirmDialog.vue'
import FormDialog from '../../components/FormDialog.vue'
import MetadataTable from '../../components/MetadataTable.vue'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}


export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ApplicationDetailView',
  components: {
    RawContentDialog,
    CompanyAutocomplete,
    ConfirmDialog,
    FormDialog,
    MetadataTable,
  },
  computed: {
    statusOptions() {
      return Object.values(ApplicationStatus).map(status => ({
        title: getApplicationStatusDisplay(status),
        value: status,
        color: getApplicationStatusColor(status),
        icon: getApplicationStatusIcon(status),
      }))
    }
  },
  methods: {
    getApplicationStatusColor,
    getApplicationStatusIcon,
  },
  setup() {
    const applicationService = inject('applicationService', () => new ApplicationService())
    const applicationMetadataService = inject('applicationMetadataService', () => new ApplicationMetaDataService())
    const route = useRoute()
    const router = useRouter()

    // Main data state
    const application = ref<IApplicationDetails | null>(null)
    const applicationMetadata = ref<IApplicationMetaData[]>([])

    // Loading states
    const loading = ref(false)
    const saving = ref(false)
    const savingMetaData = ref(false)
    const isDeleting = ref(false)

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
    const confirmDeleteMetaDataDialog = ref(false)
    const metaDataToDelete = ref<IApplicationMetaData | null>(null)
    const metaDataDialogTitle = computed(() =>
      newMetaData.value?.id ? 'Edit Meta Data' : 'Add Meta Data'
    )

    // Raw Content Dialog state
    const rawContentDialog = ref(false)

    // Delete confirmation dialog state
    const confirmDeleteDialog = ref(false)

    // Snackbar state
    const snackbar = ref<Snackbar>({
      show: false,
      message: '',
      color: 'success',
    })

    // Computed properties
    const applicationId = computed(() => route.params.id as string)

    const breadcrumbs = computed(() => [
      { title: 'Dashboard', to: '/', disabled: false },
      { title: 'Applications', to: '/applications', disabled: false },
      { title: application.value?.title || 'Application Detail', to: '', disabled: true }
    ])

    // Configuration
    const rules = {
      required: (value: string) => !!value || 'This field is required',
    }

    const workTypeOptions = ['On-site', 'Remote', 'Hybrid']

    // Utility functions
    const formatDate = (date: Date | undefined): string => {
      if (!date) return ''
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
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
      // Open the raw content dialog
      rawContentDialog.value = true
    }

    // Delete application
    const confirmDelete = () => {
      confirmDeleteDialog.value = true
    }

    const closeDeleteDialog = () => {
      confirmDeleteDialog.value = false
    }

    const performDelete = async () => {
      if (!application.value?.id) {
        closeDeleteDialog()
        return
      }
      isDeleting.value = true
      try {
        await applicationService().delete(String(application.value.id))
        snackbar.value = {
          show: true,
          message: `Deleted ${application.value.title || 'application'} successfully.`,
          color: 'success',
        }
        closeDeleteDialog()
        // Navigate back to applications list after successful deletion
        setTimeout(() => {
          router.push('/applications')
        }, 1000)
      } catch (err) {
        console.error('Failed to delete application:', err)
        snackbar.value = {
          show: true,
          message: 'Failed to delete application. Please try again.',
          color: 'error',
        }
        isDeleting.value = false
        closeDeleteDialog()
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
      newMetaData.value = { metaName: '', metaValue: '' }
      metaDataDialog.value = true
    }

    const saveMetaData = async () => {
      if (!metaDataForm.value || !application.value) return

      // Validate form
      const { valid } = await metaDataForm.value.validate()
      if (!valid) {
        return
      }

      savingMetaData.value = true
      try {
        if (!applicationMetadata.value) {
          applicationMetadata.value = []
        }
        newMetaData.value.applicationId = application.value?.id || ''
        await applicationMetadataService()
          .create(application.value?.id || '', newMetaData.value)
          .then(data => {
            applicationMetadata.value.push(data)
          })

        metaDataDialog.value = false
        newMetaData.value = { metaName: '', metaValue: '' }
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
      newMetaData.value = { metaName: '', metaValue: '' }
    }

    const removeMetaData = (id: string | undefined) => {
      const metadata = applicationMetadata.value.find(item => item.id === id)
      if (metadata) {
        metaDataToDelete.value = metadata
        confirmDeleteMetaDataDialog.value = true
      }
    }

    const closeDeleteMetaDataDialog = () => {
      confirmDeleteMetaDataDialog.value = false
      metaDataToDelete.value = null
    }

    const performMetaDataDelete = async () => {
      const metadata = metaDataToDelete.value
      if (applicationMetadata.value && metadata?.id) {
        try {
          await applicationMetadataService().delete(application.value?.id || '', metadata.id)
          const index = applicationMetadata.value.findIndex(item => item.id === metadata.id)
          if (index !== -1) {
            applicationMetadata.value.splice(index, 1)
            snackbar.value = {
              show: true,
              message: 'Meta data removed successfully!',
              color: 'success',
            }
          }
        } catch (err) {
          console.error('Failed to delete meta data:', err)
          snackbar.value = {
            show: true,
            message: 'Failed to delete meta data. Please try again.',
            color: 'error',
          }
        } finally {
          closeDeleteMetaDataDialog()
        }
      } else {
        snackbar.value = {
          show: true,
          message: 'Meta data item not found.',
          color: 'error',
        }
        closeDeleteMetaDataDialog()
      }
    }

    const loadApplication = async () => {
      try {
        loading.value = true

        await applicationService().getApplicationsDetails(applicationId.value)
          .then(data => {
            application.value = data
          })

        const metadataPaginationQuery = {
          page: 0,
          size: 100,
          sort: `metaName,asc`
        }
        await applicationMetadataService().retrieve(applicationId.value, metadataPaginationQuery)
          .then(data => {
            applicationMetadata.value = data
          })
          .catch(err => {
            console.error('Failed to load metadata:', err)
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
      applicationMetadata,

      // Loading states
      loading,
      saving,
      savingMetaData,
      isDeleting,

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
      confirmDeleteMetaDataDialog,
      metaDataToDelete,

      // Raw Content Dialog state
      rawContentDialog,

      // Delete confirmation dialog state
      confirmDeleteDialog,

      // UI state
      snackbar,

      // Computed properties
      applicationId,
      breadcrumbs,

      // Configuration
      rules,
      workTypeOptions,

      // Utility methods
      formatDate,
      getStatusColor: getApplicationStatusColor,
      markAsModified,

      // Main actions
      saveApplication,
      uploadResume,
      showRawContent,
      appliedJob,

      // Delete actions
      confirmDelete,
      closeDeleteDialog,
      performDelete,

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
      metaDataDialogTitle,
      closeDeleteMetaDataDialog,
      performMetaDataDelete,

      // Data methods
      loadApplication,
    }
  }
})

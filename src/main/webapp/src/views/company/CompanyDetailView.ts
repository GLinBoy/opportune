import { ref, computed, onMounted, defineComponent, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { type ICompany, type ICompanyMetadata } from '../../models'
import { type IApplication, type IApplicationProjection } from '../../models/application.model'
import CompanyService from '../../services/company.service'
import CompanyMetadataService from '../../services/company-metadata.service'
import CompanyApplicationService from '../../services/company-application.service'
import ApplicationService from '../../services/application.service'
import CompanyForm from '../../components/company/CompanyForm.vue'
import CompanyLogo from '../../components/company/CompanyLogo.vue'
import ConfirmDialog from '../../components/ConfirmDialog.vue'
import FormDialog from '../../components/FormDialog.vue'
import ApplicationTable from '../../components/application/ApplicationTable.vue'
import MetadataTable from '../../components/MetadataTable.vue'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyDetailView',
  components: {
    CompanyForm,
    CompanyLogo,
    ConfirmDialog,
    FormDialog,
    ApplicationTable,
    MetadataTable,
  },
  setup() {
    // Services and dependencies
    const companyService = inject('companyService', () => new CompanyService())
    const companyMetadataService = inject('companyMetadataService', () => new CompanyMetadataService())
    const companyApplicationService = inject('companyApplicationService', () => new CompanyApplicationService())
    const applicationService = inject('applicationService', () => new ApplicationService())
    const route = useRoute()
    const router = useRouter()

    // Main data state
    const company = ref<ICompany | null>(null)
    const companyMetadata = ref<ICompanyMetadata[]>([])
    const associatedApplications = ref<IApplication[]>([])

    // Loading states
    const loading = ref(false)
    const saving = ref(false)
    const savingMetaData = ref(false)
    const isDeleting = ref(false)

    // Dialog state
    const showMetaDataDialog = ref(false)
    const metaDataFormValid = ref(false)
    const newMetaData = ref<ICompanyMetadata>({ companyId: '', metaName: '', metaValue: '' })
    const confirmDeleteDialog = ref(false)
    const confirmDeleteMetaDataDialog = ref(false)
    const metaDataToDelete = ref<ICompanyMetadata | null>(null)
    const confirmDeleteApplicationDialog = ref(false)
    const applicationToDelete = ref<IApplicationProjection | null>(null)

    // Snackbar state
    const snackbar = ref<Snackbar>({ show: false, message: '', color: 'success' })

    // Computed properties
    const breadcrumbs = computed(() => [
      { title: 'Dashboard', to: '/', disabled: false },
      { title: 'Companies', to: '/companies', disabled: false },
      { title: company.value?.name || 'Company Details', to: '', disabled: true },
    ])

    const metaDataDialogTitle = computed(() =>
      newMetaData.value?.id ? 'Edit Meta Data' : 'Add Meta Data'
    )

    const rules = {
      required: (value: string) => !!value || 'This field is required',
    }

    // Utility functions
    const showSnackbar = (message: string, color: string) => {
      snackbar.value = { show: true, message, color }
    }

    const markAsModified = () => {
      // Mark as modified for change tracking
    }

    // Data fetching
    const loadCompany = async () => {
      const companyId = route.params.id as string
      if (!companyId) return

      loading.value = true
      try {
        // Mock loading time - replace with actual API implementation
        await new Promise((resolve) => setTimeout(resolve, 500))

        await companyService().find(companyId).then(data => {
          company.value = data
        }).catch(err => {
          console.error(err)
        })

        const metadataPaginationQuery = {
          page: 0,
          size: 100,
          sort: `metaName,asc`
        }
        await companyMetadataService().retrieve(companyId, metadataPaginationQuery).then(data => {
          companyMetadata.value = data
        }).catch(err => {
          console.error(err)
        })

        const applicationPaginationQuery = {
          page: 0,
          size: 3,
          sort: `appliedAt,asc`
        }
        await companyApplicationService().retrieve(companyId, applicationPaginationQuery).then(res => {
          associatedApplications.value = res.data
        }).catch(err => {
          console.error(err)
        })

      } catch (error) {
        console.error('Failed to load company details:', error)
        showSnackbar('Failed to load company details. Please try again.', 'error')
      } finally {
        loading.value = false
      }
    }

    // Main actions
    const saveCompany = async () => {
      if (!company.value) return

      saving.value = true
      try {
        await companyService().update(company.value).then(data => {
          company.value = data
        })

        showSnackbar('Company saved successfully!', 'success')
      } catch (error) {
        console.error('Failed to save company:', error)
        showSnackbar('Failed to save company. Please try again.', 'error')
      } finally {
        saving.value = false
      }
    }

    // Dialog handlers
    const showAddMetaDataDialog = () => {
      newMetaData.value = {}
      showMetaDataDialog.value = true
    }

    const showEditMetaDataDialog = (item: ICompanyMetadata) => {
      newMetaData.value = { ...item }
      showMetaDataDialog.value = true
    }

    const cancelAddMetaData = () => {
      showMetaDataDialog.value = false
      newMetaData.value = {}
    }

    const saveMetaData = async () => {
      if (!metaDataFormValid.value) return

      savingMetaData.value = true
      try {
        if (!companyMetadata.value) {
          companyMetadata.value = []
        }
        newMetaData.value.companyId = company.value?.id || ''
        if (newMetaData.value.id) {
          await companyMetadataService()
            .update(company.value?.id || '', newMetaData.value)
            .then(data => {
              const index = companyMetadata.value.findIndex(item => item.id === data.id)
              if (index !== -1) companyMetadata.value.splice(index, 1, data)
            })
          showSnackbar('Meta data updated successfully!', 'success')
        } else {
          await companyMetadataService()
            .create(company.value?.id || '', newMetaData.value)
            .then(data => {
              companyMetadata.value.push(data)
            })
          showSnackbar('Meta data added successfully!', 'success')
        }

        showMetaDataDialog.value = false
        newMetaData.value = {}
      } catch (error) {
        console.error('Failed to save meta data:', error)
        showSnackbar('Failed to save meta data. Please try again.', 'error')
      } finally {
        savingMetaData.value = false
      }
    }

    const removeMetaData = (id: string | undefined) => {
      const metadata = companyMetadata.value.find(item => item.id === id)
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
      if (companyMetadata.value && metadata?.id) {
        try {
          await companyMetadataService().delete(company.value?.id || '', metadata.id)
          const index = companyMetadata.value.findIndex(item => item.id === metadata.id)
          if (index !== -1) {
            companyMetadata.value.splice(index, 1)
            showSnackbar('Meta data removed successfully!', 'success')
          }
        } catch (err) {
          console.error('Failed to delete meta data:', err)
          showSnackbar('Failed to delete meta data. Please try again.', 'error')
        } finally {
          closeDeleteMetaDataDialog()
        }
      } else {
        showSnackbar('Meta data item not found.', 'error')
        closeDeleteMetaDataDialog()
      }
    }

    const confirmDeleteApplication = (item: IApplicationProjection) => {
      applicationToDelete.value = item
      confirmDeleteApplicationDialog.value = true
    }

    const closeDeleteApplicationDialog = () => {
      confirmDeleteApplicationDialog.value = false
      applicationToDelete.value = null
    }

    const performApplicationDelete = async () => {
      const app = applicationToDelete.value
      if (!app?.id) {
        closeDeleteApplicationDialog()
        return
      }
      try {
        await applicationService().delete(app.id)
        const index = associatedApplications.value.findIndex(a => a.id === app.id)
        if (index !== -1) associatedApplications.value.splice(index, 1)
        showSnackbar(`Deleted "${app.title}" successfully.`, 'success')
      } catch (err) {
        console.error('Failed to delete application:', err)
        showSnackbar('Failed to delete application. Please try again.', 'error')
      } finally {
        closeDeleteApplicationDialog()
      }
    }

    // Delete company
    const confirmDelete = () => {
      confirmDeleteDialog.value = true
    }

    const closeDeleteDialog = () => {
      confirmDeleteDialog.value = false
    }

    const performDelete = async () => {
      if (!company.value?.id) {
        closeDeleteDialog()
        return
      }
      isDeleting.value = true
      try {
        await companyService().delete(String(company.value.id))
        showSnackbar(`Deleted ${company.value.name || 'company'} successfully.`, 'success')
        closeDeleteDialog()
        // Navigate back to companies list after successful deletion
        setTimeout(() => {
          router.push('/companies')
        }, 1000)
      } catch (err) {
        console.error('Failed to delete company:', err)
        showSnackbar('Failed to delete company. Please try again.', 'error')
        isDeleting.value = false
        closeDeleteDialog()
      }
    }

    // Lifecycle
    onMounted(() => {
      loadCompany()
    })

    return {
      // Main data
      company,
      companyMetadata,
      associatedApplications,

      // Loading states
      loading,
      saving,
      savingMetaData,
      isDeleting,

      // UI state
      snackbar,
      breadcrumbs,
      metaDataDialogTitle,

      rules,

      // Dialog state
      showMetaDataDialog,
      metaDataFormValid,
      newMetaData,
      confirmDeleteDialog,
      confirmDeleteMetaDataDialog,
      metaDataToDelete,
      confirmDeleteApplicationDialog,
      applicationToDelete,

      // Actions
      markAsModified,
      saveCompany,

      // Dialog actions
      showAddMetaDataDialog,
      showEditMetaDataDialog,
      cancelAddMetaData,
      saveMetaData,
      removeMetaData,
      closeDeleteMetaDataDialog,
      performMetaDataDelete,
      confirmDelete,
      closeDeleteDialog,
      performDelete,
      confirmDeleteApplication,
      closeDeleteApplicationDialog,
      performApplicationDelete,
    }
  }
})

import { ref, computed, onMounted, defineComponent, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { type ICompany, type ICompanyMetadata } from '../../models'
import { type IApplication } from '../../models/application.model'
import CompanyService from '../../services/company.service'
import CompanyMetadataService from '../../services/company-metadata.service'
import CompanyApplicationService from '../../services/company-application.service'
import CompanyForm from '../../components/CompanyForm.vue'

import defaultCompanyLogo from '@/assets/images/office-building.png'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyDetailView',
  components: {
    CompanyForm
  },
  setup() {
    // Services and dependencies
    const companyService = inject('companyService', () => new CompanyService())
    const companyMetadataService = inject('companyMetadataService', () => new CompanyMetadataService())
    const companyApplicationService = inject('companyApplicationService', () => new CompanyApplicationService())
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

    // Snackbar state
    const snackbar = ref<Snackbar>({ show: false, message: '', color: 'success' })

    // Computed properties
    const breadcrumbs = computed(() => [
      { title: 'Dashboard', to: '/', disabled: false },
      { title: 'Companies', to: '/companies', disabled: false },
      { title: company.value?.name || 'Company Details', to: '', disabled: true },
    ])

    // Configuration
    const metaDataHeaders = [
      { title: 'Key', value: 'metaName', sortable: false },
      { title: 'Value', value: 'metaValue', sortable: false },
      { title: 'Actions', value: 'actions', sortable: false, width: '80px' },
    ]

    const applicationHeaders = [
      { title: 'Title', value: 'title', sortable: true },
      { title: 'Applied at', value: 'appliedAt', sortable: true },
      { title: 'Status', value: 'status', sortable: true },
      { title: 'Actions', value: 'actions', sortable: true, width: '80px' },
    ]

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
        await companyMetadataService()
          .create(company.value?.id || '', newMetaData.value)
          .then(data => {
            companyMetadata.value.push(data)
          })

        showMetaDataDialog.value = false
        newMetaData.value = {}
        showSnackbar('Meta data added successfully!', 'success')
      } catch (error) {
        console.error('Failed to add meta data:', error)
        showSnackbar('Failed to add meta data. Please try again.', 'error')
      } finally {
        savingMetaData.value = false
      }
    }

    const removeMetaData = (id: string | undefined) => {
      if (companyMetadata.value && id) {
        companyMetadataService().delete(company.value?.id || '', id)
          .then(() => {
            const index = companyMetadata.value.findIndex(item => item.id === id)
            if (index !== -1) {
              companyMetadata.value.splice(index, 1)
              showSnackbar('Meta data removed successfully!', 'success')
            }
          })
          .catch(err => {
            console.error('Failed to delete meta data:', err)
            showSnackbar('Failed to delete meta data. Please try again.', 'error')
          })
      } else {
        showSnackbar('Meta data item not found.', 'error')
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

      // Configuration
      metaDataHeaders,
      applicationHeaders,
      rules,
      defaultCompanyLogo,

      // Dialog state
      showMetaDataDialog,
      metaDataFormValid,
      newMetaData,
      confirmDeleteDialog,

      // Actions
      markAsModified,
      saveCompany,

      // Dialog actions
      showAddMetaDataDialog,
      cancelAddMetaData,
      saveMetaData,
      removeMetaData,
      confirmDelete,
      closeDeleteDialog,
      performDelete,
    }
  }
})

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
    const companyService = inject('companyService', () => new CompanyService())
    const companyMetadataService = inject('companyMetadataService', () => new CompanyMetadataService())
    const companyApplicationService = inject('companyApplicationService', () => new CompanyApplicationService())
    const route = useRoute()
    const router = useRouter()

    // Reactive data
    const company = ref<ICompany | null>(null)
    const companyMetadata = ref<ICompanyMetadata[]>([])
    const associatedApplications = ref<IApplication[]>([])
    const loading = ref(false)
    const saving = ref(false)
    const hasModifications = ref(false)

    // Meta Data Dialog
    const showMetaDataDialog = ref(false)
    const metaDataFormValid = ref(false)
    const savingMetaData = ref(false)
    const newMetaData = ref<ICompanyMetadata>({ companyId: '', metaName: '', metaValue: '' })

    // Snackbar
    const snackbar = ref<Snackbar>({ show: false, message: '', color: 'success' })

    // Breadcrumbs
    const breadcrumbs = computed(() => [
      { title: 'Dashboard', to: '/', disabled: false },
      { title: 'Companies', to: '/companies', disabled: false },
      { title: company.value?.name || 'Company Details', to: '', disabled: true },
    ])

    // Table headers for meta data
    const metaDataHeaders = [
      { title: 'Key', value: 'metaName', sortable: false },
      { title: 'Value', value: 'metaValue', sortable: false },
      { title: 'Actions', value: 'actions', sortable: false, width: '80px' },
    ]

    // Table headers for applications
    const applicationHeaders = [
      { title: 'Title', value: 'title', sortable: true },
      { title: 'Applied at', value: 'appliedAt', sortable: true },
      { title: 'Status', value: 'status', sortable: true },
      { title: 'Actions', value: 'actions', sortable: true, width: '80px' },
    ]

    // Validation rules
    const rules = {
      required: (value: string) => !!value || 'This field is required',
    }

    // Methods
    const markAsModified = () => {
      hasModifications.value = true
    }

    const saveCompany = async () => {
      if (!company.value) return

      saving.value = true
      try {
        await companyService().update(company.value).then(data => {
          company.value = data
        })

        hasModifications.value = false
        showSnackbar('Company saved successfully!', 'success')
      } catch (error) {
        console.error('Failed to save company:', error)
        showSnackbar('Failed to save company. Please try again.', 'error')
      } finally {
        saving.value = false
      }
    }

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
        markAsModified()
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
              markAsModified()
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

    const getStatusColor = (status: string): string => {
      const statusColors: Record<string, string> = {
        Applied: 'primary',
        'Phone Screen': 'info',
        'Interview Scheduled': 'warning',
        'Interview Completed': 'orange',
        'Offer Received': 'success',
        Rejected: 'error',
        Withdrawn: 'grey',
      }
      return statusColors[status] || 'default'
    }

    const formatDate = (dateString: string): string => {
      return new Date(dateString).toLocaleDateString()
    }

    const viewApplication = (applicationId: number) => {
      router.push(`/applications/${applicationId}`)
    }

    const showSnackbar = (message: string, color: string) => {
      snackbar.value = { show: true, message, color }
    }

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

    // Lifecycle
    onMounted(() => {
      loadCompany()
    })

    return {
      company,
      companyMetadata,
      associatedApplications,
      loading,
      saving,
      hasModifications,
      showMetaDataDialog,
      metaDataFormValid,
      savingMetaData,
      newMetaData,
      snackbar,
      breadcrumbs,
      metaDataHeaders,
      applicationHeaders,
      rules,
      markAsModified,
      saveCompany,
      showAddMetaDataDialog,
      cancelAddMetaData,
      saveMetaData,
      removeMetaData,
      getStatusColor,
      formatDate,
      viewApplication,
      showSnackbar,
      loadCompany,
      defaultCompanyLogo,
    }
  }
})

import { ref, computed, onMounted, defineComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// Types
export interface MetaData {
  key: string
  value: string
}

export interface Application {
  id: number
  position: string
  dateApplied: string
  status: string
}

export interface Company {
  id: number
  name: string
  industry: string
  website?: string
  size?: string
  location?: string
  founded?: number
  description?: string
  notes?: string
  metaData?: MetaData[]
}

export interface Snackbar {
  show: boolean
  message: string
  color: string
}

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyDetailView',
  setup() {
    // Router
    const route = useRoute()
    const router = useRouter()

    // Reactive data
    const company = ref<Company | null>(null)
    const associatedApplications = ref<Application[]>([])
    const loading = ref(false)
    const saving = ref(false)
    const hasModifications = ref(false)

    // Meta Data Dialog
    const showMetaDataDialog = ref(false)
    const metaDataFormValid = ref(false)
    const savingMetaData = ref(false)
    const newMetaData = ref<MetaData>({ key: '', value: '' })

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
      { title: 'Key', value: 'key', sortable: false },
      { title: 'Value', value: 'value', sortable: false },
      { title: 'Actions', value: 'actions', sortable: false, width: '80px' },
    ]

    // Table headers for applications
    const applicationHeaders = [
      { title: 'Position', value: 'position', sortable: false },
      { title: 'Status', value: 'status', sortable: false },
      { title: 'Actions', value: 'actions', sortable: false, width: '80px' },
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
        // Mock API call - replace with actual API implementation
        await new Promise((resolve) => setTimeout(resolve, 1000))

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
      newMetaData.value = { key: '', value: '' }
      showMetaDataDialog.value = true
    }

    const cancelAddMetaData = () => {
      showMetaDataDialog.value = false
      newMetaData.value = { key: '', value: '' }
    }

    const saveMetaData = async () => {
      if (!metaDataFormValid.value) return

      savingMetaData.value = true
      try {
        if (!company.value!.metaData) {
          company.value!.metaData = []
        }
        company.value!.metaData.push({ ...newMetaData.value })

        showMetaDataDialog.value = false
        newMetaData.value = { key: '', value: '' }
        markAsModified()
        showSnackbar('Meta data added successfully!', 'success')
      } catch (error) {
        console.error('Failed to add meta data:', error)
        showSnackbar('Failed to add meta data. Please try again.', 'error')
      } finally {
        savingMetaData.value = false
      }
    }

    const removeMetaData = (index: number) => {
      if (company.value?.metaData) {
        company.value.metaData.splice(index, 1)
        markAsModified()
        showSnackbar('Meta data removed successfully!', 'success')
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
      const companyId = parseInt(route.params.id as string)
      if (!companyId) return

      loading.value = true
      try {
        // Mock loading time - replace with actual API implementation
        await new Promise((resolve) => setTimeout(resolve, 500))

        // Mock data - replace with API call
        company.value = {
          id: companyId,
          name: 'Tech Corp',
          industry: 'Technology',
          website: 'https://techcorp.com',
          size: '200-500 employees',
          location: 'San Francisco, CA',
          founded: 2015,
          description:
            'A leading technology company focused on innovative solutions for modern businesses.',
          notes: 'Great company culture, competitive salary, remote-friendly',
          metaData: [
            { key: 'Contact Person', value: 'John Doe - HR Manager' },
            { key: 'Revenue', value: '$50M annually' },
            { key: 'Culture', value: 'Collaborative, innovative, work-life balance focused' },
          ],
        }

        // Mock associated applications
        associatedApplications.value = [
          {
            id: 1,
            position: 'Senior Frontend Developer',
            dateApplied: '2024-01-15',
            status: 'Interview Scheduled',
          },
          {
            id: 2,
            position: 'Full Stack Engineer',
            dateApplied: '2023-12-10',
            status: 'Rejected',
          },
        ]
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
    }
  }
})

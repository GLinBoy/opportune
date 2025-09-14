import { ref, computed, onMounted, defineComponent } from 'vue'
import { useRouter } from 'vue-router'

// Types
export interface Application {
  id: number
  position: string
  company: string
  dateApplied: string
  status: string
}

export interface StatusOption {
  title: string
  value: string
}

export interface Snackbar {
  show: boolean
  message: string
  color: string
}

export interface AddDialog {
  show: boolean
  valid: boolean
  loading: boolean
  jobUrl: string
}


export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ApplicationListView',
  setup() {
    // Router
    const router = useRouter()

    // Reactive data
    const loading = ref(false)
    const searchQuery = ref('')
    const currentPage = ref(1)
    const itemsPerPage = ref(25)

    // Snackbar for notifications
    const snackbar = ref<Snackbar>({
      show: false,
      message: '',
      color: 'success',
    })

    // Add application dialog
    const addDialog = ref<AddDialog>({
      show: false,
      valid: false,
      loading: false,
      jobUrl: '',
    })

    const addForm = ref()

    // Form validation rules
    const rules = {
      required: (value: string) => !!value || 'This field is required',
      url: (value: string) => {
        if (!value) return true
        const pattern = /^https?:\/\/.+/
        return pattern.test(value) || 'Please enter a valid URL starting with http:// or https://'
      },
    }

    // Table headers
    const headers = [
      { title: 'Position', key: 'position', sortable: true },
      { title: 'Company', key: 'company', sortable: true },
      { title: 'Date Applied', key: 'dateApplied', sortable: true },
      { title: 'Status', key: 'status', sortable: true, width: '200px' },
    ]

    // Status options
    const statusOptions: StatusOption[] = [
      { title: 'Applied', value: 'applied' },
      { title: 'Under Review', value: 'under_review' },
      { title: 'Interview Scheduled', value: 'interview_scheduled' },
      { title: 'Interview Completed', value: 'interview_completed' },
      { title: 'Offer Received', value: 'offer_received' },
      { title: 'Accepted', value: 'accepted' },
      { title: 'Rejected', value: 'rejected' },
      { title: 'Withdrawn', value: 'withdrawn' },
    ]

    // Sample data - Replace with real API call in the future
    const applications = ref<Application[]>([
      {
        id: 1,
        position: 'Senior Software Engineer',
        company: 'TechCorp Inc.',
        dateApplied: '2024-08-20',
        status: 'interview_scheduled',
      },
      {
        id: 2,
        position: 'Full Stack Developer',
        company: 'StartupXYZ',
        dateApplied: '2024-08-18',
        status: 'under_review',
      },
      {
        id: 3,
        position: 'Frontend Developer',
        company: 'Design Studio',
        dateApplied: '2024-08-15',
        status: 'applied',
      },
      {
        id: 4,
        position: 'DevOps Engineer',
        company: 'CloudSystems',
        dateApplied: '2024-08-12',
        status: 'offer_received',
      },
      {
        id: 5,
        position: 'Backend Developer',
        company: 'DataFlow Solutions',
        dateApplied: '2024-08-10',
        status: 'rejected',
      },
      {
        id: 6,
        position: 'Software Architect',
        company: 'Enterprise Corp',
        dateApplied: '2024-08-08',
        status: 'interview_completed',
      },
      {
        id: 7,
        position: 'Mobile Developer',
        company: 'AppMakers',
        dateApplied: '2024-08-05',
        status: 'applied',
      },
      {
        id: 8,
        position: 'AI/ML Engineer',
        company: 'AI Innovations',
        dateApplied: '2024-08-03',
        status: 'under_review',
      },
      {
        id: 9,
        position: 'Product Manager',
        company: 'ProductCo',
        dateApplied: '2024-08-01',
        status: 'withdrawn',
      },
      {
        id: 10,
        position: 'Security Engineer',
        company: 'SecureNet',
        dateApplied: '2024-07-30',
        status: 'accepted',
      },
      {
        id: 11,
        position: 'Data Scientist',
        company: 'Analytics Pro',
        dateApplied: '2024-07-28',
        status: 'applied',
      },
      {
        id: 12,
        position: 'System Administrator',
        company: 'Infrastructure Ltd',
        dateApplied: '2024-07-25',
        status: 'under_review',
      },
      {
        id: 13,
        position: 'QA Engineer',
        company: 'TestingSolutions',
        dateApplied: '2024-07-22',
        status: 'interview_scheduled',
      },
      {
        id: 14,
        position: 'Technical Writer',
        company: 'Documentation Inc',
        dateApplied: '2024-07-20',
        status: 'applied',
      },
      {
        id: 15,
        position: 'UX Designer',
        company: 'Creative Agency',
        dateApplied: '2024-07-18',
        status: 'rejected',
      },
      {
        id: 16,
        position: 'Database Administrator',
        company: 'Data Management Co',
        dateApplied: '2024-07-15',
        status: 'applied',
      },
      {
        id: 17,
        position: 'Cloud Engineer',
        company: 'CloudFirst',
        dateApplied: '2024-07-12',
        status: 'under_review',
      },
      {
        id: 18,
        position: 'Scrum Master',
        company: 'Agile Solutions',
        dateApplied: '2024-07-10',
        status: 'interview_completed',
      },
      {
        id: 19,
        position: 'Network Engineer',
        company: 'NetworkPro',
        dateApplied: '2024-07-08',
        status: 'applied',
      },
      {
        id: 20,
        position: 'Business Analyst',
        company: 'Business Intelligence',
        dateApplied: '2024-07-05',
        status: 'under_review',
      },
      {
        id: 21,
        position: 'Software Tester',
        company: 'Quality Assurance Inc',
        dateApplied: '2024-07-03',
        status: 'applied',
      },
      {
        id: 22,
        position: 'Solutions Architect',
        company: 'Architecture Firm',
        dateApplied: '2024-07-01',
        status: 'interview_scheduled',
      },
      {
        id: 23,
        position: 'Platform Engineer',
        company: 'Platform Systems',
        dateApplied: '2024-06-28',
        status: 'applied',
      },
      {
        id: 24,
        position: 'Site Reliability Engineer',
        company: 'Reliability Corp',
        dateApplied: '2024-06-25',
        status: 'under_review',
      },
      {
        id: 25,
        position: 'Integration Developer',
        company: 'Integration Solutions',
        dateApplied: '2024-06-22',
        status: 'applied',
      },
      {
        id: 26,
        position: 'Performance Engineer',
        company: 'Speed Optimization',
        dateApplied: '2024-06-20',
        status: 'rejected',
      },
      {
        id: 27,
        position: 'Technical Lead',
        company: 'Leadership Tech',
        dateApplied: '2024-06-18',
        status: 'offer_received',
      },
    ])

    // Computed properties
    const filteredApplications = computed(() => {
      if (!searchQuery.value) {
        return applications.value
      }

      const query = searchQuery.value.toLowerCase()
      return applications.value.filter(
        (app) =>
          app.position.toLowerCase().includes(query) ||
          app.company.toLowerCase().includes(query) ||
          app.status.toLowerCase().includes(query)
      )
    })

    const totalItems = computed(() => filteredApplications.value.length)
    const pageCount = computed(() => Math.ceil(totalItems.value / itemsPerPage.value))
    const startItem = computed(() => (currentPage.value - 1) * itemsPerPage.value + 1)
    const endItem = computed(() => Math.min(currentPage.value * itemsPerPage.value, totalItems.value))

    // Methods
    const formatDate = (dateString: string): string => {
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    }

    const getStatusColor = (status: string): string => {
      const colorMap: Record<string, string> = {
        applied: 'blue',
        under_review: 'orange',
        interview_scheduled: 'purple',
        interview_completed: 'indigo',
        offer_received: 'green',
        accepted: 'success',
        rejected: 'error',
        withdrawn: 'grey',
      }
      return colorMap[status] || 'default'
    }

    const updateApplicationStatus = async (applicationId: number, newStatus: string) => {
      try {
        loading.value = true

        // Replace with actual API call when backend is ready
        // await updateApplicationStatusAPI(applicationId, newStatus)

        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 500))

        // Update local data
        const application = applications.value.find((app) => app.id === applicationId)
        if (application) {
          application.status = newStatus
        }

        // Show success message
        snackbar.value = {
          show: true,
          message: 'Application status updated successfully!',
          color: 'success',
        }
      } catch (error) {
        console.error('Failed to update application status:', error)
        snackbar.value = {
          show: true,
          message: 'Failed to update application status. Please try again.',
          color: 'error',
        }
      } finally {
        loading.value = false
      }
    }

    const goToApplicationDetail = (event: Event | null, { item }: { item: Application }) => {
      router.push(`/applications/${item.id}`)
    }

    const handleRowClick = (event: Event, { item }: { item: Application }) => {
      goToApplicationDetail(event, { item })
    }

    const addNewApplication = () => {
      // Open dialog to get job URL from user
      addDialog.value.show = true
      addDialog.value.jobUrl = ''
      addDialog.value.valid = false
    }

    const closeAddDialog = () => {
      addDialog.value.show = false
      addDialog.value.jobUrl = ''
      addDialog.value.valid = false
    }

    const fetchJobFromUrl = async () => {
      if (!addForm.value || !addDialog.value.valid) return

      try {
        addDialog.value.loading = true

        console.log('Fetching job details from URL:', addDialog.value.jobUrl)

        // Replace with actual API call when backend is ready
        // const jobDetails = await fetchJobDetailsFromUrl(addDialog.value.jobUrl)

        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 2000))

        // Close dialog
        closeAddDialog()

        snackbar.value = {
          show: true,
          message: 'Job details fetched successfully! Raw content available for review.',
          color: 'success',
        }

        // Navigate to new application detail page when implemented
        // router.push(`/applications/new?url=${encodeURIComponent(addDialog.value.jobUrl)}`)
      } catch (error) {
        console.error('Failed to fetch job details:', error)
        snackbar.value = {
          show: true,
          message: 'Failed to fetch job details. Please check the URL and try again.',
          color: 'error',
        }
      } finally {
        addDialog.value.loading = false
      }
    }

    const handleSearch = () => {
      // Reset to first page when searching
      currentPage.value = 1
    }

    // Lifecycle
    onMounted(() => {
      // Replace with actual API call to fetch applications when backend is ready
      // loadApplications()
    })

    // Return all the reactive properties and methods
    return {
      // Reactive data
      loading,
      searchQuery,
      currentPage,
      itemsPerPage,
      snackbar,
      addDialog,
      addForm,
      applications,

      // Configuration
      rules,
      headers,
      statusOptions,

      // Computed properties
      filteredApplications,
      totalItems,
      pageCount,
      startItem,
      endItem,

      // Methods
      formatDate,
      getStatusColor,
      updateApplicationStatus,
      goToApplicationDetail,
      handleRowClick,
      addNewApplication,
      closeAddDialog,
      fetchJobFromUrl,
      handleSearch,
    }
  }
})

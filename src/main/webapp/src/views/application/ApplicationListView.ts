import { ref, onMounted, defineComponent, inject, type Ref } from 'vue'
import { useRouter } from 'vue-router'
import { type IApplication, type IApplicationProjection, getApplicationStatusDisplay, getApplicationStatusColor, getApplicationStatusIcon, type IDataTableOptions, type ISortBy } from '../../models'
import ApplicationService from '../../services/application.service'

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
  methods: {
    getApplicationStatusDisplay,
    getApplicationStatusColor,
    getApplicationStatusIcon
  },
  setup() {
    const applicationService = inject('applicationService', () => new ApplicationService())
    const router = useRouter()

    // Reactive data
    const applications = ref<IApplicationProjection[]>([])
    const isFetching = ref(false)
    const totalItems = ref(0)
    const page: Ref<number> = ref(1)
    const itemsPerPage: Ref<number> = ref(10)
    const sortBy: Ref<Array<ISortBy>> = ref([])

    // Table headers
    const headers = [
      { title: 'Title', key: 'title', sortable: true },
      { title: 'Company', key: 'companyName', sortable: true },
      { title: 'Date Applied', key: 'appliedAt', sortable: true },
      { title: 'Status', key: 'status', sortable: true, width: '200px' },
    ]

    const itemsPerPageOptions = [10, 25, 50, 100]

    // Utility functions
    const sort = (): Array<string> => {
      if (sortBy.value.length > 0) {
        return sortBy.value.map(s => `${s.key},${s.order}`)
      }
      return ['appliedAt,desc']
    }

    const search = (): string => {
      return ''
    }

    const retrieveApplications = async () => {
      isFetching.value = true
      try {
        const response = await applicationService().retrieveList({
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          search: search(),
        })
        applications.value = response.data
        totalItems.value = parseInt(response.headers['x-total-count'], 10)
      } catch (error) {
        console.error('Error fetching applications:', error)
        snackbar.value.message = 'Error fetching applications'
        snackbar.value.color = 'error'
        snackbar.value.show = true
      } finally {
        isFetching.value = false
      }
    }

    const handleUpdateOptions = async (options: IDataTableOptions) => {
      page.value = options.page
      itemsPerPage.value = options.itemsPerPage
      sortBy.value = options.sortBy
      await retrieveApplications()
    }

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

    // Methods
    const formatDate = (dateInput: string | Date): string => {
      const date = dateInput instanceof Date ? dateInput : new Date(dateInput)
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

    const goToApplicationDetail = (event: Event | null, { item }: { item: IApplication }) => {
      router.push(`/applications/${item.id}`)
    }

    const handleRowClick = (event: Event, { item }: { item: IApplication }) => {
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
      // TODO: Implement job fetching from URL
      addDialog.value.loading = true
      try {
        // Placeholder - implement actual job fetching logic
        console.log('Fetching job from URL:', addDialog.value.jobUrl)
        // Close dialog after successful fetch
        closeAddDialog()
      } catch (error) {
        console.error('Error fetching job:', error)
      } finally {
        addDialog.value.loading = false
      }
    }

    // Lifecycle
    onMounted(() => {
      // Replace with actual API call to fetch applications when backend is ready
      // retrieveApplications()
    })

    // Return all the reactive properties and methods
    return {
      applications,
      totalItems,
      isFetching,

      headers,
      itemsPerPageOptions,

      page,
      itemsPerPage,

      // Reactive data
      snackbar,
      addDialog,
      addForm,

      // Configuration
      rules,

      // Methods
      formatDate,
      getStatusColor,
      goToApplicationDetail,
      handleRowClick,
      addNewApplication,
      closeAddDialog,
      fetchJobFromUrl,
      handleUpdateOptions,
    }
  }
})

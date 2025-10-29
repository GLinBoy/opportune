import { ref, onMounted, defineComponent, inject, type Ref } from 'vue'
import { useRouter } from 'vue-router'
import { type IApplication, type IApplicationProjection, type ApplicationStatus, getApplicationStatusDisplay, getApplicationStatusColor, getApplicationStatusIcon, type IDataTableOptions, type ISortBy } from '../../models'
import ApplicationService from '../../services/application.service'
import SearchService from '../../services/search.service'

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
    const searchService = inject('searchService', () => new SearchService())
    const router = useRouter()

    // Reactive data
    const applications = ref<IApplicationProjection[]>([])
    const isFetching = ref(false)
    const totalItems = ref(0)
    const page: Ref<number> = ref(1)
    const itemsPerPage: Ref<number> = ref(10)
    const sortBy: Ref<Array<ISortBy>> = ref([])
    const searchQuery = ref<string>('')
    const searchResults = ref<Array<{ id: string; name: string; status: string; type: string }>>([])
    const isSearching = ref(false)
    const selectedApplication = ref<{ id: string; name: string; status: string; type: string } | null>(null)

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
      return ['createdDate,desc']
    }

    const search = (): string => {
      return searchQuery.value.trim()
    }

    const retrieveApplications = async () => {
      isFetching.value = true
      try {
        const searchText = search()

        // Always use the regular list endpoint for the table
        const response = await applicationService().retrieveList({
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          search: searchText,
        })
        applications.value = response.data
        totalItems.value = Number.parseInt(response.headers['x-total-count'], 10)
      } catch (error) {
        console.error('Error fetching applications:', error)
        snackbar.value.message = 'Error fetching applications'
        snackbar.value.color = 'error'
        snackbar.value.show = true
      } finally {
        isFetching.value = false
      }
    }

    // Search applications for autocomplete
    const searchApplicationsForAutocomplete = async (query: string) => {
      if (!query || query.trim().length < 2) {
        searchResults.value = []
        return
      }

      isSearching.value = true
      try {
        const response = await searchService().searchApplications(query.trim(), {
          page: 0,
          size: 10, // Limit autocomplete results
        })
        searchResults.value = response.data
      } catch (error) {
        console.error('Error searching applications:', error)
        searchResults.value = []
      } finally {
        isSearching.value = false
      }
    }

    const handleUpdateOptions = async (options: IDataTableOptions) => {
      page.value = options.page
      itemsPerPage.value = options.itemsPerPage
      sortBy.value = options.sortBy
      await retrieveApplications()
    }

    // Debounced search handler for autocomplete
    let searchTimeout: ReturnType<typeof setTimeout> | null = null
    const handleSearchInput = (value: string) => {
      searchQuery.value = value || ''

      // Clear existing timeout
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }

      // Debounce the search for autocomplete
      searchTimeout = setTimeout(async () => {
        await searchApplicationsForAutocomplete(searchQuery.value)
      }, 300) // Wait 300ms after user stops typing
    }

    // Handle application selection from autocomplete
    const handleApplicationSelect = (selected: { id: string; name: string; status: string; type: string } | null) => {
      if (selected) {
        // Navigate to the selected application
        router.push(`/applications/${selected.id}`)
      }
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

    // Helper methods for search results (to handle string to enum conversion)
    const getSearchResultStatusColor = (status: string) => {
      return getApplicationStatusColor(status as ApplicationStatus)
    }

    const getSearchResultStatusIcon = (status: string) => {
      return getApplicationStatusIcon(status as ApplicationStatus)
    }

    // Return all the reactive properties and methods
    return {
      applications,
      totalItems,
      isFetching,

      headers,
      itemsPerPageOptions,

      page,
      itemsPerPage,
      searchQuery,
      searchResults,
      isSearching,
      selectedApplication,

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
      handleSearchInput,
      handleApplicationSelect,
      getSearchResultStatusColor,
      getSearchResultStatusIcon,
    }
  }
})

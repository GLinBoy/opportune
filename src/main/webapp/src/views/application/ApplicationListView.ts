import { ref, onMounted, defineComponent, inject, type Ref } from 'vue'
import { useRouter } from 'vue-router'
import { Application, type IApplication, type IApplicationProjection, type ApplicationStatus, getApplicationStatusDisplay, getApplicationStatusColor, getApplicationStatusIcon, type IDataTableOptions, type ISortBy } from '../../models'
import ApplicationService from '../../services/application.service'
import SearchService from '../../services/search.service'
import AddApplicationByUrlForm from '../../components/application/AddApplicationByUrlForm.vue'
import AddApplicationManualForm from '../../components/application/AddApplicationManualForm.vue'
import ConfirmDialog from '../../components/ConfirmDialog.vue'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}

export interface AddDialog {
  show: boolean
  loading: boolean
  activeTab: string
}


export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ApplicationListView',
  components: {
    AddApplicationByUrlForm,
    AddApplicationManualForm,
    ConfirmDialog,
  },
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
      { title: 'Last Modified', key: 'lastModifiedDate', sortable: true },
      { title: 'Status', key: 'status', sortable: true, width: '200px' },
      { title: 'Actions', key: 'actions', sortable: false, align: 'end' as const },
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
      loading: false,
      activeTab: 'url',
    })

    const urlFormRef = ref()
    const manualFormRef = ref()

    const failedUrl = ref<string>('')

    // Delete confirmation dialog
    const confirmDeleteDialog = ref(false)
    const applicationToDelete = ref<IApplicationProjection | null>(null)
    const isDeleting = ref(false)

    // Methods
    const formatDate = (dateInput: string | Date): string => {
      const date = dateInput instanceof Date ? dateInput : new Date(dateInput)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    }

    const goToApplicationDetail = (event: Event | null, { item }: { item: IApplication }) => {
      router.push(`/applications/${item.id}`)
    }

    const handleRowClick = (event: Event, { item }: { item: IApplication }) => {
      goToApplicationDetail(event, { item })
    }

    const addNewApplication = () => {
      addDialog.value.show = true
      addDialog.value.activeTab = 'url'
      failedUrl.value = ''
    }

    const closeAddDialog = () => {
      addDialog.value.show = false
      addDialog.value.loading = false
      failedUrl.value = ''
      // Reset forms
      urlFormRef.value?.reset()
      manualFormRef.value?.reset()
    }

    // Delete methods
    const confirmDelete = (item: IApplicationProjection) => {
      applicationToDelete.value = item
      confirmDeleteDialog.value = true
    }

    const closeDeleteDialog = () => {
      confirmDeleteDialog.value = false
      applicationToDelete.value = null
    }

    const performDelete = async () => {
      if (!applicationToDelete.value?.id) {
        closeDeleteDialog()
        return
      }
      isDeleting.value = true
      try {
        await applicationService().delete(String(applicationToDelete.value.id))
        await retrieveApplications()
        snackbar.value.message = `Deleted ${applicationToDelete.value.title || 'application'} successfully.`
        snackbar.value.color = 'success'
        snackbar.value.show = true
      } catch (err) {
        console.error('Failed to delete application:', err)
        snackbar.value.message = 'Failed to delete application. Please try again.'
        snackbar.value.color = 'error'
        snackbar.value.show = true
      } finally {
        isDeleting.value = false
        closeDeleteDialog()
      }
    }

    const handleUrlSubmit = async (data: { url: string }) => {
      addDialog.value.loading = true
      try {
        const createdApplication = await applicationService().submitUrl({ url: data.url })

        snackbar.value.message = 'Application created successfully!'
        snackbar.value.color = 'success'
        snackbar.value.show = true
        closeAddDialog()

        // Redirect to the created application
        if (createdApplication.id) {
          router.push(`/applications/${createdApplication.id}`)
        }
      } catch (error) {
        console.error('Error creating application from URL:', error)

        // Store the failed URL to pre-fill in manual form
        failedUrl.value = data.url

        // Show error and suggest switching to manual entry
        snackbar.value.message = 'Failed to fetch job details from URL. Please try manual entry.'
        snackbar.value.color = 'warning'
        snackbar.value.show = true

        // Switch to manual entry tab
        addDialog.value.activeTab = 'manual'
      } finally {
        addDialog.value.loading = false
      }
    }

    const handleManualSubmit = async (data: {
      url: string
      title: string
      location: string
      salary: string
      rawContent: string
      companyId?: string
    }) => {
      addDialog.value.loading = true
      try {
        // Create an Application instance
        const application = new Application(
          undefined, // id
          data.url || undefined,
          data.title,
          data.location || undefined,
          undefined, // appliedAt
          data.salary || undefined,
          undefined, // note
          data.rawContent,
          undefined, // description
          undefined, // coverLetter
          undefined, // resumeInsights
          null, // status
          data.companyId || undefined,
          undefined, // profileId
          undefined, // resumeId
          undefined, // createdDate
          undefined  // lastModifiedDate
        )

        const createdApplication = await applicationService().create(application)

        snackbar.value.message = 'Application created successfully!'
        snackbar.value.color = 'success'
        snackbar.value.show = true
        closeAddDialog()

        // Redirect to the created application
        if (createdApplication.id) {
          router.push(`/applications/${createdApplication.id}`)
        }
      } catch (error) {
        console.error('Error creating application:', error)
        snackbar.value.message = 'Error creating application. Please try again.'
        snackbar.value.color = 'error'
        snackbar.value.show = true
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
      urlFormRef,
      manualFormRef,
      failedUrl,

      // Methods
      formatDate,
      goToApplicationDetail,
      handleRowClick,
      addNewApplication,
      closeAddDialog,
      handleUrlSubmit,
      handleManualSubmit,
      handleUpdateOptions,
      handleSearchInput,
      handleApplicationSelect,
      getSearchResultStatusColor,
      getSearchResultStatusIcon,
      // Delete
      confirmDeleteDialog,
      applicationToDelete,
      isDeleting,
      confirmDelete,
      closeDeleteDialog,
      performDelete,
    }
  }
})

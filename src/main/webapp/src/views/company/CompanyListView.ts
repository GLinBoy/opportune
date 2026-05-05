import { ref, defineComponent, inject, type Ref } from 'vue'
import { useRouter } from 'vue-router'
import { type ICompany, getCompanyStatusDisplay, getCompanyStatusColor, getCompanyStatusIcon, type IDataTableOptions, type ISortBy } from '../../models'
import CompanyService from '../../services/company.service'
import SearchService from '../../services/search.service'
import CompanyForm from '../../components/company/CompanyForm.vue'
import CompanyLogo from '../../components/company/CompanyLogo.vue'

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyListView',
  components: {
    CompanyForm,
    CompanyLogo
  },
  methods: {
    getCompanyStatusDisplay,
    getCompanyStatusColor,
    getCompanyStatusIcon
  },
  setup() {
    // Snackbar state
    const snackbar = ref<{ show: boolean; message: string; color: 'success' | 'error' }>({ show: false, message: '', color: 'success' })
    const showSnackbar = (message: string, color: 'success' | 'error' = 'success') => {
      snackbar.value = { show: true, message, color }
    }
    // Services and dependencies
    const companyService = inject('companyService', () => new CompanyService())
    const searchService = inject('searchService', () => new SearchService())
    const router = useRouter()

    // Data table state
    const companies: Ref<ICompany[]> = ref([])
    const isFetching = ref(false)
    const totalItems = ref(0)
    const page: Ref<number> = ref(1)
    const itemsPerPage: Ref<number> = ref(10)
    const sortBy: Ref<Array<ISortBy>> = ref([])

    // Search/Autocomplete state
    const searchQuery = ref<string>('')
    const searchResults = ref<Array<{ id: string; name: string; status: string; type: string }>>([])
    const isSearching = ref(false)
    const selectedCompany = ref<{ id: string; name: string; status: string; type: string } | null>(null)

    // Dialog state
    const addNewCompanyDialog = ref(false)
    const newCompany: Ref<ICompany> = ref({})
    const isCreating = ref(false)

    // Configuration
    const headers = ref([
      { title: 'Company', key: 'name', sortable: true },
      { title: 'Industry', key: 'industry', sortable: true },
      { title: 'Location', key: 'location', sortable: true },
      { title: 'Last Modified', key: 'lastModifiedDate', sortable: true },
      { title: 'Status', key: 'status', sortable: true },
      { title: 'Actions', key: 'actions', sortable: false, align: 'end' as const },
    ])

    const itemsPerPageOptions = [10, 25, 50, 100]

    // Utility functions
    const formatDate = (dateInput: string | Date): string => {
      const date = dateInput instanceof Date ? dateInput : new Date(dateInput)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    }

    const sort = (): Array<string> => {
      if (sortBy.value.length > 0) {
        return sortBy.value.map(s => `${s.key},${s.order}`)
      }
      return ['createdDate,desc']
    }

    const search = (): string => {
      return searchQuery.value.trim()
    }

    // Data fetching
    const retrieveCompanies = async () => {
      isFetching.value = true
      try {
        const searchText = search()

        // Always use the regular list endpoint for the table
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          query: searchText,
        }
        const res = await companyService().retrieve(paginationQuery)
        totalItems.value = Number(res.headers['x-total-count'])
        companies.value = res.data
      } catch (err) {
        console.error('Failed to retrieve companies:', err)
      } finally {
        isFetching.value = false
      }
    }

    // Delete state and actions
    const confirmDeleteDialog = ref(false)
    const companyToDelete: Ref<ICompany | null> = ref(null)
    const isDeleting = ref(false)

    const confirmDelete = (item: ICompany) => {
      companyToDelete.value = item
      confirmDeleteDialog.value = true
    }

    const closeDeleteDialog = () => {
      confirmDeleteDialog.value = false
      companyToDelete.value = null
    }

    const performDelete = async () => {
      if (!companyToDelete.value?.id) {
        closeDeleteDialog()
        return
      }
      isDeleting.value = true
      try {
        await companyService().delete(String(companyToDelete.value.id))
        await retrieveCompanies()
        showSnackbar(`Deleted ${companyToDelete.value.name || 'company'} successfully.`, 'success')
      } catch (err) {
        console.error('Failed to delete company:', err)
        showSnackbar('Failed to delete company. Please try again.', 'error')
      } finally {
        isDeleting.value = false
        closeDeleteDialog()
      }
    }

    // Search companies for autocomplete
    const searchCompaniesForAutocomplete = async (query: string) => {
      if (!query || query.trim().length < 2) {
        searchResults.value = []
        return
      }

      isSearching.value = true
      try {
        const response = await searchService().searchCompanies(query.trim(), {
          page: 0,
          size: 10, // Limit autocomplete results
        })
        searchResults.value = response.data
      } catch (error) {
        console.error('Error searching companies:', error)
        searchResults.value = []
      } finally {
        isSearching.value = false
      }
    }

    // Event handlers - Data table
    const handleUpdateOptions = async (options: IDataTableOptions): Promise<void> => {
      page.value = options.page
      itemsPerPage.value = options.itemsPerPage
      sortBy.value = options.sortBy
      await retrieveCompanies()
    }

    const handleRowClick = (event: Event, { item }: { item: ICompany }) => {
      goToCompanyDetail(event, { item })
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
        await searchCompaniesForAutocomplete(searchQuery.value)
      }, 300) // Wait 300ms after user stops typing
    }

    // Handle company selection from autocomplete
    const handleCompanySelect = (selected: { id: string; name: string; status: string; type: string } | null) => {
      if (selected) {
        // Navigate to the selected company
        router.push(`/companies/${selected.id}`)
      }
    }

    const handleSearch = () => {
      retrieveCompanies()
    }

    // Navigation
    const goToCompanyDetail = (event: Event | null, { item }: { item: ICompany }) => {
      router.push(`/companies/${item.id}`)
    }

    // Dialog handlers
    const addNewCompany = () => {
      newCompany.value = {}
      addNewCompanyDialog.value = true
    }

    const closeDialog = () => {
      addNewCompanyDialog.value = false
      newCompany.value = {}
    }

    const createCompany = async () => {
      isCreating.value = true
      try {
        const createdCompany = await companyService().create(newCompany.value)
        addNewCompanyDialog.value = false
        newCompany.value = {}
        // Refresh the list to include the new company
        await retrieveCompanies()
        // Redirect to the new company's detail page
        router.push(`/companies/${createdCompany.id}`)
      } catch (err) {
        console.error('Failed to create company:', err)
      } finally {
        isCreating.value = false
      }
    }

    return {
      // Data
      companies,
      totalItems,
      isFetching,

      // Table configuration
      headers,
      itemsPerPageOptions,

      // Table state
      page,
      itemsPerPage,

      // Search/Autocomplete state
      searchQuery,
      searchResults,
      isSearching,
      selectedCompany,

      // Event handlers
      handleUpdateOptions,
      handleRowClick,
      handleSearch,
      handleSearchInput,
      handleCompanySelect,

      // Dialog state
      addNewCompanyDialog,
      newCompany,
      isCreating,
      isDeleting,

      // Dialog actions
      addNewCompany,
      closeDialog,
      createCompany,
      // Delete dialog
      confirmDeleteDialog,
      companyToDelete,
      confirmDelete,
      closeDeleteDialog,
      performDelete,
      // Snackbar
      snackbar,
      showSnackbar,
      formatDate,
    }
  }
})

import { ref, defineComponent, inject, type Ref } from 'vue'
import { useRouter } from 'vue-router'
import { type ICompany, getCompanyStatusDisplay, getCompanyStatusColor, getCompanyStatusIcon, type IDataTableOptions, type ISortBy } from '../../models'
import CompanyService from '../../services/company.service'
import CompanyForm from '../../components/CompanyForm.vue'

import defaultCompanyLogo from '@/assets/images/office-building.png'

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyListView',
  components: {
    CompanyForm
  },
  methods: {
    getCompanyStatusDisplay,
    getCompanyStatusColor,
    getCompanyStatusIcon
  },
  setup() {
    // Services and dependencies
    const companyService = inject('companyService', () => new CompanyService())
    const router = useRouter()

    // Data table state
    const companies: Ref<ICompany[]> = ref([])
    const isFetching = ref(false)
    const totalItems = ref(0)
    const page: Ref<number> = ref(1)
    const itemsPerPage: Ref<number> = ref(10)
    const sortBy: Ref<Array<ISortBy>> = ref([])

    // Dialog state
    const addNewCompanyDialog = ref(false)
    const newCompany: Ref<ICompany> = ref({})
    const isCreating = ref(false)

    // Configuration
    const headers = ref([
      { title: 'Company', key: 'name', sortable: true },
      { title: 'Industry', key: 'industry', sortable: true },
      { title: 'Location', key: 'location', sortable: true },
      { title: 'Status', key: 'status', sortable: true },
      { title: 'Description', key: 'description', sortable: true },
    ])

    const itemsPerPageOptions = [10, 25, 50, 100]

    // Utility functions
    const sort = (): Array<string> => {
      if (sortBy.value.length > 0) {
        return sortBy.value.map(s => `${s.key},${s.order}`)
      }
      return ['createdDate,asc']
    }

    const search = (): string => {
      return ''
    }

    // Data fetching
    const retrieveCompanies = async () => {
      isFetching.value = true
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          query: search(),
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
      defaultCompanyLogo,

      // Table state
      page,
      itemsPerPage,

      // Event handlers
      handleUpdateOptions,
      handleRowClick,
      handleSearch,

      // Dialog state
      addNewCompanyDialog,
      newCompany,
      isCreating,

      // Dialog actions
      addNewCompany,
      closeDialog,
      createCompany,
    }
  }
})

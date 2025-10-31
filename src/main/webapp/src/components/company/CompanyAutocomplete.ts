import { ref, defineComponent, inject, watch } from 'vue'
import type { ICompany } from '../../models'
import { CompanyService } from '../../services'

export default defineComponent({
    compatConfig: { MODE: 3 },
    name: 'CompanyAutocomplete',
    props: {
        modelValue: {
            type: Object as () => ICompany | null,
            default: null
        },
        label: {
            type: String,
            default: 'Company'
        },
        variant: {
            type: String as () => 'outlined' | 'filled' | 'underlined' | 'solo' | 'plain',
            default: 'outlined'
        },
        prependInnerIcon: {
            type: String,
            default: 'mdi-domain'
        },
        rules: {
            type: Array as () => Array<(value: unknown) => boolean | string>,
            default: () => []
        },
        clearable: {
            type: Boolean,
            default: false
        },
        disabled: {
            type: Boolean,
            default: false
        },
        // Initial company to include in the list (useful when editing)
        initialCompany: {
            type: Object as () => ICompany | null,
            default: null
        }
    },
    emits: ['update:modelValue'],
    setup(props, { emit }) {
        const companyService = inject('companyService', () => new CompanyService())

        // State
        const companies = ref<ICompany[]>([])
        const search = ref<string>('')
        const isCompanyLoading = ref(false)

        // Utility functions
        const sort = (): Array<string> => {
            return ['createdDate,asc']
        }

        const generateSearchQuery = (): string => {
            // Strip spaces and generate RSQL query like: name=ilike=ant
            const cleanInput = search.value.trim().split(/\s+/).join('')
            if (cleanInput) {
                return `name=ilike=${cleanInput}`
            }
            return ''
        }

        const loadCompanies = async () => {
            try {
                isCompanyLoading.value = true
                const paginationQuery = {
                    page: 0,
                    size: 5,
                    sort: sort(),
                    query: generateSearchQuery(),
                }
                const res = await companyService().retrieve(paginationQuery)

                // If there's an initial company and no search query, include it in the list
                if (props.initialCompany && !search.value.trim()) {
                    // Add initial company at the top if not already in results
                    const initialCompanyExists = res.data.some(c => c.id === props.initialCompany?.id)
                    if (initialCompanyExists) {
                        companies.value = res.data
                    } else {
                        companies.value = [props.initialCompany, ...res.data]
                    }
                } else {
                    // During search, just show search results
                    companies.value = res.data
                }
            } catch (err) {
                console.error('Failed to retrieve companies:', err)
                companies.value = []
            } finally {
                isCompanyLoading.value = false
            }
        }

        // Debounced search handler for company autocomplete
        let companySearchTimeout: ReturnType<typeof setTimeout> | null = null
        const handleCompanySearch = (value: string) => {
            search.value = value || ''

            // Clear existing timeout
            if (companySearchTimeout) {
                clearTimeout(companySearchTimeout)
            }

            // Check if search input matches the currently selected company name
            const currentCompanyName = props.modelValue?.name || ''
            const searchInput = search.value.trim()

            // If search input matches current company name, don't call API
            if (searchInput === currentCompanyName) {
                return
            }

            // Debounce the search
            companySearchTimeout = setTimeout(async () => {
                await loadCompanies()
            }, 300) // Wait 300ms after user stops typing
        }

        // Handle company selection
        const handleCompanySelect = (company: ICompany | null) => {
            emit('update:modelValue', company)
            // Clear search input when company is selected
            if (company) {
                search.value = ''
            }
        }

        // Watch for initial company changes to populate the list
        watch(() => props.initialCompany, (newCompany) => {
            if (newCompany && companies.value.length === 0) {
                companies.value = [newCompany]
            }
        }, { immediate: true })

        return {
            companies,
            search,
            isCompanyLoading,
            handleCompanySearch,
            handleCompanySelect,
        }
    }
})

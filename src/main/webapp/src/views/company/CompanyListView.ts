import { ref, onMounted, defineComponent, inject, type Ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { type ICompany } from '../../models'
import CompanyService from '../../services/company.service'

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyListView',
  setup() {
    const companyService = inject('companyService', () => new CompanyService())
    const router = useRouter()

    const itemsPerPage = ref(20)
    const queryCount: Ref<number> = ref(0)
    const page: Ref<number> = ref(1)
    const propOrder = ref('name')
    const reverse = ref(false)
    const totalItems = ref(0)
    const currentSearch: Ref<string> = ref('')

    const headers = ref([
      { title: 'Company', key: 'name' },
      { title: 'Industry', key: 'industry' },
      { title: 'Location', key: 'location' },
      { title: 'Status', key: 'status' },
      { title: 'Description', key: 'description' },
    ])

    const companies: Ref<ICompany[]> = ref([])

    const isFetching = ref(false);

    const clear = () => {
      page.value = 1;
    };

    const sort = (): Array<any> => {
      const result = [`${propOrder.value},${reverse.value ? 'desc' : 'asc'}`];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    const search = (): string => {
      let result = '';
      if (currentSearch.value) {
        result = `receiver==*${currentSearch.value}* or subject==*${currentSearch.value}* or content==*${currentSearch.value}* or createdBy==*${currentSearch.value}*`;
      }
      return result;
    };

    const retrieveCompanies = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          query: search(),
        };
        const res = await companyService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        companies.value = res.data;
      } catch (err) {
        // TODO: Toast error
        console.error(err);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCompanies();
    };

    const goToCompanyDetail = (event: Event | null, { item }: { item: ICompany }) => {
      router.push(`/companies/${item.id}`)
    }

    const handleRowClick = (event: Event, { item }: { item: ICompany }) => {
      goToCompanyDetail(event, { item })
    }

    onMounted(async () => {
      await retrieveCompanies();
    });

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder
    }

    watch([propOrder, reverse], async () => {
      if (page.value === 1) {
        // first page, retrieve new data
        await retrieveCompanies();
      } else {
        // reset the pagination
        clear();
      }
    });

    watch(page, async () => {
      await retrieveCompanies();
    });

    const handleSearch = () => {
      retrieveCompanies();
    };

    return {
      companies,
      handleSyncList,
      isFetching,
      retrieveCompanies,
      clear,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      currentSearch,
      handleSearch,
      headers,
      handleRowClick,
    }
  }
})

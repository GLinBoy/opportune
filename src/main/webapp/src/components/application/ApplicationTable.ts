import { defineComponent, type PropType } from 'vue'
import { useRouter } from 'vue-router'
import {
  getApplicationStatusDisplay,
  getApplicationStatusColor,
  getApplicationStatusIcon,
} from '../../models/enumerations/application-status.model'
import { type IApplicationProjection } from '../../models/application.model'

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ApplicationTable',
  emits: ['delete'],
  props: {
    items: {
      type: Array as PropType<IApplicationProjection[]>,
      default: () => [],
    },
    loading: {
      type: Boolean,
      default: false,
    },
    viewAllTo: {
      type: String,
      default: null,
    },
    showCompanyColumn: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    getApplicationStatusDisplay,
    getApplicationStatusColor,
    getApplicationStatusIcon,
  },
  setup(props) {
    const router = useRouter()

    const baseHeaders = [
      { title: 'Title', key: 'title', sortable: true },
      { title: 'Date Applied', key: 'appliedAt', sortable: true },
      { title: 'Last Modified', key: 'lastModifiedDate', sortable: true },
      { title: 'Status', key: 'status', sortable: true, width: '200px' },
      { title: 'Actions', key: 'actions', sortable: false, align: 'end' as const },
    ]

    const companyHeader = { title: 'Company', key: 'companyName', sortable: true }

    const headers = props.showCompanyColumn
      ? [baseHeaders[0], companyHeader, ...baseHeaders.slice(1)]
      : baseHeaders

    const formatDate = (dateInput: string | Date): string => {
      const date = dateInput instanceof Date ? dateInput : new Date(dateInput)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    }

    const handleRowClick = (_event: Event, { item }: { item: IApplicationProjection }) => {
      router.push(`/applications/${item.id}`)
    }

    return {
      headers,
      formatDate,
      handleRowClick,
    }
  },
})

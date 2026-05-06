import { defineComponent, type PropType } from 'vue'

export interface IMetadataItem {
  id?: string
  metaName?: string
  metaValue?: string
}

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MetadataTable',
  emits: ['delete', 'edit'],
  props: {
    items: {
      type: Array as PropType<IMetadataItem[]>,
      default: () => [],
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, { emit }) {
    const headers = [
      { title: 'Key', key: 'metaName', sortable: true },
      { title: 'Value', key: 'metaValue', sortable: false },
      { title: 'Actions', key: 'actions', sortable: false, align: 'end' as const },
    ]

    const handleRowClick = (_event: Event, { item }: { item: IMetadataItem }) => {
      emit('edit', item)
    }

    return { headers, handleRowClick }
  },
})

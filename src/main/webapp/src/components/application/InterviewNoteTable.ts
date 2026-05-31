import { defineComponent, type PropType } from 'vue'
import { type IInterviewNote } from '../../models/interview-note.model'

export default defineComponent({
    compatConfig: { MODE: 3 },
    name: 'InterviewNoteTable',
    emits: ['edit', 'delete'],
    props: {
        items: {
            type: Array as PropType<IInterviewNote[]>,
            default: () => [],
        },
        loading: {
            type: Boolean,
            default: false,
        },
        attachmentCounts: {
            type: Object as PropType<Record<string, number>>,
            default: () => ({}),
        },
    },
    setup(props, { emit: _emit }) {
        const headers = [
            { title: 'Date', key: 'date', sortable: true },
            { title: 'Notes', key: 'notes', sortable: false },
            { title: 'Attachments', key: 'attachments', sortable: false, align: 'center' as const },
            { title: 'Actions', key: 'actions', sortable: false, align: 'end' as const },
        ]

        const sortBy = [{ key: 'date', order: 'desc' as const }]

        const formatDate = (value?: Date | string): string => {
            if (!value) return '—'
            const d = value instanceof Date ? value : new Date(value)
            if (Number.isNaN(d.getTime())) return '—'
            return d.toLocaleString(undefined, {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
            })
        }

        const truncateNotes = (notes?: string): string => {
            if (!notes) return '—'
            return notes.length > 100 ? notes.slice(0, 100) + '…' : notes
        }

        const getAttachmentCount = (id?: string): number => {
            if (!id) return 0
            return props.attachmentCounts[id] ?? 0
        }

        const handleRowClick = (_event: Event, { item }: { item: IInterviewNote }) => {
            _emit('edit', item)
        }

        return { headers, sortBy, formatDate, truncateNotes, getAttachmentCount, handleRowClick }
    },
})

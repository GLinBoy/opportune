import { defineComponent, ref, computed, watch, type PropType } from 'vue'
import { type IInterviewNote } from '../../models/interview-note.model'
import { type IInterviewAttachment } from '../../models/interview-attachment.model'
import InterviewNoteService from '../../services/interview-note.service'
import InterviewAttachmentService from '../../services/interview-attachment.service'
import { useToastStore } from '../../stores/toast'
import MdEditor from '../markdown/MdEditor.vue'

const noteService = new InterviewNoteService()
const attachmentService = new InterviewAttachmentService()

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InterviewNotePanel',
  components: { MdEditor },
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
    applicationId: {
      type: String,
      required: true,
    },
    interviewNote: {
      type: Object as PropType<IInterviewNote | null>,
      default: null,
    },
  },
  emits: ['update:modelValue', 'saved'],
  setup(props, { emit }) {
    const toastStore = useToastStore()

    // Working copy of the note (updated after create/update)
    const currentNote = ref<IInterviewNote | null>(null)

    // Flat form fields (avoid deep watchers on date)
    const formDate = ref<string>('')
    const formNotes = ref<string>('')

    const attachments = ref<IInterviewAttachment[]>([])
    const loading = ref(false)
    const attachmentsLoading = ref(false)

    // Template ref for the hidden file input
    const fileInputRef = ref<HTMLInputElement | null>(null)

    // v-model forwarding
    const isOpen = computed({
      get: () => props.modelValue,
      set: (val: boolean) => emit('update:modelValue', val),
    })

    // True once the note has been persisted (create completed or edit mode)
    const hasId = computed(() => !!currentNote.value?.id)

    const panelTitle = computed(() =>
      hasId.value ? 'Edit Interview Note' : 'New Interview Note'
    )

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Convert a Date (or ISO string from API) to the value expected by <input type="datetime-local"> */
    const toDatetimeLocal = (value?: Date | string): string => {
      if (!value) return ''
      const d = value instanceof Date ? value : new Date(value as string)
      if (isNaN(d.getTime())) return ''
      const pad = (n: number) => String(n).padStart(2, '0')
      return (
        `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}` +
        `T${pad(d.getHours())}:${pad(d.getMinutes())}`
      )
    }

    const formatFileSize = (bytes?: number): string => {
      if (!bytes) return '0 B'
      if (bytes < 1024) return `${bytes} B`
      if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
      return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
    }

    // ── Form initialisation ───────────────────────────────────────────────────

    const initForm = (note: IInterviewNote | null) => {
      currentNote.value = note ? { ...note } : null
      formDate.value = note?.date ? toDatetimeLocal(note.date) : ''
      formNotes.value = note?.notes ?? ''
    }

    // ── Attachments ───────────────────────────────────────────────────────────

    const loadAttachments = async () => {
      if (!currentNote.value?.id) return
      attachmentsLoading.value = true
      try {
        const list = await attachmentService.retrieve(
          props.applicationId,
          currentNote.value.id
        )
        attachments.value = list
      } catch {
        toastStore.error('Failed to load attachments')
      } finally {
        attachmentsLoading.value = false
      }
    }

    const triggerFileInput = () => {
      fileInputRef.value?.click()
    }

    const onFileSelected = async (event: Event) => {
      const input = event.target as HTMLInputElement
      const file = input.files?.[0]
      if (!file || !currentNote.value?.id) return

      attachmentsLoading.value = true
      try {
        const uploaded = await attachmentService.upload(
          props.applicationId,
          currentNote.value.id,
          file
        )
        attachments.value.push(uploaded)
        toastStore.success('Attachment uploaded')
      } catch {
        toastStore.error('Failed to upload attachment')
      } finally {
        attachmentsLoading.value = false
        // Reset so the same file can be re-selected
        input.value = ''
      }
    }

    const deleteAttachment = async (attachment: IInterviewAttachment) => {
      if (!attachment.id || !currentNote.value?.id) return
      try {
        await attachmentService.delete(
          props.applicationId,
          currentNote.value.id,
          attachment.id
        )
        attachments.value = attachments.value.filter((a) => a.id !== attachment.id)
        toastStore.success('Attachment deleted')
      } catch {
        toastStore.error('Failed to delete attachment')
      }
    }

    const downloadingIds = ref<string[]>([])

    const downloadAttachment = async (attachment: IInterviewAttachment) => {
      if (!attachment.id || !currentNote.value?.id) return
      downloadingIds.value = [...downloadingIds.value, attachment.id]
      try {
        await attachmentService.download(
          props.applicationId,
          currentNote.value.id,
          attachment.id,
          attachment.name
        )
      } catch {
        toastStore.error('Failed to download attachment')
      } finally {
        downloadingIds.value = downloadingIds.value.filter((id) => id !== attachment.id)
      }
    }

    // ── Save (create → edit-mode  OR  update → close) ────────────────────────

    const close = () => {
      isOpen.value = false
    }

    const save = async () => {
      loading.value = true
      try {
        const payload: IInterviewNote = {
          ...currentNote.value,
          date: formDate.value ? new Date(formDate.value) : undefined,
          notes: formNotes.value || undefined,
          applicationId: props.applicationId,
        }

        if (hasId.value) {
          // ── Edit mode: update and close ──────────────────────────────────
          const updated = await noteService.update(props.applicationId, payload)
          toastStore.success('Interview note saved')
          emit('saved', updated)
          close()
        } else {
          // ── Create mode: persist, then transition to edit mode ───────────
          const created = await noteService.create(props.applicationId, payload)
          currentNote.value = created
          formDate.value = created.date ? toDatetimeLocal(created.date) : formDate.value
          formNotes.value = created.notes ?? formNotes.value
          toastStore.success('Interview note created — you can now add attachments')
          // Attachments section becomes visible; load (will be empty)
          await loadAttachments()
        }
      } catch {
        toastStore.error('Failed to save interview note')
      } finally {
        loading.value = false
      }
    }

    // ── Watchers ─────────────────────────────────────────────────────────────

    watch(
      () => [props.modelValue, props.interviewNote] as const,
      ([open]) => {
        if (open) {
          initForm(props.interviewNote)
          if (props.interviewNote?.id) {
            loadAttachments()
          } else {
            attachments.value = []
          }
        }
      },
      { immediate: true }
    )

    return {
      isOpen,
      currentNote,
      formDate,
      formNotes,
      attachments,
      loading,
      attachmentsLoading,
      fileInputRef,
      hasId,
      panelTitle,
      close,
      save,
      triggerFileInput,
      onFileSelected,
      deleteAttachment,
      downloadingIds,
      downloadAttachment,
      formatFileSize,
    }
  },
})

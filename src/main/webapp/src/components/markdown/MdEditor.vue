<script setup lang="ts">
import { computed } from 'vue'
import { useTheme } from 'vuetify'
import { MdEditor as MarkdownEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import MdViewer from './MdViewer.vue'

export interface MdEditorProps {
  modelValue?: string | null
  label?: string
  placeholder?: string
  readonly?: boolean
  rows?: number
  hint?: string
}

const props = withDefaults(defineProps<MdEditorProps>(), {
  modelValue: null,
  label: '',
  placeholder: 'Write something...',
  readonly: false,
  rows: 10,
  hint: '',
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const vuetifyTheme = useTheme()
const editorTheme = computed(() => (vuetifyTheme.global.current.value.dark ? 'dark' : 'light'))

const content = computed({
  get: () => props.modelValue ?? '',
  set: (val: string) => emit('update:modelValue', val),
})

const editorHeight = computed(() => `${props.rows * 36}px`)
</script>

<template>
  <v-input :label="label" :hint="hint" persistent-hint class="md-editor-input-wrapper">
    <template #default>
      <div class="w-100">
        <MarkdownEditor
          v-if="!readonly"
          v-model="content"
          :placeholder="placeholder"
          :style="{ height: editorHeight, minHeight: editorHeight }"
          language="en-US"
          :theme="editorTheme"
          preview-theme="default"
          :preview="false"
          :toolbars="[
            'bold',
            'underline',
            'italic',
            '-',
            'title',
            'strikeThrough',
            'sub',
            'sup',
            '-',
            'unorderedList',
            'orderedList',
            'task',
            '-',
            'link',
            'quote',
            'code',
            'codeRow',
            '-',
            'preview',
            'fullscreen',
          ]"
        />
        <div v-else class="md-readonly-wrapper">
          <MdViewer :content="content" />
        </div>
      </div>
    </template>
  </v-input>
</template>

<style scoped>
/* Blend editor border with Vuetify border tokens */
.md-editor-input-wrapper :deep(.md-editor) {
  border: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 8px;
  overflow: hidden;
}

/* Toolbar background matches Vuetify surface */
.md-editor-input-wrapper :deep(.md-editor-toolbar-wrapper) {
  background: rgb(var(--v-theme-surface));
  border-bottom: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
}

/* Editor and preview area background */
.md-editor-input-wrapper :deep(.md-editor-input-wrapper),
.md-editor-input-wrapper :deep(.md-editor-preview-wrapper) {
  background: rgb(var(--v-theme-surface));
}

/* Text color follows Vuetify theme */
.md-editor-input-wrapper :deep(.md-editor-input),
.md-editor-input-wrapper :deep(.md-editor-preview) {
  color: rgb(var(--v-theme-on-surface));
  font-family: inherit;
  font-size: 0.875rem;
}

/* Readonly fallback */
.md-readonly-wrapper {
  padding: 0.75rem 1rem;
  border: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 8px;
  background: rgb(var(--v-theme-surface));
  min-height: 3rem;
  width: 100%;
}
</style>

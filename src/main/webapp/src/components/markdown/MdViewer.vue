<template>
  <div class="md-renderer-body" v-html="rendered" />
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const props = defineProps<{
  content?: string | null
}>()

const normalizeLineBreaks = (raw: string): string => {
  // Convert lone \n (not already preceded by two spaces or following a blank line)
  // into a markdown hard break (two trailing spaces + newline)
  return raw
    .split('\n')
    .map((line, i, arr) => {
      const next = arr[i + 1]
      // If current line and next line are both non-empty → hard break
      if (line.trim() !== '' && next !== undefined && next.trim() !== '') {
        return line.trimEnd() + '  ' // two trailing spaces = hard break in markdown
      }
      return line
    })
    .join('\n')
}

const rendered = computed(() => {
  if (!props.content) return ''
  const normalized = normalizeLineBreaks(props.content)
  return DOMPurify.sanitize(marked.parse(normalized) as string)
})
</script>

<style scoped>
.md-renderer-body :deep(h1),
.md-renderer-body :deep(h2),
.md-renderer-body :deep(h3) {
  font-weight: 600;
  margin-top: 1.25em;
  margin-bottom: 0.5em;
}
.md-renderer-body :deep(p) {
  margin-bottom: 0.6em;
}
.md-renderer-body :deep(ul),
.md-renderer-body :deep(ol) {
  padding-left: 1.25rem;
  margin-bottom: 0.75em;
}
.md-renderer-body :deep(li) {
  margin-bottom: 0.2rem;
}
.md-renderer-body :deep(strong) {
  font-weight: 600;
}
.md-renderer-body :deep(code) {
  background: rgba(var(--v-theme-on-surface), 0.07);
  padding: 2px 5px;
  border-radius: 4px;
  font-size: 0.88em;
}
.md-renderer-body :deep(pre) {
  background: rgba(var(--v-theme-on-surface), 0.05);
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  margin-bottom: 0.75em;
}
.md-renderer-body :deep(blockquote) {
  border-left: 3px solid rgba(var(--v-theme-primary), 0.5);
  padding-left: 1rem;
  color: rgb(var(--v-theme-on-surface-variant));
  margin-bottom: 0.75em;
}
</style>

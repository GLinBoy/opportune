<template>
  <v-dialog
    v-model="isOpen"
    fullscreen
    transition="dialog-bottom-transition"
    :scrim="false"
  >
    <v-card>
      <v-toolbar dark color="primary">
        <v-btn icon dark @click="closeDialog">
          <v-icon>mdi-close</v-icon>
        </v-btn>
        <v-toolbar-title>Original Job Description</v-toolbar-title>
        <v-spacer></v-spacer>
      </v-toolbar>

      <v-card-text class="pa-0" style="height: calc(100vh - 64px); overflow-y: auto;">
        <!-- Rendered HTML View -->
        <div
          class="pa-6"
          v-html="sanitizedContent"
          style="max-width: 1200px; margin: 0 auto;"
        ></div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import DOMPurify from 'dompurify'

export interface Props {
  modelValue: boolean
  content: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const isOpen = ref(props.modelValue)

// Watch for external changes to modelValue
watch(
  () => props.modelValue,
  (newValue) => {
    isOpen.value = newValue
  }
)

// Watch for internal changes and emit
watch(isOpen, (newValue) => {
  emit('update:modelValue', newValue)
})

// Sanitize HTML content to prevent XSS attacks
const sanitizedContent = computed(() => {
  if (!props.content) return '<p>No content available</p>'

  // Check if content is HTML or plain text
  const isHtml = /<[a-z][\s\S]*>/i.test(props.content)

  if (isHtml) {
    return DOMPurify.sanitize(props.content, {
      ALLOWED_TAGS: [
        'a', 'b', 'i', 'em', 'strong', 'u', 'p', 'br', 'div', 'span',
        'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'li',
        'table', 'thead', 'tbody', 'tr', 'th', 'td',
        'blockquote', 'code', 'pre', 'hr', 'img'
      ],
      ALLOWED_ATTR: ['href', 'target', 'rel', 'class', 'style', 'src', 'alt', 'title']
    })
  } else {
    // Convert plain text to HTML with line breaks
    return `<pre style="white-space: pre-wrap; font-family: inherit;">${props.content}</pre>`
  }
})

const closeDialog = () => {
  isOpen.value = false
}
</script>

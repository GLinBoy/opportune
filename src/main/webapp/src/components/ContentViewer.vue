<template>
  <div class="content-viewer mt-4">
    <!-- Toolbar -->
    <v-toolbar density="compact" color="surface" rounded="t-lg" border>
      <v-toolbar-title class="text-body-2 text-medium-emphasis">
        <v-icon :icon="icon" size="16" class="mr-1" />
        {{ title }}
      </v-toolbar-title>

      <v-spacer />

      <!-- Copy to clipboard -->
      <v-tooltip text="Copy to clipboard" location="top">
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            :icon="copied ? 'mdi-check' : 'mdi-content-copy'"
            :color="copied ? 'success' : undefined"
            size="small"
            variant="text"
            @click="copyContent"
          />
        </template>
      </v-tooltip>

      <!-- Edit toggle (editable panels only) -->
      <v-tooltip v-if="editable" :text="editMode ? 'Preview' : 'Edit'" location="top">
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            :icon="editMode ? 'mdi-eye' : 'mdi-pencil'"
            :color="editMode ? 'primary' : undefined"
            size="small"
            variant="text"
            @click="editMode = !editMode"
          />
        </template>
      </v-tooltip>

      <!-- Export as PDF -->
      <v-tooltip text="Export as PDF" location="top">
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            icon="mdi-file-pdf-box"
            size="small"
            variant="text"
            color="error"
            @click="exportPdf"
          />
        </template>
      </v-tooltip>

      <!-- Print -->
      <v-tooltip text="Print" location="top">
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            icon="mdi-printer"
            size="small"
            variant="text"
            @click="printContent"
          />
        </template>
      </v-tooltip>

      <!-- Fullscreen -->
      <v-tooltip text="Fullscreen" location="top">
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            icon="mdi-fullscreen"
            size="small"
            variant="text"
            @click="fullscreen = true"
          />
        </template>
      </v-tooltip>
    </v-toolbar>

    <!-- Fixed-height content area -->
    <v-sheet
      ref="contentSheetRef"
      rounded="b-lg"
      border
      class="content-viewer__body"
      :style="{ height }"
    >
      <MdEditor
        v-if="editable && editMode"
        :model-value="content"
        class="h-100"
        @update:model-value="$emit('update:content', $event)"
      />
      <div v-else ref="scrollRef" class="content-viewer__scroll pa-4">
        <MdViewer :content="content" />
      </div>
    </v-sheet>

    <!-- Fullscreen Dialog -->
    <v-dialog v-model="fullscreen" fullscreen transition="dialog-bottom-transition">
      <v-card>
        <v-toolbar color="primary" density="compact">
          <v-toolbar-title>
            <v-icon :icon="icon" class="mr-2" />
            {{ title }}
          </v-toolbar-title>
          <v-spacer />

          <!-- Copy — added here -->
          <v-tooltip text="Copy to clipboard" location="bottom">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                :icon="copied ? 'mdi-check' : 'mdi-content-copy'"
                :color="copied ? 'success' : 'white'"
                variant="text"
                @click="copyContent"
              />
            </template>
          </v-tooltip>

          <v-tooltip text="Export as PDF" location="bottom">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-file-pdf-box"
                color="white"
                variant="text"
                @click="exportPdf"
              />
            </template>
          </v-tooltip>

          <v-tooltip text="Print" location="bottom">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-printer"
                color="white"
                variant="text"
                @click="printContent"
              />
            </template>
          </v-tooltip>

          <v-btn icon="mdi-fullscreen-exit" @click="fullscreen = false" />
        </v-toolbar>

        <v-card-text
          ref="fullscreenContentRef"
          class="pa-6"
          style="overflow-y: auto; height: calc(100vh - 48px)"
        >
          <MdEditor
            v-if="editable && editMode"
            :model-value="content"
            :rows="20"
            @update:model-value="$emit('update:content', $event)"
          />
          <MdViewer v-else :content="content" />
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import MdViewer from '@/components/markdown/MdViewer.vue'
import MdEditor from '@/components/markdown/MdEditor.vue'

const props = withDefaults(
  defineProps<{
    content?: string | null
    title?: string
    icon?: string
    editable?: boolean
    height?: string
  }>(),
  {
    content: '',
    title: 'Content',
    icon: 'mdi-text',
    editable: false,
    height: '480px',
  }
)

defineEmits<{
  'update:content': [value: string]
}>()

const fullscreen = ref(false)
const editMode = ref(false)
const copied = ref(false)

const copyContent = async () => {
  if (!props.content) return
  await navigator.clipboard.writeText(props.content)
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}

const printContent = () => {
  const win = window.open('', '_blank')
  if (!win) return
  win.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <title>${props.title}</title>
        <style>
          *, *::before, *::after { box-sizing: border-box; }

          @page {
            size: A4;
            margin: 20mm 18mm;   /* clean page margins — no browser header/footer room */
            /* These two remove the browser-injected date, URL, title, page number */
          }

          html, body {
            margin: 0;
            padding: 0;
            width: 100%;
            overflow-x: hidden;
          }

          body {
            font-family: Georgia, 'Times New Roman', serif;
            font-size: 11pt;
            line-height: 1.8;
            color: #111;
            padding: 0;
            text-align: justify;
            word-wrap: break-word;
            overflow-wrap: break-word;
            hyphens: auto;
          }

          h1 { font-size: 18pt; margin: 0 0 4pt; }
          h2 { font-size: 14pt; margin: 16pt 0 4pt; }
          h3 { font-size: 12pt; margin: 12pt 0 4pt; }
          h1, h2, h3 { font-weight: 700; line-height: 1.3; text-align: left; }

          p {
            margin: 0 0 8pt;
            text-align: justify;
            word-wrap: break-word;
            overflow-wrap: break-word;
          }

          ul, ol { padding-left: 20pt; margin: 0 0 8pt; text-align: left; }
          li { margin-bottom: 3pt; }
          strong { font-weight: 700; }
          em { font-style: italic; }

          code {
            font-family: 'Courier New', monospace;
            font-size: 9pt;
            background: #f3f3f3;
            padding: 1px 4px;
            border-radius: 3px;
          }

          pre {
            font-family: 'Courier New', monospace;
            font-size: 9pt;
            background: #f3f3f3;
            padding: 10pt;
            border-radius: 4pt;
            white-space: pre-wrap;
            word-wrap: break-word;
            overflow-wrap: break-word;
            margin-bottom: 8pt;
          }

          blockquote {
            border-left: 3pt solid #aaa;
            padding-left: 12pt;
            color: #444;
            font-style: italic;
            margin: 0 0 8pt;
          }

          hr { border: none; border-top: 1pt solid #ccc; margin: 16pt 0; }

          @media print {
            html, body { width: 100%; height: auto; overflow: visible; }
            pre { page-break-inside: avoid; }
          }
        </style>
      </head>
      <body>
        ${renderedHtml()}
      </body>
    </html>
  `)
  win.document.close()
  win.focus()
  setTimeout(() => {
    win.print()
    win.close()
  }, 400)
}

const exportPdf = () => {
  // Opens the print dialog with PDF as a save option in all modern browsers.
  // For a true headless PDF (no dialog), a server-side or library approach
  // (e.g. pdfmake, jsPDF) would be needed — wire that in when ready.
  printContent()
}

// Shared helper: get the rendered HTML string from the MdViewer output in the DOM,
// or fall back to the raw content if not available.
const scrollRef = ref<HTMLElement | null>(null)

const renderedHtml = (): string => {
  const el = scrollRef.value?.querySelector('.md-viewer-body')
  return el?.innerHTML ?? `<pre>${props.content ?? ''}</pre>`
}
</script>

<style scoped>
.content-viewer__body {
  overflow: hidden;
}
.content-viewer__scroll {
  height: 100%;
  overflow-y: auto;
}
</style>

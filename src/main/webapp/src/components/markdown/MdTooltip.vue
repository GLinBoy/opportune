<script setup lang="ts">
import { computed } from 'vue'
import MdViewer from './MdViewer.vue'

export interface Props {
  content?: string | null
  maxLength?: number
  location?: 'top' | 'bottom' | 'start' | 'end' | 'right' | 'left'
  iconSize?: string | number
  iconColor?: string
}

const props = withDefaults(defineProps<Props>(), {
  content: null,
  maxLength: 200,
  location: 'top',
  iconSize: '18',
  iconColor: 'medium-emphasis',
})

const hasContent = computed(() => !!props.content?.trim())

const truncated = computed(() => {
  if (!props.content) return ''
  if (props.content.length <= props.maxLength) return props.content
  return props.content.slice(0, props.maxLength).trimEnd() + '…'
})

const isTruncated = computed(() => !!props.content && props.content.length > props.maxLength)
</script>

<template>
  <v-tooltip v-if="hasContent" :location="location" max-width="320">
    <template #activator="{ props: tooltipProps }">
      <v-icon
        v-bind="tooltipProps"
        icon="mdi-information-outline"
        :size="iconSize"
        :color="iconColor"
        class="md-tooltip-icon"
        role="img"
        :aria-label="content ?? ''"
      />
    </template>

    <template #default>
      <div class="md-tooltip-content text-body-2">
        <MdViewer :content="truncated" />
        <p v-if="isTruncated" class="md-tooltip-truncation-hint">… (truncated)</p>
      </div>
    </template>
  </v-tooltip>
</template>

<style scoped>
.md-tooltip-icon {
  cursor: default;
  vertical-align: middle;
}

.md-tooltip-content :deep(.markdown-body p) {
  margin-bottom: 0.25rem;
  line-height: 1.4;
}
.md-tooltip-content :deep(.markdown-body ul),
.md-tooltip-content :deep(.markdown-body ol) {
  padding-left: 1rem;
  margin: 0.15rem 0 0.4rem;
}
.md-tooltip-content :deep(.markdown-body li) {
  margin-bottom: 0.1rem;
}

/* Override MdViewer's on-surface color — tooltip bg is dark so inherit tooltip's own text color */
.md-tooltip-content :deep(.markdown-body strong) {
  font-weight: 600;
  color: inherit;
}

.md-tooltip-content :deep(.markdown-body h1),
.md-tooltip-content :deep(.markdown-body h2),
.md-tooltip-content :deep(.markdown-body h3) {
  font-size: 0.85rem;
  font-weight: 600;
  margin: 0.25rem 0;
  color: inherit;
}
.md-tooltip-content :deep(.markdown-body code) {
  font-size: 0.78rem;
  padding: 0.05rem 0.25rem;
}

.md-tooltip-truncation-hint {
  margin-top: 0.35rem;
  font-size: 0.7rem;
  opacity: 0.6;
  font-style: italic;
}
</style>

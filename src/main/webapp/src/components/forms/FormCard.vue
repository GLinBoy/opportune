<template>
  <v-card elevation="0" border rounded="lg">
    <v-card-title class="pa-5 pb-4 text-body-1 font-weight-semibold d-flex align-center">
      <span class="flex-grow-1">
        <slot name="title" />
      </span>
      <v-btn
        v-if="collapsible && showToggle"
        :icon="isOpen ? 'mdi-chevron-up' : 'mdi-chevron-down'"
        variant="text"
        density="compact"
        size="small"
        :aria-label="isOpen ? 'Collapse section' : 'Expand section'"
        :aria-expanded="isOpen"
        @click="isOpen = !isOpen"
      />
    </v-card-title>
    <v-expand-transition>
      <div v-if="!collapsible || isOpen">
        <v-divider />
        <v-card-text class="pa-5">
          <slot />
        </v-card-text>
        <template v-if="$slots.actions">
          <v-divider />
          <v-card-actions class="px-5 py-4 justify-end gap-2">
            <slot name="actions" />
          </v-card-actions>
        </template>
      </div>
    </v-expand-transition>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineOptions({
  name: 'FormCard',
})

const props = defineProps({
  collapsible: { type: Boolean, default: true },
  defaultOpen: { type: Boolean, default: true },
  showToggle: { type: Boolean, default: true },
})

const isOpen = ref(props.defaultOpen)
</script>

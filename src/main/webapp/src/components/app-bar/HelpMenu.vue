<template>
  <v-menu v-model="helpOpen" :close-on-content-click="true" offset="8" max-width="220">
    <template #activator="{ props: menuProps }">
      <v-btn v-bind="menuProps" variant="text" size="small" icon aria-label="Help">
        <v-icon>mdi-help-circle-outline</v-icon>
      </v-btn>
    </template>

    <v-list density="compact" nav>
      <v-list-subheader>Help &amp; Resources</v-list-subheader>

      <v-list-item
        prepend-icon="mdi-book-open-outline"
        title="Documentation"
        href="/docs"
        target="_blank"
        rel="noopener noreferrer"
      />
      <v-list-item
        prepend-icon="mdi-lifebuoy"
        title="Support"
        href="/support"
        target="_blank"
        rel="noopener noreferrer"
      />
      <v-list-item
        prepend-icon="mdi-keyboard-outline"
        title="Keyboard shortcuts"
        @click="showShortcuts"
      />
      <v-divider class="my-1" />
      <v-list-item
        prepend-icon="mdi-information-outline"
        title="What's new"
        href="/changelog"
        target="_blank"
        rel="noopener noreferrer"
      />
    </v-list>
  </v-menu>
</template>

<script lang="ts" setup>
import { ref } from 'vue'

const helpOpen = ref(false)

function showShortcuts() {
  // TODO: open keyboard shortcuts dialog
}
</script>

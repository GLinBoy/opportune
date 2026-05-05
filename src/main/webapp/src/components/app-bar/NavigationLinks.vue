<template>
  <template v-for="link in links" :key="link.title">
    <!-- Mobile: icon-only with tooltip -->
    <v-tooltip v-if="smAndDown" :text="link.title" location="bottom">
      <template #activator="{ props }">
        <v-btn
          v-bind="props"
          :icon="link.icon"
          :to="link.path"
          variant="text"
          size="small"
          class="ms-1"
        />
      </template>
    </v-tooltip>

    <!-- Desktop: full text + icon button -->
    <v-btn
      v-else
      :text="link.title"
      :prepend-icon="link.icon"
      :to="link.path"
      variant="text"
      class="ms-1"
    />
  </template>
</template>

<script setup lang="ts">
import { useDisplay } from 'vuetify'

const { smAndDown } = useDisplay()

const links = [
  { title: 'Dashboard', path: '/dashboard', icon: 'mdi-view-dashboard-outline' },
  { title: 'Companies', path: '/companies', icon: 'mdi-domain' },
  { title: 'Applications', path: '/applications', icon: 'mdi-briefcase-outline' },
]
</script>

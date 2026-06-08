<template>
  <v-app :theme="currentTheme">
    <!-- Top App Bar -->
    <AppBar />

    <!-- Left Sidebar / Nav Drawer -->
    <v-navigation-drawer
      width="240"
      permanent
      border="e"
      app
    >
      <!-- Nav items -->
      <v-list nav density="compact">
        <v-list-item
          v-for="item in navItems"
          :key="item.route"
          :prepend-icon="item.icon"
          :title="item.label"
          :to="item.route"
          :value="item.route"
          rounded="lg"
          active-class="text-primary"
          :active="route.path.startsWith(item.route)"
        />
      </v-list>
    </v-navigation-drawer>

    <!-- Main Content -->
    <v-main>
      <!-- Page title bar -->
      <v-toolbar flat color="surface" border="b" height="48" class="px-4">
        <v-toolbar-title class="text-subtitle-1 font-weight-medium">
          {{ pageTitle }}
        </v-toolbar-title>
      </v-toolbar>

      <v-container fluid class="pa-4">
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useTheme } from 'vuetify'
import AppBar from '@/components/app-bar/AppBar.vue'

const route = useRoute()

// ── Theme ─────────────────────────────────────────────────────────────────
const vuetifyTheme = useTheme()
const currentTheme = computed(() => vuetifyTheme.global.name.value)

const navItems = [
  { label: 'Dashboard', icon: 'mdi-view-dashboard-outline', route: '/admin/dashboard' },
  { label: 'Users', icon: 'mdi-account-group-outline', route: '/admin/users' },
  { label: 'Companies', icon: 'mdi-office-building-outline', route: '/admin/companies' },
  { label: 'Security', icon: 'mdi-shield-lock-outline', route: '/admin/security' },
  { label: 'AI Monitor', icon: 'mdi-robot-outline', route: '/admin/ai-monitor' },
  { label: 'Settings', icon: 'mdi-cog-outline', route: '/admin/settings' },
]

// ── Page title (driven by route meta) ─────────────────────────────────────
const pageTitle = computed<string>(() => (route.meta?.pageTitle as string) ?? 'Admin Panel')
</script>

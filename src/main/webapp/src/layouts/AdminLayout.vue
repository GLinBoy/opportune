<template>
  <v-app :theme="currentTheme">
    <!-- Top App Bar -->
    <v-app-bar flat color="surface" height="56" border="b">
      <v-app-bar-nav-icon @click="drawer = !drawer" aria-label="Toggle sidebar" />

      <router-link
        to="/admin/dashboard"
        class="brand-link d-flex align-center text-decoration-none me-2"
      >
        <img src="@/assets/logo.png" alt="Opportune Admin" width="28" height="28" />
        <span class="text-subtitle-1 font-weight-bold ms-2 d-none d-sm-inline">Admin</span>
      </router-link>

      <v-spacer />

      <!-- Breadcrumbs (hidden on xs) -->
      <v-breadcrumbs
        v-if="breadcrumbs.length"
        :items="breadcrumbs"
        class="d-none d-md-flex pa-0 me-2"
        density="compact"
      >
        <template #divider>
          <v-icon icon="mdi-chevron-right" size="small" />
        </template>
      </v-breadcrumbs>

      <v-spacer />

      <!-- Theme toggle -->
      <v-btn
        :icon="isDark ? 'mdi-weather-sunny' : 'mdi-weather-night'"
        variant="text"
        size="small"
        class="me-1"
        :aria-label="isDark ? 'Switch to light mode' : 'Switch to dark mode'"
        @click="toggleTheme"
      />

      <!-- Back to main app -->
      <v-btn
        icon="mdi-home-outline"
        variant="text"
        size="small"
        class="me-1"
        aria-label="Go to main app"
        @click="router.push('/dashboard')"
      />

      <!-- Profile menu -->
      <ProfileMenu />
    </v-app-bar>

    <!-- Left Sidebar / Nav Drawer -->
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail"
      rail-width="56"
      width="240"
      permanent
      border="e"
    >
      <!-- Drawer header -->
      <v-list-item class="py-3" :prepend-icon="rail ? undefined : undefined">
        <template #append>
          <v-btn
            :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'"
            variant="text"
            size="small"
            @click="rail = !rail"
            :aria-label="rail ? 'Expand sidebar' : 'Collapse sidebar'"
          />
        </template>
      </v-list-item>

      <v-divider />

      <!-- Nav items -->
      <v-list nav density="compact" class="mt-1">
        <v-list-item
          v-for="item in navItems"
          :key="item.route"
          :prepend-icon="item.icon"
          :title="item.label"
          :to="item.route"
          :value="item.route"
          rounded="lg"
          active-class="text-primary"
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
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTheme } from 'vuetify'
import ProfileMenu from '@/components/profile-menu/ProfileMenu.vue'

const router = useRouter()
const route = useRoute()

// ── Theme ─────────────────────────────────────────────────────────────────
const vuetifyTheme = useTheme()
const isDark = computed(() => vuetifyTheme.global.current.value.dark)
const currentTheme = computed(() => vuetifyTheme.global.name.value)

function toggleTheme() {
  vuetifyTheme.global.name.value =
    vuetifyTheme.global.name.value === 'tablerLight' ? 'tablerDark' : 'tablerLight'
}

// ── Drawer state ──────────────────────────────────────────────────────────
const drawer = ref(true)
const rail = ref(false)

// ── Navigation items ──────────────────────────────────────────────────────
const navItems = [
  { label: 'Dashboard', icon: 'mdi-view-dashboard-outline', route: '/admin/dashboard' },
  { label: 'Users', icon: 'mdi-account-group-outline', route: '/admin/users' },
  { label: 'Companies', icon: 'mdi-office-building-outline', route: '/admin/companies' },
  { label: 'Security', icon: 'mdi-shield-lock-outline', route: '/admin/security' },
  { label: 'AI Monitor', icon: 'mdi-robot-outline', route: '/admin/ai-monitor' },
  { label: 'Settings', icon: 'mdi-cog-outline', route: '/admin/settings' },
]

// ── Breadcrumbs & page title (driven by route meta) ───────────────────────
const pageTitle = computed<string>(() => (route.meta?.pageTitle as string) ?? 'Admin Panel')

const breadcrumbs = computed(() => {
  const crumbs: { title: string; disabled?: boolean; to?: string }[] = [
    { title: 'Admin', to: '/admin/dashboard' },
  ]
  const meta = route.meta
  if (meta?.breadcrumbs) {
    ;(meta.breadcrumbs as { title: string; to?: string }[]).forEach((b) => crumbs.push(b))
  } else if (meta?.pageTitle) {
    crumbs.push({ title: meta.pageTitle as string, disabled: true })
  }
  return crumbs
})
</script>

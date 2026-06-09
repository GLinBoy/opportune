<template>
  <v-app :theme="currentTheme">
    <AppBar />

    <v-main>
      <v-container fluid class="pa-4 pb-0">
        <!-- Page Header -->
        <div class="d-flex align-center mb-4 mb-md-6 ga-3">
          <v-icon icon="mdi-shield-crown" size="48" />
          <div class="d-flex flex-column">
            <span class="text-headline-small font-weight-bold">Admin Panel</span>
            <span class="text-label-medium text-medium-emphasis">Manage system settings, users, and monitor activity</span>
          </div>
        </div>
      </v-container>

      <v-container fluid class="pa-4 pt-0">
        <v-row>
          <!-- Left Sidebar with Nav Items -->
          <v-col cols="12" md="3">
            <v-card class="mb-4">
              <v-list density="compact" nav>
                <v-list-item
                  v-for="item in navItems"
                  :key="item.route"
                  :prepend-icon="item.icon"
                  :title="item.label"
                  :to="item.route"
                  :value="item.route"
                  :active="isActive(item.route)"
                  :ripple="true"
                  rounded="lg"
                  active-class="text-primary"
                />
              </v-list>
            </v-card>
          </v-col>

          <!-- Main Content Area -->
          <v-col cols="12" md="9">
            <router-view />
          </v-col>
        </v-row>
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

function isActive(path: string): boolean {
  return route.path.startsWith(path)
}
</script>

<style scoped>
.v-list-item--active {
  background-color: rgba(var(--v-theme-primary), 0.08) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.v-list-item--active .v-icon {
  color: rgb(var(--v-theme-primary)) !important;
}
</style>

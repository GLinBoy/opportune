<template>
  <v-app-bar flat color="surface" height="56" border="b">
    <v-container class="mx-auto d-flex align-center" style="max-width: 1400px">
      <!-- Logo -->
      <router-link to="/dashboard" class="brand-link d-flex align-center text-decoration-none me-4">
        <img src="@/assets/logo.png" alt="Opportune" width="28" height="28" />
      </router-link>

      <!-- Nav links -->
      <NavigationLinks />

      <v-spacer />

      <!-- Search: hidden on xs -->
      <div
        class="d-none d-sm-flex align-center me-1"
        :style="{ width: smOnly ? '200px' : '400px', minWidth: 0 }"
      >
        <SearchBar />
      </div>

      <!-- Theme toggle -->
      <v-btn
        :icon="isDark ? 'mdi-weather-sunny' : 'mdi-weather-night'"
        variant="text"
        size="small"
        class="me-1"
        :aria-label="isDark ? 'Switch to light mode' : 'Switch to dark mode'"
        @click="toggleTheme"
      />

      <!-- Notification bell -->
      <v-menu v-model="notifOpen" :close-on-content-click="false" offset="8" max-width="360">
        <template #activator="{ props: menuProps }">
          <v-btn v-bind="menuProps" variant="text" size="small" icon class="me-1">
            <v-badge :content="unreadCount" :model-value="unreadCount > 0" color="error" floating>
              <v-icon>mdi-bell-outline</v-icon>
            </v-badge>
          </v-btn>
        </template>

        <v-card width="360">
          <v-card-title class="d-flex align-center justify-space-between pa-4 pb-2">
            <span class="text-body-1 font-weight-semibold">Notifications</span>
            <v-btn
              v-if="unreadCount > 0"
              variant="text"
              size="x-small"
              color="primary"
              @click="markAllRead"
            >
              Mark all read
            </v-btn>
          </v-card-title>

          <v-divider />

          <v-list
            lines="two"
            density="compact"
            class="py-1"
            style="max-height: 340px; overflow-y: auto"
          >
            <template v-if="notifications.length > 0">
              <v-list-item
                v-for="notif in notifications"
                :key="notif.id"
                :class="{ 'notif-unread': !notif.read }"
                class="notif-item"
              >
                <template #prepend>
                  <v-avatar :color="notif.color" size="36">
                    <v-icon :icon="notif.icon" size="18" color="white" />
                  </v-avatar>
                </template>

                <v-list-item-title class="text-body-2 font-weight-medium">
                  {{ notif.title }}
                </v-list-item-title>
                <v-list-item-subtitle class="text-caption">
                  {{ notif.body }}
                </v-list-item-subtitle>

                <template #append>
                  <div class="d-flex flex-column align-end">
                    <span class="text-caption text-medium-emphasis">{{ notif.time }}</span>
                    <v-btn
                      v-if="!notif.read"
                      icon="mdi-check"
                      variant="text"
                      size="x-small"
                      class="mt-1"
                      @click.stop="markRead(notif.id)"
                    />
                  </div>
                </template>
              </v-list-item>
            </template>

            <v-list-item v-else>
              <v-list-item-title class="text-body-2 text-medium-emphasis text-center py-4">
                No notifications
              </v-list-item-title>
            </v-list-item>
          </v-list>

          <v-divider />
          <v-card-actions class="pa-2">
            <v-btn variant="text" size="small" color="primary" block>
              View all notifications
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-menu>

      <!-- Profile menu -->
      <ProfileMenu />
    </v-container>
  </v-app-bar>
</template>

<script lang="ts" src="./AppBar.ts" />

<style lang="css" scoped src="./AppBar.css" />

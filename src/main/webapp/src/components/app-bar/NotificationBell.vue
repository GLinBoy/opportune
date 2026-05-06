<template>
  <v-menu v-model="notifOpen" :close-on-content-click="false" offset="8" max-width="360">
    <template #activator="{ props: menuProps }">
      <v-btn v-bind="menuProps" variant="text" size="small" icon>
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
        <v-btn variant="text" size="small" color="primary" block> View all notifications </v-btn>
      </v-card-actions>
    </v-card>
  </v-menu>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'

interface Notification {
  id: number
  title: string
  body: string
  time: string
  icon: string
  color: string
  read: boolean
}

const notifOpen = ref(false)

const notifications = ref<Notification[]>([
  {
    id: 1,
    title: 'Application status updated',
    body: 'Your application at Acme Corp moved to Interview.',
    time: '2 min ago',
    icon: 'mdi-briefcase-check-outline',
    color: 'success',
    read: false,
  },
  {
    id: 2,
    title: 'New company added',
    body: 'Globex Inc. was added to your watchlist.',
    time: '1 hr ago',
    icon: 'mdi-domain',
    color: 'info',
    read: false,
  },
  {
    id: 3,
    title: 'Profile reminder',
    body: 'Complete your profile to improve matches.',
    time: 'Yesterday',
    icon: 'mdi-account-alert-outline',
    color: 'warning',
    read: true,
  },
])

const unreadCount = computed(() => notifications.value.filter((n) => !n.read).length)

function markRead(id: number) {
  const n = notifications.value.find((n) => n.id === id)
  if (n) n.read = true
}

function markAllRead() {
  notifications.value.forEach((n) => (n.read = true))
}
</script>

<style scoped>
.notif-item {
  border-radius: 4px;
  margin: 2px 4px;
}

.notif-unread {
  background-color: rgba(var(--v-theme-primary), 0.05);
}
</style>

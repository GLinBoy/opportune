<template>
  <v-container fluid class="pa-4">
    <!-- Loading skeleton -->
    <template v-if="loading">
      <v-skeleton-loader type="card" class="mb-4" />
      <v-skeleton-loader type="table" />
    </template>

    <!-- Error state -->
    <v-alert v-else-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <template v-else-if="detail">
      <!-- Profile card -->
      <v-card flat border rounded="lg" class="mb-4">
        <v-card-text class="pa-6">
          <v-row align="center">
            <v-col cols="auto">
              <v-avatar size="72" color="primary" class="text-h5 font-weight-bold">
                {{ initials }}
              </v-avatar>
            </v-col>
            <v-col>
              <div class="text-h6 font-weight-medium">{{ fullName }}</div>
              <div class="text-body-2 text-medium-emphasis">{{ detail.profile.email }}</div>
              <div class="d-flex flex-wrap gap-2 mt-2">
                <v-chip :color="statusColor(detail.profile.status)" size="small" label>
                  {{ detail.profile.status ?? '—' }}
                </v-chip>
                <v-chip
                  v-for="role in detail.profile.roles"
                  :key="role"
                  size="x-small"
                  :color="role === 'ROLE_ADMIN' ? 'warning' : 'default'"
                  label
                >
                  {{ role === 'ROLE_ADMIN' ? 'Admin' : 'User' }}
                </v-chip>
              </div>
            </v-col>
            <!-- KPI chips -->
            <v-col cols="12" sm="auto" class="d-flex gap-4 flex-wrap">
              <div class="text-center">
                <div class="text-h6 font-weight-bold">{{ detail.applicationCount }}</div>
                <div class="text-caption text-medium-emphasis">Applications</div>
              </div>
              <div class="text-center">
                <div class="text-h6 font-weight-bold">{{ activeSessions }}</div>
                <div class="text-caption text-medium-emphasis">Active Sessions</div>
              </div>
            </v-col>
          </v-row>

          <!-- Profile details -->
          <v-divider class="my-4" />
          <v-row dense>
            <v-col v-if="detail.profile.jobTitle" cols="12" sm="6" md="4">
              <div class="text-caption text-medium-emphasis">Job Title</div>
              <div class="text-body-2">{{ detail.profile.jobTitle }}</div>
            </v-col>
            <v-col v-if="detail.profile.createdDate" cols="12" sm="6" md="4">
              <div class="text-caption text-medium-emphasis">Registered</div>
              <div class="text-body-2">{{ formatDate(detail.profile.createdDate) }}</div>
            </v-col>
            <v-col v-if="detail.profile.lastLogin" cols="12" sm="6" md="4">
              <div class="text-caption text-medium-emphasis">Last Login</div>
              <div class="text-body-2">{{ formatDate(detail.profile.lastLogin) }}</div>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <div class="text-caption text-medium-emphasis">Email Verified</div>
              <div class="text-body-2">{{ detail.profile.emailVerification ? 'Yes' : 'No' }}</div>
            </v-col>
          </v-row>
        </v-card-text>

        <!-- Actions toolbar -->
        <v-divider />
        <v-card-actions class="px-6 py-3 flex-wrap gap-2">
          <v-btn
            v-if="detail.profile.status !== 'ACTIVE'"
            color="success"
            variant="tonal"
            prepend-icon="mdi-account-check-outline"
            size="small"
            @click="confirmAction('activate')"
          >
            Activate
          </v-btn>
          <v-btn
            v-if="detail.profile.status !== 'SUSPENDED'"
            color="warning"
            variant="tonal"
            prepend-icon="mdi-account-cancel-outline"
            size="small"
            @click="confirmAction('suspend')"
          >
            Suspend
          </v-btn>
          <v-btn
            v-if="!detail.profile.roles?.includes('ROLE_ADMIN')"
            color="secondary"
            variant="tonal"
            prepend-icon="mdi-shield-account-outline"
            size="small"
            @click="confirmAction('promote')"
          >
            Promote to Admin
          </v-btn>
          <v-btn
            v-if="detail.profile.roles?.includes('ROLE_ADMIN')"
            color="secondary"
            variant="tonal"
            prepend-icon="mdi-shield-off-outline"
            size="small"
            @click="confirmAction('demote')"
          >
            Demote to User
          </v-btn>
          <v-btn
            color="info"
            variant="tonal"
            prepend-icon="mdi-logout"
            size="small"
            @click="confirmAction('revoke')"
          >
            Force Logout
          </v-btn>
          <v-spacer />
          <v-btn
            color="error"
            variant="tonal"
            prepend-icon="mdi-delete-outline"
            size="small"
            @click="confirmAction('delete')"
          >
            Delete User
          </v-btn>
        </v-card-actions>
      </v-card>

      <!-- Sessions table -->
      <v-card flat border rounded="lg">
        <v-card-title class="pa-4 text-subtitle-1 font-weight-medium">
          Sessions ({{ detail.sessions.length }})
        </v-card-title>
        <v-divider />
        <v-data-table
          :headers="sessionHeaders"
          :items="detail.sessions"
          item-value="refreshTokenId"
          density="compact"
        >
          <template #item.status="{ item }">
            <v-chip :color="item.status === 'ACTIVE' ? 'success' : 'default'" size="x-small" label>
              {{ item.status }}
            </v-chip>
          </template>
          <template #item.loginAt="{ item }">
            {{ item.loginAt ? formatDate(item.loginAt) : '—' }}
          </template>
          <template #item.lastActiveAt="{ item }">
            {{ item.lastActiveAt ? formatDate(item.lastActiveAt) : '—' }}
          </template>
          <template #item.device="{ item }">
            <span class="text-caption">
              {{ [item.browser, item.os].filter(Boolean).join(' / ') || '—' }}
            </span>
          </template>
          <template #item.clientGeo="{ item }">
            {{ item.clientGeo || item.clientIp || '—' }}
          </template>
          <template #no-data>
            <div class="pa-6 text-center text-medium-emphasis text-body-2">No sessions found</div>
          </template>
        </v-data-table>
      </v-card>
    </template>

    <!-- Confirmation dialog -->
    <ConfirmDialog
      v-model="dialog.open"
      :title="dialog.title"
      :variant="dialog.variant"
      :loading="dialog.loading"
      @confirm="executeAction"
      @cancel="dialog.open = false"
    >
      {{ dialog.message }}
    </ConfirmDialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToastStore } from '../../stores/toast'
import AdminUserService from '../../services/admin/admin-user.service'
import type { IAdminUserDetail } from '../../models'
import ConfirmDialog from '../../components/ConfirmDialog.vue'

const route = useRoute()
const router = useRouter()
const toast = useToastStore()
const service = new AdminUserService()

const userId = route.params.id as string

const detail = ref<IAdminUserDetail | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

// — Computed —
const fullName = computed(() => {
  const p = detail.value?.profile
  return [p?.forename, p?.surname].filter(Boolean).join(' ') || p?.email || '—'
})

const initials = computed(() => {
  const p = detail.value?.profile
  const f = p?.forename?.[0] ?? ''
  const s = p?.surname?.[0] ?? ''
  return (f + s).toUpperCase() || (p?.email?.[0] ?? '?').toUpperCase()
})

const activeSessions = computed(
  () => detail.value?.sessions.filter((s) => s.status === 'ACTIVE').length ?? 0
)

const sessionHeaders = [
  { title: 'Status', key: 'status', width: '100px' },
  { title: 'Browser / OS', key: 'device', sortable: false },
  { title: 'IP / Location', key: 'clientGeo', sortable: false },
  { title: 'Login', key: 'loginAt', sortable: true },
  { title: 'Last Active', key: 'lastActiveAt', sortable: true },
]

// — Helpers —
function statusColor(status: string | null | undefined) {
  switch (status) {
    case 'ACTIVE':
      return 'success'
    case 'SUSPENDED':
      return 'error'
    case 'PENDING_VERIFICATION':
      return 'warning'
    default:
      return 'default'
  }
}

function formatDate(iso: string | Date) {
  return new Date(iso).toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// — Data loading —
async function load() {
  loading.value = true
  error.value = null
  try {
    detail.value = await service.get(userId)
  } catch {
    error.value = 'Failed to load user details'
  } finally {
    loading.value = false
  }
}

onMounted(load)

// — Confirmation dialog —
const dialog = reactive({
  open: false,
  title: '',
  message: '',
  variant: 'warning' as 'error' | 'warning',
  loading: false,
  action: '',
})

function confirmAction(action: string) {
  const name = fullName.value
  const titles: Record<string, string> = {
    activate: 'Activate Account',
    suspend: 'Suspend Account',
    promote: 'Promote to Admin',
    demote: 'Demote to User',
    revoke: 'Force Logout',
    delete: 'Delete User',
  }
  const messages: Record<string, string> = {
    activate: `Activate the account for ${name}?`,
    suspend: `Suspend the account for ${name}? All active sessions will be revoked.`,
    promote: `Promote ${name} to Administrator?`,
    demote: `Demote ${name} back to regular User?`,
    revoke: `Revoke all active sessions for ${name}? They will be forced to log in again.`,
    delete: `Permanently delete ${name}'s account? This cannot be undone.`,
  }
  dialog.action = action
  dialog.title = titles[action] ?? ''
  dialog.message = messages[action] ?? ''
  dialog.variant = action === 'delete' || action === 'suspend' ? 'error' : 'warning'
  dialog.open = true
}

async function executeAction() {
  dialog.loading = true
  try {
    switch (dialog.action) {
      case 'activate':
        await service.updateStatus(userId, 'ACTIVE')
        toast.success('Account activated')
        break
      case 'suspend':
        await service.updateStatus(userId, 'SUSPENDED')
        toast.success('Account suspended')
        break
      case 'promote':
        await service.updateRole(userId, 'ROLE_ADMIN')
        toast.success('User promoted to Admin')
        break
      case 'demote':
        await service.updateRole(userId, 'ROLE_USER')
        toast.success('User demoted to User')
        break
      case 'revoke':
        await service.revokeSessions(userId)
        toast.success('All sessions revoked')
        break
      case 'delete':
        await service.deleteUser(userId)
        toast.success('User deleted')
        dialog.open = false
        router.push({ name: 'admin-users' })
        return
    }
    dialog.open = false
    await load()
  } catch {
    toast.error('Action failed')
  } finally {
    dialog.loading = false
  }
}
</script>

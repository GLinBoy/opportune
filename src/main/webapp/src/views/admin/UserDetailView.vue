<template>
  <div>
    <!-- Loading skeleton -->
    <template v-if="loading">
      <FormCard :collapsible="false">
        <template #title><v-skeleton-loader type="heading" /></template>
        <v-skeleton-loader type="card" />
      </FormCard>
      <FormCard :collapsible="false" class="mt-4">
        <template #title><v-skeleton-loader type="heading" /></template>
        <v-skeleton-loader type="table" />
      </FormCard>
    </template>

    <!-- Error state -->
    <v-alert v-else-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <template v-else-if="detail">
      <!-- Profile card -->
      <FormCard :collapsible="false">
        <template #title>
          <v-icon icon="mdi-account-outline" class="mr-2" />
          Profile Information
        </template>

        <v-row align="center">
          <v-col cols="auto">
            <UserAvatar
              :email="detail.profile.email"
              :avatar-url="detail.profile.avatar"
              :size="72"
            />
          </v-col>
          <v-col>
            <div class="text-h6 font-weight-medium">{{ fullName }}</div>
            <div class="text-body-2 text-medium-emphasis d-flex align-center">
              {{ detail.profile.email }}
              <span class="px-1" />
              <v-tooltip
                v-if="detail.profile.emailVerification !== undefined"
                :text="detail.profile.emailVerification ? 'Email verified' : 'Email not verified'"
                location="bottom"
              >
                <template #activator="{ props }">
                  <v-icon
                    v-bind="props"
                    :icon="
                      detail.profile.emailVerification
                        ? 'mdi-email-check-outline'
                        : 'mdi-email-alert-outline'
                    "
                    :color="detail.profile.emailVerification ? 'success' : 'warning'"
                    size="small"
                    class="mr-1"
                  />
                </template>
              </v-tooltip>
            </div>
            <div class="d-flex flex-wrap gap-3 mt-2">
              <v-chip
                :color="statusColor(detail.profile.status)"
                size="small"
                class="mx-1"
                label
                :prepend-icon="statusIcon(detail.profile.status)"
              >
                {{ detail.profile.status ?? '—' }}
              </v-chip>
              <v-chip
                v-for="role in detail.profile.roles"
                :key="role"
                size="small"
                class="mx-1"
                :color="role === 'ROLE_ADMIN' ? 'warning' : 'default'"
                :prepend-icon="
                  role === 'ROLE_ADMIN' ? 'mdi-shield-account' : 'mdi-account-circle-outline'
                "
                label
              >
                {{ role === 'ROLE_ADMIN' ? 'Admin' : 'User' }}
              </v-chip>
            </div>
          </v-col>
          <!-- Compact KPI cards -->
          <v-col cols="12" sm="auto" class="d-flex flex-wrap">
            <div class="kpi-card-compact mx-1">
              <div class="kpi-card-compact__body">
                <div class="kpi-card-compact__icon" style="color: rgb(var(--v-theme-primary))">
                  <v-icon size="28">mdi-file-document-multiple-outline</v-icon>
                </div>
                <div class="kpi-card-compact__content">
                  <div class="kpi-card-compact__value">{{ detail.applicationCount }}</div>
                  <div class="kpi-card-compact__label">Applications</div>
                </div>
              </div>
              <div
                class="kpi-card-compact__accent"
                style="background: rgb(var(--v-theme-primary))"
              />
            </div>
            <div class="kpi-card-compact mx-1">
              <div class="kpi-card-compact__body">
                <div class="kpi-card-compact__icon" style="color: rgb(var(--v-theme-info))">
                  <v-icon size="28">mdi-monitor-cellphone</v-icon>
                </div>
                <div class="kpi-card-compact__content">
                  <div class="kpi-card-compact__value">{{ activeSessions }}</div>
                  <div class="kpi-card-compact__label">Active Sessions</div>
                </div>
              </div>
              <div
                class="kpi-card-compact__accent"
                style="background: rgb(var(--v-theme-info))"
              />
            </div>
          </v-col>
        </v-row>

        <!-- Profile details -->
        <v-divider class="my-4" />
        <v-row density="comfortable">
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
        </v-row>

        <template #actions>
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
        </template>
      </FormCard>

      <!-- Sessions table -->
      <FormCard class="mt-4" :collapsible="true" default-open>
        <template #title>
          <v-icon icon="mdi-monitor-cellphone" class="mr-2" />
          Sessions ({{ detail.sessions.length }})
        </template>
        <v-data-table
          :headers="sessionHeaders"
          :items="detail.sessions"
          item-value="refreshTokenId"
          density="compact"
        >
          <template #item.status="{ item }">
            <v-chip
              :color="item.status === 'ACTIVE' ? 'success' : 'default'"
              size="x-small"
              label
              :prepend-icon="sessionStatusIcon(item.status)"
            >
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
      </FormCard>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToastStore } from '../../stores/toast'
import AdminUserService from '../../services/admin/admin-user.service'
import type { IAdminUserDetail } from '../../models'
import ConfirmDialog from '../../components/ConfirmDialog.vue'
import UserAvatar from '../../components/UserAvatar.vue'
import FormCard from '../../components/forms/FormCard.vue'

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

function statusIcon(status: string | null | undefined) {
  switch (status) {
    case 'ACTIVE':
      return 'mdi-account-check-outline'
    case 'SUSPENDED':
      return 'mdi-account-cancel-outline'
    case 'PENDING_VERIFICATION':
      return 'mdi-account-clock-outline'
    default:
      return undefined
  }
}

function sessionStatusIcon(status: string | null | undefined) {
  switch (status) {
    case 'ACTIVE':
      return 'mdi-check-circle'
    case 'EXPIRED':
      return 'mdi-clock-alert-outline'
    case 'REVOKED':
      return 'mdi-close-circle'
    default:
      return undefined
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

<style scoped>
.kpi-card-compact {
  position: relative;
  overflow: hidden;
  min-width: 160px;
  border: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 12px;
  background: rgb(var(--v-theme-surface));
}

.kpi-card-compact__body {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  min-height: 60px;
}

.kpi-card-compact__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.85;
  flex-shrink: 0;
}

.kpi-card-compact__icon .v-icon {
  padding: 12px;
  border-radius: 50%;
  background: color-mix(in oklch, currentColor 12%, transparent);
}

.kpi-card-compact__content {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.kpi-card-compact__value {
  font-size: 1.35rem;
  font-weight: 700;
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.01em;
}

.kpi-card-compact__label {
  font-size: 0.7rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  line-height: 1.3;
  color: rgba(var(--v-theme-on-surface), var(--v-medium-emphasis-opacity));
}

.kpi-card-compact__accent {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  opacity: 0.7;
  border-radius: 0 0 12px 12px;
}
</style>

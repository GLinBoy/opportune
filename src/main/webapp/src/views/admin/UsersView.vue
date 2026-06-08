<template>
  <v-container fluid class="pa-4">
    <!-- Toolbar -->
    <v-row align="center" class="mb-4" density="compact">
      <v-col cols="12" sm="5" md="4">
        <v-text-field
          v-model="searchText"
          density="compact"
          variant="outlined"
          prepend-inner-icon="mdi-magnify"
          placeholder="Search by name or email…"
          clearable
          hide-details
          @keyup.enter="applyFilters"
          @click:clear="onClearSearch"
        />
      </v-col>
      <v-col cols="12" sm="4" md="3" class="pl-sm-3 mt-3 mt-sm-0">
        <v-select
          v-model="statusFilter"
          :items="statusOptions"
          density="compact"
          variant="outlined"
          placeholder="All statuses"
          clearable
          hide-details
          @update:model-value="applyFilters"
        />
      </v-col>
    </v-row>

    <!-- Data table -->
    <v-card flat border rounded="lg">
      <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="users"
        :items-length="totalItems"
        :loading="loading"
        :page="page"
        item-value="id"
        @update:options="onTableOptions"
        @click:row="navigateToDetail"
      >
        <!-- Name -->
        <template #item.name="{ item }">
          <div class="d-flex align-center gap-2 py-1">
            <UserAvatar :email="item.email" :avatar-url="item.avatar" :size="32" />
            <div class="ml-3">
              <div class="text-body-2 font-weight-medium">{{ fullName(item) }}</div>
              <div class="text-caption text-medium-emphasis">{{ item.email }}</div>
            </div>
          </div>
        </template>

        <!-- Status -->
        <template #item.status="{ item }">
          <v-chip
            :color="statusColor(item.status)"
            :prepend-icon="statusIcon(item.status)"
            size="small"
            label
          >
            {{ item.status ?? '—' }}
          </v-chip>
        </template>

        <!-- Roles -->
        <template #item.roles="{ item }">
          <v-chip
            v-for="role in item.roles"
            :key="role"
            size="x-small"
            :color="role === 'ROLE_ADMIN' ? 'warning' : 'default'"
            :prepend-icon="role === 'ROLE_ADMIN' ? 'mdi-shield-crown-outline' : 'mdi-account-outline'"
            class="mr-1"
            label
          >
            {{ role === 'ROLE_ADMIN' ? 'Admin' : 'User' }}
          </v-chip>
        </template>

        <!-- Last Login -->
        <template #item.lastLogin="{ item }">
          {{ item.lastLogin ? formatDate(item.lastLogin) : '—' }}
        </template>

        <!-- Registered -->
        <template #item.createdDate="{ item }">
          {{ item.createdDate ? formatDate(item.createdDate) : '—' }}
        </template>

        <!-- Actions -->
        <template #item.actions="{ item }">
          <v-menu>
            <template #activator="{ props }">
              <v-btn size="small" variant="text" icon="mdi-dots-vertical" v-bind="props" @click.stop />
            </template>
            <v-list density="compact" min-width="200">
              <v-list-item
                v-if="item.status !== 'ACTIVE'"
                prepend-icon="mdi-account-check-outline"
                title="Activate"
                @click="confirmAction('activate', item)"
              />
              <v-list-item
                v-if="item.status !== 'SUSPENDED'"
                prepend-icon="mdi-account-cancel-outline"
                title="Suspend"
                @click="confirmAction('suspend', item)"
              />
              <v-divider />
              <v-list-item
                v-if="!item.roles?.includes('ROLE_ADMIN')"
                prepend-icon="mdi-shield-account-outline"
                title="Promote to Admin"
                @click="confirmAction('promote', item)"
              />
              <v-list-item
                v-if="item.roles?.includes('ROLE_ADMIN')"
                prepend-icon="mdi-shield-off-outline"
                title="Demote to User"
                @click="confirmAction('demote', item)"
              />
              <v-divider />
              <v-list-item
                prepend-icon="mdi-logout"
                title="Force Logout"
                @click="confirmAction('revoke', item)"
              />
              <v-list-item
                prepend-icon="mdi-delete-outline"
                title="Delete"
                class="text-error"
                @click="confirmAction('delete', item)"
              />
            </v-list>
          </v-menu>
        </template>

        <!-- Empty state -->
        <template #no-data>
          <div class="pa-8 text-center text-medium-emphasis">
            <v-icon icon="mdi-account-off-outline" size="48" class="mb-2" />
            <div>No users found</div>
          </div>
        </template>
      </v-data-table-server>
    </v-card>

    <!-- Confirmation Dialog -->
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useToastStore } from '../../stores/toast'
import AdminUserService from '../../services/admin/admin-user.service'
import type { IAdminUserListItem } from '../../models'
import ConfirmDialog from '../../components/ConfirmDialog.vue'
import UserAvatar from '../../components/UserAvatar.vue'

const toast = useToastStore()
const router = useRouter()
const service = new AdminUserService()

// — Table state —
const users = ref<IAdminUserListItem[]>([])
const totalItems = ref(0)
const loading = ref(false)
const page = ref(1)
const itemsPerPage = ref(15)

// — Filter state —
const searchText = ref('')
const statusFilter = ref<string | null>(null)

const statusOptions = [
  { title: 'Active', value: 'ACTIVE' },
  { title: 'Suspended', value: 'SUSPENDED' },
  { title: 'Pending Verification', value: 'PENDING_VERIFICATION' },
]

const headers = [
  { title: 'User', key: 'name', sortable: false },
  { title: 'Status', key: 'status', sortable: true },
  { title: 'Roles', key: 'roles', sortable: false },
  { title: 'Last Login', key: 'lastLogin', sortable: true },
  { title: 'Registered', key: 'createdDate', sortable: true },
  { title: '', key: 'actions', sortable: false, align: 'end' as const },
]

// — Confirmation dialog —
const dialog = reactive({
  open: false,
  title: '',
  message: '',
  variant: 'warning' as 'error' | 'warning',
  loading: false,
  action: '' as string,
  targetId: '' as string,
})

// — Helpers —
function fullName(item: IAdminUserListItem) {
  return [item.forename, item.surname].filter(Boolean).join(' ') || item.email || '—'
}

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

function formatDate(iso: string) {
  return new Date(iso).toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

function statusIcon(status: string | null | undefined) {
  switch (status) {
    case 'ACTIVE':
      return 'mdi-check-circle-outline'
    case 'SUSPENDED':
      return 'mdi-cancel'
    case 'PENDING_VERIFICATION':
      return 'mdi-clock-outline'
    default:
      return 'mdi-help-circle-outline'
  }
}

function navigateToDetail(_: any, row: { item: IAdminUserListItem }) {
  router.push({ name: 'admin-user-detail', params: { id: row.item.id } })
}

// — RSQL builder —
function buildFilter(): string | undefined {
  const parts: string[] = []
  if (searchText.value.trim()) {
    const terms = searchText.value.trim().split(/\s+/)
    const termFilters = terms.map(term => {
      const q = encodeRsqlValue(term)
      return `(email=ilike="${q}",forename=ilike="${q}",surname=ilike="${q}")`
    })
    parts.push(termFilters.length === 1 ? termFilters[0]! : `(${termFilters.join(';')})`)
  }
  if (statusFilter.value) {
    parts.push(`status==${statusFilter.value}`)
  }
  return parts.length ? parts.join(';') : undefined
}

function encodeRsqlValue(v: string) {
  // escape RSQL special chars inside values
  return v.replace(/['"();,=<>!~]/g, '\\$&')
}

// — Data loading —
async function loadUsers(opts: {
  page: number
  itemsPerPage: number
  sortBy?: { key: string; order: string }[]
}) {
  loading.value = true
  try {
    const sortBy = opts.sortBy?.[0]
    const query: Record<string, unknown> = {
      page: opts.page - 1,
      size: opts.itemsPerPage,
    }
    if (sortBy) {
      query.sort = `${sortBy.key},${sortBy.order}`
    }
    const filter = buildFilter()
    if (filter) {
      query.filter = filter
    }
    const res = await service.list(query)
    users.value = res.data
    totalItems.value = Number(res.headers['x-total-count'] ?? res.data.length)
  } catch {
    toast.error('Failed to load users')
  } finally {
    loading.value = false
  }
}

function onTableOptions(opts: {
  page: number
  itemsPerPage: number
  sortBy: { key: string; order: string }[]
}) {
  page.value = opts.page
  itemsPerPage.value = opts.itemsPerPage
  loadUsers(opts)
}

function applyFilters() {
  page.value = 1
  loadUsers({ page: 1, itemsPerPage: itemsPerPage.value })
}

function onClearSearch() {
  searchText.value = ''
  applyFilters()
}

// — Action flow —
function confirmAction(action: string, item: IAdminUserListItem) {
  const name = fullName(item)
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
  dialog.targetId = item.id!
  dialog.title = titles[action] ?? ''
  dialog.message = messages[action] ?? ''
  dialog.variant = action === 'delete' || action === 'suspend' ? 'error' : 'warning'
  dialog.open = true
}

async function executeAction() {
  dialog.loading = true
  try {
    const id = dialog.targetId
    switch (dialog.action) {
      case 'activate':
        await service.updateStatus(id, 'ACTIVE')
        toast.success('Account activated')
        break
      case 'suspend':
        await service.updateStatus(id, 'SUSPENDED')
        toast.success('Account suspended')
        break
      case 'promote':
        await service.updateRole(id, 'ROLE_ADMIN')
        toast.success('User promoted to Admin')
        break
      case 'demote':
        await service.updateRole(id, 'ROLE_USER')
        toast.success('User demoted to User')
        break
      case 'revoke':
        await service.revokeSessions(id)
        toast.success('All sessions revoked')
        break
      case 'delete':
        await service.deleteUser(id)
        toast.success('User deleted')
        break
    }
    dialog.open = false
    await loadUsers({ page: page.value, itemsPerPage: itemsPerPage.value })
  } catch {
    toast.error(`Action failed`)
  } finally {
    dialog.loading = false
  }
}
</script>

<style scoped>
:deep(.v-data-table__tr) {
  cursor: pointer;
}
</style>

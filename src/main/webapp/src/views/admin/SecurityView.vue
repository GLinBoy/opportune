<template>
  <v-container fluid class="pa-4">
    <!-- Tab switcher -->
    <v-tabs v-model="activeTab" class="mb-4" color="primary">
      <v-tab value="active">
        <v-icon start icon="mdi-account-multiple-outline" />
        Live Sessions
      </v-tab>
      <v-tab value="audit">
        <v-icon start icon="mdi-history" />
        Revocation Audit Log
      </v-tab>
    </v-tabs>

    <!-- ─── LIVE SESSIONS ─── -->
    <div v-if="activeTab === 'active'">
      <!-- Filter toolbar -->
      <v-row align="center" class="mb-4" no-gutters>
        <v-col cols="12" sm="4" md="3">
          <v-text-field
            v-model="filterUser"
            density="compact"
            variant="outlined"
            prepend-inner-icon="mdi-account-search-outline"
            placeholder="Search by name or email…"
            clearable
            hide-details
            @keyup.enter="loadSessions"
            @click:clear="onClearFilters"
          />
        </v-col>
        <v-col cols="12" sm="6" md="4" class="pl-sm-2 mt-2 mt-sm-0">
          <v-text-field
            v-model="filterIp"
            density="compact"
            variant="outlined"
            prepend-inner-icon="mdi-ip-network-outline"
            placeholder="IP address…"
            clearable
            hide-details
            @keyup.enter="loadSessions"
            @click:clear="onClearFilters"
          />
        </v-col>
        <v-col cols="12" sm="3" md="2" class="pl-sm-2 mt-2 mt-sm-0">
          <v-text-field
            v-model="filterGeo"
            density="compact"
            variant="outlined"
            prepend-inner-icon="mdi-map-marker-outline"
            placeholder="Geo / country…"
            clearable
            hide-details
            @keyup.enter="loadSessions"
            @click:clear="onClearFilters"
          />
        </v-col>
        <v-col cols="12" sm="2" md="2" class="pl-sm-2 mt-2 mt-sm-0">
          <v-select
            v-model="filterDevice"
            :items="deviceOptions"
            density="compact"
            variant="outlined"
            placeholder="Device"
            clearable
            hide-details
            @update:model-value="() => loadSessions()"
          />
        </v-col>
      </v-row>

      <!-- Active sessions table -->
      <v-card flat border rounded="lg">
        <v-data-table-server
          v-model:items-per-page="itemsPerPage"
          :headers="activeHeaders"
          :items="activeSessions"
          :items-length="totalActive"
          :loading="loadingActive"
          :page="page"
          item-value="refreshTokenId"
          @update:options="onActiveTableOptions"
        >
          <!-- User column -->
          <template #item.user="{ item }">
            <div class="d-flex align-center gap-2 py-1">
              <v-avatar size="28" color="primary" class="text-caption font-weight-bold">
                {{ userInitial(item) }}
              </v-avatar>
              <div>
                <div class="text-body-2 font-weight-medium">{{ userFullName(item) }}</div>
                <div class="text-caption text-medium-emphasis">{{ item.userEmail ?? '—' }}</div>
              </div>
            </div>
          </template>

          <!-- IP + Geo -->
          <template #item.clientIp="{ item }">
            <div>
              <div class="text-body-2">{{ item.clientIp ?? '—' }}</div>
              <div class="text-caption text-medium-emphasis">{{ item.clientGeo ?? '' }}</div>
            </div>
          </template>

          <!-- Device -->
          <template #item.device="{ item }">
            <div class="d-flex align-center gap-1">
              <v-icon
                :icon="item.isMobile ? 'mdi-cellphone' : 'mdi-monitor'"
                size="16"
                color="medium-emphasis"
              />
              <div>
                <div class="text-body-2">{{ item.browser ?? '—' }}</div>
                <div class="text-caption text-medium-emphasis">{{ item.os ?? '' }}</div>
              </div>
            </div>
          </template>

          <!-- Login time -->
          <template #item.loginAt="{ item }">
            {{ item.loginAt ? formatDateTime(item.loginAt) : '—' }}
          </template>

          <!-- Last active -->
          <template #item.lastActiveAt="{ item }">
            {{ item.lastActiveAt ? formatDateTime(item.lastActiveAt) : '—' }}
          </template>

          <!-- Actions -->
          <template #item.actions="{ item }">
            <v-btn
              size="small"
              variant="text"
              icon="mdi-logout"
              title="Revoke session"
              color="error"
              @click="confirmRevoke(item)"
            />
            <v-menu>
              <template #activator="{ props }">
                <v-btn size="small" variant="text" icon="mdi-dots-vertical" v-bind="props" />
              </template>
              <v-list density="compact" min-width="220">
                <v-list-item
                  prepend-icon="mdi-account-cancel-outline"
                  title="Revoke all sessions for this user"
                  @click="confirmBulkByUser(item)"
                />
                <v-list-item
                  prepend-icon="mdi-ip-network-outline"
                  title="Revoke all sessions from this IP"
                  @click="confirmBulkByIp(item)"
                />
              </v-list>
            </v-menu>
          </template>

          <!-- Empty -->
          <template #no-data>
            <div class="pa-8 text-center text-medium-emphasis">
              <v-icon icon="mdi-shield-check-outline" size="48" class="mb-2" />
              <div>No active sessions found</div>
            </div>
          </template>
        </v-data-table-server>
      </v-card>
    </div>

    <!-- ─── REVOCATION AUDIT LOG ─── -->
    <div v-else>
      <v-row align="center" class="mb-4" no-gutters>
        <v-col cols="12" sm="4" md="3">
          <v-text-field
            v-model="auditFilterUser"
            density="compact"
            variant="outlined"
            prepend-inner-icon="mdi-account-search-outline"
            placeholder="Search by name or email…"
            clearable
            hide-details
            @keyup.enter="loadAudit"
            @click:clear="onClearAudit"
          />
        </v-col>
        <v-col cols="12" sm="6" md="4" class="pl-sm-2 mt-2 mt-sm-0">
          <v-text-field
            v-model="auditFilterIp"
            density="compact"
            variant="outlined"
            prepend-inner-icon="mdi-ip-network-outline"
            placeholder="IP address…"
            clearable
            hide-details
            @keyup.enter="loadAudit"
            @click:clear="onClearAudit"
          />
        </v-col>
        <v-col cols="12" sm="3" md="2" class="pl-sm-2 mt-2 mt-sm-0">
          <v-select
            v-model="auditFilterReason"
            :items="revocationReasonOptions"
            density="compact"
            variant="outlined"
            placeholder="Reason"
            clearable
            hide-details
            @update:model-value="() => loadAudit()"
          />
        </v-col>
      </v-row>

      <v-card flat border rounded="lg">
        <v-data-table-server
          v-model:items-per-page="auditItemsPerPage"
          :headers="auditHeaders"
          :items="auditSessions"
          :items-length="totalAudit"
          :loading="loadingAudit"
          :page="auditPage"
          item-value="refreshTokenId"
          @update:options="onAuditTableOptions"
        >
          <!-- User -->
          <template #item.user="{ item }">
            <div class="d-flex align-center gap-2 py-1">
              <v-avatar size="28" color="grey" class="text-caption font-weight-bold">
                {{ userInitial(item) }}
              </v-avatar>
              <div>
                <div class="text-body-2 font-weight-medium">{{ userFullName(item) }}</div>
                <div class="text-caption text-medium-emphasis">{{ item.userEmail ?? '—' }}</div>
              </div>
            </div>
          </template>

          <!-- IP + Geo -->
          <template #item.clientIp="{ item }">
            <div>
              <div class="text-body-2">{{ item.clientIp ?? '—' }}</div>
              <div class="text-caption text-medium-emphasis">{{ item.clientGeo ?? '' }}</div>
            </div>
          </template>

          <!-- Device -->
          <template #item.device="{ item }">
            <div class="d-flex align-center gap-1">
              <v-icon
                :icon="item.isMobile ? 'mdi-cellphone' : 'mdi-monitor'"
                size="16"
                color="medium-emphasis"
              />
              <span class="text-body-2">{{ item.browser ?? '—' }} / {{ item.os ?? '—' }}</span>
            </div>
          </template>

          <!-- Revocation reason -->
          <template #item.revocationReason="{ item }">
            <v-chip :color="reasonColor(item.revocationReason)" size="small" label>
              {{ formatReason(item.revocationReason) }}
            </v-chip>
          </template>

          <!-- Revoked at -->
          <template #item.revokedAt="{ item }">
            {{ item.revokedAt ? formatDateTime(item.revokedAt) : '—' }}
          </template>

          <!-- Empty -->
          <template #no-data>
            <div class="pa-8 text-center text-medium-emphasis">
              <v-icon icon="mdi-clipboard-check-outline" size="48" class="mb-2" />
              <div>No revocation records found</div>
            </div>
          </template>
        </v-data-table-server>
      </v-card>
    </div>

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
import { ref, reactive, watch } from 'vue'
import { useToastStore } from '../../stores/toast'
import AdminSessionService from '../../services/admin/admin-session.service'
import type { IAdminSessionListItem } from '../../models'
import ConfirmDialog from '../../components/ConfirmDialog.vue'

const toast = useToastStore()
const service = new AdminSessionService()

// ─── Tab ───
const activeTab = ref<'active' | 'audit'>('active')

watch(activeTab, (tab) => {
  if (tab === 'active') loadSessions()
  else loadAudit()
})

// ─── Active sessions state ───
const activeSessions = ref<IAdminSessionListItem[]>([])
const totalActive = ref(0)
const loadingActive = ref(false)
const page = ref(1)
const itemsPerPage = ref(15)
const lastActiveOpts = ref<{
  page: number
  itemsPerPage: number
  sortBy?: { key: string; order: string }[]
}>({
  page: 1,
  itemsPerPage: 15,
})

// ─── Active filters ───
const filterUser = ref('')
const filterIp = ref('')
const filterGeo = ref('')
const filterDevice = ref<'mobile' | 'desktop' | null>(null)

const deviceOptions = [
  { title: 'Mobile', value: 'mobile' },
  { title: 'Desktop', value: 'desktop' },
]

const activeHeaders = [
  { title: 'User', key: 'user', sortable: false },
  { title: 'IP / Location', key: 'clientIp', sortable: true },
  { title: 'Device', key: 'device', sortable: false },
  { title: 'Login Time', key: 'loginAt', sortable: true },
  { title: 'Last Active', key: 'lastActiveAt', sortable: true },
  { title: '', key: 'actions', sortable: false, align: 'end' as const },
]

// ─── Audit log state ───
const auditSessions = ref<IAdminSessionListItem[]>([])
const totalAudit = ref(0)
const loadingAudit = ref(false)
const auditPage = ref(1)
const auditItemsPerPage = ref(15)
const lastAuditOpts = ref<{
  page: number
  itemsPerPage: number
  sortBy?: { key: string; order: string }[]
}>({
  page: 1,
  itemsPerPage: 15,
})

// ─── Audit filters ───
const auditFilterUser = ref('')
const auditFilterIp = ref('')
const auditFilterReason = ref<string | null>(null)

const revocationReasonOptions = [
  { title: 'Admin Action', value: 'ADMIN_ACTION' },
  { title: 'User Initiated', value: 'USER_INITIATED' },
  { title: 'User — All Sessions', value: 'USER_ALL_SESSIONS' },
  { title: 'Account Suspended', value: 'ACCOUNT_SUSPENDED' },
  { title: 'Password Change', value: 'PASSWORD_CHANGE' },
  { title: 'Password Reset', value: 'PASSWORD_RESET' },
  { title: 'Suspicious Activity', value: 'SUSPICIOUS_ACTIVITY' },
  { title: 'Device Lost/Stolen', value: 'DEVICE_LOST_OR_STOLEN' },
  { title: 'Security Incident', value: 'SECURITY_INCIDENT' },
  { title: 'Other', value: 'OTHER' },
]

const auditHeaders = [
  { title: 'User', key: 'user', sortable: false },
  { title: 'IP / Location', key: 'clientIp', sortable: true },
  { title: 'Device', key: 'device', sortable: false },
  { title: 'Revocation Reason', key: 'revocationReason', sortable: true },
  { title: 'Revoked At', key: 'revokedAt', sortable: true },
]

// ─── Confirmation dialog ───
const dialog = reactive({
  open: false,
  title: '',
  message: '',
  variant: 'warning' as 'error' | 'warning',
  loading: false,
  action: '' as 'revoke' | 'bulk-user' | 'bulk-ip',
  sessionId: '' as string,
  profileId: '' as string,
  ip: '' as string,
})

// ─── Helpers ───
function userFullName(item: IAdminSessionListItem) {
  return [item.userForename, item.userSurname].filter(Boolean).join(' ') || item.userEmail || '—'
}

function userInitial(item: IAdminSessionListItem) {
  const f = item.userForename?.[0] ?? ''
  const s = item.userSurname?.[0] ?? ''
  return (f + s).toUpperCase() || (item.userEmail?.[0] ?? '?').toUpperCase()
}

function formatDateTime(iso: string) {
  return new Date(iso).toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatReason(reason?: string | null) {
  if (!reason) return '—'
  return reason
    .split('_')
    .map((w) => w[0] + w.slice(1).toLowerCase())
    .join(' ')
}

function reasonColor(reason?: string | null) {
  switch (reason) {
    case 'ADMIN_ACTION':
      return 'warning'
    case 'ACCOUNT_SUSPENDED':
    case 'SUSPICIOUS_ACTIVITY':
    case 'SECURITY_INCIDENT':
      return 'error'
    case 'USER_INITIATED':
    case 'USER_ALL_SESSIONS':
      return 'info'
    default:
      return 'default'
  }
}

function encodeRsql(v: string) {
  return v.replace(/['"();,=<>!~]/gu, (m) => `\\${m}`)
}

// ─── RSQL filter builders ───
function buildActiveFilter(): string | undefined {
  const parts: string[] = ['status==ACTIVE']
  if (filterUser.value.trim()) {
    const terms = filterUser.value.trim().split(/\s+/)
    const termFilters = terms.map(term => {
      const q = encodeRsql(term)
      return `(profile.email=ilike="${q}",profile.forename=ilike="${q}",profile.surname=ilike="${q}")`
    })
    parts.push(termFilters.length === 1 ? termFilters[0]! : `(${termFilters.join(';')})`)
  }
  if (filterIp.value.trim()) {
    parts.push(`clientIp==${encodeRsql(filterIp.value.trim())}`)
  }
  if (filterGeo.value.trim()) {
    const q = encodeRsql(filterGeo.value.trim())
    parts.push(`clientGeo=ilike=*${q}*`)
  }
  if (filterDevice.value === 'mobile') {
    parts.push('isMobile==true')
  } else if (filterDevice.value === 'desktop') {
    parts.push('isMobile==false')
  }
  return parts.join(';')
}

function buildAuditFilter(): string | undefined {
  const parts: string[] = ['status==REVOKED']
  if (auditFilterUser.value.trim()) {
    const terms = auditFilterUser.value.trim().split(/\s+/)
    const termFilters = terms.map(term => {
      const q = encodeRsql(term)
      return `(profile.email=ilike="${q}",profile.forename=ilike="${q}",profile.surname=ilike="${q}")`
    })
    parts.push(termFilters.length === 1 ? termFilters[0]! : `(${termFilters.join(';')})`)
  }
  if (auditFilterIp.value.trim()) {
    parts.push(`clientIp==${encodeRsql(auditFilterIp.value.trim())}`)
  }
  if (auditFilterReason.value) {
    parts.push(`revocationReason==${auditFilterReason.value}`)
  }
  return parts.join(';')
}

// ─── Data loading ───
async function loadSessions(opts?: {
  page: number
  itemsPerPage: number
  sortBy?: { key: string; order: string }[]
}) {
  const o = opts ?? lastActiveOpts.value
  lastActiveOpts.value = o
  loadingActive.value = true
  try {
    const query: Record<string, unknown> = {
      page: o.page - 1,
      size: o.itemsPerPage,
    }
    const sortBy = o.sortBy?.[0]
    if (sortBy) query.sort = `${sortBy.key},${sortBy.order}`
    query.filter = buildActiveFilter()
    const res = await service.list(query)
    activeSessions.value = res.data
    totalActive.value = Number(res.headers['x-total-count'] ?? res.data.length)
  } catch {
    toast.error('Failed to load active sessions')
  } finally {
    loadingActive.value = false
  }
}

async function loadAudit(opts?: {
  page: number
  itemsPerPage: number
  sortBy?: { key: string; order: string }[]
}) {
  const o = opts ?? lastAuditOpts.value
  lastAuditOpts.value = o
  loadingAudit.value = true
  try {
    const query: Record<string, unknown> = {
      page: o.page - 1,
      size: o.itemsPerPage,
    }
    const sortBy = o.sortBy?.[0]
    if (sortBy) query.sort = `${sortBy.key},${sortBy.order}`
    query.filter = buildAuditFilter()
    const res = await service.list(query)
    auditSessions.value = res.data
    totalAudit.value = Number(res.headers['x-total-count'] ?? res.data.length)
  } catch {
    toast.error('Failed to load revocation log')
  } finally {
    loadingAudit.value = false
  }
}

function onActiveTableOptions(opts: {
  page: number
  itemsPerPage: number
  sortBy?: { key: string; order: string }[]
}) {
  page.value = opts.page
  itemsPerPage.value = opts.itemsPerPage
  loadSessions(opts)
}

function onAuditTableOptions(opts: {
  page: number
  itemsPerPage: number
  sortBy?: { key: string; order: string }[]
}) {
  auditPage.value = opts.page
  auditItemsPerPage.value = opts.itemsPerPage
  loadAudit(opts)
}

// ─── Filter reset ───
function onClearFilters() {
  filterUser.value = ''
  filterIp.value = ''
  filterGeo.value = ''
  filterDevice.value = null
  loadSessions()
}

function onClearAudit() {
  auditFilterUser.value = ''
  auditFilterIp.value = ''
  auditFilterReason.value = null
  loadAudit()
}

// ─── Action confirmations ───
function confirmRevoke(item: IAdminSessionListItem) {
  dialog.action = 'revoke'
  dialog.sessionId = item.refreshTokenId ?? ''
  dialog.title = 'Revoke Session'
  dialog.message = `Revoke the session for ${userFullName(item)} (${
    item.clientIp ?? 'unknown IP'
  })?`
  dialog.variant = 'warning'
  dialog.open = true
}

function confirmBulkByUser(item: IAdminSessionListItem) {
  dialog.action = 'bulk-user'
  dialog.profileId = item.profileId ?? ''
  dialog.title = 'Bulk Revoke — User'
  dialog.message = `Revoke ALL active sessions for ${userFullName(
    item
  )}? This will force-logout the user everywhere.`
  dialog.variant = 'error'
  dialog.open = true
}

function confirmBulkByIp(item: IAdminSessionListItem) {
  dialog.action = 'bulk-ip'
  dialog.ip = item.clientIp ?? ''
  dialog.title = 'Bulk Revoke — IP'
  dialog.message = `Revoke ALL active sessions from IP ${item.clientIp}? This affects every user logged in from that address.`
  dialog.variant = 'error'
  dialog.open = true
}

async function executeAction() {
  dialog.loading = true
  try {
    if (dialog.action === 'revoke') {
      await service.revokeSession(dialog.sessionId)
      toast.success('Session revoked')
    } else if (dialog.action === 'bulk-user') {
      await service.bulkRevokeByUser(dialog.profileId)
      toast.success('All sessions for this user have been revoked')
    } else if (dialog.action === 'bulk-ip') {
      await service.bulkRevokeByIp(dialog.ip)
      toast.success(`All sessions from ${dialog.ip} have been revoked`)
    }
    dialog.open = false
    loadSessions()
  } catch {
    toast.error('Action failed — please try again')
  } finally {
    dialog.loading = false
  }
}
</script>

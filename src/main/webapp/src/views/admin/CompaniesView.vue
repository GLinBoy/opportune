<template>
  <v-container fluid class="pa-4">
    <!-- Toolbar -->
    <v-row align="center" class="mb-4" no-gutters>
      <v-col cols="12" sm="5" md="4">
        <v-text-field
          v-model="searchText"
          density="compact"
          variant="outlined"
          prepend-inner-icon="mdi-magnify"
          placeholder="Search by name…"
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
      <v-col class="d-flex justify-end mt-3 mt-sm-0">
        <v-btn variant="tonal" color="primary" prepend-icon="mdi-magnify" @click="applyFilters">
          Search
        </v-btn>
      </v-col>
    </v-row>

    <!-- Data table -->
    <v-card flat border rounded="lg">
      <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="companies"
        :items-length="totalItems"
        :loading="loading"
        :page="page"
        item-value="id"
        @update:options="onTableOptions"
      >
        <!-- Name + logo -->
        <template #item.name="{ item }">
          <div class="d-flex align-center gap-2 py-1">
            <v-avatar size="32" color="primary" class="text-caption font-weight-bold">
              <v-img v-if="item.logo" :src="item.logo" :alt="item.name ?? ''" />
              <span v-else>{{ (item.name?.[0] ?? '?').toUpperCase() }}</span>
            </v-avatar>
            <div>
              <div class="text-body-2 font-weight-medium">{{ item.name ?? '—' }}</div>
              <div v-if="item.website" class="text-caption text-medium-emphasis">
                <a :href="item.website" target="_blank" rel="noopener noreferrer">{{
                  item.website
                }}</a>
              </div>
            </div>
          </div>
        </template>

        <!-- Status -->
        <template #item.status="{ item }">
          <v-chip :color="statusColor(item.status)" size="small" label>
            <v-icon start :icon="statusIcon(item.status)" />
            {{ statusLabel(item.status) }}
          </v-chip>
        </template>

        <!-- Industry -->
        <template #item.industry="{ item }">
          {{ item.industry ?? '—' }}
        </template>

        <!-- Application count -->
        <template #item.applicationCount="{ item }">
          <v-chip size="small" variant="tonal">{{ item.applicationCount ?? 0 }}</v-chip>
        </template>

        <!-- Created date -->
        <template #item.createdDate="{ item }">
          {{ item.createdDate ? formatDate(item.createdDate) : '—' }}
        </template>

        <!-- Actions -->
        <template #item.actions="{ item }">
          <v-btn
            size="small"
            variant="text"
            icon="mdi-pencil-outline"
            title="Edit details"
            @click="openEditDialog(item)"
          />
          <v-menu>
            <template #activator="{ props }">
              <v-btn size="small" variant="text" icon="mdi-dots-vertical" v-bind="props" />
            </template>
            <v-list density="compact" min-width="200">
              <v-list-item
                v-if="item.status !== 'BLOCKED'"
                prepend-icon="mdi-cancel"
                title="Blacklist"
                class="text-error"
                @click="confirmAction('blacklist', item)"
              />
              <v-list-item
                v-if="item.status === 'BLOCKED'"
                prepend-icon="mdi-check-circle-outline"
                title="Un-blacklist"
                @click="confirmAction('unblacklist', item)"
              />
            </v-list>
          </v-menu>
        </template>

        <!-- Empty state -->
        <template #no-data>
          <div class="pa-8 text-center text-medium-emphasis">
            <v-icon icon="mdi-office-building-off-outline" size="48" class="mb-2" />
            <div>No companies found</div>
          </div>
        </template>
      </v-data-table-server>
    </v-card>

    <!-- Confirmation Dialog (blacklist / un-blacklist) -->
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

    <!-- Edit Details Dialog -->
    <v-dialog v-model="editDialog.open" max-width="600" :persistent="editDialog.loading">
      <v-card rounded="lg">
        <v-card-title class="pa-6 pb-2 text-h6 font-weight-bold">Edit Company</v-card-title>
        <v-divider />
        <v-card-text class="pa-6">
          <v-row dense>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="editForm.name"
                label="Company name"
                density="compact"
                variant="outlined"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="editForm.industry"
                label="Industry"
                density="compact"
                variant="outlined"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="editForm.website"
                label="Website"
                density="compact"
                variant="outlined"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="editForm.companySize"
                label="Company size"
                density="compact"
                variant="outlined"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="editForm.location"
                label="Location"
                density="compact"
                variant="outlined"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="editForm.foundedYear"
                label="Founded year"
                density="compact"
                variant="outlined"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12">
              <v-select
                v-model="editForm.status"
                :items="statusOptions"
                label="Status"
                density="compact"
                variant="outlined"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="editForm.description"
                label="Description"
                density="compact"
                variant="outlined"
                rows="3"
                hide-details="auto"
              />
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="editForm.note"
                label="Note"
                density="compact"
                variant="outlined"
                rows="2"
                hide-details="auto"
              />
            </v-col>
          </v-row>
        </v-card-text>
        <v-divider />
        <v-card-actions class="pa-4 ga-2 justify-end">
          <v-btn variant="outlined" :disabled="editDialog.loading" @click="editDialog.open = false">
            Cancel
          </v-btn>
          <v-btn color="primary" variant="flat" :loading="editDialog.loading" @click="saveEdit">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useToastStore } from '../../stores/toast'
import AdminCompanyService from '../../services/admin/admin-company.service'
import type { IAdminCompanyListItem, IAdminCompanyUpdate } from '../../models'
import ConfirmDialog from '../../components/ConfirmDialog.vue'
import {
  getCompanyStatusColor,
  getCompanyStatusIcon,
  getCompanyStatusDisplay,
  CompanyStatus,
} from '../../models/enumerations/company-status.model'

const toast = useToastStore()
const service = new AdminCompanyService()

// — Table state —
const companies = ref<IAdminCompanyListItem[]>([])
const totalItems = ref(0)
const loading = ref(false)
const page = ref(1)
const itemsPerPage = ref(15)

// — Filter state —
const searchText = ref('')
const statusFilter = ref<string | null>(null)

const statusOptions = [
  { title: 'Interested', value: 'INTERESTED' },
  { title: 'Not Interested', value: 'NOT_INTERESTED' },
  { title: 'Blacklisted', value: 'BLOCKED' },
]

const headers = [
  { title: 'Company', key: 'name', sortable: false },
  { title: 'Status', key: 'status', sortable: true },
  { title: 'Industry', key: 'industry', sortable: false },
  { title: 'Applications', key: 'applicationCount', sortable: true, align: 'center' as const },
  { title: 'Created', key: 'createdDate', sortable: true },
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

// — Edit dialog —
const editDialog = reactive({
  open: false,
  loading: false,
  targetId: '' as string,
})

const editForm = reactive<IAdminCompanyUpdate>({
  name: '',
  industry: '',
  website: '',
  companySize: '',
  location: '',
  foundedYear: '',
  description: '',
  note: '',
  status: undefined,
})

// — Helpers —
function statusColor(status: string | null | undefined) {
  return getCompanyStatusColor(status)
}

function statusIcon(status: string | null | undefined) {
  return getCompanyStatusIcon(status)
}

function statusLabel(status: string | null | undefined) {
  if (!status) return '—'
  return getCompanyStatusDisplay(status as CompanyStatus)
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

// — RSQL builder —
function buildFilter(): string | undefined {
  const parts: string[] = []
  if (searchText.value.trim()) {
    const q = encodeRsqlValue(searchText.value.trim())
    parts.push(`name=ilike=*${q}*`)
  }
  if (statusFilter.value) {
    parts.push(`status==${statusFilter.value}`)
  }
  return parts.length ? parts.join(';') : undefined
}

function encodeRsqlValue(v: string) {
  return v.replace(/['"();,=<>!~]/g, '\\$&')
}

// — Data loading —
async function loadCompanies(opts: {
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
    companies.value = res.data
    totalItems.value = Number(res.headers['x-total-count'] ?? res.data.length)
  } catch {
    toast.error('Failed to load companies')
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
  loadCompanies(opts)
}

function applyFilters() {
  page.value = 1
  loadCompanies({ page: 1, itemsPerPage: itemsPerPage.value })
}

function onClearSearch() {
  searchText.value = ''
  applyFilters()
}

// — Blacklist / un-blacklist confirmation —
function confirmAction(action: string, item: IAdminCompanyListItem) {
  const name = item.name ?? '(unnamed)'
  dialog.action = action
  dialog.targetId = item.id!
  if (action === 'blacklist') {
    dialog.title = 'Blacklist Company'
    dialog.message = `Blacklist "${name}"? Users will no longer be able to track applications to this company.`
    dialog.variant = 'error'
  } else {
    dialog.title = 'Remove Blacklist'
    dialog.message = `Remove "${name}" from the blacklist and restore its status to Interested?`
    dialog.variant = 'warning'
  }
  dialog.open = true
}

async function executeAction() {
  dialog.loading = true
  try {
    const newStatus = dialog.action === 'blacklist' ? 'BLOCKED' : 'INTERESTED'
    await service.updateStatus(dialog.targetId, newStatus)
    toast.success(dialog.action === 'blacklist' ? 'Company blacklisted' : 'Blacklist removed')
    dialog.open = false
    await loadCompanies({ page: page.value, itemsPerPage: itemsPerPage.value })
  } catch {
    toast.error('Action failed')
  } finally {
    dialog.loading = false
  }
}

// — Edit dialog —
function openEditDialog(item: IAdminCompanyListItem) {
  editDialog.targetId = item.id!
  editForm.name = item.name ?? ''
  editForm.industry = item.industry ?? ''
  editForm.website = item.website ?? ''
  editForm.companySize = item.companySize ?? ''
  editForm.location = item.location ?? ''
  editForm.foundedYear = item.foundedYear ?? ''
  editForm.description = item.description ?? ''
  editForm.note = item.note ?? ''
  editForm.status = item.status ?? undefined
  editDialog.open = true
}

async function saveEdit() {
  editDialog.loading = true
  try {
    await service.updateDetails(editDialog.targetId, { ...editForm })
    toast.success('Company updated')
    editDialog.open = false
    await loadCompanies({ page: page.value, itemsPerPage: itemsPerPage.value })
  } catch {
    toast.error('Failed to update company')
  } finally {
    editDialog.loading = false
  }
}
</script>

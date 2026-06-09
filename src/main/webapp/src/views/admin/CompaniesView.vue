<template>
  <FormCard :collapsible="false">
    <template #title>
      <v-icon icon="mdi-office-building" class="mr-2" />
      Companies
    </template>

    <!-- Toolbar -->
    <v-row align="center" class="mb-4" density="compact">
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
    </v-row>

    <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="companies"
        :items-length="totalItems"
        :loading="loading"
        :page="page"
        item-value="id"
        @update:options="onTableOptions"
        @click:row="openDetailDialog"
      >
        <!-- Name + logo -->
        <template #item.name="{ item }">
          <div class="d-flex align-center gap-2 py-1">
            <CompanyLogo :alt="item.name" :logo="item.logo" :website="item.website" size="32" />
            <div class="ml-3">
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

        <!-- Empty state -->
        <template #no-data>
          <div class="pa-8 text-center text-medium-emphasis">
            <v-icon icon="mdi-office-building-off-outline" size="48" class="mb-2" />
            <div>No companies found</div>
          </div>
        </template>
      </v-data-table-server>
    </FormCard>

    <!-- Company Detail Dialog -->
    <v-dialog v-model="detailDialog" max-width="600">
      <v-card rounded="lg">
        <v-card-title class="pa-6 pb-2 d-flex align-center">
          <span class="text-h6 font-weight-bold">Company Details</span>
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="detailDialog = false" />
        </v-card-title>
        <v-divider />
        <v-card-text class="pa-6">
          <template v-if="detailCompany">
            <v-row dense>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Company Name</div>
                <div class="text-body-2">{{ detailCompany.name ?? '—' }}</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Industry</div>
                <div class="text-body-2">{{ detailCompany.industry ?? '—' }}</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Website</div>
                <div class="text-body-2">
                  <a v-if="detailCompany.website" :href="detailCompany.website" target="_blank" rel="noopener noreferrer">{{ detailCompany.website }}</a>
                  <span v-else>—</span>
                </div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Company Size</div>
                <div class="text-body-2">{{ detailCompany.companySize ?? '—' }}</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Location</div>
                <div class="text-body-2">{{ detailCompany.location ?? '—' }}</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Founded Year</div>
                <div class="text-body-2">{{ detailCompany.foundedYear ?? '—' }}</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Status</div>
                <v-chip :color="statusColor(detailCompany.status)" size="small" label>
                  <v-icon start :icon="statusIcon(detailCompany.status)" />
                  {{ statusLabel(detailCompany.status) }}
                </v-chip>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Applications</div>
                <div class="text-body-2">{{ detailCompany.applicationCount ?? 0 }}</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Created</div>
                <div class="text-body-2">{{ detailCompany.createdDate ? formatDate(detailCompany.createdDate) : '—' }}</div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-caption text-medium-emphasis mb-1">Last Modified</div>
                <div class="text-body-2">{{ detailCompany.lastModifiedDate ? formatDate(detailCompany.lastModifiedDate) : '—' }}</div>
              </v-col>
            </v-row>

            <v-divider class="my-4" />

            <div class="text-caption text-medium-emphasis mb-2">Description</div>
            <v-sheet rounded="lg" border class="pa-3 mb-4">
              <MdViewer :content="detailCompany.description" />
            </v-sheet>

            <div class="text-caption text-medium-emphasis mb-2">Note</div>
            <v-sheet rounded="lg" border class="pa-3">
              <MdViewer :content="detailCompany.note" />
            </v-sheet>
          </template>
        </v-card-text>
        <v-divider />
        <v-card-actions class="pa-4 justify-end">
          <v-btn variant="outlined" @click="detailDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useToastStore } from '../../stores/toast'
import AdminCompanyService from '../../services/admin/admin-company.service'
import type { IAdminCompanyListItem } from '../../models'
import CompanyLogo from '../../components/company/CompanyLogo.vue'
import MdViewer from '../../components/markdown/MdViewer.vue'
import FormCard from '../../components/forms/FormCard.vue'
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

const headers = [
  { title: 'Company', key: 'name', sortable: false },
  { title: 'Status', key: 'status', sortable: true },
  { title: 'Industry', key: 'industry', sortable: false },
  { title: 'Applications', key: 'applicationCount', sortable: true, align: 'center' as const },
  { title: 'Created', key: 'createdDate', sortable: true },
]

// — Detail dialog —
const detailDialog = ref(false)
const detailCompany = ref<IAdminCompanyListItem | null>(null)

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
    parts.push(`(name=ilike="${q}",industry=ilike="${q}")`)
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
  sortBy?: { key: string; order: string }[]
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

// — Detail dialog —
function openDetailDialog(_: any, row: { item: IAdminCompanyListItem }) {
  detailCompany.value = row.item
  detailDialog.value = true
}
</script>

<style scoped>
:deep(.v-data-table__tr) {
  cursor: pointer;
}
</style>

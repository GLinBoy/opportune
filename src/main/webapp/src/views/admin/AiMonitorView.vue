<template>
  <v-container fluid class="pa-4">
    <!-- ─── Processing Queue ─── -->
    <v-card flat border rounded="lg" class="mb-6">
      <v-card-title class="d-flex align-center justify-space-between pa-3">
        <div class="d-flex align-center">
          <v-icon icon="mdi-robot-outline" color="warning" class="mx-2" />
          <span>AI Processing Queue</span>
          <v-chip
            v-if="!loadingQueue && totalQueue > 0"
            color="warning"
            size="x-small"
            class="ms-2"
          >
            {{ totalQueue }}
          </v-chip>
        </div>
        <div class="d-flex align-center gap-2">
          <v-switch
            v-model="autoRefresh"
            density="compact"
            color="primary"
            label="Auto-refresh"
            hide-details
          />
          <v-btn
            icon="mdi-refresh"
            size="small"
            variant="text"
            :loading="loadingQueue"
            title="Refresh queue"
            @click="loadQueue"
          />
        </div>
      </v-card-title>

      <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        :headers="queueHeaders"
        :items="queueItems"
        :items-length="totalQueue"
        :loading="loadingQueue"
        :page="page"
        item-value="id"
        @update:options="onTableOptions"
      >
        <!-- Title + company -->
        <template #item.title="{ item }">
          <div>
            <div class="text-body-2 font-weight-medium">
              {{ item.title ?? 'Untitled application' }}
            </div>
            <div class="text-caption text-medium-emphasis">
              {{ item.companyName ?? 'Unknown company' }}
            </div>
          </div>
        </template>

        <!-- Wait time -->
        <template #item.waitMinutes="{ item }">
          <v-chip :color="waitColor(item.waitMinutes)" size="small" label>
            {{ formatWait(item.waitMinutes) }}
          </v-chip>
        </template>

        <!-- Created date -->
        <template #item.createdDate="{ item }">
          {{ formatDateTime(item.createdDate) }}
        </template>

        <!-- Retry action -->
        <template #item.actions="{ item }">
          <v-btn
            size="small"
            variant="text"
            icon="mdi-refresh"
            title="Retry AI analysis"
            color="primary"
            :loading="retryingId === item.id"
            @click="confirmRetry(item)"
          />
        </template>

        <!-- Empty state -->
        <template #no-data>
          <div class="pa-8 text-center text-medium-emphasis">
            <v-icon icon="mdi-check-circle-outline" size="48" class="mb-2" />
            <div>No applications stuck in AI processing</div>
          </div>
        </template>
      </v-data-table-server>
    </v-card>

    <!-- ─── Score Distribution Chart ─── -->
    <v-card flat border rounded="lg" class="mb-6">
      <v-card-title class="d-flex align-center justify-space-between pa-3">
        <div class="d-flex align-center">
          <v-icon icon="mdi-chart-bar" color="primary" class="mx-2" />
          <span>Resume Score Distribution</span>
          <span class="text-caption font-weight-regular text-medium-emphasis ms-1">
            · resumeOverallScore across all scored applications
          </span>
        </div>
      </v-card-title>

      <v-card-text>
        <v-skeleton-loader v-if="loadingChart" type="image" height="280" />
        <template v-else-if="totalScored === 0">
          <div class="pa-8 text-center text-medium-emphasis">
            <v-icon icon="mdi-chart-bar-stacked" size="48" class="mb-2" />
            <div>No scored applications yet</div>
          </div>
        </template>
        <v-chart v-else :option="chartOption" autoresize style="height: 280px" />
      </v-card-text>
    </v-card>

    <!-- Retry confirmation dialog -->
    <ConfirmDialog
      v-model="dialog.open"
      title="Retry AI Analysis"
      :loading="dialog.loading"
      variant="warning"
      confirm-text="Retry"
      @confirm="executeRetry"
      @cancel="dialog.open = false"
    >
      Re-trigger AI analysis for
      <strong>{{ dialog.title ?? 'this application' }}</strong
      >? The job will be re-queued and processed asynchronously.
    </ConfirmDialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import type { EChartsOption } from 'echarts'
import { useToastStore } from '../../stores/toast'
import AdminAiService from '../../services/admin/admin-ai.service'
import ConfirmDialog from '../../components/ConfirmDialog.vue'
import type { AdminAiQueueItem } from '../../models'

const toast = useToastStore()
const service = new AdminAiService()

// ─── Queue state ───────────────────────────────────────────────────────────
const queueItems = ref<AdminAiQueueItem[]>([])
const totalQueue = ref(0)
const loadingQueue = ref(false)
const page = ref(1)
const itemsPerPage = ref(10)
const lastOpts = ref<{ page: number; itemsPerPage: number }>({ page: 1, itemsPerPage: 10 })

const queueHeaders = [
  { title: 'Application', key: 'title', sortable: false },
  { title: 'Queued At', key: 'createdDate', sortable: true },
  { title: 'Wait Time', key: 'waitMinutes', sortable: false },
  { title: '', key: 'actions', sortable: false, align: 'end' as const },
]

// ─── Auto-refresh ──────────────────────────────────────────────────────────
const autoRefresh = ref(false)
let refreshTimer: ReturnType<typeof setInterval> | null = null

watch(autoRefresh, (enabled) => {
  if (enabled) {
    refreshTimer = setInterval(() => loadQueue(), 30_000)
  } else {
    if (refreshTimer) clearInterval(refreshTimer)
  }
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})

// ─── Retry dialog ──────────────────────────────────────────────────────────
const retryingId = ref<string | null>(null)
const dialog = ref({ open: false, id: '', title: null as string | null, loading: false })

function confirmRetry(item: AdminAiQueueItem) {
  dialog.value = { open: true, id: item.id, title: item.title, loading: false }
}

async function executeRetry() {
  dialog.value.loading = true
  retryingId.value = dialog.value.id
  try {
    await service.retryJob(dialog.value.id)
    toast.success('AI analysis re-queued successfully.')
    dialog.value.open = false
    loadQueue()
  } catch {
    toast.error('Failed to retry AI analysis. Please try again.')
  } finally {
    dialog.value.loading = false
    retryingId.value = null
  }
}

// ─── Load queue ────────────────────────────────────────────────────────────
async function loadQueue() {
  loadingQueue.value = true
  try {
    const { page: p, itemsPerPage: ipp } = lastOpts.value
    const res = await service.getQueue({ page: p - 1, size: ipp })
    queueItems.value = res.data
    totalQueue.value = parseInt(res.headers['x-total-count'] ?? '0', 10)
  } catch {
    toast.error('Failed to load AI processing queue.')
  } finally {
    loadingQueue.value = false
  }
}

function onTableOptions(opts: { page: number; itemsPerPage: number }) {
  lastOpts.value = opts
  page.value = opts.page
  loadQueue()
}

// ─── Score distribution ────────────────────────────────────────────────────
const scoreBucketLabels = ref<string[]>([])
const scoreBucketCounts = ref<number[]>([])
const loadingChart = ref(false)
const totalScored = computed(() => scoreBucketCounts.value.reduce((a, b) => a + b, 0))

async function loadScoreDistribution() {
  loadingChart.value = true
  try {
    const res = await service.getScoreDistribution()
    scoreBucketLabels.value = res.data.buckets.map((b) => b.label)
    scoreBucketCounts.value = res.data.buckets.map((b) => b.count)
  } catch {
    toast.error('Failed to load score distribution.')
  } finally {
    loadingChart.value = false
  }
}

// ─── ECharts option ────────────────────────────────────────────────────────
function resolveVuetifyColor(variable: string): string {
  const raw = getComputedStyle(document.documentElement).getPropertyValue(variable).trim()
  return raw ? `rgb(${raw})` : '#1976D2'
}

const chartOption = computed<EChartsOption>(() => {
  const primary = resolveVuetifyColor('--v-theme-primary')
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: unknown) => {
        const p = (params as { name: string; value: number }[])[0]
        if (!p) return ''
        return `Score ${p.name}: <b>${p.value}</b> application${p.value !== 1 ? 's' : ''}`
      },
    },
    grid: { left: 50, right: 16, top: 16, bottom: 40 },
    xAxis: {
      type: 'category',
      data: scoreBucketLabels.value,
      axisLabel: { fontSize: 11 },
      name: 'Score range',
      nameLocation: 'middle',
      nameGap: 30,
    },
    yAxis: {
      type: 'value',
      name: 'Applications',
      nameLocation: 'middle',
      nameGap: 36,
      minInterval: 1,
    },
    series: [
      {
        type: 'bar',
        data: scoreBucketCounts.value,
        itemStyle: { color: primary, borderRadius: [4, 4, 0, 0] },
        emphasis: { itemStyle: { opacity: 0.8 } },
      },
    ],
  }
})

// ─── Helpers ───────────────────────────────────────────────────────────────
function formatWait(minutes: number): string {
  if (minutes < 60) return `${minutes}m`
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return m > 0 ? `${h}h ${m}m` : `${h}h`
}

function waitColor(minutes: number): string {
  if (minutes > 60) return 'error'
  if (minutes > 15) return 'warning'
  return 'info'
}

function formatDateTime(iso: string): string {
  return new Date(iso).toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// ─── Init ──────────────────────────────────────────────────────────────────
onMounted(() => {
  loadQueue()
  loadScoreDistribution()
})
</script>

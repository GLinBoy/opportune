// src/main/webapp/src/composables/useDashboard.ts
import { ref, computed, onMounted } from 'vue'
import { DashboardService } from '../services'
import { ApplicationStatus } from '../models/enumerations/application-status.model'
import type {
  UserDashboardSummaryDto,
  DashboardKpis,
  ApplicationStatProjection,
} from '../models'

// Fix 2a: use enum members, not string literals
// Fix 2b: WITHDRAWN doesn't exist — the real enum has ACCEPTED and DECLINED
const ACTIVE_STATUSES: ApplicationStatus[] = [
  ApplicationStatus.APPLIED,
  ApplicationStatus.IN_PROGRESS,
  ApplicationStatus.OFFER_RECEIVED,
]

const RESPONDED_STATUSES: ApplicationStatus[] = [
  ApplicationStatus.IN_PROGRESS,
  ApplicationStatus.OFFER_RECEIVED,
  ApplicationStatus.REJECTED,
  ApplicationStatus.ACCEPTED,
  ApplicationStatus.DECLINED,
]

function computeKpis(stats: ApplicationStatProjection[]): DashboardKpis {
  const total = stats.reduce((sum, r) => sum + r.total, 0)
  if (total === 0) {
    return { totalApplications: 0, activePipeline: 0, responseRate: 0, offerRate: 0, rejectionRate: 0 }
  }

  // Fix 2c: type the accumulator as Partial<Record<...>> to avoid undefined access errors
  const countByStatus = stats.reduce<Partial<Record<ApplicationStatus, number>>>((acc, r) => {
    acc[r.status] = (acc[r.status] ?? 0) + r.total
    return acc
  }, {})

  const active = ACTIVE_STATUSES.reduce((s, st) => s + (countByStatus[st] ?? 0), 0)
  const responded = RESPONDED_STATUSES.reduce((s, st) => s + (countByStatus[st] ?? 0), 0)
  const offers = countByStatus[ApplicationStatus.OFFER_RECEIVED] ?? 0
  const rejected = countByStatus[ApplicationStatus.REJECTED] ?? 0

  return {
    totalApplications: total,
    activePipeline: active,
    responseRate: Math.round((responded / total) * 100),
    offerRate: Math.round((offers / total) * 100),
    rejectionRate: Math.round((rejected / total) * 100),
  }
}

// Fix 2d: no default param — avoids the TS2554 "Expected 0 arguments" ambiguity
export function useDashboard() {
  const dashboardService = new DashboardService()

  const summary = ref<UserDashboardSummaryDto | null>(null)
  const loading = ref(true)
  const error = ref<string | null>(null)   // Fix 2e: was missing from return

  const kpis = computed<DashboardKpis>(() =>
    summary.value ? computeKpis(summary.value.stats) : computeKpis([])
  )

  const statusCounts = computed(() => {
    if (!summary.value) return []
    const countByStatus = summary.value.stats.reduce<Partial<Record<ApplicationStatus, number>>>((acc, r) => {
      acc[r.status] = (acc[r.status] ?? 0) + r.total
      return acc
    }, {})
    return (Object.entries(countByStatus) as [ApplicationStatus, number][]).map(
      ([status, total]) => ({ status, total })
    )
  })

  const trendData = computed(() => {
    if (!summary.value) return { dates: [] as string[], counts: [] as number[] }
    const byDay = summary.value.stats.reduce<Partial<Record<string, number>>>((acc, r) => {
      acc[r.createdDay] = (acc[r.createdDay] ?? 0) + r.total
      return acc
    }, {})
    const days = summary.value.summaryDays ?? 90
    const dates: string[] = []
    const counts: number[] = []
    const today = new Date()
    for (let i = days - 1; i >= 0; i--) {
      const d = new Date(today)
      d.setDate(today.getDate() - i)
      const dateStr = d.toISOString().slice(0, 10)
      dates.push(dateStr)
      counts.push(byDay[dateStr] ?? 0)
    }
    return { dates, counts }
  })

  const scores = computed(() => summary.value?.scores ?? null)

  async function load() {
    loading.value = true
    error.value = null
    try {
      const res = await dashboardService.getUserSummary()
      summary.value = res.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to load dashboard'
    } finally {
      loading.value = false
    }
  }

  onMounted(load)

  // Fix 2e: error is now included in the return
  return { summary, kpis, statusCounts, trendData, scores, loading, error, reload: load }
}

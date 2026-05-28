// src/main/webapp/src/composables/useAdminDashboard.ts
import { ref, computed, onMounted } from 'vue'
import AdminDashboardService from '../services/admin-dashboard.service'
import type { AdminDashboardSummaryDto, AdminKpis } from '../models'

export function useAdminDashboard() {
    const service = new AdminDashboardService()

    const summary = ref<AdminDashboardSummaryDto | null>(null)
    const loading = ref(true)
    const error = ref<string | null>(null)

    const kpis = computed<AdminKpis>(() => ({
        totalUsers: summary.value?.totalUsers ?? 0,
        totalCompanies: summary.value?.totalCompanies ?? 0,
        activeSessions: summary.value?.activeSessions ?? 0,
        aiQueueCount: summary.value?.aiQueueCount ?? 0,
    }))

    const userTrend = computed<{ dates: string[]; counts: number[] }>(() => {
        const days = summary.value?.trendDays ?? 30
        const today = new Date()
        const dates: string[] = []
        for (let i = days - 1; i >= 0; i--) {
            const d = new Date(today)
            d.setDate(today.getDate() - i)
            dates.push(d.toISOString().slice(0, 10))
        }
        const lookup = new Map(
            (summary.value?.userRegistrationTrend ?? []).map(p => [p.date, p.count])
        )
        const counts = dates.map(d => lookup.get(d) ?? 0)
        return { dates, counts }
    })

    const accountStatusDistribution = computed(() =>
        summary.value?.accountStatusDistribution ?? []
    )

    const applicationStatusDistribution = computed(() =>
        summary.value?.applicationStatusDistribution ?? []
    )

    const aiQueueItems = computed(() =>
        summary.value?.aiQueueItems ?? []
    )

    async function load() {
        loading.value = true
        error.value = null
        try {
            const res = await service.getSummary()
            summary.value = res.data
        } catch (e) {
            error.value = e instanceof Error ? e.message : 'Failed to load admin dashboard'
        } finally {
            loading.value = false
        }
    }

    onMounted(load)

    return {
        summary,
        kpis,
        userTrendDates: computed(() => userTrend.value.dates),
        userTrendCounts: computed(() => userTrend.value.counts),
        accountStatusDistribution,
        applicationStatusDistribution,
        aiQueueItems,
        loading,
        error,
        reload: load,
    }
}

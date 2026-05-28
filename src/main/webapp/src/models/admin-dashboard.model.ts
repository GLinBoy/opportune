// src/main/webapp/src/models/admin-dashboard.model.ts

export type AccountStatus = 'ACTIVE' | 'SUSPENDED' | 'PENDING_VERIFICATION'

export interface UserRegistrationTrendPoint {
    date: string   // YYYY-MM-DD
    count: number
}

export interface AccountStatusCount {
    status: AccountStatus
    count: number
}

export interface ApplicationStatusCount {
    status: string
    count: number
}

export interface AiQueueItem {
    id: string
    title: string | null
    companyName: string | null
    createdDate: string  // ISO instant
    waitMinutes: number
}

export interface AdminDashboardSummaryDto {
    totalUsers: number
    totalCompanies: number
    activeSessions: number
    aiQueueCount: number
    trendDays: number
    userRegistrationTrend: UserRegistrationTrendPoint[]
    accountStatusDistribution: AccountStatusCount[]
    applicationStatusDistribution: ApplicationStatusCount[]
    aiQueueItems: AiQueueItem[]
}

export interface AdminKpis {
    totalUsers: number
    totalCompanies: number
    activeSessions: number
    aiQueueCount: number
}

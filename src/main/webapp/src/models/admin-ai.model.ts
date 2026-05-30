// src/main/webapp/src/models/admin-ai.model.ts

export interface AdminAiQueueItem {
    id: string
    title: string | null
    companyName: string | null
    createdDate: string  // ISO instant
    waitMinutes: number
}

export interface ScoreBucket {
    label: string
    count: number
}

export interface AdminScoreDistribution {
    buckets: ScoreBucket[]
}

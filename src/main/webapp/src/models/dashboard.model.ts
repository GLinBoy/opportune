import type { ApplicationStatus } from "./enumerations/application-status.model"

// Raw row from the backend stats array
export interface ApplicationStatProjection {
  createdDay: string
  status: ApplicationStatus
  total: number
}

export interface ScoreSummaryProjection {
  avgResumeScore: number | null
  avgSkillScore: number | null
  avgExperienceScore: number | null
  avgEducationScore: number | null
  avgKeywordScore: number | null
}

export interface UserDashboardSummaryDto {
  summaryDays: number
  stats: ApplicationStatProjection[]
  scores: ScoreSummaryProjection
}

// Computed client-side from stats — not from the API
export interface DashboardKpis {
  totalApplications: number
  activePipeline: number
  responseRate: number    // percentage 0–100
  offerRate: number
  rejectionRate: number
}

// src/main/webapp/src/models/admin-settings.model.ts

export interface AdminSettings {
  registrationEnabled: boolean
  aiScoringEnabled: boolean
  emailNotificationsEnabled: boolean
  maintenanceMode: boolean
  maxApplicationsPerUser: number
  maxAiRequestsPerDay: number
}

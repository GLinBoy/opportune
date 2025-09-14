import { type ApplicationStatus } from "./enumerations/application-status.model";

export interface IApplication {
  id?: string
  url?: string
  title?: string
  location?: string
  appliedAt?: string // ISO date-time string per DTO
  salary?: string
  note?: string
  rawContent?: string
  description?: string
  coverLetter?: string
  resumeInsights?: string
  status?: ApplicationStatus
  companyId?: string
  profileId?: string
  resumeId?: string
}

export class Application implements IApplication {
  constructor(
    public id?: string,
    public url?: string,
    public title?: string,
    public location?: string,
    public appliedAt?: string,
    public salary?: string,
    public note?: string,
    public rawContent?: string,
    public description?: string,
    public coverLetter?: string,
    public resumeInsights?: string,
    public status?: ApplicationStatus,
    public companyId?: string,
    public profileId?: string,
    public resumeId?: string
  ) { }
}

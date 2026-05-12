import { type ApplicationStatus } from "./enumerations/application-status.model";
import { type ICompany } from "./company.model";
import { type IApplicationResume } from "./application-resume.model";
import { type IApplicationTimeline } from "./application-timeline.model";
import { type IInterviewNote } from "./interview-note.model";
import { type IApplicationMetaData } from "./application-meta-data.model";
import { type IApplicationAttachment } from "./application-attachment.model";

export interface IApplication {
  id?: string
  url?: string
  title?: string
  location?: string
  appliedAt?: Date
  salary?: string
  note?: string
  rawContent?: string
  description?: string
  coverLetter?: string
  resumeInsights?: string
  interviewIntroduction?: string
  resumeOverallScore?: number
  skillMatchScore?: number
  skillMatchRationale?: string
  experienceMatchScore?: number
  experienceMatchRationale?: string
  educationMatchScore?: number
  educationMatchRationale?: string
  keywordMatchScore?: number
  keywordMatchRationale?: string
  status?: keyof typeof ApplicationStatus | null
  companyId?: string
  profileId?: string
  resumeId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export class Application implements IApplication {
  constructor(
    public id?: string,
    public url?: string,
    public title?: string,
    public location?: string,
    public appliedAt?: Date,
    public salary?: string,
    public note?: string,
    public rawContent?: string,
    public description?: string,
    public coverLetter?: string,
    public resumeInsights?: string,
    public interviewIntroduction?: string,
    public resumeOverallScore?: number,
    public skillMatchScore?: number,
    public skillMatchRationale?: string,
    public experienceMatchScore?: number,
    public experienceMatchRationale?: string,
    public educationMatchScore?: number,
    public educationMatchRationale?: string,
    public keywordMatchScore?: number,
    public keywordMatchRationale?: string,
    public status?: keyof typeof ApplicationStatus | null,
    public companyId?: string,
    public profileId?: string,
    public resumeId?: string,
    public createdDate?: Date,
    public lastModifiedDate?: Date
  ) { }
}

export interface IApplicationProjection {
  id?: string
  url?: string
  title?: string
  location?: string
  appliedAt?: Date
  salary?: string
  note?: string
  status?: keyof typeof ApplicationStatus | null
  companyId?: string
  companyName?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IApplicationDetails {
  id?: string
  url?: string
  title?: string
  location?: string
  appliedAt?: Date
  salary?: string
  note?: string
  rawContent?: string
  description?: string
  coverLetter?: string
  resumeInsights?: string
  interviewIntroduction?: string,
  resumeOverallScore?: number,
  skillMatchScore?: number,
  skillMatchRationale?: string,
  experienceMatchScore?: number,
  experienceMatchRationale?: string,
  educationMatchScore?: number,
  educationMatchRationale?: string,
  keywordMatchScore?: number,
  keywordMatchRationale?: string,
  status?: keyof typeof ApplicationStatus | null
  company?: ICompany | null
  timeline?: IApplicationTimeline[] | null
  interviewNotes?: IInterviewNote[] | null
  metadata?: IApplicationMetaData[] | null
  resume?: IApplicationResume | null
  attachments?: IApplicationAttachment[] | null
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IApplicationUrlSubmission {
  url: string
}

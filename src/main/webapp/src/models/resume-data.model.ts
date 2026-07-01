export interface IWorkExperienceBullet {
  id?: string
  content?: string
  displayOrder?: number
  createdDate?: Date
  lastModifiedDate?: Date
}

export type EmploymentType =
  | 'FULL_TIME'
  | 'PART_TIME'
  | 'SELF_EMPLOYED'
  | 'FREELANCE'
  | 'CONTRACT'
  | 'INTERNSHIP'
  | 'APPRENTICESHIP'
  | 'CO_OP'
  | 'LIFETIME_CIVIL_SERVANT'
  | 'CIVIL_SERVICE_INTERNSHIP'
  | 'WORK_STUDY'

export type LocationType = 'ON_SITE' | 'REMOTE' | 'HYBRID'

export interface IWorkExperience {
  id?: string
  jobTitle?: string
  company?: string
  industry?: string
  employmentType?: EmploymentType
  startMonth?: number
  startYear?: number
  endMonth?: number
  endYear?: number
  isCurrent?: boolean
  location?: string
  locationType?: LocationType
  description?: string
  displayOrder?: number
  profileId?: string
  bullets?: IWorkExperienceBullet[]
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IEducation {
  id?: string
  school?: string
  degree?: string
  fieldOfStudy?: string
  startMonth?: number
  startYear?: number
  endMonth?: number
  endYear?: number
  isCurrent?: boolean
  gpa?: string
  honors?: string
  displayOrder?: number
  profileId?: string
  courses?: string[]
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface ISkillGroup {
  id?: string
  category?: string
  displayOrder?: number
  profileId?: string
  skills?: string[]
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IResumeAggregate {
  workExperiences?: IWorkExperience[]
  education?: IEducation[]
  skillGroups?: ISkillGroup[]
  projects?: IResumeProject[]
  certifications?: IResumeCertification[]
  languages?: IResumeLanguage[]
  volunteerWork?: IVolunteerWork[]
  publications?: IResumePublication[]
  awards?: IResumeAward[]
  affiliations?: IProfessionalAffiliation[]
}

export interface IResumeExtractionResult {
  workExperiences?: IWorkExperience[]
  education?: IEducation[]
  skillGroups?: ISkillGroup[]
}

export interface IResumeProject {
  id?: string
  name?: string
  description?: string
  url?: string
  startMonth?: number
  startYear?: number
  endMonth?: number
  endYear?: number
  isCurrent?: boolean
  displayOrder?: number
  profileId?: string
  techStack?: string[]
  createdDate?: Date
  lastModifiedDate?: Date
}

export type LanguageProficiency = 'NATIVE' | 'FLUENT' | 'CONVERSATIONAL' | 'BASIC'

export interface IResumeCertification {
  id?: string
  name?: string
  issuingOrganization?: string
  issueMonth?: number
  issueYear?: number
  expirationMonth?: number
  expirationYear?: number
  credentialId?: string
  credentialUrl?: string
  displayOrder?: number
  profileId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IResumeLanguage {
  id?: string
  language?: string
  proficiency?: LanguageProficiency
  displayOrder?: number
  profileId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IVolunteerWork {
  id?: string
  role?: string
  organization?: string
  location?: string
  startMonth?: number
  startYear?: number
  endMonth?: number
  endYear?: number
  isCurrent?: boolean
  description?: string
  displayOrder?: number
  profileId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IResumePublication {
  id?: string
  title?: string
  publisher?: string
  publicationDate?: string
  url?: string
  description?: string
  displayOrder?: number
  profileId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IResumeAward {
  id?: string
  title?: string
  issuer?: string
  awardDate?: string
  description?: string
  displayOrder?: number
  profileId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IProfessionalAffiliation {
  id?: string
  organization?: string
  role?: string
  startYear?: number
  endYear?: number
  isCurrent?: boolean
  description?: string
  displayOrder?: number
  profileId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

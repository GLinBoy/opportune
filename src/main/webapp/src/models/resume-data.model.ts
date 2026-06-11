export interface IWorkExperienceBullet {
  id?: string
  content?: string
  displayOrder?: number
  createdDate?: Date
  lastModifiedDate?: Date
}

export interface IWorkExperience {
  id?: string
  jobTitle?: string
  company?: string
  location?: string
  startMonth?: number
  startYear?: number
  endMonth?: number
  endYear?: number
  isCurrent?: boolean
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
  startYear?: number
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
}

export interface IResumeExtractionResult {
  workExperiences?: IWorkExperience[]
  education?: IEducation[]
  skillGroups?: ISkillGroup[]
}

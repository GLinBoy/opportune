import apiClient from './api'

import type {
  IWorkExperience,
  IWorkExperienceBullet,
  IEducation,
  ISkillGroup,
  IResumeAggregate,
  IResumeExtractionResult,
  IResumeProject,
  IResumeCertification,
  IResumeLanguage,
  IVolunteerWork,
  IResumePublication,
  IResumeAward,
  IProfessionalAffiliation,
} from '../models/resume-data.model'

const RESUME_API_URL = '/api/profiles/resume'

export default class ResumeDataService {

  getAggregate(): Promise<IResumeAggregate> {
    return new Promise<IResumeAggregate>((resolve, reject) => {
      apiClient
        .get(RESUME_API_URL)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  extractFromResume(): Promise<IResumeExtractionResult> {
    return new Promise<IResumeExtractionResult>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/extract`, null, { timeout: 60000 })
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createWorkExperience(dto: IWorkExperience): Promise<IWorkExperience> {
    return new Promise<IWorkExperience>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/work-experiences`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateWorkExperience(id: string, dto: IWorkExperience): Promise<IWorkExperience> {
    return new Promise<IWorkExperience>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/work-experiences/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteWorkExperience(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/work-experiences/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  replaceBullets(workExperienceId: string, bullets: IWorkExperienceBullet[]): Promise<IWorkExperienceBullet[]> {
    return new Promise<IWorkExperienceBullet[]>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/work-experiences/${workExperienceId}/bullets`, bullets)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createEducation(dto: IEducation): Promise<IEducation> {
    return new Promise<IEducation>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/education`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateEducation(id: string, dto: IEducation): Promise<IEducation> {
    return new Promise<IEducation>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/education/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteEducation(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/education/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createSkillGroup(dto: ISkillGroup): Promise<ISkillGroup> {
    return new Promise<ISkillGroup>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/skill-groups`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateSkillGroup(id: string, dto: ISkillGroup): Promise<ISkillGroup> {
    return new Promise<ISkillGroup>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/skill-groups/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteSkillGroup(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/skill-groups/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createProject(dto: IResumeProject): Promise<IResumeProject> {
    return new Promise<IResumeProject>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/projects`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateProject(id: string, dto: IResumeProject): Promise<IResumeProject> {
    return new Promise<IResumeProject>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/projects/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteProject(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/projects/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createCertification(dto: IResumeCertification): Promise<IResumeCertification> {
    return new Promise<IResumeCertification>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/certifications`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateCertification(id: string, dto: IResumeCertification): Promise<IResumeCertification> {
    return new Promise<IResumeCertification>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/certifications/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteCertification(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/certifications/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createLanguage(dto: IResumeLanguage): Promise<IResumeLanguage> {
    return new Promise<IResumeLanguage>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/languages`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateLanguage(id: string, dto: IResumeLanguage): Promise<IResumeLanguage> {
    return new Promise<IResumeLanguage>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/languages/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteLanguage(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/languages/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createVolunteerWork(dto: IVolunteerWork): Promise<IVolunteerWork> {
    return new Promise<IVolunteerWork>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/volunteer-work`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateVolunteerWork(id: string, dto: IVolunteerWork): Promise<IVolunteerWork> {
    return new Promise<IVolunteerWork>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/volunteer-work/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteVolunteerWork(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/volunteer-work/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createPublication(dto: IResumePublication): Promise<IResumePublication> {
    return new Promise<IResumePublication>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/publications`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updatePublication(id: string, dto: IResumePublication): Promise<IResumePublication> {
    return new Promise<IResumePublication>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/publications/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deletePublication(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/publications/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createAward(dto: IResumeAward): Promise<IResumeAward> {
    return new Promise<IResumeAward>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/awards`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateAward(id: string, dto: IResumeAward): Promise<IResumeAward> {
    return new Promise<IResumeAward>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/awards/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteAward(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/awards/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  createAffiliation(dto: IProfessionalAffiliation): Promise<IProfessionalAffiliation> {
    return new Promise<IProfessionalAffiliation>((resolve, reject) => {
      apiClient
        .post(`${RESUME_API_URL}/affiliations`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  updateAffiliation(id: string, dto: IProfessionalAffiliation): Promise<IProfessionalAffiliation> {
    return new Promise<IProfessionalAffiliation>((resolve, reject) => {
      apiClient
        .put(`${RESUME_API_URL}/affiliations/${id}`, dto)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteAffiliation(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${RESUME_API_URL}/affiliations/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}

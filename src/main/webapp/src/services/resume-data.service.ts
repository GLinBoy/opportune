import apiClient from './api'

import type {
  IWorkExperience,
  IWorkExperienceBullet,
  IEducation,
  ISkillGroup,
  IResumeAggregate,
  IResumeExtractionResult,
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
}

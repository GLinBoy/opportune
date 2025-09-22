import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'

import { type IInterviewNote } from '../models'
import type { AxiosResponse } from 'axios'

const INTERVIEW_NOTE_API_URL = '/api/applications/{application_id}/interview-notes'

export default class InterviewNoteService {

  retrieve(applicationId: string, paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IInterviewNote[]>> {
    return new Promise<AxiosResponse<IInterviewNote[]>>((resolve, reject) => {
      const query = buildPaginationQueryOpts(paginationQuery)
      const urlBase = INTERVIEW_NOTE_API_URL.replace('{application_id}', applicationId)
      const url = query ? `${urlBase}?${query}` : urlBase
      apiClient.get<IInterviewNote[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  find(applicationId: string, id: string): Promise<IInterviewNote> {
    return new Promise<IInterviewNote>((resolve, reject) => {
      apiClient
        .get(`${INTERVIEW_NOTE_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  create(applicationId: string, entity: IInterviewNote): Promise<IInterviewNote> {
    return new Promise<IInterviewNote>((resolve, reject) => {
      apiClient
        .post(`${INTERVIEW_NOTE_API_URL.replace('{application_id}', applicationId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  update(applicationId: string, entity: IInterviewNote): Promise<IInterviewNote> {
    return new Promise<IInterviewNote>((resolve, reject) => {
      apiClient
        .put(`${INTERVIEW_NOTE_API_URL.replace('{application_id}', applicationId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(applicationId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${INTERVIEW_NOTE_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

}

import apiClient from './api'

import { type IInterviewAttachment } from '../models'

const INTERVIEW_ATTACHMENT_API_URL = '/api/applications/{application_id}/interview-notes/{interview_note_id}/attachments'

export default class InterviewAttachmentService {
  retrieve(applicationId: string, interviewNoteId: string): Promise<IInterviewAttachment[]> {
    return new Promise<IInterviewAttachment[]>((resolve, reject) => {
      const url = INTERVIEW_ATTACHMENT_API_URL
        .replace('{application_id}', applicationId)
        .replace('{interview_note_id}', interviewNoteId)
      apiClient.get<IInterviewAttachment[]>(url)
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  upload(applicationId: string, interviewNoteId: string, file: File): Promise<IInterviewAttachment> {
    return new Promise<IInterviewAttachment>((resolve, reject) => {
      const url = INTERVIEW_ATTACHMENT_API_URL
        .replace('{application_id}', applicationId)
        .replace('{interview_note_id}', interviewNoteId)
      const formData = new FormData()
      formData.append('interview_attachment', file)
      apiClient.post(url, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  find(applicationId: string, interviewNoteId: string, id: string): Promise<IInterviewAttachment> {
    return new Promise<IInterviewAttachment>((resolve, reject) => {
      const url = `${INTERVIEW_ATTACHMENT_API_URL.replace('{application_id}', applicationId).replace('{interview_note_id}', interviewNoteId)}/${id}`
      apiClient
        .get(url)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(applicationId: string, interviewNoteId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const url = `${INTERVIEW_ATTACHMENT_API_URL.replace('{application_id}', applicationId).replace('{interview_note_id}', interviewNoteId)}/${id}`
      apiClient
        .delete(url)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}

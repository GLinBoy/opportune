export interface IInterviewAttachment {
  id?: string
  name?: string
  path?: string
  contentType?: string
  contentLength?: number
  interviewNoteId?: string
}

export class InterviewAttachment implements IInterviewAttachment {
  constructor(
    public id?: string,
    public name?: string,
    public path?: string,
    public contentType?: string,
    public contentLength?: number,
    public interviewNoteId?: string
  ) { }
}

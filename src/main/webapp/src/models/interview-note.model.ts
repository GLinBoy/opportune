export interface IInterviewNote {
  id?: string
  date?: Date
  notes?: string
  applicationId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export class InterviewNote implements IInterviewNote {
  constructor(
    public id?: string,
    public date?: Date,
    public notes?: string,
    public applicationId?: string,
    public createdDate?: Date,
    public lastModifiedDate?: Date
  ) { }
}

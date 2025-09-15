export interface IInterviewNote {
  id?: string
  date?: Date
  notes?: string
  applicationId?: string
}

export class InterviewNote implements IInterviewNote {
  constructor(
    public id?: string,
    public date?: Date,
    public notes?: string,
    public applicationId?: string
  ) { }
}

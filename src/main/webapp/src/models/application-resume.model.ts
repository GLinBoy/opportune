export interface IApplicationResume {
  id?: string
  name?: string
  path?: string
  contentType?: string
  contentLength?: number
  applicationId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export class ApplicationResume implements IApplicationResume {
  constructor(
    public id?: string,
    public name?: string,
    public path?: string,
    public contentType?: string,
    public contentLength?: number,
    public applicationId?: string,
    public createdDate?: Date,
    public lastModifiedDate?: Date
  ) { }
}

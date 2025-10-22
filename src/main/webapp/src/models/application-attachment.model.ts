export interface IApplicationAttachment {
  id?: string
  name?: string
  path?: string
  contentType?: string
  contentLength?: number
  applicationId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export class ApplicationAttachment implements IApplicationAttachment {
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

export interface IApplicationResume {
  id?: string
  name?: string
  path?: string
  contentType?: string
  contentLength?: number
  applicationId?: string
}

export class ApplicationResume implements IApplicationResume {
  constructor(
    public id?: string,
    public name?: string,
    public path?: string,
    public contentType?: string,
    public contentLength?: number,
    public applicationId?: string
  ) { }
}

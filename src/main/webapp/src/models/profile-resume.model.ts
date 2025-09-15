export interface IProfileResume {
  id?: string
  name?: string
  path?: string
  contentType?: string
  contentLength?: number
  profileId?: string
}

export class ProfileResume implements IProfileResume {
  constructor(
    public id?: string,
    public name?: string,
    public path?: string,
    public contentType?: string,
    public contentLength?: number,
    public profileId?: string
  ) { }
}

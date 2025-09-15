export interface IApplicationMetaData {
  id?: string
  metaName?: string
  metaValue?: string
  applicationId?: string
}

export class ApplicationMetaData implements IApplicationMetaData {
  constructor(
    public id?: string,
    public metaName?: string,
    public metaValue?: string,
    public applicationId?: string
  ) { }
}

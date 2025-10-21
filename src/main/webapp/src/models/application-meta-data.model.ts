export interface IApplicationMetaData {
  id?: string
  metaName?: string
  metaValue?: string
  applicationId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export class ApplicationMetaData implements IApplicationMetaData {
  constructor(
    public id?: string,
    public metaName?: string,
    public metaValue?: string,
    public applicationId?: string,
    public createdDate?: Date,
    public lastModifiedDate?: Date
  ) { }
}

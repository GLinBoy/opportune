export interface ICompanyMetadata {
  id?: string
  metaName?: string
  metaValue?: string
  companyId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export class CompanyMetadata implements ICompanyMetadata {
  constructor(
    public id?: string,
    public metaName?: string,
    public metaValue?: string,
    public companyId?: string,
    public createdDate?: Date,
    public lastModifiedDate?: Date
  ) { }
}

export interface ICompanyMetaData {
  id?: string
  metaName?: string
  metaValue?: string
  companyId?: string
}

export class CompanyMetaData implements ICompanyMetaData {
  constructor(
    public id?: string,
    public metaName?: string,
    public metaValue?: string,
    public companyId?: string
  ) { }
}

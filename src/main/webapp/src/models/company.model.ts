import { type CompanyStatus } from "./enumerations/company-status.model"

export interface ICompany {
  id?: string
  name?: string
  industry?: string
  website?: string
  companySize?: string
  location?: string
  foundedYear?: string
  description?: string
  note?: string
  logo?: string
  status?: keyof typeof CompanyStatus | null
  profileId?: string
  createdDate?: Date
  lastModifiedDate?: Date
}

export class Company implements ICompany {
  constructor(
    public id?: string,
    public name?: string,
    public industry?: string,
    public website?: string,
    public companySize?: string,
    public location?: string,
    public foundedYear?: string,
    public description?: string,
    public note?: string,
    public logo?: string,
    public status?: keyof typeof CompanyStatus | null,
    public profileId?: string,
    public resumeId?: string,
    public createdDate?: Date,
    public lastModifiedDate?: Date
  ) { }
}

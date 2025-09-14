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
  status?: CompanyStatus
  profileId?: string
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
    public status?: CompanyStatus,
    public profileId?: string
  ) { }
}

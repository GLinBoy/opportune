import { ProfileStatus } from "./enumerations/profile-status.model"

export interface IProfile {
  id?: string
  email?: string
  forename?: string
  surname?: string
  password?: string
  jobTitle?: string
  location?: string
  avatar?: string
  emailVerification?: boolean
  lastLogin?: Date
  status?: keyof typeof ProfileStatus | null
  subscription?: string
  resumeId?: string
}

export class Profile implements IProfile {
  constructor(
    public id?: string,
    public email?: string,
    public forename?: string,
    public surname?: string,
    public password?: string,
    public jobTitle?: string,
    public location?: string,
    public avatar?: string,
    public emailVerification?: boolean,
    public lastLogin?: Date,
    public status?: keyof typeof ProfileStatus | null,
    public subscription?: string,
    public resumeId?: string
  ) { }
}

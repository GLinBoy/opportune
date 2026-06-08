import type { ISession } from './session.model'

export interface IAdminUserListItem {
    id?: string
    email?: string
    forename?: string
    surname?: string
    jobTitle?: string
    status?: string | null
    roles?: string[]
    emailVerification?: boolean
    lastLogin?: string
    createdDate?: string
    lastModifiedDate?: string
    avatar?: string
}

export interface IAdminUserDetail {
    profile: IAdminUserListItem & { resumeId?: string }
    applicationCount: number
    sessions: ISession[]
}

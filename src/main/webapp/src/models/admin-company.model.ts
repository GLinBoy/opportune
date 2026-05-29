export interface IAdminCompanyListItem {
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
    status?: string | null
    profileId?: string
    applicationCount?: number
    createdDate?: string
    lastModifiedDate?: string
}

export interface IAdminCompanyUpdate {
    name?: string
    industry?: string
    website?: string
    companySize?: string
    location?: string
    foundedYear?: string
    description?: string
    note?: string
    status?: string
}

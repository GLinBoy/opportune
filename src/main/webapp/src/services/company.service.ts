import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'

import { type ICompany } from '../models'

const COMPANY_API_URL = '/companies'

export default class CompanyService {

  retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      apiClient.get<ICompany[]>(`${COMPANY_API_URL}?${buildPaginationQueryOpts(paginationQuery)}`)
        .then(res => resolve(res))
        .catch(error => reject(error))
    })
  }

}

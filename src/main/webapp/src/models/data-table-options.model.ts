export interface IDataTableOptions {
  page: number
  itemsPerPage: number
  sortBy: Array<ISortBy>
  groupBy: Array<IGroupBy>
  search: string
}

export interface ISortBy {
  key: string
  order: 'asc' | 'desc'
}

export interface IGroupBy {
  key: string
  order: 'asc' | 'desc'
}

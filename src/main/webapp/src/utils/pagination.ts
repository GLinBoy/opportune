export default function buildPaginationQueryOpts(paginationQuery: any): string {
  if (paginationQuery) {
    return Object.entries(paginationQuery)
      .map(([paramName, paramValue]) => {
        if (Array.isArray(paramValue)) {
          return paramValue.map(eachValue => `${paramName}=${eachValue}`).join('&');
        }
        return `${paramName}=${paramValue}`;
      })
      .join('&');
  }
  return '';
}

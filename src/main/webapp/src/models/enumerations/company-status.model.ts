export enum CompanyStatus {
  INTERESTED = 'INTERESTED',
  NOT_INTERESTED = 'NOT_INTERESTED',
  BLOCKED = 'BLOCKED'
}

export function getCompanyStatusDisplay(status: CompanyStatus): string {
  const statusMap: Record<CompanyStatus, string> = {
    [CompanyStatus.INTERESTED]: 'Interested',
    [CompanyStatus.NOT_INTERESTED]: 'Not interested',
    [CompanyStatus.BLOCKED]: 'Blocked'
  };
  return statusMap[status];
}

export function getCompanyStatusColor(status: string | null | undefined): string {
  if (!status) return 'default'
  const colorMap: Record<CompanyStatus, string> = {
    [CompanyStatus.INTERESTED]: 'green',
    [CompanyStatus.NOT_INTERESTED]: 'grey',
    [CompanyStatus.BLOCKED]: 'error',
  }
  return colorMap[status as CompanyStatus] ?? 'default'
}

export function getCompanyStatusIcon(status: string | null | undefined): string {
  if (!status) return 'mdi-help-circle'
  const iconMap: Record<CompanyStatus, string> = {
    [CompanyStatus.INTERESTED]: 'mdi-thumb-up',
    [CompanyStatus.NOT_INTERESTED]: 'mdi-minus-circle',
    [CompanyStatus.BLOCKED]: 'mdi-thumb-down',
  }
  return iconMap[status as CompanyStatus] ?? 'mdi-help-circle'
}

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

export function getCompanyStatusColor(status: CompanyStatus): string {
  if (status === CompanyStatus.INTERESTED) return 'green'
  if (status === CompanyStatus.BLOCKED) return 'red'
  return 'gray'
}

export function getCompanyStatusIcon(status: CompanyStatus): string {
  if (status === CompanyStatus.INTERESTED) return 'mdi-thumb-up'
  if (status === CompanyStatus.BLOCKED) return 'mdi-thumb-down'
  return 'mdi-alert-circle'
}

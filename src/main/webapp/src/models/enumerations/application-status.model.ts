export enum ApplicationStatus {
  INITIATED = 'INITIATED',
  APPLIED = 'APPLIED',
  IN_PROGRESS = 'IN_PROGRESS',
  REJECTED = 'REJECTED',
  OFFER_RECEIVED = 'OFFER_RECEIVED',
  ACCEPTED = 'ACCEPTED',
  DECLINED = 'DECLINED'
}

export function getApplicationStatusDisplay(status: ApplicationStatus): string {
  const statusMap: Record<ApplicationStatus, string> = {
    [ApplicationStatus.INITIATED]: 'Initiated',
    [ApplicationStatus.APPLIED]: 'Applied',
    [ApplicationStatus.IN_PROGRESS]: 'In Progress',
    [ApplicationStatus.REJECTED]: 'Rejected',
    [ApplicationStatus.OFFER_RECEIVED]: 'Offer Received',
    [ApplicationStatus.ACCEPTED]: 'Accepted',
    [ApplicationStatus.DECLINED]: 'Declined'
  };
  return statusMap[status];
}

export function getApplicationStatusColor(status: ApplicationStatus): string {
  if (status === ApplicationStatus.ACCEPTED || status === ApplicationStatus.OFFER_RECEIVED) return 'green'
  if (status === ApplicationStatus.REJECTED || status === ApplicationStatus.DECLINED) return 'red'
  if (status === ApplicationStatus.IN_PROGRESS) return 'blue'
  if (status === ApplicationStatus.APPLIED) return 'orange'
  return 'gray'
}

export function getApplicationStatusIcon(status: ApplicationStatus): string {
  if (status === ApplicationStatus.INITIATED) return 'mdi-play-circle-outline'
  if (status === ApplicationStatus.APPLIED) return 'mdi-send'
  if (status === ApplicationStatus.IN_PROGRESS) return 'mdi-clock-outline'
  if (status === ApplicationStatus.REJECTED) return 'mdi-close-circle'
  if (status === ApplicationStatus.OFFER_RECEIVED) return 'mdi-email-arrow-left'
  if (status === ApplicationStatus.ACCEPTED) return 'mdi-check-circle'
  if (status === ApplicationStatus.DECLINED) return 'mdi-close-circle-outline'
  return 'mdi-help-circle'
}

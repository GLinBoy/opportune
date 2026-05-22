export enum ApplicationStatus {
  INITIATED = 'INITIATED',
  AI_PROCESSING = 'AI_PROCESSING',
  READY_TO_APPLY = 'READY_TO_APPLY',
  APPLIED = 'APPLIED',
  IN_PROGRESS = 'IN_PROGRESS',
  REJECTED = 'REJECTED',
  OFFER_RECEIVED = 'OFFER_RECEIVED',
  ACCEPTED = 'ACCEPTED',
  DECLINED = 'DECLINED'
}

export const APPLICATION_STATUS_ORDER: string[] = [
  ApplicationStatus.INITIATED,
  ApplicationStatus.AI_PROCESSING,
  ApplicationStatus.READY_TO_APPLY,
  ApplicationStatus.APPLIED,
  ApplicationStatus.IN_PROGRESS,
  ApplicationStatus.REJECTED,
  ApplicationStatus.OFFER_RECEIVED,
  ApplicationStatus.ACCEPTED,
  ApplicationStatus.DECLINED,
]

export function getApplicationStatusDisplay(status: ApplicationStatus | string): string {
  const statusMap: Record<string, string> = {
    [ApplicationStatus.INITIATED]: 'Initiated',
    [ApplicationStatus.AI_PROCESSING]: 'AI Processing',
    [ApplicationStatus.READY_TO_APPLY]: 'Ready to Apply',
    [ApplicationStatus.APPLIED]: 'Applied',
    [ApplicationStatus.IN_PROGRESS]: 'In Progress',
    [ApplicationStatus.REJECTED]: 'Rejected',
    [ApplicationStatus.OFFER_RECEIVED]: 'Offer Received',
    [ApplicationStatus.ACCEPTED]: 'Accepted',
    [ApplicationStatus.DECLINED]: 'Declined',
  };
  return statusMap[status] ?? status.replace(/_/g, ' ');
}

export function getApplicationStatusChartColor(status: string): string {
  const colorMap: Record<string, string> = {
    [ApplicationStatus.INITIATED]: '#FFA726',
    [ApplicationStatus.AI_PROCESSING]: '#AB47BC',
    [ApplicationStatus.READY_TO_APPLY]: '#26C6DA',
    [ApplicationStatus.APPLIED]: '#5C6BC0',
    [ApplicationStatus.IN_PROGRESS]: '#42A5F5',
    [ApplicationStatus.REJECTED]: '#EF5350',
    [ApplicationStatus.OFFER_RECEIVED]: '#66BB6A',
    [ApplicationStatus.ACCEPTED]: '#26A69A',
    [ApplicationStatus.DECLINED]: '#FF7043',
    WITHDRAWN: '#BDBDBD',
  };
  return colorMap[status] ?? '#78909C';
}

export function getApplicationStatusColor(status: ApplicationStatus | keyof typeof ApplicationStatus | null | undefined): string {
  if (!status) return 'default'
  const colorMap: Record<ApplicationStatus, string> = {
    [ApplicationStatus.INITIATED]: 'info',
    [ApplicationStatus.AI_PROCESSING]: 'purple',
    [ApplicationStatus.READY_TO_APPLY]: 'teal',
    [ApplicationStatus.APPLIED]: 'orange',
    [ApplicationStatus.IN_PROGRESS]: 'blue',
    [ApplicationStatus.REJECTED]: 'error',
    [ApplicationStatus.OFFER_RECEIVED]: 'green',
    [ApplicationStatus.ACCEPTED]: 'success',
    [ApplicationStatus.DECLINED]: 'grey',
  }
  return colorMap[status] ?? 'default'
}

export function getApplicationStatusIcon(status: ApplicationStatus | keyof typeof ApplicationStatus | null | undefined): string {
  if (status === ApplicationStatus.INITIATED) return 'mdi-play-circle-outline'
  if (status === ApplicationStatus.AI_PROCESSING) return 'mdi-robot'
  if (status === ApplicationStatus.READY_TO_APPLY) return 'mdi-clipboard-check-outline'
  if (status === ApplicationStatus.APPLIED) return 'mdi-send'
  if (status === ApplicationStatus.IN_PROGRESS) return 'mdi-clock-outline'
  if (status === ApplicationStatus.REJECTED) return 'mdi-close-circle'
  if (status === ApplicationStatus.OFFER_RECEIVED) return 'mdi-email-arrow-left'
  if (status === ApplicationStatus.ACCEPTED) return 'mdi-check-circle'
  if (status === ApplicationStatus.DECLINED) return 'mdi-close-circle-outline'
  return 'mdi-help-circle'
}

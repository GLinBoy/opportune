import { type ApplicationStatus } from "./enumerations/application-status.model";

export interface IApplicationTimeline {
  id?: string
  title?: string
  description?: string
  status?: keyof typeof ApplicationStatus | null
  occurredAt?: Date
  applicationId?: string
}

export class ApplicationTimeline implements IApplicationTimeline {
  constructor(
    public id?: string,
    public title?: string,
    public description?: string,
    public status?: keyof typeof ApplicationStatus | null,
    public occurredAt?: Date,
    public applicationId?: string
  ) { }
}

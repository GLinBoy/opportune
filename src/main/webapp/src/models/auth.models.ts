export interface ILoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

export class LoginRequest implements ILoginRequest {
  constructor(
    public email: string,
    public password: string,
    public rememberMe?: boolean
  ) { }
}

export interface IAccessTokenResponse {
  accessToken: string
  expiresIn: number
  refreshToken: string
  refreshExpiresIn: number
  tokenType: string
}

export interface IRefreshTokenRequest {
  refreshToken: string
}

export class RefreshTokenRequest implements IRefreshTokenRequest {
  constructor(
    public refreshToken: string
  ) { }
}

export interface IPasswordResetInitiationRequest {
  email: string
}

export class PasswordResetInitiationRequest implements IPasswordResetInitiationRequest {
  constructor(
    public email: string
  ) { }
}

export interface IPasswordResetFinalizationRequest {
  code: string
  newPassword: string
}

export class PasswordResetFinalizationRequest implements IPasswordResetFinalizationRequest {
  constructor(
    public code: string,
    public newPassword: string
  ) { }
}

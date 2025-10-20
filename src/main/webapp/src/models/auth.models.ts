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

import FingerprintJS from '@fingerprintjs/fingerprintjs'

const DEVICE_ID_KEY = 'deviceId'

let cachedDeviceId: string | null = null

export async function getDeviceId(): Promise<string> {
  if (cachedDeviceId) {
    return cachedDeviceId
  }

  const stored = localStorage.getItem(DEVICE_ID_KEY)
  if (stored) {
    cachedDeviceId = stored
    return cachedDeviceId
  }

  const fp = await FingerprintJS.load()
  const result = await fp.get()
  cachedDeviceId = result.visitorId

  localStorage.setItem(DEVICE_ID_KEY, cachedDeviceId)
  return cachedDeviceId
}

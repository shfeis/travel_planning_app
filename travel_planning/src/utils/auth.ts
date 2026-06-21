import { BASE_URL, type AuthUser } from '@/utils/request'

const AUTH_USER_KEY = 'travel-planning-user'
const DEFAULT_AVATAR_URL = `${BASE_URL}/logo.png`

const canUseStorage = () => typeof window !== 'undefined' && Boolean(window.localStorage)

export const getStoredUser = (): AuthUser | null => {
  if (!canUseStorage()) {
    return null
  }

  const rawUser = window.localStorage.getItem(AUTH_USER_KEY)

  if (!rawUser) {
    return null
  }

  try {
    const user = JSON.parse(rawUser) as Partial<AuthUser>

    if (typeof user.id === 'string' && typeof user.name === 'string') {
      return {
        id: user.id,
        name: user.name,
        logo: typeof user.logo === 'string' ? user.logo : null,
      }
    }
  } catch {
    window.localStorage.removeItem(AUTH_USER_KEY)
  }

  return null
}

export const saveStoredUser = (user: AuthUser) => {
  if (!canUseStorage()) {
    return
  }

  window.localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user))
}

export const clearStoredUser = () => {
  if (!canUseStorage()) {
    return
  }

  window.localStorage.removeItem(AUTH_USER_KEY)
}

export const isAuthenticated = () => Boolean(getStoredUser())

export const getAvatarUrl = (logo?: string | null) => {
  if (!logo) {
    return DEFAULT_AVATAR_URL
  }

  if (logo.startsWith('data:image') || logo.startsWith('http') || logo.startsWith('/')) {
    return logo
  }

  return `data:image/png;base64,${logo}`
}

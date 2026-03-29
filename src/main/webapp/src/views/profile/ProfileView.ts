import { ref, computed, watch, onMounted, defineComponent, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { type IProfile, type IPasswordChangeRequest, type ISession } from '../../models'
import ProfileService from '../../services/profile.service'
import { useAuthStore } from '../../stores/auth.store'
import ProfileInfoCard from '../../components/profile/ProfileInfoCard.vue'
import PasswordSecurityCard from '../../components/profile/PasswordSecurityCard.vue'
import SessionsCard from '../../components/profile/SessionsCard.vue'
import ApiWebhookCard from '../../components/profile/ApiWebhookCard.vue'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProfileView',
  components: {
    ProfileInfoCard,
    PasswordSecurityCard,
    SessionsCard,
    ApiWebhookCard
  },
  setup() {
    // Router
    const route = useRoute()
    const router = useRouter()

    // Services and dependencies
    const profileService = inject('profileService', () => new ProfileService())
    const authStore = useAuthStore()

    // Main data state
    const profile = ref<IProfile | null>(null)
    const loading = ref(false)
    const saving = ref(false)
    const hasChanges = ref(false)

    // Password change state
    const passwordForm = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    const passwordFormValid = ref(false)
    const passwordChanging = ref(false)

    // Tab navigation
    const tabRouteMap: Record<string, string> = {
      info: 'profile-settings',
      password: 'profile-security',
      sessions: 'profile-sessions',
      api: 'profile-api'
    }
    const activeTab = computed({
      get: () => (route.meta.tab as string) || 'info',
      set: (tab: string) => {
        router.push({ name: tabRouteMap[tab] })
      }
    })
    const highlightedSessionId = computed(() => (route.params.sessionId as string) || null)
    const tabs = [
      {
        value: 'info',
        title: 'Profile Info',
        icon: 'mdi-account'
      },
      {
        value: 'password',
        title: 'Password & Security',
        icon: 'mdi-lock'
      },
      {
        value: 'sessions',
        title: 'Sessions',
        icon: 'mdi-devices'
      },
      {
        value: 'api',
        title: 'API & Webhooks',
        icon: 'mdi-api'
      }
    ]

    // Breadcrumbs
    const breadcrumbs = [
      {
        title: 'Dashboard',
        disabled: false,
        href: '/dashboard'
      },
      {
        title: 'Profile Settings',
        disabled: true,
        href: '/profile'
      }
    ]

    // Snackbar for messages
    const snackbar = ref<Snackbar>({
      show: false,
      message: '',
      color: 'success'
    })

    // Methods
    const showSnackbar = (message: string, color: 'success' | 'error' = 'success') => {
      snackbar.value = {
        show: true,
        message,
        color
      }
    }

    const markAsModified = () => {
      hasChanges.value = true
    }

    // Load profile data
    const loadProfile = async () => {
      loading.value = true
      try {
        await profileService().getCurrentProfile().then(data => {
          profile.value = data
          hasChanges.value = false
        })
      } catch (error) {
        console.error('Failed to load profile:', error)
        showSnackbar('Failed to load profile information. Please try again.', 'error')
      } finally {
        loading.value = false
      }
    }

    // Save profile changes
    const saveProfile = async () => {
      if (!profile.value || !hasChanges.value) return

      saving.value = true
      try {
        await profileService().update(profile.value).then(data => {
          profile.value = data
          hasChanges.value = false
        })
        showSnackbar('Profile updated successfully!', 'success')
      } catch (error) {
        console.error('Failed to save profile:', error)
        showSnackbar('Failed to save profile. Please try again.', 'error')
      } finally {
        saving.value = false
      }
    }

    // Validate password form
    const validatePasswordForm = () => {
      const { currentPassword, newPassword, confirmPassword } = passwordForm.value
      passwordFormValid.value =
        !!currentPassword &&
        !!newPassword &&
        newPassword.length >= 8 &&
        newPassword === confirmPassword
    }

    // Change password
    const changePassword = async () => {
      if (!passwordFormValid.value) {
        showSnackbar('Please fill all password fields correctly', 'error')
        return
      }

      passwordChanging.value = true
      try {
        const request: IPasswordChangeRequest = {
          currentPassword: passwordForm.value.currentPassword,
          newPassword: passwordForm.value.newPassword
        }

        await profileService().changePassword(request)

        // Reset form
        passwordForm.value = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
        passwordFormValid.value = false

        showSnackbar('Password changed successfully!', 'success')
      } catch (error: unknown) {
        console.error('Failed to change password:', error)
        let message = 'Failed to change password. Please check your current password and try again.'
        if (error && typeof error === 'object' && 'response' in error) {
          const axiosError = error as { response?: { data?: { message?: string } } }
          message = axiosError.response?.data?.message || message
        }
        showSnackbar(message, 'error')
      } finally {
        passwordChanging.value = false
      }
    }

    // Sessions state
    const sessions = ref<ISession[]>([])
    const sessionsLoading = ref(false)
    const terminatingSessionId = ref<string | null>(null)

    // Load sessions
    const loadSessions = async () => {
      sessionsLoading.value = true
      try {
        const response = await profileService().getSessions({ size: 100, sort: ['loginAt,desc'] })
        sessions.value = response.data
      } catch (error) {
        console.error('Failed to load sessions:', error)
        showSnackbar('Failed to load sessions. Please try again.', 'error')
      } finally {
        sessionsLoading.value = false
      }
    }

    // Terminate a session
    const terminateSession = async (refreshTokenId: string) => {
      // Check if this is the current session
      const currentSession = sessions.value.find(s => {
        const token = localStorage.getItem('accessToken')
        if (!token) return false
        try {
          const parts = token.split('.')
          if (parts.length !== 3) return false
          const base64 = parts[1]!.split('-').join('+').split('_').join('/')
          const payload = JSON.parse(atob(base64))
          return payload.jti && s.accessTokenId === payload.jti
        } catch {
          return false
        }
      })
      const isCurrentSession = currentSession?.refreshTokenId === refreshTokenId

      terminatingSessionId.value = refreshTokenId
      try {
        await profileService().terminateSession(refreshTokenId)
        if (isCurrentSession) {
          await authStore.logout()
          router.push({ name: 'login' })
        } else {
          showSnackbar('Session revoked successfully.', 'success')
          await loadSessions()
        }
      } catch (error) {
        console.error('Failed to terminate session:', error)
        showSnackbar('Failed to revoke session. Please try again.', 'error')
      } finally {
        terminatingSessionId.value = null
      }
    }

    // Lifecycle
    onMounted(() => {
      loadProfile()
    })

    watch(() => route.meta.tab, (newTab) => {
      if (newTab === 'sessions' && sessions.value.length === 0) {
        loadSessions()
      }
    }, { immediate: true })

    return {
      // Data
      profile,
      loading,
      saving,
      hasChanges,
      activeTab,
      tabs,
      breadcrumbs,
      snackbar,
      // Password change data
      passwordForm,
      passwordFormValid,
      passwordChanging,
      // Sessions data
      sessions,
      sessionsLoading,
      terminatingSessionId,
      highlightedSessionId,
      // Methods
      showSnackbar,
      markAsModified,
      saveProfile,
      validatePasswordForm,
      changePassword,
      loadSessions,
      terminateSession
    }
  }
})

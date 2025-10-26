import { ref, onMounted, defineComponent, inject } from 'vue'
import { type IProfile, type IPasswordChangeRequest } from '../../models'
import ProfileService from '../../services/profile.service'
import ProfileForm from '../../components/ProfileForm.vue'

export interface Snackbar {
  show: boolean
  message: string
  color: string
}

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProfileView',
  components: {
    ProfileForm
  },
  setup() {
    // Services and dependencies
    const profileService = inject('profileService', () => new ProfileService())

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
    const showCurrentPassword = ref(false)
    const showNewPassword = ref(false)
    const showConfirmPassword = ref(false)

    // Tab navigation
    const activeTab = ref('info')
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

    // Password validation rules
    const passwordRules = {
      required: (v: string) => !!v || 'Password is required',
      minLength: (v: string) => v.length >= 8 || 'Password must be at least 8 characters',
      match: (v: string) => v === passwordForm.value.newPassword || 'Passwords do not match'
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

    // Lifecycle
    onMounted(() => {
      loadProfile()
    })

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
      showCurrentPassword,
      showNewPassword,
      showConfirmPassword,
      passwordRules,
      // Methods
      showSnackbar,
      markAsModified,
      saveProfile,
      validatePasswordForm,
      changePassword
    }
  }
})

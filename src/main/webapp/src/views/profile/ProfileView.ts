import { ref, onMounted, defineComponent, inject } from 'vue'
import { type IProfile } from '../../models'
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
      // Methods
      showSnackbar,
      markAsModified,
      saveProfile
    }
  }
})

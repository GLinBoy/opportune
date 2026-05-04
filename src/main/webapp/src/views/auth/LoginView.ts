import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth.store'
import { LoginRequest } from '../../models'
import AppLogo from '@/components/AppLogo.vue'

export default {
  name: 'LoginView',
  components: {
    AppLogo,
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()

    // Form state
    const loginForm = reactive({
      email: '',
      password: '',
      rememberMe: false
    })

    // Validation errors
    const errors = reactive({
      email: '',
      password: ''
    })

    const loginError = ref<string | null>(null)
    const isLoading = ref(false)
    const registrationSuccess = ref(false)
    const showPassword = ref(false)

    // Validation
    function validateForm(): boolean {
      let isValid = true
      errors.email = ''
      errors.password = ''

      if (!loginForm.email) {
        errors.email = 'Email is required'
        isValid = false
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.email)) {
        errors.email = 'Please enter a valid email'
        isValid = false
      }

      if (!loginForm.password) {
        errors.password = 'Password is required'
        isValid = false
      } else if (loginForm.password.length < 6) {
        errors.password = 'Password must be at least 6 characters'
        isValid = false
      }

      return isValid
    }

    // Handle login
    async function handleLogin(): Promise<void> {
      loginError.value = null

      if (!validateForm()) {
        return
      }

      isLoading.value = true

      try {
        const credentials = new LoginRequest(
          loginForm.email,
          loginForm.password,
          loginForm.rememberMe
        )

        await authStore.login(credentials)

        // Redirect to dashboard on success
        router.push('/dashboard')
      } catch (err) {
        loginError.value = err instanceof Error
          ? err.message
          : 'Login failed. Please check your credentials and try again.'
      } finally {
        isLoading.value = false
      }
    }

    // Handle registration redirect
    onMounted(() => {
      // Check if redirected from registration
      if (route.query.registered === 'true') {
        registrationSuccess.value = true
        if (route.query.email) {
          loginForm.email = route.query.email as string
        }
        // Clear success message after 5 seconds
        setTimeout(() => {
          registrationSuccess.value = false
        }, 5000)
      }
    })

    return {
      loginForm,
      errors,
      loginError,
      isLoading,
      registrationSuccess,
      showPassword,
      handleLogin
    }
  }
}

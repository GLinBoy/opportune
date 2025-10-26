import { reactive, ref } from 'vue'
import AuthService from '../../services/auth.service'
import { PasswordResetInitiationRequest } from '../../models'

export default {
    name: 'ForgotPasswordView',
    setup() {
        const authService = new AuthService()

        // Form state
        const email = ref('')

        // Validation errors
        const errors = reactive({
            email: ''
        })

        const resetError = ref<string | null>(null)
        const resetSuccess = ref(false)
        const isLoading = ref(false)

        // Validation
        function validateForm(): boolean {
            errors.email = ''

            if (!email.value) {
                errors.email = 'Email is required'
                return false
            }

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
            if (!emailRegex.test(email.value)) {
                errors.email = 'Please enter a valid email address'
                return false
            }

            return true
        }

        // Handle forgot password
        async function handleForgotPassword(): Promise<void> {
            resetError.value = null

            if (!validateForm()) {
                return
            }

            isLoading.value = true

            try {
                const request = new PasswordResetInitiationRequest(email.value)
                await authService.forgetPassword(request)
                resetSuccess.value = true
            } catch (err) {
                resetError.value = err instanceof Error
                    ? err.message
                    : 'Failed to send reset email. Please try again later.'
            } finally {
                isLoading.value = false
            }
        }

        // Reset form to try another email
        function resetForm(): void {
            email.value = ''
            resetSuccess.value = false
            resetError.value = null
            errors.email = ''
        }

        return {
            email,
            errors,
            resetError,
            resetSuccess,
            isLoading,
            validateForm,
            handleForgotPassword,
            resetForm
        }
    }
}

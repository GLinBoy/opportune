import { reactive, ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AuthService from '../../services/auth.service'
import { PasswordResetFinalizationRequest } from '../../models'
import AppLogo from '@/components/AppLogo.vue'

export default {
    name: 'ResetPasswordView',
    components: {
        AppLogo,
    },
    setup() {
        const route = useRoute()
        const authService = new AuthService()

        // Form state
        const resetForm = reactive({
            password: '',
            confirmPassword: ''
        })

        // Validation errors
        const errors = reactive({
            password: '',
            confirmPassword: ''
        })

        const resetError = ref<string | null>(null)
        const resetSuccess = ref(false)
        const isLoading = ref(false)
        const invalidToken = ref(false)
        const resetCode = ref<string>('')
        const showPassword = ref(false)
        const showConfirmPassword = ref(false)

        // Password validation state
        const passwordValidation = computed(() => ({
            minLength: resetForm.password.length >= 8,
            hasUppercase: /[A-Z]/.test(resetForm.password),
            hasLowercase: /[a-z]/.test(resetForm.password),
            hasNumber: /\d/.test(resetForm.password)
        }))

        // Check if password meets all requirements
        const isPasswordValid = computed(() => {
            return passwordValidation.value.minLength &&
                passwordValidation.value.hasUppercase &&
                passwordValidation.value.hasLowercase &&
                passwordValidation.value.hasNumber
        })

        // Validation
        function validateForm(): boolean {
            let isValid = true
            errors.password = ''
            errors.confirmPassword = ''

            if (!resetForm.password) {
                errors.password = 'Password is required'
                isValid = false
            } else if (!isPasswordValid.value) {
                errors.password = 'Password does not meet requirements'
                isValid = false
            }

            if (!resetForm.confirmPassword) {
                errors.confirmPassword = 'Please confirm your password'
                isValid = false
            } else if (resetForm.password !== resetForm.confirmPassword) {
                errors.confirmPassword = 'Passwords do not match'
                isValid = false
            }

            return isValid
        }

        // Handle password reset
        async function handleResetPassword(): Promise<void> {
            resetError.value = null

            if (!validateForm()) {
                return
            }

            isLoading.value = true

            try {
                const request = new PasswordResetFinalizationRequest(
                    resetCode.value,
                    resetForm.password
                )
                await authService.resetPassword(request)
                resetSuccess.value = true
            } catch (err) {
                resetError.value = err instanceof Error
                    ? err.message
                    : 'Failed to reset password. Please try again or request a new reset link.'
            } finally {
                isLoading.value = false
            }
        }

        // Check for reset code in URL
        onMounted(() => {
            const code = route.query.code as string
            if (code) {
                resetCode.value = code
            } else {
                invalidToken.value = true
            }
        })

        return {
            resetForm,
            errors,
            resetError,
            resetSuccess,
            isLoading,
            invalidToken,
            passwordValidation,
            isPasswordValid,
            showPassword,
            showConfirmPassword,
            validateForm,
            handleResetPassword
        }
    }
}

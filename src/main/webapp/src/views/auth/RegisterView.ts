import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthService from '../../services/auth.service'
import { Profile } from '../../models'

const authService = new AuthService()

export default {
    name: 'RegisterView',
    setup() {
        const router = useRouter()

        // Form state
        const registerForm = reactive({
            email: '',
            forename: '',
            surname: '',
            password: '',
            confirmPassword: '',
            jobTitle: '',
            location: ''
        })

        // Validation errors
        const errors = reactive({
            email: '',
            forename: '',
            surname: '',
            password: '',
            confirmPassword: ''
        })

        const registerError = ref<string | null>(null)
        const registerSuccess = ref(false)
        const isLoading = ref(false)

        // Validation
        function validateForm(): boolean {
            let isValid = true

            // Reset errors
            errors.email = ''
            errors.forename = ''
            errors.surname = ''
            errors.password = ''
            errors.confirmPassword = ''

            // Email validation
            if (!registerForm.email) {
                errors.email = 'Email is required'
                isValid = false
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
                errors.email = 'Please enter a valid email'
                isValid = false
            }

            // Forename validation
            if (!registerForm.forename) {
                errors.forename = 'First name is required'
                isValid = false
            } else if (registerForm.forename.length < 2) {
                errors.forename = 'First name must be at least 2 characters'
                isValid = false
            }

            // Surname validation
            if (!registerForm.surname) {
                errors.surname = 'Last name is required'
                isValid = false
            } else if (registerForm.surname.length < 2) {
                errors.surname = 'Last name must be at least 2 characters'
                isValid = false
            }

            // Password validation
            if (!registerForm.password) {
                errors.password = 'Password is required'
                isValid = false
            } else if (registerForm.password.length < 8) {
                errors.password = 'Password must be at least 8 characters'
                isValid = false
            } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(registerForm.password)) {
                errors.password = 'Password must contain uppercase, lowercase, and number'
                isValid = false
            }

            // Confirm password validation
            if (!registerForm.confirmPassword) {
                errors.confirmPassword = 'Please confirm your password'
                isValid = false
            } else if (registerForm.password !== registerForm.confirmPassword) {
                errors.confirmPassword = 'Passwords do not match'
                isValid = false
            }

            return isValid
        }

        // Handle registration
        async function handleRegister(): Promise<void> {
            registerError.value = null
            registerSuccess.value = false

            if (!validateForm()) {
                return
            }

            isLoading.value = true

            try {
                const profile = new Profile(
                    undefined, // id
                    registerForm.email,
                    registerForm.forename,
                    registerForm.surname,
                    registerForm.password,
                    registerForm.jobTitle || undefined,
                    registerForm.location || undefined
                )

                await authService.register(profile)

                registerSuccess.value = true

                // Redirect to login after 2 seconds
                setTimeout(() => {
                    router.push({
                        name: 'login',
                        query: { registered: 'true', email: registerForm.email }
                    })
                }, 2000)
            } catch (err) {
                registerError.value = err instanceof Error
                    ? err.message
                    : 'Registration failed. Please try again.'
            } finally {
                isLoading.value = false
            }
        }

        return {
            registerForm,
            errors,
            registerError,
            registerSuccess,
            isLoading,
            validateForm,
            handleRegister
        }
    }
}

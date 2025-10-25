import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ProfileService from '../../services/profile.service'

export default {
    name: 'EmailConfirmationView',
    setup() {
        const router = useRouter()
        const route = useRoute()
        const profileService = new ProfileService()

        const isLoading = ref(true)
        const isSuccess = ref(false)
        const isError = ref(false)
        const errorMessage = ref('')
        const countdown = ref(5)
        let countdownInterval: number | null = null

        const confirmEmail = async (code: string) => {
            try {
                isLoading.value = true
                await profileService.confirmEmail(code)
                isSuccess.value = true
                startCountdown()
            } catch (error: unknown) {
                isError.value = true

                // Handle different error scenarios
                const err = error as { response?: { status: number }; request?: unknown }
                if (err.response) {
                    switch (err.response.status) {
                        case 400:
                            errorMessage.value = 'Invalid or expired verification code.'
                            break
                        case 404:
                            errorMessage.value = 'Verification code not found. It may have already been used or expired.'
                            break
                        case 409:
                            errorMessage.value = 'This email address has already been verified.'
                            break
                        default:
                            errorMessage.value = 'An unexpected error occurred. Please try again later.'
                    }
                } else if (err.request) {
                    errorMessage.value = 'Unable to connect to the server. Please check your internet connection.'
                } else {
                    errorMessage.value = 'An error occurred while processing your request.'
                }

                console.error('Email confirmation error:', error)
            } finally {
                isLoading.value = false
            }
        }

        const startCountdown = () => {
            countdownInterval = globalThis.setInterval(() => {
                countdown.value--
                if (countdown.value <= 0) {
                    if (countdownInterval !== null) {
                        clearInterval(countdownInterval)
                    }
                    goToDashboard()
                }
            }, 1000)
        }

        const goToDashboard = () => {
            if (countdownInterval !== null) {
                clearInterval(countdownInterval)
            }
            router.push('/dashboard')
        }

        const goToHome = () => {
            if (countdownInterval !== null) {
                clearInterval(countdownInterval)
            }
            router.push('/')
        }

        onMounted(() => {
            const code = route.query.code as string

            if (code) {
                confirmEmail(code)
            } else {
                // Redirect to home if no code is provided
                router.push('/')
            }
        })

        return {
            isLoading,
            isSuccess,
            isError,
            errorMessage,
            countdown,
            goToDashboard,
            goToHome
        }
    }
}

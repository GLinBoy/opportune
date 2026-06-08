<template>
  <div>
    <v-menu min-width="200px" rounded transition="scale-y-transition" location="bottom right">
      <template v-slot:activator="{ props }">
        <v-btn icon v-bind="props">
          <UserAvatar :email="userEmail" :size="32" />
        </v-btn>
      </template>
      <v-card class="px-5">
        <v-card-text>
          <div class="mx-auto text-center">
            <UserAvatar :email="userEmail" :size="48" class="my-2" />
            <h3>{{ fullname }}</h3>
            <p class="text-caption mt-1">{{ userEmail }}</p>
            <v-divider class="my-3"></v-divider>
            <v-btn
              color="primary"
              variant="text"
              prepend-icon="mdi-card-account-details-outline"
              @click="goToProfile"
            >
              Profile
            </v-btn>
            <template v-if="isAdmin">
              <v-divider class="my-3"></v-divider>
              <v-btn
                color="primary"
                variant="text"
                prepend-icon="mdi-shield-crown-outline"
                @click="goToAdmin"
              >
                Admin Panel
              </v-btn>
            </template>
            <v-divider class="my-3"></v-divider>
            <v-btn color="primary" variant="text" prepend-icon="mdi-logout" @click="handleLogout">
              Logout
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth.store'
import ProfileService from '../../services/profile.service'
import type { IProfile } from '../../models'
import UserAvatar from '@/components/UserAvatar.vue'

const router = useRouter()
const authStore = useAuthStore()

const isAdmin = computed(() => authStore.isAdmin)
const profileService = new ProfileService()

const profile = ref<IProfile | null>(null)
const isLoading = ref(false)

const userEmail = computed(() => profile.value?.email || '')
const fullname = computed(() => {
  if (!profile.value) return 'Loading...'
  const forename = profile.value.forename || ''
  const surname = profile.value.surname || ''
  return `${forename} ${surname}`.trim() || 'User'
})

const loadProfile = async () => {
  try {
    isLoading.value = true
    const data = await profileService.getCurrentProfile()
    profile.value = data
  } catch (error) {
    console.error('Failed to load profile:', error)
    // Clear auth tokens and redirect to login if profile cannot be loaded
    authStore.clearAuth()
    router.push('/auth/login')
  } finally {
    isLoading.value = false
  }
}

const goToProfile = () => {
  router.push('/profile')
}

const goToAdmin = () => {
  router.push('/admin')
}

const handleLogout = async () => {
  try {
    await authStore.logout()
    // Redirect to login page
    router.push('/auth/login')
  } catch (error) {
    console.error('Logout error:', error)
    // Even if API call fails, clear local auth and redirect
    authStore.clearAuth()
    router.push('/auth/login')
  }
}

// Load profile on component mount
onMounted(() => {
  loadProfile()
})
</script>

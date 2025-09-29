<template>
  <div>
    <v-menu min-width="200px" rounded transition="scale-y-transition" location="bottom right">
      <template v-slot:activator="{ props }">
        <v-btn icon v-bind="props">
          <v-avatar :image="gravatarImage" size="32"> </v-avatar>
        </v-btn>
      </template>
      <v-card class="px-5">
        <v-card-text>
          <div class="mx-auto text-center">
            <v-avatar class="my-2" :image="gravatarImage" size="48"> </v-avatar>
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
            <v-divider class="my-3"></v-divider>
            <v-btn color="primary" variant="text" prepend-icon="mdi-shield-crown-outline">
              Control Panel
            </v-btn>
            <v-divider class="my-3"></v-divider>
            <v-btn color="primary" variant="text" prepend-icon="mdi-logout"> Logout </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import CryptoJS from 'crypto-js'

const router = useRouter()

const userEmail = ref('john.doe@example.com')
const fullname = ref('John Doe')

const gravatarImage = computed(() => {
  const trimmedEmail = userEmail.value.trim().toLowerCase()
  const hash = CryptoJS.MD5(trimmedEmail).toString()
  return `https://www.gravatar.com/avatar/${hash}?s=48&d=mp&r=g`
})

const goToProfile = () => {
  router.push('/profile')
}
</script>

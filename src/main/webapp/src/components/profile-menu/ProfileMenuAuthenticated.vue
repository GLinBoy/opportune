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
            <v-btn color="primary" variant="text" prepend-icon="mdi-card-account-details-outline">
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
import { ref } from 'vue'

const userEmail = ref('john.doe@example.com')
const gravatarImage = ref(
  'https://www.gravatar.com/avatar/00000000000000000000000000000000?s=32&d=mp&f=y'
)
const fullname = ref('John Doe')
</script>

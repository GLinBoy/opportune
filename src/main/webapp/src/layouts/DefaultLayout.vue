<template>
  <v-layout class="rounded rounded-md border">
    <v-app-bar flat color="primary">
      <v-container class="mx-auto d-flex align-center justify-center" style="max-width: 1400px">
        <v-btn icon to="/dashboard">
          <img src="@/assets/logo.png" alt="Opportune" width="32px" height="32px" />
        </v-btn>

        <v-btn v-for="link in links" :key="link" :text="link" variant="text" class="ms-1"></v-btn>

        <v-spacer></v-spacer>

        <v-text-field
          flat
          clearable
          hide-details
          single-line
          max-width="100%"
          rounded="lg"
          label="Search..."
          density="compact"
          variant="solo-filled"
          prepend-inner-icon="mdi-magnify"
        ></v-text-field>
        <ProfileMenu></ProfileMenu>
      </v-container>
    </v-app-bar>

    <v-main>
      <v-container class="mx-auto" style="max-width: 1400px">
        <router-view />
      </v-container>
    </v-main>
  </v-layout>
</template>

<script setup lang="ts">
import ProfileMenu from '@/components/profile-menu/ProfileMenu.vue'
const links = ['Dashboard', 'Applications', 'Companies']
</script>

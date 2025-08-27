<template>
  <v-app-bar flat color="primary">
    <v-container class="mx-auto d-flex align-center justify-center" style="max-width: 1400px">
      <v-btn icon to="/dashboard">
        <img src="@/assets/logo.png" alt="Opportune" width="32px" height="32px" />
      </v-btn>

      <NavigationLinks />

      <v-spacer></v-spacer>

      <SearchBar />
      <ProfileMenu></ProfileMenu>
    </v-container>
  </v-app-bar>
</template>

<script setup lang="ts">
import NavigationLinks from './NavigationLinks.vue'
import ProfileMenu from '@/components/profile-menu/ProfileMenu.vue'
import SearchBar from './SearchBar.vue'
</script>

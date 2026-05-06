<template>
  <v-app-bar flat color="surface" height="56" border="b">
    <v-container class="mx-auto d-flex align-center" style="max-width: 1400px">
      <!-- Logo -->
      <router-link to="/dashboard" class="brand-link d-flex align-center text-decoration-none me-4">
        <img src="@/assets/logo.png" alt="Opportune" width="28" height="28" />
      </router-link>

      <!-- Nav links -->
      <NavigationLinks />

      <v-spacer />

      <!-- Search: hidden on xs -->
      <div
        class="d-none d-sm-flex align-center me-1"
        :style="{ width: sm ? '200px' : '400px', minWidth: 0 }"
      >
        <SearchBar />
      </div>

      <!-- Theme toggle -->
      <v-btn
        :icon="isDark ? 'mdi-weather-sunny' : 'mdi-weather-night'"
        variant="text"
        size="small"
        class="me-1"
        :aria-label="isDark ? 'Switch to light mode' : 'Switch to dark mode'"
        @click="toggleTheme"
      />

      <!-- Help menu -->
      <HelpMenu class="me-1" />

      <!-- Notification bell -->
      <NotificationBell class="me-1" />

      <!-- Profile menu -->
      <ProfileMenu />
    </v-container>
  </v-app-bar>
</template>

<script lang="ts" src="./AppBar.ts" />

<style lang="css" scoped src="./AppBar.css" />

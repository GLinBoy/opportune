<template>
  <v-app :theme="currentTheme">
    <AppBar />

    <v-main class="tabler-main">
      <v-container class="mx-auto pa-6" style="max-width: 1400px">
        <router-view />
      </v-container>
    </v-main>

    <GoToTopButton />
  </v-app>
</template>

<script lang="ts" src="./DefaultLayout.ts" />

<style lang="css" scoped src="./DefaultLayout.css" />

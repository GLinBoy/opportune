<template>
  <v-layout class="rounded rounded-md border">
    <AppBar />

    <v-main>
      <v-container class="mx-auto" style="max-width: 1400px">
        <router-view />
      </v-container>
    </v-main>
  </v-layout>
</template>

<script setup lang="ts">
import AppBar from '@/components/app-bar/AppBar.vue'
</script>

<template>
  <v-layout class="rounded rounded-md border">
    <v-app-bar title="Application bar"></v-app-bar>
    <v-main class="d-flex align-center justify-center" height="300">
      <v-container>
        <router-view />
      </v-container>
    </v-main>
  </v-layout>
</template>

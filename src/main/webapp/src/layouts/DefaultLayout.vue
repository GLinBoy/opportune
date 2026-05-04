<template>
  <v-layout class="default-layout">
    <AppBar />

    <v-main>
      <v-container class="mx-auto" style="max-width: 1400px">
        <router-view />
      </v-container>
    </v-main>

    <GoToTopButton />
  </v-layout>
</template>

<script lang="ts" src="./DefaultLayout.ts" />

<style src="./DefaultLayout.css" />

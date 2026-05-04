<template>
  <v-avatar :size="size">
    <v-img :alt="alt" :src="currentSrc" @error="onError" />
  </v-avatar>
</template>
<script lang="ts" src="./CompanyLogo.ts" />

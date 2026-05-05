<template>
  <v-avatar :size="size">
    <v-img :alt="alt" :src="currentSrc" @error="onError">
      <template #placeholder>
        <v-img :alt="alt" :src="defaultLogo" />
      </template>
    </v-img>
  </v-avatar>
</template>
<script lang="ts" src="./CompanyLogo.ts" />

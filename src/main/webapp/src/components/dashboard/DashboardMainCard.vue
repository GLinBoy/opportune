<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'DashboardMainCard',
  props: {
    title: {
      type: String,
      required: true,
    },
    subtitle: {
      type: String,
      required: false,
      default: '',
    },
  },
})
</script>

<template>
  <v-card class="mx-auto" border flat>
    <v-list-item class="px-6" height="88">
      <template v-slot:title>{{ title }}</template>
      <template v-slot:subtitle v-if="subtitle">{{ subtitle }}</template>
    </v-list-item>

    <v-divider></v-divider>

    <v-card-text class="pa-0">
      <slot></slot>
    </v-card-text>
  </v-card>
</template>

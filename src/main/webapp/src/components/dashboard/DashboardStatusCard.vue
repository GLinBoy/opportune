<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'DashboardCard',
  props: {
    title: {
      type: String,
      required: true,
    },
    icon: {
      type: String,
      required: true,
    },
    value: {
      type: [String, Number],
      required: true,
    },
    description: {
      type: String,
      required: true,
    },
  },
})
</script>

<template>
  <v-card class="mx-auto" border flat>
    <v-list-item class="px-6" height="88">
      <template v-slot:title>{{ title }}</template>

      <template v-slot:append>
        <v-icon>{{ icon }}</v-icon>
      </template>
    </v-list-item>

    <v-divider></v-divider>

    <v-card-text class="text-medium-emphasis pa-6">
      <div class="text-h4 font-weight-black mb-4">{{ value }}</div>

      <div>{{ description }}</div>
    </v-card-text>
  </v-card>
</template>

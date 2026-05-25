<template>
  <RouterView />
  <v-snackbar v-model="toast.show" :color="toast.color" :timeout="toast.timeout">
    {{ toast.message }}
    <template #actions>
      <v-btn variant="text" @click="toast.dismiss()">Close</v-btn>
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { useToastStore } from './stores/toast'

const toast = useToastStore()
</script>

<template>
  <v-btn
    v-for="link in links"
    :key="link.title"
    :text="link.title"
    :prepend-icon="link.icon"
    :to="link.path"
    variant="text"
    class="ms-1"
  ></v-btn>
</template>

<script setup lang="ts">
const links = [
  { title: 'Dashboard', path: '/dashboard', icon: 'mdi-view-dashboard-outline' },
  { title: 'Companies', path: '/companies', icon: 'mdi-domain' },
  { title: 'Applications', path: '/applications', icon: 'mdi-briefcase-outline' },
]
</script>

<template>
  <v-btn
    v-for="link in links"
    :key="link.title"
    :text="link.title"
    :to="link.path"
    variant="text"
    class="ms-1"
  ></v-btn>
</template>

<script setup lang="ts">
const links = [
  { title: 'Dashboard', path: '/dashboard' },
  { title: 'Applications', path: '/applications' },
  { title: 'Companies', path: '/companies' },
]
</script>

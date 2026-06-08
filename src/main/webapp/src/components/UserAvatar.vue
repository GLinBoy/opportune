<template>
  <v-avatar :image="displayUrl" :size="size" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import CryptoJS from 'crypto-js'

const props = defineProps<{
  email: string
  avatarUrl?: string
  size?: number
}>()

const displayUrl = computed(() => {
  if (props.avatarUrl) {
    return props.avatarUrl
  }
  const trimmedEmail = props.email.trim().toLowerCase()
  const hash = CryptoJS.MD5(trimmedEmail).toString()
  const s = props.size ?? 40
  return `https://www.gravatar.com/avatar/${hash}?s=${s}&d=mp&r=g`
})
</script>

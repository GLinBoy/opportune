<template>
  <v-avatar :image="displayUrl" :size="size" />
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted } from 'vue'
import CryptoJS from 'crypto-js'
import apiClient from '@/services/api'

const props = defineProps<{
  email?: string
  avatarUrl?: string
  size?: number
  profileId?: string
}>()

const avatarBlobUrl = ref('')

const fetchAvatarBlob = async () => {
  if (avatarBlobUrl.value) {
    URL.revokeObjectURL(avatarBlobUrl.value)
    avatarBlobUrl.value = ''
  }
  if (props.avatarUrl && !props.avatarUrl.startsWith('http')) {
    try {
      const endpoint = props.profileId
        ? `/api/admin/users/${props.profileId}/avatar`
        : '/api/profiles/avatar'
      const response = await apiClient.get(endpoint, { responseType: 'blob' })
      avatarBlobUrl.value = URL.createObjectURL(response.data)
    } catch {
      // Fall through to Gravatar fallback
    }
  }
}

watch(() => props.avatarUrl, fetchAvatarBlob)
onMounted(fetchAvatarBlob)
onUnmounted(() => {
  if (avatarBlobUrl.value) {
    URL.revokeObjectURL(avatarBlobUrl.value)
  }
})

const displayUrl = computed(() => {
  if (props.avatarUrl) {
    if (props.avatarUrl.startsWith('http')) {
      return props.avatarUrl
    }
    if (avatarBlobUrl.value) {
      return avatarBlobUrl.value
    }
  }
  const email = (props.email ?? '').trim().toLowerCase()
  if (!email) return ''
  const hash = CryptoJS.MD5(email).toString()
  const s = props.size ?? 40
  return `https://www.gravatar.com/avatar/${hash}?s=${s}&d=mp&r=g`
})
</script>

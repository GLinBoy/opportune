import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { IProfile } from '../models'

export const useProfileStore = defineStore('profile', () => {
  const profile = ref<IProfile | null>(null)

  function setProfile(data: IProfile | null) {
    profile.value = data
  }

  return { profile, setProfile }
})

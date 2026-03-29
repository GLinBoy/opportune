<template>
  <v-card>
    <v-card-title class="d-flex align-center justify-space-between">
      <div class="d-flex align-center">
        <v-icon icon="mdi-account" class="mr-2" />
        Profile Information
      </div>
      <v-btn
        color="primary"
        variant="flat"
        prepend-icon="mdi-content-save"
        :loading="saving"
        :disabled="!hasChanges"
        @click="$emit('save')"
      >
        Save Changes
      </v-btn>
    </v-card-title>
    <v-card-text>
      <ProfileForm
        v-if="profile"
        :model-value="profile"
        @update:model-value="$emit('update:profile', $event)"
        @change="$emit('change')"
      />
      <div v-else class="text-center py-8 text-medium-emphasis">
        <v-icon icon="mdi-account-off" size="48" class="mb-2" />
        <p>Unable to load profile information.</p>
      </div>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import type { IProfile } from '../../models'
import ProfileForm from '../ProfileForm.vue'

export default defineComponent({
  name: 'ProfileInfoCard',
  components: { ProfileForm },
  props: {
    profile: {
      type: Object as () => IProfile | null,
      default: null
    },
    saving: {
      type: Boolean,
      default: false
    },
    hasChanges: {
      type: Boolean,
      default: false
    }
  },
  emits: ['save', 'change', 'update:profile']
})
</script>

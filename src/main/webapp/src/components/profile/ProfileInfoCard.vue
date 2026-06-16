<template>
  <FormCard :collapsible="false">
    <template #title>
      <v-icon icon="mdi-account" class="mr-2" />
      Profile Information
    </template>

    <template #default>
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
    </template>

    <template #actions>
      <v-btn
        text="Save Changes"
        variant="text"
        color="primary"
        prepend-icon="mdi-content-save"
        :loading="saving"
        :disabled="!hasChanges"
        min-width="120"
        @click="$emit('save')"
      />
    </template>
  </FormCard>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import type { IProfile } from '../../models'
import ProfileForm from './ProfileForm.vue'
import FormCard from '../forms/FormCard.vue'

export default defineComponent({
  name: 'ProfileInfoCard',
  components: { ProfileForm, FormCard },
  props: {
    profile: {
      type: Object as () => IProfile | null,
      default: null,
    },
    saving: {
      type: Boolean,
      default: false,
    },
    hasChanges: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['save', 'change', 'update:profile'],
})
</script>

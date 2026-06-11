<template>
  <v-card elevation="0" border rounded="lg" class="mb-4">
    <v-card-title class="pa-4 pb-0 d-flex align-center">
      <v-icon icon="mdi-card-account-details" color="primary" class="mr-2" />
      <span class="text-body-1 font-weight-semibold">Contact Information</span>
    </v-card-title>
    <v-card-text class="pa-4">
      <v-row>
        <v-col cols="12" md="4">
          <v-text-field
            :model-value="profile.phone"
            label="Phone"
            variant="outlined"
            density="compact"
            prepend-inner-icon="mdi-phone"
            placeholder="+1 (555) 123-4567"
            @update:model-value="updateField('phone', $event as string | undefined)"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field
            :model-value="profile.linkedinUrl"
            label="LinkedIn URL"
            variant="outlined"
            density="compact"
            prepend-inner-icon="mdi-linkedin"
            placeholder="https://linkedin.com/in/..."
            @update:model-value="updateField('linkedinUrl', $event as string | undefined)"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field
            :model-value="profile.portfolioUrl"
            label="Portfolio URL"
            variant="outlined"
            density="compact"
            prepend-inner-icon="mdi-web"
            placeholder="https://..."
            @update:model-value="updateField('portfolioUrl', $event as string | undefined)"
          />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { IProfile } from '../../../models'

defineProps<{
  profile: IProfile
}>()

const emit = defineEmits<{
  'update': [field: string, value: string | undefined | boolean]
}>()

function updateField(field: string, value: string | undefined) {
  emit('update', field, value)
}
</script>

<template>
  <v-card>
    <v-card-title class="d-flex align-center">
      <v-icon icon="mdi-lock" class="mr-2" />
      Password & Security
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12">
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">
              <v-icon icon="mdi-key-variant" class="mr-2" />
              Change Password
            </v-card-title>
            <v-card-text>
              <v-form @submit.prevent="$emit('change-password')">
                <v-text-field
                  :model-value="passwordForm.currentPassword"
                  @update:model-value="updateField('currentPassword', $event)"
                  label="Current Password"
                  :type="showCurrentPassword ? 'text' : 'password'"
                  :append-inner-icon="showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  @click:append-inner="showCurrentPassword = !showCurrentPassword"
                  variant="outlined"
                  @input="$emit('validate')"
                  class="mb-3"
                />
                <v-text-field
                  :model-value="passwordForm.newPassword"
                  @update:model-value="updateField('newPassword', $event)"
                  label="New Password"
                  :type="showNewPassword ? 'text' : 'password'"
                  :append-inner-icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  @click:append-inner="showNewPassword = !showNewPassword"
                  variant="outlined"
                  @input="$emit('validate')"
                  hint="Password must be at least 8 characters"
                  persistent-hint
                  class="mb-3"
                />
                <v-text-field
                  :model-value="passwordForm.confirmPassword"
                  @update:model-value="updateField('confirmPassword', $event)"
                  label="Confirm New Password"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  @click:append-inner="showConfirmPassword = !showConfirmPassword"
                  variant="outlined"
                  @input="$emit('validate')"
                />
              </v-form>
            </v-card-text>
            <v-card-actions>
              <v-btn
                color="primary"
                variant="flat"
                :disabled="!passwordFormValid"
                :loading="passwordChanging"
                @click="$emit('change-password')"
              >
                Update Password
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>

        <v-col cols="12">
          <v-card variant="outlined">
            <v-card-title class="text-subtitle-1">
              <v-icon icon="mdi-shield-key" class="mr-2" />
              Two-Factor Authentication
            </v-card-title>
            <v-card-text>
              <v-alert
                color="info"
                variant="tonal"
                icon="mdi-information"
                class="mb-4"
              >
                Two-factor authentication (2FA) feature is coming soon.
              </v-alert>
              <p class="text-body-2 mb-4">
                Two-factor authentication (2FA) adds an extra layer of security to your account.
              </p>
              <v-chip
                color="grey"
                variant="outlined"
                prepend-icon="mdi-shield-off"
              >
                Not Enabled
              </v-chip>
            </v-card-text>
            <v-card-actions>
              <v-btn color="success" variant="flat" disabled>
                Enable 2FA
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { ref, defineComponent } from 'vue'

export interface PasswordForm {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

export default defineComponent({
  name: 'PasswordSecurityCard',
  props: {
    passwordForm: {
      type: Object as () => PasswordForm,
      required: true
    },
    passwordFormValid: {
      type: Boolean,
      default: false
    },
    passwordChanging: {
      type: Boolean,
      default: false
    }
  },
  emits: ['change-password', 'validate', 'update:passwordForm'],
  setup(props, { emit }) {
    const showCurrentPassword = ref(false)
    const showNewPassword = ref(false)
    const showConfirmPassword = ref(false)

    const updateField = (field: keyof PasswordForm, value: string) => {
      emit('update:passwordForm', { ...props.passwordForm, [field]: value })
    }

    return {
      showCurrentPassword,
      showNewPassword,
      showConfirmPassword,
      updateField
    }
  }
})
</script>

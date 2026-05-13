<template>
  <div>
    <!-- Change Password -->
    <FormCard class="mb-4" :collapsible="false">
      <template #title>
        <v-icon icon="mdi-key-variant" class="mr-2" />
        Change Password
      </template>

      <template #default>
        <v-form @submit.prevent="$emit('change-password')">
          <v-text-field
            :model-value="passwordForm.currentPassword"
            label="Current Password"
            :type="showCurrentPassword ? 'text' : 'password'"
            :append-inner-icon="showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'"
            variant="outlined"
            class="mb-3"
            @update:model-value="updateField('currentPassword', $event)"
            @click:append-inner="showCurrentPassword = !showCurrentPassword"
            @input="$emit('validate')"
          />
          <v-text-field
            :model-value="passwordForm.newPassword"
            label="New Password"
            :type="showNewPassword ? 'text' : 'password'"
            :append-inner-icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
            variant="outlined"
            hint="Password must be at least 8 characters"
            persistent-hint
            class="mb-3"
            @update:model-value="updateField('newPassword', $event)"
            @click:append-inner="showNewPassword = !showNewPassword"
            @input="$emit('validate')"
          />
          <v-text-field
            :model-value="passwordForm.confirmPassword"
            label="Confirm New Password"
            :type="showConfirmPassword ? 'text' : 'password'"
            :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
            variant="outlined"
            @update:model-value="updateField('confirmPassword', $event)"
            @click:append-inner="showConfirmPassword = !showConfirmPassword"
            @input="$emit('validate')"
          />
        </v-form>
      </template>

      <template #actions>
        <v-btn
          text="Update Password"
          variant="text"
          color="primary"
          prepend-icon="mdi-content-save"
          :disabled="!passwordFormValid"
          :loading="passwordChanging"
          min-width="120"
          @click="$emit('change-password')"
        />
      </template>
    </FormCard>

    <!-- Two-Factor Authentication -->
    <FormCard :collapsible="false">
      <template #title>
        <v-icon icon="mdi-shield-key" class="mr-2" />
        Two-Factor Authentication
      </template>

      <template #default>
        <v-alert color="info" variant="tonal" icon="mdi-information" class="mb-4">
          Two-factor authentication (2FA) feature is coming soon.
        </v-alert>
        <p class="text-body-2 mb-4">
          Two-factor authentication (2FA) adds an extra layer of security to your account.
        </p>
        <v-chip color="grey" variant="outlined" prepend-icon="mdi-shield-off"> Not Enabled </v-chip>
      </template>

      <template #actions>
        <v-btn
          text="Enable 2FA"
          variant="text"
          color="success"
          prepend-icon="mdi-shield-check"
          min-width="120"
          disabled
        />
      </template>
    </FormCard>
  </div>
</template>

<script lang="ts">
import { ref, defineComponent } from 'vue'
import FormCard from '../forms/FormCard.vue'

export interface PasswordForm {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

export default defineComponent({
  name: 'PasswordSecurityCard',
  components: { FormCard },
  props: {
    passwordForm: {
      type: Object as () => PasswordForm,
      required: true,
    },
    passwordFormValid: {
      type: Boolean,
      default: false,
    },
    passwordChanging: {
      type: Boolean,
      default: false,
    },
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
      updateField,
    }
  },
})
</script>

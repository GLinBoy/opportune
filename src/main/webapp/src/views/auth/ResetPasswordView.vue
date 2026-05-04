<template>
  <v-container class="d-flex align-center justify-center" fluid style="min-height: 100vh">
    <v-row align="center" justify="center" class="w-100">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card rounded="lg" elevation="8">
          <v-card-text class="text-center pt-6 pb-0">
            <AppLogo />
          </v-card-text>
          <v-card-title class="text-h5 font-weight-bold text-center">
            Set New Password
          </v-card-title>
          <v-card-subtitle class="text-center pb-2">
            Please enter your new password below
          </v-card-subtitle>

          <v-card-text>
            <!-- Invalid/Expired Token Message -->
            <template v-if="invalidToken">
              <v-alert type="error" variant="tonal" class="mb-4">
                <strong>Invalid or Expired Link</strong>
                <p class="mb-0 mt-1">
                  This password reset link is invalid or has expired. Please request a new password
                  reset link.
                </p>
              </v-alert>
              <v-btn color="primary" variant="elevated" block :to="'/auth/forgot-password'">
                Request New Link
              </v-btn>
            </template>

            <!-- Reset Form -->
            <v-form v-else-if="!resetSuccess" @submit.prevent="handleResetPassword">
              <v-text-field
                v-model="resetForm.password"
                label="New Password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Enter new password"
                :error-messages="errors.password"
                :disabled="isLoading"
                autocomplete="new-password"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-lock-outline"
                :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                @click:append-inner="showPassword = !showPassword"
                class="mb-2"
                required
              />

              <v-text-field
                v-model="resetForm.confirmPassword"
                label="Confirm Password"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="Confirm new password"
                :error-messages="errors.confirmPassword"
                :disabled="isLoading"
                autocomplete="new-password"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-lock-check-outline"
                :append-inner-icon="showConfirmPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                @click:append-inner="showConfirmPassword = !showConfirmPassword"
                class="mb-2"
                required
              />

              <!-- Password Requirements -->
              <v-card variant="tonal" color="surface-variant" class="mb-4 pa-3" rounded="lg">
                <div class="text-caption font-weight-bold mb-1">Password must contain:</div>
                <div
                  class="text-caption"
                  :class="passwordValidation.minLength ? 'text-success' : 'text-medium-emphasis'"
                >
                  {{ passwordValidation.minLength ? '✓' : '○' }} At least 8 characters
                </div>
                <div
                  class="text-caption"
                  :class="passwordValidation.hasUppercase ? 'text-success' : 'text-medium-emphasis'"
                >
                  {{ passwordValidation.hasUppercase ? '✓' : '○' }} At least one uppercase letter
                </div>
                <div
                  class="text-caption"
                  :class="passwordValidation.hasLowercase ? 'text-success' : 'text-medium-emphasis'"
                >
                  {{ passwordValidation.hasLowercase ? '✓' : '○' }} At least one lowercase letter
                </div>
                <div
                  class="text-caption"
                  :class="passwordValidation.hasNumber ? 'text-success' : 'text-medium-emphasis'"
                >
                  {{ passwordValidation.hasNumber ? '✓' : '○' }} At least one number
                </div>
              </v-card>

              <v-alert v-if="resetError" type="error" variant="tonal" class="mb-4">
                {{ resetError }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                variant="elevated"
                :loading="isLoading"
                :disabled="!isPasswordValid"
                block
                size="large"
              >
                Reset Password
              </v-btn>
            </v-form>

            <!-- Success Message -->
            <template v-else>
              <v-alert type="success" variant="tonal" class="mb-4">
                <strong>Password Reset Successfully!</strong>
                <p class="mb-0 mt-1">
                  Your password has been changed. You can now login with your new password.
                </p>
              </v-alert>
              <v-btn color="primary" variant="elevated" block :to="'/auth/login'">
                Go to Login
              </v-btn>
            </template>
          </v-card-text>

          <template v-if="!invalidToken && !resetSuccess">
            <v-divider />
            <v-card-actions class="justify-center pa-4">
              <v-btn variant="text" color="primary" size="small" :to="'/auth/login'">
                ← Back to Login
              </v-btn>
            </v-card-actions>
          </template>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" src="./ResetPasswordView.ts" />

<template>
  <v-container class="d-flex align-center justify-center" fluid style="min-height: 100vh">
    <v-row align="center" justify="center" class="w-100">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card rounded="lg" elevation="8">
          <v-card-text class="text-center pt-6 pb-0">
            <AppLogo />
          </v-card-text>
          <v-card-title class="text-h5 font-weight-bold text-center"> Reset Password </v-card-title>
          <v-card-subtitle class="text-center pb-2" style="white-space: normal">
            Enter your email address and we'll send you a link to reset your password
          </v-card-subtitle>

          <v-card-text>
            <v-form v-if="!resetSuccess" @submit.prevent="handleForgotPassword">
              <v-text-field
                v-model="email"
                label="Email Address"
                type="email"
                placeholder="Enter your email"
                :error-messages="errors.email"
                :disabled="isLoading"
                autocomplete="email"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-email-outline"
                class="mb-2"
                required
              />

              <v-alert v-if="resetError" type="error" variant="tonal" class="mb-4">
                {{ resetError }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                variant="elevated"
                :loading="isLoading"
                block
                size="large"
              >
                Send Reset Link
              </v-btn>
            </v-form>

            <div v-else>
              <v-alert type="success" variant="tonal" class="mb-4">
                <strong>Email Sent Successfully!</strong>
                <p class="mt-1 mb-0">
                  We've sent a password reset link to <strong>{{ email }}</strong
                  >. Please check your inbox and follow the instructions to reset your password.
                </p>
              </v-alert>

              <v-card variant="tonal" color="surface-variant" rounded="lg" class="pa-4 mb-2">
                <p class="text-subtitle-2 font-weight-bold mb-2">Didn't receive the email?</p>
                <ul class="text-body-2 pl-4 mb-4">
                  <li>Check your spam or junk folder</li>
                  <li>Make sure you entered the correct email address</li>
                  <li>Wait a few minutes and check again</li>
                </ul>
                <v-btn variant="outlined" color="primary" block @click="resetForm">
                  Try Another Email
                </v-btn>
              </v-card>
            </div>
          </v-card-text>

          <v-card-actions class="justify-center pa-4">
            <v-btn variant="text" color="primary" size="small" :to="'/auth/login'">
              <v-icon start>mdi-arrow-left</v-icon>
              Back to Login
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" src="./ForgotPasswordView.ts" />

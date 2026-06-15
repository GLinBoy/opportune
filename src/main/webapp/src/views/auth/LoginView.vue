<template>
  <v-container class="d-flex align-center justify-center" fluid style="min-height: 100vh">
    <v-row align="center" justify="center" class="w-100">
      <v-col cols="12" sm="8" md="5" lg="4" style="max-width: 500px; width: 100%">
        <v-card rounded="lg" elevation="8">
          <v-card-text class="text-center pt-6 pb-0">
            <AppLogo />
          </v-card-text>
          <v-card-title class="text-h5 font-weight-bold text-center"> Welcome Back </v-card-title>
          <v-card-subtitle class="text-center pb-2"> Please login to your account </v-card-subtitle>

          <v-card-text>
            <v-alert
              v-if="registrationSuccess"
              type="success"
              variant="tonal"
              class="mb-4"
              closable
            >
              Account created successfully! Please login with your credentials.
            </v-alert>

            <v-form @submit.prevent="handleLogin">
              <v-text-field
                v-model="loginForm.email"
                label="Email"
                type="email"
                placeholder="Enter your email"
                :error-messages="errors.email"
                :disabled="isLoading"
                autocomplete="email"
                variant="outlined"
                prepend-inner-icon="mdi-email-outline"
                class="mb-2"
                required
              />

              <v-text-field
                v-model="loginForm.password"
                label="Password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Enter your password"
                :error-messages="errors.password"
                :disabled="isLoading"
                autocomplete="current-password"
                variant="outlined"
                prepend-inner-icon="mdi-lock-outline"
                :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                @click:append-inner="showPassword = !showPassword"
                class="mb-2"
                required
              />

              <v-checkbox
                v-model="loginForm.rememberMe"
                label="Remember me"
                :disabled="isLoading"
                density="compact"
                class="mb-2"
              />

              <v-alert v-if="loginError" type="error" variant="tonal" class="mb-4">
                {{ loginError }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                variant="elevated"
                :loading="isLoading"
                block
                size="large"
              >
                Login
              </v-btn>
            </v-form>
          </v-card-text>

          <v-divider />

          <v-card-actions class="justify-center pa-4">
            <v-btn variant="text" color="primary" size="small" :to="'/auth/forgot-password'">
              Forgot password?
            </v-btn>
            <v-divider vertical class="mx-2" />
            <v-btn variant="text" color="primary" size="small" :to="'/auth/register'">
              Create an account
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" src="./LoginView.ts" />

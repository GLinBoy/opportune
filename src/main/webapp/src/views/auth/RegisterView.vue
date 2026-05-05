<template>
  <v-container class="d-flex align-center justify-center" fluid style="min-height: 100vh">
    <v-row align="center" justify="center" class="w-100">
      <v-col cols="12" sm="8" md="6" lg="5">
        <v-card rounded="lg" elevation="8">
          <v-card-text class="text-center pt-6 pb-0">
            <AppLogo />
          </v-card-text>
          <v-card-title class="text-h5 font-weight-bold text-center"> Create Account </v-card-title>
          <v-card-subtitle class="text-center pb-2" style="white-space: normal">
            Join us and start tracking your job applications
          </v-card-subtitle>

          <v-card-text>
            <v-alert v-if="registerSuccess" type="success" variant="tonal" class="mb-4">
              Account created successfully! Redirecting to login...
            </v-alert>

            <v-alert v-if="registerError" type="error" variant="tonal" class="mb-4">
              {{ registerError }}
            </v-alert>

            <v-form @submit.prevent="handleRegister">
              <v-row dense>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="registerForm.forename"
                    label="First Name *"
                    type="text"
                    placeholder="Enter your first name"
                    :error-messages="errors.forename"
                    :disabled="isLoading"
                    autocomplete="given-name"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-account-outline"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="registerForm.surname"
                    label="Last Name *"
                    type="text"
                    placeholder="Enter your last name"
                    :error-messages="errors.surname"
                    :disabled="isLoading"
                    autocomplete="family-name"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-account-outline"
                    required
                  />
                </v-col>
              </v-row>

              <v-text-field
                v-model="registerForm.email"
                label="Email *"
                type="email"
                placeholder="Enter your email"
                :error-messages="errors.email"
                :disabled="isLoading"
                autocomplete="email"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-email-outline"
                class="mb-1"
                required
              />

              <v-row dense>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="registerForm.jobTitle"
                    label="Job Title"
                    type="text"
                    placeholder="e.g., Software Engineer"
                    :disabled="isLoading"
                    autocomplete="organization-title"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-briefcase-outline"
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="registerForm.location"
                    label="Location"
                    type="text"
                    placeholder="e.g., New York, USA"
                    :disabled="isLoading"
                    autocomplete="address-level2"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-map-marker-outline"
                  />
                </v-col>
              </v-row>

              <v-text-field
                v-model="registerForm.password"
                label="Password *"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Create a password"
                :error-messages="errors.password"
                :disabled="isLoading"
                autocomplete="new-password"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-lock-outline"
                :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                @click:append-inner="showPassword = !showPassword"
                class="mb-1"
                required
              />

              <v-text-field
                v-model="registerForm.confirmPassword"
                label="Confirm Password *"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="Confirm your password"
                :error-messages="errors.confirmPassword"
                :disabled="isLoading"
                autocomplete="new-password"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-lock-check-outline"
                :append-inner-icon="showConfirmPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                @click:append-inner="showConfirmPassword = !showConfirmPassword"
                class="mb-1"
                required
              />

              <v-card variant="tonal" color="info" rounded="lg" class="pa-3 mb-4">
                <p class="text-body-2 font-weight-bold mb-1">Password must contain:</p>
                <ul class="text-body-2 pl-4">
                  <li>At least 8 characters</li>
                  <li>At least one uppercase letter</li>
                  <li>At least one lowercase letter</li>
                  <li>At least one number</li>
                </ul>
              </v-card>

              <v-btn
                type="submit"
                color="primary"
                variant="elevated"
                :loading="isLoading"
                :disabled="registerSuccess"
                block
                size="large"
              >
                {{ registerSuccess ? 'Account Created!' : 'Create Account' }}
              </v-btn>
            </v-form>
          </v-card-text>

          <v-divider />

          <v-card-actions class="justify-center pa-4">
            <span class="text-body-2 text-medium-emphasis mr-1">Already have an account?</span>
            <v-btn variant="text" color="primary" size="small" :to="'/auth/login'"> Sign in </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" src="./RegisterView.ts" />

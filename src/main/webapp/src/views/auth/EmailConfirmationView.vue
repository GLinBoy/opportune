<template>
  <v-container class="d-flex align-center justify-center" fluid style="min-height: 100vh">
    <v-row align="center" justify="center" class="w-100">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card rounded="lg" elevation="8">
          <v-card-text class="text-center pt-6 pb-0">
            <AppLogo />
          </v-card-text>
          <!-- Loading State -->
          <v-card-text v-if="isLoading" class="text-center py-8">
            <v-progress-circular indeterminate color="primary" size="64" class="mb-6" />
            <p class="text-h6 font-weight-bold mb-2">Verifying Your Email</p>
            <p class="text-body-2 text-medium-emphasis">
              Please wait while we confirm your email address...
            </p>
          </v-card-text>

          <!-- Success State -->
          <v-card-text v-else-if="isSuccess" class="text-center py-8">
            <v-icon icon="mdi-check-circle" color="success" size="72" class="mb-4" />
            <p class="text-h6 font-weight-bold mb-2">Email Confirmed!</p>
            <p class="text-body-2 text-medium-emphasis mb-2">
              Your email address has been successfully verified.
            </p>
            <p class="text-body-2 text-medium-emphasis mb-6">
              Redirecting to dashboard in {{ countdown }} seconds...
            </p>
            <v-btn color="primary" variant="elevated" block @click="goToDashboard">
              Go to Dashboard Now
            </v-btn>
          </v-card-text>

          <!-- Error State -->
          <v-card-text v-else-if="isError" class="text-center py-8">
            <v-icon icon="mdi-close-circle" color="error" size="72" class="mb-4" />
            <p class="text-h6 font-weight-bold mb-2">Verification Failed</p>
            <v-alert type="error" variant="tonal" class="mb-4 text-left">
              {{ errorMessage }}
            </v-alert>
            <p class="text-body-2 text-medium-emphasis mb-4">
              You can request a new verification email from your profile settings.
            </p>
            <v-row dense>
              <v-col cols="6">
                <v-btn variant="outlined" color="primary" block @click="goToHome">
                  Go to Home
                </v-btn>
              </v-col>
              <v-col cols="6">
                <v-btn color="primary" variant="elevated" block @click="goToDashboard">
                  Go to Dashboard
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>

          <!-- No Code State -->
          <v-card-text v-else class="text-center py-8">
            <v-icon icon="mdi-alert-circle" color="warning" size="72" class="mb-4" />
            <p class="text-h6 font-weight-bold mb-2">Invalid Link</p>
            <p class="text-body-2 text-medium-emphasis mb-6">
              The verification link is invalid or missing the confirmation code.
            </p>
            <v-btn color="primary" variant="elevated" block @click="goToHome"> Go to Home </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" src="./EmailConfirmationView.ts" />

<template>
  <div class="email-confirmation-container">
    <div class="email-confirmation-card">
      <!-- Loading State -->
      <div v-if="isLoading" class="confirmation-content">
        <div class="loading-icon">
          <div class="spinner"></div>
        </div>
        <h1>Verifying Your Email</h1>
        <p>Please wait while we confirm your email address...</p>
      </div>

      <!-- Success State -->
      <div v-else-if="isSuccess" class="confirmation-content">
        <div class="success-icon">✓</div>
        <h1>Email Confirmed!</h1>
        <p>Your email address has been successfully verified.</p>
        <p class="redirect-message">Redirecting to dashboard in {{ countdown }} seconds...</p>
        <button @click="goToDashboard" class="btn-primary">
          Go to Dashboard Now
        </button>
      </div>

      <!-- Error State -->
      <div v-else-if="isError" class="confirmation-content">
        <div class="error-icon">✕</div>
        <h1>Verification Failed</h1>
        <p class="error-message">{{ errorMessage }}</p>
        <div class="error-actions">
          <p>You can request a new verification email from your profile settings.</p>
          <div class="button-group">
            <button @click="goToHome" class="btn-secondary">
              Go to Home
            </button>
            <button @click="goToDashboard" class="btn-primary">
              Go to Dashboard
            </button>
          </div>
        </div>
      </div>

      <!-- No Code State -->
      <div v-else class="confirmation-content">
        <div class="warning-icon">!</div>
        <h1>Invalid Link</h1>
        <p>The verification link is invalid or missing the confirmation code.</p>
        <button @click="goToHome" class="btn-primary">
          Go to Home
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./EmailConfirmationView.ts" />

<style lang="css" scoped src="./EmailConfirmationView.css" />

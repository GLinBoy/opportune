<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card">
      <div class="forgot-password-header">
        <h1>Reset Password</h1>
        <p>Enter your email address and we'll send you a link to reset your password</p>
      </div>

      <form v-if="!resetSuccess" @submit.prevent="handleForgotPassword" class="forgot-password-form">
        <div class="form-group">
          <label for="email">Email Address</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="Enter your email"
            required
            :disabled="isLoading"
            autocomplete="email"
          />
          <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
        </div>

        <div v-if="resetError" class="alert alert-error">
          <span class="alert-icon">⚠</span>
          <span>{{ resetError }}</span>
        </div>

        <button type="submit" class="btn-reset" :disabled="isLoading">
          <span v-if="!isLoading">Send Reset Link</span>
          <span v-else>Sending...</span>
        </button>
      </form>

      <!-- Success Message -->
      <div v-else class="success-container">
        <div class="alert alert-success">
          <span class="alert-icon">✓</span>
          <div class="success-content">
            <strong>Email Sent Successfully!</strong>
            <p>
              We've sent a password reset link to <strong>{{ email }}</strong>.
              Please check your inbox and follow the instructions to reset your password.
            </p>
          </div>
        </div>
        <div class="info-message">
          <p>Didn't receive the email?</p>
          <ul>
            <li>Check your spam or junk folder</li>
            <li>Make sure you entered the correct email address</li>
            <li>Wait a few minutes and check again</li>
          </ul>
          <button @click="resetForm" class="btn-try-again">
            Try Another Email
          </button>
        </div>
      </div>

      <div class="forgot-password-footer">
        <router-link to="/auth/login" class="link">
          <span class="back-arrow">←</span> Back to Login
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./ForgotPasswordView.ts" />

<style lang="css" scoped src="./ForgotPasswordView.css" />

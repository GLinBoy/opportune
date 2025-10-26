<template>
  <div class="reset-password-container">
    <div class="reset-password-card">
      <div class="reset-password-header">
        <h1>Set New Password</h1>
        <p>Please enter your new password below</p>
      </div>

      <!-- Invalid/Expired Token Message -->
      <div v-if="invalidToken" class="error-container">
        <div class="alert alert-error">
          <span class="alert-icon">⚠</span>
          <div class="error-content">
            <strong>Invalid or Expired Link</strong>
            <p>
              This password reset link is invalid or has expired.
              Please request a new password reset link.
            </p>
          </div>
        </div>
        <router-link to="/auth/forgot-password" class="btn-primary">
          Request New Link
        </router-link>
      </div>

      <!-- Reset Form -->
      <form v-else-if="!resetSuccess" @submit.prevent="handleResetPassword" class="reset-password-form">
        <div class="form-group">
          <label for="password">
            New Password<span class="required">*</span>
          </label>
          <input
            id="password"
            v-model="resetForm.password"
            type="password"
            placeholder="Enter new password"
            required
            :disabled="isLoading"
            autocomplete="new-password"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>

        <div class="form-group">
          <label for="confirmPassword">
            Confirm Password<span class="required">*</span>
          </label>
          <input
            id="confirmPassword"
            v-model="resetForm.confirmPassword"
            type="password"
            placeholder="Confirm new password"
            required
            :disabled="isLoading"
            autocomplete="new-password"
          />
          <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
        </div>

        <!-- Password Requirements -->
        <div class="password-requirements">
          <strong>Password must contain:</strong>
          <ul>
            <li :class="{ valid: passwordValidation.minLength }">At least 8 characters</li>
            <li :class="{ valid: passwordValidation.hasUppercase }">At least one uppercase letter</li>
            <li :class="{ valid: passwordValidation.hasLowercase }">At least one lowercase letter</li>
            <li :class="{ valid: passwordValidation.hasNumber }">At least one number</li>
          </ul>
        </div>

        <div v-if="resetError" class="alert alert-error">
          <span class="alert-icon">⚠</span>
          <span>{{ resetError }}</span>
        </div>

        <button type="submit" class="btn-reset" :disabled="isLoading || !isPasswordValid">
          <span v-if="!isLoading">Reset Password</span>
          <span v-else>Resetting...</span>
        </button>
      </form>

      <!-- Success Message -->
      <div v-else class="success-container">
        <div class="alert alert-success">
          <span class="alert-icon">✓</span>
          <div class="success-content">
            <strong>Password Reset Successfully!</strong>
            <p>
              Your password has been changed. You can now login with your new password.
            </p>
          </div>
        </div>
        <router-link to="/auth/login" class="btn-primary">
          Go to Login
        </router-link>
      </div>

      <div v-if="!invalidToken && !resetSuccess" class="reset-password-footer">
        <router-link to="/auth/login" class="link">
          <span class="back-arrow">←</span> Back to Login
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./ResetPasswordView.ts" />

<style lang="css" scoped src="./ResetPasswordView.css" />

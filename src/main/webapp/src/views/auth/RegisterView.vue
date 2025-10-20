<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>Create Account</h1>
        <p>Join us and start tracking your job applications</p>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <!-- Name Fields -->
        <div class="form-row">
          <div class="form-group">
            <label for="forename">
              First Name<span class="required">*</span>
            </label>
            <input
              id="forename"
              v-model="registerForm.forename"
              type="text"
              placeholder="Enter your first name"
              required
              :disabled="isLoading"
              autocomplete="given-name"
            />
            <span v-if="errors.forename" class="error-message">{{ errors.forename }}</span>
          </div>

          <div class="form-group">
            <label for="surname">
              Last Name<span class="required">*</span>
            </label>
            <input
              id="surname"
              v-model="registerForm.surname"
              type="text"
              placeholder="Enter your last name"
              required
              :disabled="isLoading"
              autocomplete="family-name"
            />
            <span v-if="errors.surname" class="error-message">{{ errors.surname }}</span>
          </div>
        </div>

        <!-- Email -->
        <div class="form-group">
          <label for="email">
            Email<span class="required">*</span>
          </label>
          <input
            id="email"
            v-model="registerForm.email"
            type="email"
            placeholder="Enter your email"
            required
            :disabled="isLoading"
            autocomplete="email"
          />
          <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
        </div>

        <!-- Optional Fields -->
        <div class="form-row">
          <div class="form-group">
            <label for="jobTitle">
              Job Title<span class="optional">(optional)</span>
            </label>
            <input
              id="jobTitle"
              v-model="registerForm.jobTitle"
              type="text"
              placeholder="e.g., Software Engineer"
              :disabled="isLoading"
              autocomplete="organization-title"
            />
          </div>

          <div class="form-group">
            <label for="location">
              Location<span class="optional">(optional)</span>
            </label>
            <input
              id="location"
              v-model="registerForm.location"
              type="text"
              placeholder="e.g., New York, USA"
              :disabled="isLoading"
              autocomplete="address-level2"
            />
          </div>
        </div>

        <!-- Password Fields -->
        <div class="form-row">
          <div class="form-group">
            <label for="password">
              Password<span class="required">*</span>
            </label>
            <input
              id="password"
              v-model="registerForm.password"
              type="password"
              placeholder="Create a password"
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
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="Confirm your password"
              required
              :disabled="isLoading"
              autocomplete="new-password"
            />
            <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
          </div>
        </div>

        <!-- Password Requirements -->
        <div class="password-requirements">
          <strong>Password must contain:</strong>
          <ul>
            <li>At least 8 characters</li>
            <li>At least one uppercase letter</li>
            <li>At least one lowercase letter</li>
            <li>At least one number</li>
          </ul>
        </div>

        <!-- Success Message -->
        <div v-if="registerSuccess" class="alert alert-success">
          <span class="alert-icon">✓</span>
          <span>Account created successfully! Redirecting to login...</span>
        </div>

        <!-- Error Message -->
        <div v-if="registerError" class="alert alert-error">
          <span class="alert-icon">⚠</span>
          <span>{{ registerError }}</span>
        </div>

        <!-- Submit Button -->
        <button type="submit" class="btn-register" :disabled="isLoading || registerSuccess">
          <span v-if="!isLoading && !registerSuccess">Create Account</span>
          <span v-else-if="registerSuccess">Account Created!</span>
          <span v-else>Creating Account...</span>
        </button>
      </form>

      <!-- Footer -->
      <div class="register-footer">
        <span>Already have an account?</span>
        <router-link to="/auth/login" class="link">Sign in</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./RegisterView.ts" />

<style lang="css" scoped src="./RegisterView.css" />

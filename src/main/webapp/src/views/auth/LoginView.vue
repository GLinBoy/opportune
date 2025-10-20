<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>Welcome Back</h1>
        <p>Please login to your account</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <!-- Registration Success Message -->
        <div v-if="registrationSuccess" class="alert alert-success">
          <span class="alert-icon">✓</span>
          <span>Account created successfully! Please login with your credentials.</span>
        </div>

        <div class="form-group">
          <label for="email">Email</label>
          <input
            id="email"
            v-model="loginForm.email"
            type="email"
            placeholder="Enter your email"
            required
            :disabled="isLoading"
            autocomplete="email"
          />
          <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            placeholder="Enter your password"
            required
            :disabled="isLoading"
            autocomplete="current-password"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input
              v-model="loginForm.rememberMe"
              type="checkbox"
              :disabled="isLoading"
            />
            <span>Remember me</span>
          </label>
        </div>

        <div v-if="loginError" class="alert alert-error">
          {{ loginError }}
        </div>

        <button type="submit" class="btn-login" :disabled="isLoading">
          <span v-if="!isLoading">Login</span>
          <span v-else>Logging in...</span>
        </button>
      </form>

      <div class="login-footer">
        <router-link to="/auth/forgot-password" class="link">Forgot password?</router-link>
        <div class="divider">|</div>
        <router-link to="/auth/register" class="link">Create an account</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./LoginView.ts" />

<style lang="css" scoped src="./LoginView.css" />

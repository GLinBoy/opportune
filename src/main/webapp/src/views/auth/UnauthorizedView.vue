<template>
  <v-container class="d-flex align-center justify-center" fluid style="min-height: 100vh">
    <v-row align="center" justify="center" class="w-100">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card rounded="lg" elevation="8" class="text-center">
          <v-card-text class="pt-6 pb-0">
            <AppLogo />
          </v-card-text>
          <v-card-text class="py-8">
            <v-icon icon="mdi-shield-lock-outline" color="error" size="80" class="mb-4" />
            <p class="text-h5 font-weight-bold mb-2">Access Denied</p>
            <p class="text-body-1 text-medium-emphasis mb-6">
              You don't have permission to access this resource.
            </p>
            <v-btn color="primary" variant="elevated" :to="'/dashboard'">
              <v-icon start>mdi-home-outline</v-icon>
              Go to Dashboard
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" src="./UnauthorizedView.ts" />

<style src="./UnauthorizedView.css" />

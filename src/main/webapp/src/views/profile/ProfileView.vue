<template>
  <div>
    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <div v-if="!loading">
      <!-- Breadcrumbs -->
      <v-breadcrumbs :items="breadcrumbs" class="pa-0 mb-4">
        <template #prepend>
          <v-icon icon="mdi-home" />
        </template>
      </v-breadcrumbs>

      <!-- Page Header -->
      <v-row class="mb-6" align="center">
        <v-col>
          <h1 class="text-h4 font-weight-bold">Profile Settings</h1>
          <p class="text-subtitle-1 text-medium-emphasis ma-0">
            Manage your account settings and profile information
          </p>
        </v-col>
      </v-row>

      <!-- Settings Layout (GitHub Style) -->
      <v-row>
        <!-- Left Sidebar with Tabs -->
        <v-col cols="12" md="3">
          <v-card class="mb-4">
            <v-list density="compact" nav>
              <v-list-item
                v-for="tab in tabs"
                :key="tab.value"
                :value="tab.value"
                :prepend-icon="tab.icon"
                :title="tab.title"
                :active="activeTab === tab.value"
                @click="activeTab = tab.value"
              />
            </v-list>
          </v-card>
        </v-col>

        <!-- Main Content Area -->
        <v-col cols="12" md="9">
          <!-- Info Tab -->
          <v-card v-show="activeTab === 'info'">
            <v-card-title class="d-flex align-center justify-space-between">
              <div class="d-flex align-center">
                <v-icon icon="mdi-account" class="mr-2" />
                Profile Information
              </div>
              <v-btn
                color="primary"
                variant="flat"
                prepend-icon="mdi-content-save"
                :loading="saving"
                @click="saveProfile"
                :disabled="!hasChanges"
              >
                Save Changes
              </v-btn>
            </v-card-title>
            <v-card-text>
              <ProfileForm
                v-if="profile"
                v-model="profile"
                @change="markAsModified"
              />
              <div v-else class="text-center py-8 text-medium-emphasis">
                <v-icon icon="mdi-account-off" size="48" class="mb-2" />
                <p>Unable to load profile information.</p>
              </div>
            </v-card-text>
          </v-card>

          <!-- Password Tab -->
          <v-card v-show="activeTab === 'password'">
            <v-card-title class="d-flex align-center">
              <v-icon icon="mdi-lock" class="mr-2" />
              Password & Security
            </v-card-title>
            <v-card-text>
              <v-alert
                color="info"
                variant="tonal"
                icon="mdi-information"
                class="mb-4"
              >
                Password and 2FA management features are coming soon.
              </v-alert>

              <v-row>
                <v-col cols="12">
                  <v-card variant="outlined" class="mb-4">
                    <v-card-title class="text-subtitle-1">
                      <v-icon icon="mdi-key-variant" class="mr-2" />
                      Change Password
                    </v-card-title>
                    <v-card-text>
                      <v-form>
                        <v-text-field
                          label="Current Password"
                          type="password"
                          variant="outlined"
                          disabled
                          class="mb-3"
                        />
                        <v-text-field
                          label="New Password"
                          type="password"
                          variant="outlined"
                          disabled
                          class="mb-3"
                        />
                        <v-text-field
                          label="Confirm New Password"
                          type="password"
                          variant="outlined"
                          disabled
                        />
                      </v-form>
                    </v-card-text>
                    <v-card-actions>
                      <v-btn color="primary" variant="flat" disabled>
                        Update Password
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-col>

                <v-col cols="12">
                  <v-card variant="outlined">
                    <v-card-title class="text-subtitle-1">
                      <v-icon icon="mdi-shield-key" class="mr-2" />
                      Two-Factor Authentication
                    </v-card-title>
                    <v-card-text>
                      <p class="text-body-2 mb-4">
                        Two-factor authentication (2FA) adds an extra layer of security to your account.
                      </p>
                      <v-chip
                        color="grey"
                        variant="outlined"
                        prepend-icon="mdi-shield-off"
                      >
                        Not Enabled
                      </v-chip>
                    </v-card-text>
                    <v-card-actions>
                      <v-btn color="success" variant="flat" disabled>
                        Enable 2FA
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>

          <!-- API & Webhook Tab -->
          <v-card v-show="activeTab === 'api'">
            <v-card-title class="d-flex align-center">
              <v-icon icon="mdi-api" class="mr-2" />
              API & Webhooks
            </v-card-title>
            <v-card-text>
              <v-alert
                color="info"
                variant="tonal"
                icon="mdi-information"
                class="mb-4"
              >
                API key and webhook management features are coming soon.
              </v-alert>

              <v-row>
                <v-col cols="12">
                  <v-card variant="outlined">
                    <v-card-title class="text-subtitle-1">
                      <v-icon icon="mdi-key" class="mr-2" />
                      API Key & Webhook Configuration
                    </v-card-title>
                    <v-card-text>
                      <p class="text-body-2 mb-4">
                        Configure your API key and webhook URL. The API key is required for authenticating webhook calls.
                      </p>

                      <v-alert
                        color="warning"
                        variant="tonal"
                        icon="mdi-alert"
                        class="mb-4"
                      >
                        Keep your API key secure and never share it publicly. It will be used to authenticate webhook requests.
                      </v-alert>

                      <!-- API Key Section -->
                      <div class="mb-6">
                        <h3 class="text-subtitle-1 mb-3 d-flex align-center">
                          <v-icon icon="mdi-key" class="mr-2" size="small" />
                          API Key
                        </h3>
                        <div class="text-center py-4">
                          <v-icon icon="mdi-key-off" size="48" class="mb-2" color="grey" />
                          <p class="text-medium-emphasis">No API key configured</p>
                          <v-btn color="primary" variant="flat" disabled class="mt-2">
                            <v-icon icon="mdi-plus" class="mr-1" />
                            Generate API Key
                          </v-btn>
                        </div>
                      </div>

                      <!-- Webhook Section -->
                      <div>
                        <h3 class="text-subtitle-1 mb-3 d-flex align-center">
                          <v-icon icon="mdi-webhook" class="mr-2" size="small" />
                          Webhook URL
                        </h3>
                        <p class="text-body-2 mb-3">
                          Enter your webhook URL to receive notifications about events in your account.
                        </p>
                        <v-text-field
                          label="Webhook URL"
                          variant="outlined"
                          prepend-inner-icon="mdi-link"
                          placeholder="https://your-app.com/webhook"
                          disabled
                          class="mb-3"
                        />
                        <div class="d-flex gap-2">
                          <v-btn color="primary" variant="flat" disabled>
                            <v-icon icon="mdi-content-save" class="mr-1" />
                            Save Webhook URL
                          </v-btn>
                          <v-btn color="error" variant="outlined" disabled>
                            <v-icon icon="mdi-test-tube" class="mr-1" />
                            Test Webhook
                          </v-btn>
                        </div>
                      </div>
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>

    <!-- Success/Error Snackbar -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.message }}
    </v-snackbar>
  </div>
</template>

<script lang="ts" src="./ProfileView.ts" />

<style lang="css" scoped>
.v-list-item--active {
  background-color: rgba(var(--v-theme-primary), 0.08) !important;
  color: rgb(var(--v-theme-primary)) !important;
}

.v-list-item--active .v-icon {
  color: rgb(var(--v-theme-primary)) !important;
}

.d-flex.gap-2 {
  gap: 8px;
}
</style>

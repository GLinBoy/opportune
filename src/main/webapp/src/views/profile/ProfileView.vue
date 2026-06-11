<template>
  <div>
    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <div v-if="!loading">
      <!-- Page Header -->
      <div class="d-flex align-center mb-4 mb-md-6 ga-3">
        <v-icon icon="mdi-account-cog" size="48" />
        <div class="d-flex flex-column">
          <span class="text-headline-small font-weight-bold">Profile Settings</span>
          <span class="text-label-medium text-medium-emphasis"
            >Manage your account settings and profile information</span
          >
        </div>
      </div>

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
                :ripple="true"
                @click="activeTab = tab.value"
              />
            </v-list>
          </v-card>
        </v-col>

        <!-- Main Content Area -->
        <v-col cols="12" md="9">
          <!-- Info Tab -->
          <ProfileInfoCard
            v-show="activeTab === 'info'"
            :profile="profile"
            :saving="saving"
            :has-changes="hasChanges"
            @update:profile="profile = $event"
            @save="saveProfile"
            @change="markAsModified"
          />

          <!-- Password Tab -->
          <PasswordSecurityCard
            v-show="activeTab === 'password'"
            :password-form="passwordForm"
            :password-form-valid="passwordFormValid"
            :password-changing="passwordChanging"
            @update:password-form="passwordForm = $event"
            @change-password="changePassword"
            @validate="validatePasswordForm"
          />

          <!-- Sessions Tab -->
          <SessionsCard
            v-show="activeTab === 'sessions'"
            :sessions="sessions"
            :loading="sessionsLoading"
            :terminating-session-id="terminatingSessionId"
            :highlighted-session-id="highlightedSessionId"
            @refresh="loadSessions"
            @terminate="terminateSession"
          />

          <!-- API & Webhook Tab -->
          <ApiWebhookCard v-show="activeTab === 'api'" />

          <!-- Resume Tab -->
          <ResumeTab
            v-show="activeTab === 'resume'"
            :profile="profile"
            @update:profile="profile = $event"
            @change="markAsModified"
          />
        </v-col>
      </v-row>
    </div>
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
</style>

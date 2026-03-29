<template>
  <v-card>
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
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'ApiWebhookCard'
})
</script>

<style lang="css" scoped>
.d-flex.gap-2 {
  gap: 8px;
}
</style>

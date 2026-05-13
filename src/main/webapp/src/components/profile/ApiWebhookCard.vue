<template>
  <div>
    <!-- API Key -->
    <FormCard class="mb-4" :collapsible="false">
      <template #title>
        <v-icon icon="mdi-key" class="mr-2" />
        API Key
      </template>

      <template #default>
        <v-alert color="info" variant="tonal" icon="mdi-information" class="mb-4">
          API key management is coming soon.
        </v-alert>
        <v-alert color="warning" variant="tonal" icon="mdi-alert" class="mb-4">
          Keep your API key secure and never share it publicly. It will be used to authenticate
          webhook requests.
        </v-alert>
        <div class="text-center py-4">
          <v-icon icon="mdi-key-off" size="48" class="mb-2" color="grey" />
          <p class="text-medium-emphasis">No API key configured</p>
        </div>
      </template>

      <template #actions>
        <v-btn
          text="Generate API Key"
          variant="text"
          color="primary"
          prepend-icon="mdi-plus"
          min-width="120"
          disabled
        />
      </template>
    </FormCard>

    <!-- Webhook -->
    <FormCard :collapsible="false">
      <template #title>
        <v-icon icon="mdi-webhook" class="mr-2" />
        Webhook URL
      </template>

      <template #default>
        <v-alert color="info" variant="tonal" icon="mdi-information" class="mb-4">
          Webhook management is coming soon.
        </v-alert>
        <p class="text-body-2 mb-4">
          Enter your webhook URL to receive notifications about events in your account.
        </p>
        <v-text-field
          label="Webhook URL"
          variant="outlined"
          prepend-inner-icon="mdi-link"
          placeholder="https://your-app.com/webhook"
          disabled
        />
      </template>

      <template #actions>
        <v-btn
          text="Test Webhook"
          variant="text"
          color="error"
          prepend-icon="mdi-test-tube"
          min-width="120"
          disabled
        />
        <v-btn
          text="Save Webhook URL"
          variant="text"
          color="primary"
          prepend-icon="mdi-content-save"
          min-width="120"
          disabled
        />
      </template>
    </FormCard>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import FormCard from '../forms/FormCard.vue'

export default defineComponent({
  name: 'ApiWebhookCard',
  components: { FormCard },
})
</script>

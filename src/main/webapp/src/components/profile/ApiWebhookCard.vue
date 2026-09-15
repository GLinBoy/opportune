<template>
  <div>
    <!-- API Token -->
    <FormCard class="mb-4" :collapsible="false">
      <template #title>
        <v-icon icon="mdi-key-plus" class="mr-2" />
        API Token
      </template>

      <template #default>
        <v-alert color="info" variant="tonal" icon="mdi-information" class="mb-4">
          Generate a token to connect the Send to Opportune browser extension. Each token has its own
          webhook URL and can be revoked independently.
        </v-alert>

        <template v-if="generated">
          <v-alert color="warning" variant="tonal" icon="mdi-alert" class="mb-4">
            Copy your endpoint URL and token now. The token is shown
            <strong>only once</strong> and cannot be retrieved again.
          </v-alert>

          <div class="text-caption text-medium-emphasis mb-1">Webhook endpoint URL</div>
          <v-text-field
            :model-value="generatedEndpointUrl"
            variant="outlined"
            density="comfortable"
            readonly
            hide-details
            prepend-inner-icon="mdi-link-variant"
            class="mb-3"
          >
            <template #append-inner>
              <v-tooltip text="Copy endpoint URL" location="top">
                <template #activator="{ props: tooltipProps }">
                  <v-btn
                    v-bind="tooltipProps"
                    :icon="copiedKey === 'generated-url' ? 'mdi-check' : 'mdi-content-copy'"
                    :color="copiedKey === 'generated-url' ? 'success' : undefined"
                    variant="text"
                    density="compact"
                    size="small"
                    @click="copyToClipboard('generated-url', generatedEndpointUrl)"
                  />
                </template>
              </v-tooltip>
            </template>
          </v-text-field>

          <div class="text-caption text-medium-emphasis mb-1">Token</div>
          <v-text-field
            :model-value="generated.token"
            :type="showToken ? 'text' : 'password'"
            variant="outlined"
            density="comfortable"
            readonly
            hide-details
            prepend-inner-icon="mdi-key"
          >
            <template #append-inner>
              <v-tooltip :text="showToken ? 'Hide token' : 'Reveal token'" location="top">
                <template #activator="{ props: tooltipProps }">
                  <v-btn
                    v-bind="tooltipProps"
                    :icon="showToken ? 'mdi-eye-off' : 'mdi-eye'"
                    variant="text"
                    density="compact"
                    size="small"
                    @click="showToken = !showToken"
                  />
                </template>
              </v-tooltip>
              <v-tooltip text="Copy token" location="top">
                <template #activator="{ props: tooltipProps }">
                  <v-btn
                    v-bind="tooltipProps"
                    :icon="copiedKey === 'generated-token' ? 'mdi-check' : 'mdi-content-copy'"
                    :color="copiedKey === 'generated-token' ? 'success' : undefined"
                    variant="text"
                    density="compact"
                    size="small"
                    @click="copyToClipboard('generated-token', generated.token)"
                  />
                </template>
              </v-tooltip>
            </template>
          </v-text-field>
        </template>

        <div v-else class="text-center py-4 text-medium-emphasis">
          <v-icon icon="mdi-key-off" size="48" class="mb-2" color="grey" />
          <p>No token generated yet.</p>
        </div>
      </template>

      <template #actions>
        <v-btn
          v-if="generated"
          text="Done"
          variant="text"
          color="primary"
          min-width="120"
          @click="emit('dismiss-generated')"
        />
        <v-btn
          text="Generate Token"
          variant="text"
          color="primary"
          prepend-icon="mdi-plus"
          min-width="120"
          :loading="generating"
          @click="emit('generate')"
        />
      </template>
    </FormCard>

    <!-- Webhooks -->
    <FormCard :collapsible="false">
      <template #title>
        <v-icon icon="mdi-webhook" class="mr-2" />
        Webhook Endpoints
        <v-tooltip text="Refresh tokens" location="top">
          <template #activator="{ props: tooltipProps }">
            <v-btn
              v-bind="tooltipProps"
              icon="mdi-refresh"
              variant="text"
              density="compact"
              size="small"
              class="ml-2"
              :loading="loading"
              @click="emit('refresh')"
            />
          </template>
        </v-tooltip>
      </template>

      <template #default>
        <v-alert color="info" variant="tonal" icon="mdi-information" class="mb-4">
          These are the webhook tokens connected to your account. Revoke any token you no longer use.
        </v-alert>

        <!-- Loading -->
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular color="primary" indeterminate size="48" />
        </div>

        <!-- Empty state -->
        <div v-else-if="tokens.length === 0" class="text-center py-8 text-medium-emphasis">
          <v-icon icon="mdi-webhook" size="48" class="mb-2" />
          <p>No webhook tokens configured.</p>
        </div>

        <!-- Tokens list -->
        <div v-else>
          <v-card v-for="token in tokens" :key="token.id" variant="outlined" class="mb-3">
            <v-card-text class="d-flex align-center">
              <v-icon
                icon="mdi-webhook"
                size="36"
                class="mr-4"
                :color="token.status === 'ACTIVE' ? 'success' : 'grey'"
              />
              <div class="flex-grow-1">
                <div class="d-flex align-center mb-1">
                  <span
                    class="text-subtitle-2 font-weight-bold mr-2 text-truncate flex-grow-1"
                    style="min-width: 0"
                  >
                    {{ endpointUrlFor(token) }}
                  </span>
                  <v-chip :color="statusColor(token.status)" size="x-small" variant="flat">
                    {{ token.status }}
                  </v-chip>
                </div>
                <div class="text-body-2 text-medium-emphasis">
                  <span v-if="token.createdAt">
                    <v-icon icon="mdi-calendar-plus" size="x-small" class="mr-1" />
                    Created: {{ formatDate(token.createdAt) }}
                  </span>
                  <span v-if="token.lastUsedAt" class="ml-3">
                    <v-icon icon="mdi-clock-outline" size="x-small" class="mr-1" />
                    Last used: {{ formatDate(token.lastUsedAt) }}
                  </span>
                </div>
                <div v-if="token.revokedAt" class="text-body-2 text-medium-emphasis mt-1">
                  <v-icon icon="mdi-cancel" size="x-small" class="mr-1" />
                  Revoked: {{ formatDate(token.revokedAt) }}
                </div>
              </div>
              <v-tooltip text="Copy endpoint URL" location="top">
                <template #activator="{ props: tooltipProps }">
                  <v-btn
                    v-bind="tooltipProps"
                    :icon="copiedKey === token.id ? 'mdi-check' : 'mdi-content-copy'"
                    :color="copiedKey === token.id ? 'success' : undefined"
                    variant="text"
                    density="compact"
                    size="small"
                    class="mr-1"
                    @click="copyToClipboard(token.id, endpointUrlFor(token))"
                  />
                </template>
              </v-tooltip>
              <v-btn
                v-if="token.status === 'ACTIVE'"
                color="error"
                variant="outlined"
                size="small"
                prepend-icon="mdi-shield-off-outline"
                :loading="revokingId === token.id"
                @click="confirmRevoke(token.id)"
              >
                Revoke
              </v-btn>
            </v-card-text>
          </v-card>
        </div>
      </template>
    </FormCard>

    <!-- Confirm Revoke Dialog -->
    <ConfirmDialog
      v-model="showRevokeDialog"
      title="Revoke Webhook Token"
      variant="warning"
      confirm-text="Revoke"
      cancel-text="Cancel"
      :loading="!!revokingId"
      @confirm="handleRevoke"
      @cancel="showRevokeDialog = false"
    >
      Are you sure you want to revoke this webhook token? Any extension using it will stop working
      immediately.
    </ConfirmDialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { IWebhookToken, IWebhookTokenGeneration } from '../../models'
import apiClient from '../../services/api'
import ConfirmDialog from '../ConfirmDialog.vue'
import FormCard from '../forms/FormCard.vue'

defineOptions({
  name: 'ApiWebhookCard'
})

const props = withDefaults(
  defineProps<{
    tokens: IWebhookToken[]
    loading?: boolean
    generating?: boolean
    generated?: IWebhookTokenGeneration | null
    revokingId?: string | null
  }>(),
  {
    loading: false,
    generating: false,
    generated: null,
    revokingId: null
  }
)

const emit = defineEmits<{
  refresh: []
  generate: []
  revoke: [id: string]
  'dismiss-generated': []
}>()

const apiBase = (apiClient.defaults.baseURL ?? '').replace(/\/+$/, '')

const showToken = ref(false)
const copiedKey = ref<string | null>(null)
const showRevokeDialog = ref(false)
const tokenToRevoke = ref<string | null>(null)

const generatedEndpointUrl = computed(() => {
  const endpointIdentifier = props.generated?.dto.endpointIdentifier
  return endpointIdentifier ? `${apiBase}/api/webhook/${endpointIdentifier}` : ''
})

const endpointUrlFor = (token: IWebhookToken): string =>
  token.endpointIdentifier ? `${apiBase}/api/webhook/${token.endpointIdentifier}` : 'Unknown endpoint'

const statusColor = (status?: string | null): string => {
  if (status === 'ACTIVE') return 'success'
  if (status === 'REVOKED') return 'error'
  return 'grey'
}

const formatDate = (value?: string | Date | null): string =>
  value ? new Date(value).toLocaleString() : '—'

const copyToClipboard = async (key: string | undefined, value?: string | null) => {
  if (!key || !value) return
  try {
    await navigator.clipboard.writeText(value)
    copiedKey.value = key
    setTimeout(() => {
      if (copiedKey.value === key) copiedKey.value = null
    }, 2000)
  } catch (error) {
    console.error('Failed to copy to clipboard:', error)
  }
}

const confirmRevoke = (id: string | undefined) => {
  if (!id) return
  tokenToRevoke.value = id
  showRevokeDialog.value = true
}

const handleRevoke = () => {
  if (tokenToRevoke.value) {
    emit('revoke', tokenToRevoke.value)
  }
  showRevokeDialog.value = false
  tokenToRevoke.value = null
}
</script>

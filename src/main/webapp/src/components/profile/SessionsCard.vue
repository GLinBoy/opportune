<template>
  <div>
    <v-card>
      <v-card-title class="d-flex align-center justify-space-between">
        <div class="d-flex align-center">
          <v-icon icon="mdi-devices" class="mr-2" />
          Active Sessions
        </div>
        <v-btn
          color="primary"
          variant="outlined"
          prepend-icon="mdi-refresh"
          :loading="loading"
          @click="$emit('refresh')"
        >
          Refresh
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-alert color="info" variant="tonal" icon="mdi-information" class="mb-4">
          These are the devices that are currently logged in to your account. Revoke any sessions
          that you do not recognize.
        </v-alert>

        <!-- Loading -->
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular color="primary" indeterminate size="48" />
        </div>

        <!-- Empty state -->
        <div v-else-if="sessions.length === 0" class="text-center py-8 text-medium-emphasis">
          <v-icon icon="mdi-devices" size="48" class="mb-2" />
          <p>No active sessions found.</p>
        </div>

        <!-- Sessions list -->
        <div v-else>
          <v-card
            v-for="session in sessions"
            :key="session.refreshTokenId"
            :ref="(el) => setSessionRef(session.refreshTokenId, el)"
            variant="outlined"
            class="mb-3"
            :class="{ 'highlighted-session': highlightedSessionId === session.refreshTokenId }"
          >
            <v-card-text class="d-flex align-center">
              <v-icon
                :icon="session.mobile ? 'mdi-cellphone' : 'mdi-monitor'"
                size="36"
                class="mr-4"
                :color="session.status === 'ACTIVE' ? 'success' : 'grey'"
              />
              <div class="flex-grow-1">
                <div class="d-flex align-center mb-1">
                  <span class="text-subtitle-2 font-weight-bold mr-2">
                    {{ session.browser || 'Unknown Browser' }}
                    <span v-if="session.os"> on {{ session.os }}</span>
                  </span>
                  <v-chip
                    :color="
                      session.status === 'ACTIVE'
                        ? 'success'
                        : session.status === 'REVOKED'
                        ? 'error'
                        : 'grey'
                    "
                    size="x-small"
                    variant="flat"
                  >
                    {{ session.status }}
                  </v-chip>
                  <v-chip
                    v-if="isCurrentSession(session)"
                    color="primary"
                    size="x-small"
                    variant="flat"
                    prepend-icon="mdi-check-circle"
                    class="ml-1"
                  >
                    This device
                  </v-chip>
                </div>
                <div class="text-body-2 text-medium-emphasis">
                  <span v-if="session.clientIp">
                    <v-icon icon="mdi-ip-network" size="x-small" class="mr-1" />
                    {{ session.clientIp }}
                  </span>
                  <span v-if="session.clientGeo" class="ml-3">{{ session.clientGeo }}</span>
                </div>
                <div class="text-body-2 text-medium-emphasis mt-1">
                  <span v-if="session.loginAt">
                    <v-icon icon="mdi-login" size="x-small" class="mr-1" />
                    Logged in: {{ formatDate(session.loginAt) }}
                  </span>
                  <span v-if="session.lastActiveAt" class="ml-3">
                    <v-icon icon="mdi-clock-outline" size="x-small" class="mr-1" />
                    Last active: {{ formatDate(session.lastActiveAt) }}
                  </span>
                </div>
                <div v-if="session.deviceType" class="text-body-2 text-medium-emphasis mt-1">
                  <v-icon icon="mdi-devices" size="x-small" class="mr-1" />
                  {{ session.deviceType }}
                </div>
              </div>
              <v-btn
                v-if="session.status === 'ACTIVE'"
                color="error"
                variant="outlined"
                size="small"
                prepend-icon="mdi-shield-off-outline"
                :loading="terminatingSessionId === session.refreshTokenId"
                @click="confirmRevoke(session.refreshTokenId)"
              >
                Revoke
              </v-btn>
            </v-card-text>
          </v-card>
        </div>
      </v-card-text>
    </v-card>

    <!-- Confirm Revoke Dialog -->
    <ConfirmDialog
      v-model="showRevokeDialog"
      title="Revoke Session"
      variant="warning"
      confirm-text="Revoke"
      cancel-text="Cancel"
      @confirm="handleRevoke"
      @cancel="showRevokeDialog = false"
    >
      Are you sure you want to revoke this session? The device will be signed out immediately.
    </ConfirmDialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, watch, nextTick, ref } from 'vue'
import type { ISession } from '../../models'
import ConfirmDialog from '../ConfirmDialog.vue'

function getAccessTokenId(): string | null {
  const token = localStorage.getItem('accessToken')
  if (!token) return null
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return null
    const base64 = parts[1]!.split('-').join('+').split('_').join('/')
    const payload = JSON.parse(atob(base64))
    return payload.jti || null
  } catch {
    return null
  }
}

export default defineComponent({
  name: 'SessionsCard',
  components: { ConfirmDialog },
  props: {
    sessions: {
      type: Array as () => ISession[],
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    terminatingSessionId: {
      type: String as () => string | null,
      default: null,
    },
    highlightedSessionId: {
      type: String as () => string | null,
      default: null,
    },
  },
  emits: ['refresh', 'terminate'],
  setup(props, context) {
    const currentAccessTokenId = computed(() => getAccessTokenId())
    const sessionRefs: Record<string, Element | null> = {}
    const showRevokeDialog = ref(false)
    const sessionToRevoke = ref<string | null>(null)

    const confirmRevoke = (id: string | undefined) => {
      if (!id) return
      sessionToRevoke.value = id
      showRevokeDialog.value = true
    }

    const handleRevoke = () => {
      if (sessionToRevoke.value) {
        context.emit('terminate', sessionToRevoke.value)
      }
      showRevokeDialog.value = false
      sessionToRevoke.value = null
    }

    const setSessionRef = (id: string | undefined, el: unknown) => {
      if (id) sessionRefs[id] = el as Element | null
    }

    const formatDate = (value: string | Date): string => {
      return new Date(value).toLocaleString()
    }

    const isCurrentSession = (session: ISession): boolean => {
      return !!currentAccessTokenId.value && session.accessTokenId === currentAccessTokenId.value
    }

    watch(
      () => [props.sessions, props.highlightedSessionId] as const,
      () => {
        if (props.highlightedSessionId && props.sessions.length > 0) {
          nextTick(() => {
            const el = sessionRefs[props.highlightedSessionId!]
            if (el) {
              el.scrollIntoView({ behavior: 'smooth', block: 'center' })
            }
          })
        }
      },
      { immediate: true }
    )

    return {
      formatDate,
      setSessionRef,
      isCurrentSession,
      showRevokeDialog,
      confirmRevoke,
      handleRevoke,
    }
  },
})
</script>

<style lang="css" scoped>
.highlighted-session {
  border-color: rgb(var(--v-theme-warning)) !important;
  border-width: 2px !important;
  box-shadow: 0 0 8px rgba(var(--v-theme-warning), 0.3);
  animation: highlight-pulse 2s ease-in-out 3;
}

@keyframes highlight-pulse {
  0%,
  100% {
    box-shadow: 0 0 8px rgba(var(--v-theme-warning), 0.3);
  }
  50% {
    box-shadow: 0 0 16px rgba(var(--v-theme-warning), 0.6);
  }
}
</style>

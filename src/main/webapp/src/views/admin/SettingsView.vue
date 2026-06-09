<template>
  <div>
    <!-- ─── Feature Flags ─── -->
    <FormCard class="mb-6" :collapsible="false">
      <template #title>
        <v-icon icon="mdi-toggle-switch-outline" color="primary" class="mr-2" />
        Feature Flags
      </template>

      <v-skeleton-loader
        v-if="loading"
        type="list-item-two-line,list-item-two-line,list-item-two-line"
      />

      <v-list v-else lines="two">
        <v-list-item>
          <template #prepend>
            <v-icon icon="mdi-account-plus-outline" />
          </template>
          <v-list-item-title>User Registration</v-list-item-title>
          <v-list-item-subtitle>Allow new users to create accounts</v-list-item-subtitle>
          <template #append>
            <v-switch
              v-model="form.registrationEnabled"
              color="primary"
              density="compact"
              hide-details
              inset
            />
          </template>
        </v-list-item>

        <v-divider inset />

        <v-list-item>
          <template #prepend>
            <v-icon icon="mdi-robot-outline" />
          </template>
          <v-list-item-title>AI Scoring</v-list-item-title>
          <v-list-item-subtitle
            >Run AI analysis and scoring on submitted applications</v-list-item-subtitle
          >
          <template #append>
            <v-switch
              v-model="form.aiScoringEnabled"
              color="primary"
              density="compact"
              hide-details
              inset
            />
          </template>
        </v-list-item>

        <v-divider inset />

        <v-list-item>
          <template #prepend>
            <v-icon icon="mdi-email-outline" />
          </template>
          <v-list-item-title>Email Notifications</v-list-item-title>
          <v-list-item-subtitle
            >Send transactional emails (verification, alerts, etc.)</v-list-item-subtitle
          >
          <template #append>
            <v-switch
              v-model="form.emailNotificationsEnabled"
              color="primary"
              density="compact"
              hide-details
              inset
            />
          </template>
        </v-list-item>
      </v-list>
    </FormCard>

    <!-- ─── Maintenance Mode ─── -->
    <FormCard class="mb-6" :collapsible="false">
      <template #title>
        <v-icon icon="mdi-wrench-outline" color="warning" class="mr-2" />
        Maintenance Mode
      </template>

      <v-skeleton-loader v-if="loading" type="list-item-two-line" />

      <v-list v-else lines="two">
        <v-list-item>
          <template #prepend>
            <v-icon
              icon="mdi-alert-circle-outline"
              class="mr-1"
              :color="form.maintenanceMode ? 'warning' : undefined"
            />
          </template>
          <v-list-item-title>Enable Maintenance Mode</v-list-item-title>
          <v-list-item-subtitle>
            Show a maintenance banner to all users. API remains accessible to admins.
          </v-list-item-subtitle>
          <template #append>
            <v-switch
              v-model="form.maintenanceMode"
              color="warning"
              density="compact"
              hide-details
              inset
            />
          </template>
        </v-list-item>
      </v-list>

      <v-expand-transition>
        <v-alert
          v-if="form.maintenanceMode"
          type="warning"
          variant="tonal"
          density="compact"
          class="mx-4 mb-4"
          icon="mdi-alert"
        >
          Maintenance mode is <strong>ON</strong>. Regular users will see a maintenance banner.
          Remember to save to apply the change.
        </v-alert>
      </v-expand-transition>
    </FormCard>

    <!-- ─── Rate Limits ─── -->
    <FormCard class="mb-6" :collapsible="false">
      <template #title>
        <v-icon icon="mdi-speedometer" color="secondary" class="mr-2" />
        Rate Limits
      </template>

      <v-skeleton-loader v-if="loading" type="list-item-two-line,list-item-two-line" />

      <v-row v-else>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="form.maxApplicationsPerUser"
            label="Max Applications per User"
            type="number"
            min="1"
            max="100000"
            :rules="[(v) => v >= 1 || 'Must be at least 1']"
            variant="outlined"
            density="comfortable"
            hint="Maximum number of job applications a single user can create"
            persistent-hint
            prepend-inner-icon="mdi-briefcase-outline"
          />
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="form.maxAiRequestsPerDay"
            label="Max AI Requests per Day"
            type="number"
            min="1"
            max="1000000"
            :rules="[(v) => v >= 1 || 'Must be at least 1']"
            variant="outlined"
            density="comfortable"
            hint="Maximum number of AI scoring requests across all users per calendar day"
            persistent-hint
            prepend-inner-icon="mdi-robot-outline"
          />
        </v-col>
      </v-row>
    </FormCard>

    <!-- ─── Save Button ─── -->
    <div class="d-flex justify-end">
      <v-btn
        color="primary"
        size="large"
        variant="elevated"
        prepend-icon="mdi-content-save-outline"
        :loading="saving"
        :disabled="loading"
        @click="save"
      >
        Save Settings
      </v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useToastStore } from '../../stores/toast'
import AdminSettingsService from '../../services/admin/admin-settings.service'
import type { AdminSettings } from '../../models'
import FormCard from '../../components/forms/FormCard.vue'

const toast = useToastStore()
const service = new AdminSettingsService()

const loading = ref(false)
const saving = ref(false)

const form = reactive<AdminSettings>({
  registrationEnabled: true,
  aiScoringEnabled: true,
  emailNotificationsEnabled: true,
  maintenanceMode: false,
  maxApplicationsPerUser: 100,
  maxAiRequestsPerDay: 1000,
})

async function loadSettings() {
  loading.value = true
  try {
    const res = await service.get()
    Object.assign(form, res.data)
  } catch {
    toast.error('Failed to load system settings')
  } finally {
    loading.value = false
  }
}

async function save() {
  saving.value = true
  try {
    const res = await service.update({ ...form })
    Object.assign(form, res.data)
    toast.success('Settings saved successfully')
  } catch {
    toast.error('Failed to save settings')
  } finally {
    saving.value = false
  }
}

onMounted(loadSettings)
</script>

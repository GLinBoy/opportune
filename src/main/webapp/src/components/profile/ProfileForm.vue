<template>
  <div>
    <v-form>
      <v-row>
        <!-- Avatar Section -->
        <v-col cols="12" md="4">
          <FormCard :collapsible="false">
            <template #title>
              <v-icon icon="mdi-account-circle" class="mr-2" />
              Avatar
            </template>

            <template #default>
              <div class="d-flex flex-column align-center">
                <v-avatar size="80" class="mb-4">
                  <v-img :src="avatarUrl" :alt="fullName" />
                </v-avatar>

                <div class="w-100">
                  <v-file-input
                    v-model="avatarFile"
                    label="Upload Avatar"
                    variant="outlined"
                    prepend-inner-icon="mdi-camera"
                    accept="image/*"
                    density="compact"
                    :loading="uploadingAvatar"
                    clearable
                    @update:model-value="handleAvatarUpload"
                  />

                  <div class="d-flex align-center mt-2">
                    <v-btn
                      v-if="!isUsingGravatar"
                      color="primary"
                      variant="text"
                      size="small"
                      prepend-icon="mdi-web"
                      @click="useGravatar"
                    >
                      Use Gravatar
                    </v-btn>
                    <v-spacer />
                    <p class="text-caption text-medium-emphasis ma-0">
                      {{
                        isUsingGravatar
                          ? 'Using Gravatar based on email'
                          : 'Using custom uploaded image'
                      }}
                    </p>
                  </div>

                  <!-- Account Information -->
                  <v-divider class="my-4" />
                  <h4 class="text-subtitle-2 mb-3 d-flex align-center">
                    <v-icon icon="mdi-cog" class="mr-2" size="small" />
                    Account Information
                  </h4>

                  <v-select
                    :model-value="modelValue.status"
                    :items="statusOptions"
                    label="Account Status"
                    variant="outlined"
                    prepend-inner-icon="mdi-account-check"
                    density="compact"
                    readonly
                    class="mb-3"
                  />

                  <v-text-field
                    :model-value="lastLoginFormatted"
                    label="Last Login"
                    variant="outlined"
                    prepend-inner-icon="mdi-clock"
                    density="compact"
                    readonly
                    class="mb-3"
                  />

                  <v-text-field
                    :model-value="modelValue.subscription"
                    label="Subscription Plan"
                    variant="outlined"
                    prepend-inner-icon="mdi-credit-card"
                    density="compact"
                    readonly
                    class="mb-3"
                  />

                  <v-switch
                    :model-value="modelValue.emailVerification"
                    label="Email Verified"
                    color="success"
                    readonly
                  />
                </div>
              </div>
            </template>
          </FormCard>
        </v-col>

        <!-- Personal Information -->
        <v-col cols="12" md="8">
          <FormCard :collapsible="false">
            <template #title>
              <v-icon icon="mdi-account-details" class="mr-2" />
              Your Details
            </template>

            <template #default>
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.forename"
                    label="First Name"
                    variant="outlined"
                    prepend-inner-icon="mdi-account"
                    :rules="[rules.required]"
                    @update:model-value="updateField('forename', $event)"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.surname"
                    label="Last Name"
                    variant="outlined"
                    prepend-inner-icon="mdi-account"
                    :rules="[rules.required]"
                    @update:model-value="updateField('surname', $event)"
                  />
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    :model-value="modelValue.email"
                    label="Email Address"
                    variant="outlined"
                    prepend-inner-icon="mdi-email"
                    type="email"
                    :rules="[rules.required, rules.email]"
                    @update:model-value="updateField('email', $event)"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.jobTitle"
                    label="Job Title"
                    variant="outlined"
                    prepend-inner-icon="mdi-briefcase"
                    placeholder="e.g., Senior Software Engineer"
                    @update:model-value="updateField('jobTitle', $event)"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.location"
                    label="Location"
                    variant="outlined"
                    prepend-inner-icon="mdi-map-marker"
                    placeholder="e.g., San Francisco, CA"
                    @update:model-value="updateField('location', $event)"
                  />
                </v-col>
                <v-col cols="12">
                  <v-alert
                    type="info"
                    variant="tonal"
                    icon="mdi-file-account"
                    title="Resume & Structured Data"
                    text="Upload and manage your resume, work experience, education, and skills in the dedicated Resume section."
                    density="compact"
                  >
                    <template #append>
                      <v-btn
                        variant="text"
                        color="primary"
                        size="small"
                        :to="{ name: 'profile-resume' }"
                      >
                        Go to Resume
                      </v-btn>
                    </template>
                  </v-alert>
                </v-col>
              </v-row>
            </template>
          </FormCard>
        </v-col>
      </v-row>
    </v-form>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { type IProfile, ProfileStatus } from '../../models'
import CryptoJS from 'crypto-js'
import FormCard from '../forms/FormCard.vue'
// import { AvatarService } from '../services' // Uncomment when avatar endpoint is ready

const props = defineProps<{
  modelValue: IProfile
}>()

const emit = defineEmits<{
  'update:modelValue': [value: IProfile]
  change: []
}>()

// Avatar upload state
const avatarFile = ref<File[]>([])
const uploadingAvatar = ref(false)

// Computed properties
const fullName = computed(() => {
  if (props.modelValue.forename && props.modelValue.surname) {
    return `${props.modelValue.forename} ${props.modelValue.surname}`
  }
  return 'User'
})

const gravatarUrl = computed(() => {
  if (!props.modelValue.email) return ''

  const trimmedEmail = props.modelValue.email.trim().toLowerCase()
  const hash = CryptoJS.MD5(trimmedEmail).toString()
  return `https://www.gravatar.com/avatar/${hash}?s=80&d=mp&r=g`
})

const isUsingGravatar = computed(() => {
  return !props.modelValue.avatar || props.modelValue.avatar === gravatarUrl.value
})

const avatarUrl = computed(() => {
  if (isUsingGravatar.value) {
    return gravatarUrl.value
  }
  return props.modelValue.avatar || gravatarUrl.value
})

const lastLoginFormatted = computed(() => {
  if (props.modelValue.lastLogin) {
    return new Date(props.modelValue.lastLogin).toLocaleString()
  }
  return 'Never'
})

const statusOptions = computed(() => {
  return Object.values(ProfileStatus).map((status) => ({
    title: status.replace(/_/g, ' ').replace(/\b\w/g, (l) => l.toUpperCase()),
    value: status,
  }))
})

// Validation rules
const rules = {
  required: (value: string) => !!value || 'This field is required',
  email: (value: string) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || 'Invalid email format'
  },
}

// Methods
const updateField = (field: keyof IProfile, value: string | boolean | undefined | null) => {
  const updatedProfile = {
    ...props.modelValue,
    [field]: value,
  }
  emit('update:modelValue', updatedProfile)
  emit('change')
}

const useGravatar = () => {
  updateField('avatar', gravatarUrl.value)
}

const handleAvatarUpload = async (files: File | File[]) => {
  const fileArray = Array.isArray(files) ? files : [files]

  if (!fileArray || fileArray.length === 0) return

  const file = fileArray[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    alert('Please select an image file')
    return
  }

  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    alert('File size must be less than 5MB')
    return
  }

  uploadingAvatar.value = true

  try {
    await new Promise((resolve) => setTimeout(resolve, 1000))
    const timestamp = new Date().getTime()
    const fileName = `avatar_${props.modelValue.id}_${timestamp}.${file.name.split('.').pop()}`
    const avatarPath = `/uploads/avatars/${fileName}`
    updateField('avatar', avatarPath)

    console.log('Avatar upload simulated:', {
      originalFile: file.name,
      size: file.size,
      type: file.type,
      simulatedPath: avatarPath,
    })
  } catch (error) {
    console.error('Failed to upload avatar:', error)
    alert('Failed to upload avatar. Please try again.')
  } finally {
    uploadingAvatar.value = false
    avatarFile.value = []
  }
}
</script>

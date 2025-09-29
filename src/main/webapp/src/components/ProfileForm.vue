<template>
  <div>
    <v-form>
      <v-row>
        <!-- Avatar Section -->
        <v-col cols="12" md="4">
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">
              <v-icon icon="mdi-account-circle" class="mr-2" />
              Avatar
            </v-card-title>
            <v-card-text>
              <div class="d-flex flex-column align-center">
                <v-avatar size="80" class="mb-4">
                  <v-img
                    :src="avatarUrl"
                    :alt="fullName"
                  />
                </v-avatar>

                <div class="w-100">
                  <v-file-input
                    v-model="avatarFile"
                    @update:model-value="handleAvatarUpload"
                    label="Upload Avatar"
                    variant="outlined"
                    prepend-inner-icon="mdi-camera"
                    accept="image/*"
                    density="compact"
                    :loading="uploadingAvatar"
                    clearable
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
                      {{ isUsingGravatar ? 'Using Gravatar based on email' : 'Using custom uploaded image' }}
                    </p>
                  </div>

                  <!-- Account Information -->
                  <v-divider class="my-4" />
                  <h4 class="text-subtitle-2 mb-3 d-flex align-center">
                    <v-icon icon="mdi-cog" class="mr-2" size="small" />
                    Account Information
                  </h4>

                  <!-- Account Status -->
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

                  <!-- Last Login -->
                  <v-text-field
                    :model-value="lastLoginFormatted"
                    label="Last Login"
                    variant="outlined"
                    prepend-inner-icon="mdi-clock"
                    density="compact"
                    readonly
                    class="mb-3"
                  />

                  <!-- Subscription Plan -->
                  <v-text-field
                    :model-value="modelValue.subscription"
                    label="Subscription Plan"
                    variant="outlined"
                    prepend-inner-icon="mdi-credit-card"
                    density="compact"
                    readonly
                    class="mb-3"
                  />

                  <!-- Email Verified -->
                  <v-switch
                    :model-value="modelValue.emailVerification"
                    label="Email Verified"
                    color="success"
                    readonly
                  />
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Personal Information -->
        <v-col cols="12" md="8">
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">
              <v-icon icon="mdi-account-details" class="mr-2" />
              Personal Information
            </v-card-title>
            <v-card-text>
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.forename"
                    @update:model-value="updateField('forename', $event)"
                    label="First Name"
                    variant="outlined"
                    prepend-inner-icon="mdi-account"
                    :rules="[rules.required]"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.surname"
                    @update:model-value="updateField('surname', $event)"
                    label="Last Name"
                    variant="outlined"
                    prepend-inner-icon="mdi-account"
                    :rules="[rules.required]"
                  />
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    :model-value="modelValue.email"
                    @update:model-value="updateField('email', $event)"
                    label="Email Address"
                    variant="outlined"
                    prepend-inner-icon="mdi-email"
                    type="email"
                    :rules="[rules.required, rules.email]"
                  />
                </v-col>
                <v-col cols="12">
                  <v-file-input
                    v-model="resumeFile"
                    @update:model-value="handleResumeUpload"
                    label="Upload Resume"
                    variant="outlined"
                    prepend-inner-icon="mdi-file-document"
                    accept=".pdf,.doc,.docx"
                    density="compact"
                    :loading="uploadingResume"
                    clearable
                    hint="Supported formats: PDF, DOC, DOCX (Max 10MB)"
                    persistent-hint
                  />
                  <div v-if="currentResumeFileName" class="d-flex align-center mt-2">
                    <v-chip
                      color="success"
                      variant="outlined"
                      prepend-icon="mdi-check-circle"
                      size="small"
                    >
                      Current: {{ currentResumeFileName }}
                    </v-chip>
                    <v-spacer />
                    <v-btn
                      color="primary"
                      variant="text"
                      size="small"
                      prepend-icon="mdi-download"
                      @click="downloadResume"
                    >
                      Download
                    </v-btn>
                  </div>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.jobTitle"
                    @update:model-value="updateField('jobTitle', $event)"
                    label="Job Title"
                    variant="outlined"
                    prepend-inner-icon="mdi-briefcase"
                    placeholder="e.g., Senior Software Engineer"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    :model-value="modelValue.location"
                    @update:model-value="updateField('location', $event)"
                    label="Location"
                    variant="outlined"
                    prepend-inner-icon="mdi-map-marker"
                    placeholder="e.g., San Francisco, CA"
                  />
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-form>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { type IProfile, ProfileStatus } from '../models'
import CryptoJS from 'crypto-js'
// import { AvatarService } from '../services' // Uncomment when avatar endpoint is ready
// import { ProfileResumeService } from '../services' // Uncomment when resume endpoint is ready

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

// Resume upload state
const resumeFile = ref<File[]>([])
const uploadingResume = ref(false)

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
  return Object.values(ProfileStatus).map(status => ({
    title: status.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()),
    value: status
  }))
})

const currentResumeFileName = computed(() => {
  // Extract filename from resume path or resumeId
  if (props.modelValue.resumeId) {
    // If resumeId exists, show a generic filename (real filename would come from API)
    return 'resume.pdf'
  }
  return null
})

// Validation rules
const rules = {
  required: (value: string) => !!value || 'This field is required',
  email: (value: string) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || 'Invalid email format'
  }
}

// Methods
const updateField = (field: keyof IProfile, value: string | boolean | undefined | null) => {
  const updatedProfile = {
    ...props.modelValue,
    [field]: value
  }
  emit('update:modelValue', updatedProfile)
  emit('change')
}

const useGravatar = () => {
  updateField('avatar', gravatarUrl.value)
}

const handleAvatarUpload = async (files: File | File[]) => {
  const fileArray = Array.isArray(files) ? files : [files]

  if (!fileArray || fileArray.length === 0) {
    return
  }

  const file = fileArray[0]

  // Validate file type
  if (!file.type.startsWith('image/')) {
    alert('Please select an image file')
    return
  }

  // Validate file size (max 5MB)
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    alert('File size must be less than 5MB')
    return
  }

  uploadingAvatar.value = true

  try {
    // When avatar upload endpoint is ready, replace the simulation below with:
    // const avatarService = new AvatarService()
    // const result = await avatarService.uploadAvatar(props.modelValue.id || '', file)
    // updateField('avatar', result.avatarPath)

    // SIMULATION CODE (remove when real endpoint is ready)
    await new Promise(resolve => setTimeout(resolve, 1000))
    const timestamp = new Date().getTime()
    const fileName = `avatar_${props.modelValue.id}_${timestamp}.${file.name.split('.').pop()}`
    const avatarPath = `/uploads/avatars/${fileName}`
    updateField('avatar', avatarPath)

    console.log('Avatar upload simulated:', {
      originalFile: file.name,
      size: file.size,
      type: file.type,
      simulatedPath: avatarPath
    })

  } catch (error) {
    console.error('Failed to upload avatar:', error)
    alert('Failed to upload avatar. Please try again.')
  } finally {
    uploadingAvatar.value = false
    avatarFile.value = []
  }
}

const handleResumeUpload = async (files: File | File[]) => {
  const fileArray = Array.isArray(files) ? files : [files]

  if (!fileArray || fileArray.length === 0) {
    return
  }

  const file = fileArray[0]

  // Validate file type
  const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
  if (!allowedTypes.includes(file.type)) {
    alert('Please select a valid document file (PDF, DOC, DOCX)')
    return
  }

  // Validate file size (max 10MB)
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    alert('File size must be less than 10MB')
    return
  }

  uploadingResume.value = true

  try {
    // When resume upload endpoint is ready, replace the simulation below with:
    // const resumeService = new ProfileResumeService()
    // const result = await resumeService.upload(props.modelValue.id || '', file)
    // updateField('resumeId', result.id)

    // SIMULATION CODE (remove when real endpoint is ready)
    await new Promise(resolve => setTimeout(resolve, 1500))
    const timestamp = new Date().getTime()
    const resumeId = `resume_${props.modelValue.id}_${timestamp}`
    updateField('resumeId', resumeId)

    console.log('Resume upload simulated:', {
      originalFile: file.name,
      size: file.size,
      type: file.type,
      simulatedResumeId: resumeId
    })

    alert('Resume uploaded successfully!')

  } catch (error) {
    console.error('Failed to upload resume:', error)
    alert('Failed to upload resume. Please try again.')
  } finally {
    uploadingResume.value = false
    resumeFile.value = []
  }
}

const downloadResume = () => {
  if (props.modelValue.resumeId) {
    // When download endpoint is ready, replace with actual API call:
    // window.open(`/api/profiles/${props.modelValue.id}/resumes/${props.modelValue.resumeId}/download`, '_blank')

    // SIMULATION CODE (remove when real endpoint is ready)
    alert(`Download resume: ${props.modelValue.resumeId}`)
    console.log('Resume download simulated for resumeId:', props.modelValue.resumeId)
  }
}
</script>

<style scoped>
.v-card {
  transition: all 0.3s ease;
}

.v-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>

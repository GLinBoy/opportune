<template>
  <v-form>
    <v-row>
      <!-- Row 1: Avatar + Name/Email -->
      <v-col cols="12" md="4" class="d-flex flex-column align-center">
        <div style="position: relative; display: inline-block;">
          <v-avatar
            size="120"
            class="cursor-pointer"
            @click="triggerFileInput"
          >
            <v-img :src="avatarUrl" :alt="fullName" />
            <v-overlay
              v-if="uploadingAvatar"
              absolute
              class="align-center justify-center"
              contained
            >
              <v-progress-circular indeterminate size="28" color="primary" />
            </v-overlay>
          </v-avatar>

          <v-tooltip v-if="avatarSource" :text="avatarSource.tooltip" location="bottom">
            <template #activator="{ props }">
              <v-icon
                v-bind="props"
                :icon="avatarSource.icon"
                size="x-small"
                color="primary"
                class="position-absolute"
                style="bottom: -2px; right: -2px; background: rgb(var(--v-theme-surface)); border-radius: 50%; padding: 2px;"
              />
            </template>
          </v-tooltip>

          <v-tooltip v-if="isUploadedAvatar" text="Remove avatar" location="top">
            <template #activator="{ props }">
              <v-icon
                v-bind="props"
                icon="mdi-close-circle"
                size="small"
                color="error"
                class="position-absolute"
                style="top: -6px; right: -6px; cursor: pointer; background: rgb(var(--v-theme-surface)); border-radius: 50%;"
                @click.stop="handleRemoveAvatar"
              />
            </template>
          </v-tooltip>
        </div>

        <p class="text-caption text-medium-emphasis mt-2">Click avatar to upload</p>

        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          hidden
          @change="handleFileSelected"
        />
      </v-col>

      <v-col cols="12" md="8">
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
              readonly
              :rules="[rules.required, rules.email]"
            >
              <template #append-inner>
                <v-tooltip
                  :text="modelValue.emailVerification ? 'Email verified' : 'Email not verified'"
                  location="top"
                >
                  <template #activator="{ props }">
                    <v-icon
                      v-bind="props"
                      :icon="modelValue.emailVerification ? 'mdi-check-circle' : 'mdi-alert-circle-outline'"
                      :color="modelValue.emailVerification ? 'success' : 'warning'"
                    />
                  </template>
                </v-tooltip>
              </template>
            </v-text-field>
          </v-col>
        </v-row>
      </v-col>
    </v-row>

    <!-- Row 2: Job Title + Location -->
    <v-row>
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
    </v-row>

    <!-- Row 3: Phone + LinkedIn + Portfolio -->
    <v-row>
      <v-col cols="12" md="4">
        <v-text-field
          :model-value="modelValue.phone"
          label="Phone"
          variant="outlined"
          prepend-inner-icon="mdi-phone"
          placeholder="+1 (555) 123-4567"
          @update:model-value="updateField('phone', $event)"
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-text-field
          :model-value="modelValue.linkedinUrl"
          label="LinkedIn URL"
          variant="outlined"
          prepend-inner-icon="mdi-linkedin"
          placeholder="https://linkedin.com/in/..."
          @update:model-value="updateField('linkedinUrl', $event)"
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-text-field
          :model-value="modelValue.portfolioUrl"
          label="Portfolio URL"
          variant="outlined"
          prepend-inner-icon="mdi-web"
          placeholder="https://..."
          @update:model-value="updateField('portfolioUrl', $event)"
        />
      </v-col>
    </v-row>

    <!-- Row 4: Professional Summary -->
    <v-row>
      <v-col cols="12">
        <v-textarea
          :model-value="modelValue.professionalSummary"
          label="Professional Summary"
          variant="outlined"
          placeholder="Write a brief summary of your professional background, key achievements, and career goals..."
          rows="5"
          auto-grow
          hint="This summary will be used by AI to tailor your cover letters and interview introductions."
          persistent-hint
          @update:model-value="updateField('professionalSummary', $event)"
        />
      </v-col>
    </v-row>
  </v-form>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted } from 'vue'
import { type IProfile } from '../../models'
import AvatarService from '../../services/avatar.service'
import { useToastStore } from '../../stores/toast'
import apiClient from '../../services/api'

const props = defineProps<{
  modelValue: IProfile
}>()

const emit = defineEmits<{
  'update:modelValue': [value: IProfile]
  change: []
}>()

const avatarService = new AvatarService()
const toast = useToastStore()

const fileInput = ref<HTMLInputElement | null>(null)
const uploadingAvatar = ref(false)
const avatarBlobUrl = ref('')

const fetchAvatarBlob = async () => {
  if (avatarBlobUrl.value) {
    URL.revokeObjectURL(avatarBlobUrl.value)
    avatarBlobUrl.value = ''
  }
  if (props.modelValue.avatar && !props.modelValue.avatar.startsWith('http')) {
    try {
      const response = await apiClient.get('/api/profiles/avatar', {
        responseType: 'blob',
      })
      avatarBlobUrl.value = URL.createObjectURL(response.data)
    } catch {
      console.error('Failed to load avatar')
    }
  }
}

watch(() => props.modelValue.avatar, fetchAvatarBlob)
onMounted(fetchAvatarBlob)
onUnmounted(() => {
  if (avatarBlobUrl.value) {
    URL.revokeObjectURL(avatarBlobUrl.value)
  }
})

const fullName = computed(() => {
  if (props.modelValue.forename && props.modelValue.surname) {
    return `${props.modelValue.forename} ${props.modelValue.surname}`
  }
  return 'User'
})

const gravatarUrl = computed(() => {
  if (!props.modelValue.email) return ''
  return avatarService.getGravatarUrl(props.modelValue.email, 120)
})

const avatarUrl = computed(() => {
  if (props.modelValue.avatar) {
    if (props.modelValue.avatar.startsWith('http')) {
      return props.modelValue.avatar
    }
    return avatarBlobUrl.value || gravatarUrl.value
  }
  return gravatarUrl.value
})

const isUploadedAvatar = computed(() => {
  return !!props.modelValue.avatar && !props.modelValue.avatar.startsWith('http')
})

const avatarSource = computed(() => {
  if (!props.modelValue.avatar) return null
  if (props.modelValue.avatar.startsWith('http')) {
    return { icon: 'mdi-web', tooltip: 'Using Gravatar' }
  }
  return { icon: 'mdi-upload', tooltip: 'Uploaded avatar' }
})

const rules = {
  required: (value: string) => !!value || 'This field is required',
  email: (value: string) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || 'Invalid email format'
  },
}

const updateField = (field: keyof IProfile, value: string | boolean | undefined | null) => {
  const updatedProfile = {
    ...props.modelValue,
    [field]: value,
  }
  emit('update:modelValue', updatedProfile)
  emit('change')
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelected = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    toast.error('Please select an image file')
    input.value = ''
    return
  }

  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    toast.error('File size must be less than 5MB')
    input.value = ''
    return
  }

  uploadingAvatar.value = true
  try {
    const profileId = props.modelValue.id
    if (!profileId) {
      toast.error('Profile ID not available')
      return
    }
    const response = await avatarService.uploadAvatar(profileId, file)
    updateField('avatar', response.avatarPath)
    toast.success('Avatar uploaded successfully')
  } catch (error) {
    console.error('Failed to upload avatar:', error)
    toast.error('Failed to upload avatar. Please try again.')
  } finally {
    uploadingAvatar.value = false
    input.value = ''
  }
}

const handleRemoveAvatar = async () => {
  if (!props.modelValue.avatar) return

  uploadingAvatar.value = true
  try {
    const profileId = props.modelValue.id
    if (!profileId) {
      toast.error('Profile ID not available')
      return
    }
    await avatarService.deleteAvatar()
    updateField('avatar', null)
    toast.success('Avatar removed successfully')
  } catch (error) {
    console.error('Failed to remove avatar:', error)
    toast.error('Failed to remove avatar. Please try again.')
  } finally {
    uploadingAvatar.value = false
  }
}
</script>

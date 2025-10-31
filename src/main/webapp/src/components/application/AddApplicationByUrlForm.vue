<template>
  <v-card flat>
    <v-card-text>
      <v-form ref="formRef" v-model="isValid" @submit.prevent="handleSubmit">
        <v-text-field
          v-model="formData.url"
          label="Job Posting URL"
          placeholder="https://company.com/careers/job-posting"
          variant="outlined"
          prepend-inner-icon="mdi-link"
          :rules="[rules.required, rules.url]"
          hint="Paste the URL of the job posting to automatically fetch job details"
          persistent-hint
          class="mb-4"
        />

        <v-alert
          type="info"
          variant="tonal"
          class="mb-4"
        >
          <div class="text-body-2">
            <v-icon icon="mdi-information" size="small" class="mr-2" />
            The system will automatically fetch and parse the job details from the provided URL.
          </div>
        </v-alert>
      </v-form>
    </v-card-text>

    <v-card-actions>
      <v-spacer />
      <v-btn
        variant="text"
        @click="handleCancel"
      >
        Cancel
      </v-btn>
      <v-btn
        color="primary"
        :loading="loading"
        :disabled="!isValid"
        @click="handleSubmit"
      >
        Fetch & Create Application
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'

// Props
export interface UrlFormProps {
  loading?: boolean
}

withDefaults(defineProps<UrlFormProps>(), {
  loading: false
})

// Emits
const emit = defineEmits<{
  submit: [data: { url: string }]
  cancel: []
}>()

// Refs
const formRef = ref()
const isValid = ref(false)

// Form data
const formData = reactive({
  url: ''
})

// Validation rules
const rules = {
  required: (value: string) => !!value || 'This field is required',
  url: (value: string) => {
    if (!value) return true
    const pattern = /^https?:\/\/.+/
    return pattern.test(value) || 'Please enter a valid URL starting with http:// or https://'
  }
}

// Methods
const handleSubmit = async () => {
  const { valid } = await formRef.value.validate()
  if (valid) {
    emit('submit', { url: formData.url })
  }
}

const handleCancel = () => {
  emit('cancel')
}

// Expose reset method
const reset = () => {
  formData.url = ''
  formRef.value?.reset()
}

defineExpose({
  reset
})
</script>

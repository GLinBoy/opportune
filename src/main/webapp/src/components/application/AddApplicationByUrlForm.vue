<template>
  <FormCard>
    <template #title>
      <v-icon icon="mdi-link-plus" class="mr-2" />
      Add Application by URL
    </template>

    <v-form ref="formRef" v-model="isValid" @submit.prevent="handleSubmit">
      <v-row>
        <v-col cols="12">
          <FieldLabel label="Job Posting URL" input-id="url-field" />
          <v-text-field
            id="url-field"
            v-model="formData.url"
            placeholder="https://company.com/careers/job-posting"
            variant="outlined"
            density="compact"
            bg-color="surface"
            rounded="md"
            prepend-inner-icon="mdi-link"
            :rules="[rules.required, rules.url]"
            hint="Paste the URL of the job posting to automatically fetch job details"
            persistent-hint
          />
        </v-col>
      </v-row>

      <v-alert type="info" variant="tonal" class="mt-5">
        <p class="text-body-2">
          The system will try to automatically fetch and parse the job details from the provided
          URL.
        </p>
      </v-alert>
    </v-form>

    <template #actions>
      <v-btn variant="outlined" color="secondary" rounded="md" @click="handleCancel">
        Cancel
      </v-btn>
      <v-btn
        color="primary"
        variant="flat"
        rounded="md"
        min-width="120"
        :loading="loading"
        :disabled="!isValid"
        @click="handleSubmit"
      >
        Fetch &amp; Create Application
      </v-btn>
    </template>
  </FormCard>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import FormCard from '../forms/FormCard.vue'
import FieldLabel from '../forms/FieldLabel.vue'

// Props
export interface UrlFormProps {
  loading?: boolean
}

withDefaults(defineProps<UrlFormProps>(), {
  loading: false,
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
  url: '',
})

// Validation rules
const rules = {
  required: (value: string) => !!value || 'This field is required',
  url: (value: string) => {
    if (!value) return true
    const pattern = /^https?:\/\/.+/
    return pattern.test(value) || 'Please enter a valid URL starting with http:// or https://'
  },
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
  reset,
})
</script>

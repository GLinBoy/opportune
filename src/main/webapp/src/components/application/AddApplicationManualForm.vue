<template>
  <v-card flat>
    <v-card-text>
      <v-form ref="formRef" v-model="isValid" @submit.prevent="handleSubmit">
        <v-alert
          v-if="props.initialUrl"
          type="warning"
          variant="tonal"
          class="mb-4"
          closable
        >
          <div class="text-body-2">
            <v-icon icon="mdi-alert-circle" size="small" class="mr-2" />
            Automatic fetch failed for the provided URL. Please fill in the job details manually.
          </div>
        </v-alert>

        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="formData.url"
              label="Job Posting URL"
              placeholder="https://company.com/careers/job-posting"
              variant="outlined"
              prepend-inner-icon="mdi-link"
              :rules="[rules.url]"
              hint="Optional: Link to the original job posting"
              persistent-hint
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.title"
              label="Job Title"
              placeholder="e.g., Senior Software Engineer"
              variant="outlined"
              prepend-inner-icon="mdi-briefcase"
              :rules="[rules.required]"
              required
            />
          </v-col>

          <v-col cols="12" md="6">
            <CompanyAutocomplete
              v-model="selectedCompany"
              label="Company"
              prepend-inner-icon="mdi-office-building"
              clearable
            >
              <template #append>
                <v-tooltip text="You can assign a company later" location="top">
                  <template v-slot:activator="{ props }">
                    <v-icon v-bind="props" icon="mdi-information-outline" size="small" />
                  </template>
                </v-tooltip>
              </template>
            </CompanyAutocomplete>
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.location"
              label="Location"
              placeholder="e.g., San Francisco, CA or Remote"
              variant="outlined"
              prepend-inner-icon="mdi-map-marker"
            />
          </v-col>

          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.salary"
              label="Salary Range"
              placeholder="e.g., $100k - $150k"
              variant="outlined"
              prepend-inner-icon="mdi-currency-usd"
            />
          </v-col>

          <v-col cols="12">
            <v-textarea
              v-model="formData.rawContent"
              label="Job Description"
              placeholder="Paste the complete job description here..."
              variant="outlined"
              prepend-inner-icon="mdi-text-box-outline"
              :rules="[rules.required]"
              rows="10"
              required
              hint="This is the most important field - paste the full job description"
              persistent-hint
            />
          </v-col>
        </v-row>

        <v-alert
          type="warning"
          variant="tonal"
          class="mt-4"
        >
          <div class="text-body-2">
            <v-icon icon="mdi-alert" size="small" class="mr-2" />
            Make sure to include the complete job description in the "Job Description" field for better tracking and analysis.
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
        Create Application
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts" setup>
import { ref, reactive, watch } from 'vue'
import type { ICompany } from '../../models'
import CompanyAutocomplete from '../company/CompanyAutocomplete.vue'

// Props
export interface ManualFormProps {
  loading?: boolean
  initialUrl?: string
}

const props = withDefaults(defineProps<ManualFormProps>(), {
  loading: false,
  initialUrl: ''
})

// Emits
export interface ManualFormData {
  url: string
  title: string
  location: string
  salary: string
  rawContent: string
  companyId?: string
}

const emit = defineEmits<{
  submit: [data: ManualFormData]
  cancel: []
}>()

// Refs
const formRef = ref()
const isValid = ref(false)
const selectedCompany = ref<ICompany | null>(null)

// Form data
const formData = reactive<ManualFormData>({
  url: props.initialUrl || '',
  title: '',
  location: '',
  salary: '',
  rawContent: '',
  companyId: ''
})

// Watch for company selection changes
watch(selectedCompany, (newCompany) => {
  formData.companyId = newCompany?.id || ''
})

// Watch for initialUrl changes and update formData
watch(() => props.initialUrl, (newUrl) => {
  if (newUrl) {
    formData.url = newUrl
  }
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
    emit('submit', { ...formData })
  }
}

const handleCancel = () => {
  emit('cancel')
}

// Expose reset method
const reset = () => {
  formData.url = ''
  formData.title = ''
  formData.location = ''
  formData.salary = ''
  formData.rawContent = ''
  formData.companyId = ''
  selectedCompany.value = null
  formRef.value?.reset()
}

defineExpose({
  reset
})
</script>

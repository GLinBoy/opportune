<template>
  <v-dialog v-model="isOpen" :max-width="maxWidth" :persistent="loading">
    <v-card class="form-dialog" :rounded="'lg'">
      <!-- Tabler-style colored top status bar -->
      <div class="form-dialog__status" />

      <!-- Header -->
      <v-card-title class="form-dialog__header d-flex align-center pt-5 px-6 pb-3">
        <v-icon v-if="icon" :icon="icon" color="primary" size="36" class="mr-3 flex-shrink-0" />
        <div>
          <div class="text-h6 font-weight-bold">{{ title }}</div>
          <div v-if="subtitle" class="text-body-2 text-medium-emphasis font-weight-regular mt-1">
            {{ subtitle }}
          </div>
        </div>
      </v-card-title>

      <v-divider />

      <!-- Form content slot -->
      <v-card-text class="px-6 pt-5 pb-3">
        <slot />
      </v-card-text>

      <v-divider />

      <!-- Footer actions -->
      <v-card-actions class="px-6 py-4">
        <v-spacer />
        <v-btn
          :text="cancelText"
          variant="text"
          color="secondary"
          :disabled="loading"
          min-width="120"
          @click="onCancel"
        />
        <v-btn
          :text="confirmText"
          color="primary"
          variant="flat"
          rounded="md"
          :loading="loading"
          :disabled="!valid"
          min-width="120"
          @click="onConfirm"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'

export interface FormDialogProps {
  modelValue: boolean
  title: string
  subtitle?: string
  icon?: string
  confirmText?: string
  cancelText?: string
  loading?: boolean
  valid?: boolean | null
  maxWidth?: string | number
}

const props = withDefaults(defineProps<FormDialogProps>(), {
  confirmText: 'Save',
  cancelText: 'Cancel',
  loading: false,
  valid: true,
  maxWidth: 600,
  icon: undefined,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
  cancel: []
}>()

const isOpen = ref(props.modelValue)

watch(
  () => props.modelValue,
  (newValue) => {
    isOpen.value = newValue
  }
)

watch(isOpen, (newValue) => {
  emit('update:modelValue', newValue)
})

const onConfirm = () => {
  emit('confirm')
}

const onCancel = () => {
  emit('cancel')
  isOpen.value = false
}
</script>

<style scoped>
.form-dialog__status {
  height: 4px;
  border-radius: 8px 8px 0 0;
  background-color: rgb(var(--v-theme-primary));
}
</style>

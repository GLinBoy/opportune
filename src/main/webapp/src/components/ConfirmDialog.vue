<template>
  <v-dialog v-model="isOpen" max-width="420" :persistent="loading">
    <v-card class="confirm-dialog" :rounded="'lg'">
      <!-- Tabler-style colored top status bar -->
      <div
        class="confirm-dialog__status"
        :style="{ backgroundColor: `rgb(var(--v-theme-${variantColor}))` }"
      />

      <v-card-text class="text-center pt-8 pb-4 px-6">
        <v-icon :icon="variantIcon" :color="variantColor" size="52" class="mb-4" />
        <div class="text-h6 font-weight-bold mb-2">{{ title }}</div>
        <div class="text-body-2 text-medium-emphasis">
          <slot>{{ message }}</slot>
        </div>
      </v-card-text>

      <v-card-actions class="px-6 pb-6 ga-3">
        <v-btn
          :text="cancelText"
          variant="outlined"
          rounded="md"
          :disabled="loading"
          class="flex-grow-1"
          @click="onCancel"
        />
        <v-btn
          :text="confirmText"
          :color="variantColor"
          variant="flat"
          rounded="md"
          :loading="loading"
          class="flex-grow-1"
          @click="onConfirm"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'

type DialogVariant = 'success' | 'error' | 'warning' | 'info'

export interface ConfirmDialogProps {
  modelValue: boolean
  title: string
  message?: string
  variant?: DialogVariant
  confirmText?: string
  cancelText?: string
  loading?: boolean
}

const props = withDefaults(defineProps<ConfirmDialogProps>(), {
  variant: 'warning',
  confirmText: 'Confirm',
  cancelText: 'Cancel',
  loading: false,
  message: '',
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

const variantColor = computed<string>(() => {
  const map: Record<DialogVariant, string> = {
    success: 'success',
    error: 'error',
    warning: 'warning',
    info: 'info',
  }
  return map[props.variant]
})

const variantIcon = computed<string>(() => {
  const map: Record<DialogVariant, string> = {
    success: 'mdi-check-circle-outline',
    error: 'mdi-alert-circle-outline',
    warning: 'mdi-alert-outline',
    info: 'mdi-information-outline',
  }
  return map[props.variant]
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
.confirm-dialog__status {
  height: 4px;
  border-radius: 8px 8px 0 0;
}
</style>

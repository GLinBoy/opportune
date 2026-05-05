<template>
  <label :for="inputId" class="field-label">
    {{ label }}
    <span v-if="optional" class="text-medium-emphasis text-caption ml-1"> (optional) </span>
  </label>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'FieldLabel',
  props: {
    label: {
      type: String,
      required: true,
    },
    inputId: {
      type: String,
      default: undefined,
    },
    optional: {
      type: Boolean,
      default: false,
    },
  },
})
</script>

<style scoped>
.field-label {
  display: block;
  font-size: 0.8125rem;
  font-weight: 500;
  margin-bottom: 4px;
  color: rgb(var(--v-theme-on-surface));
}
</style>

<template>
  <FormCard>
    <template #title>
      <v-icon icon="mdi-domain" class="mr-2" />
      Company Details
    </template>

    <v-row>
      <v-col cols="12" md="6">
        <FieldLabel label="Company Name" input-id="company-name" />
        <v-text-field
          id="company-name"
          :model-value="modelValue.name"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-domain"
          :rules="[(v) => !!v || 'Company name is required']"
          required
          @update:model-value="updateField('name', $event)"
        />
      </v-col>
      <v-col cols="12" md="6">
        <FieldLabel label="Industry" input-id="company-industry" :optional="true" />
        <v-text-field
          id="company-industry"
          :model-value="modelValue.industry"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-factory"
          @update:model-value="updateField('industry', $event)"
        />
      </v-col>
      <v-col cols="12" md="6">
        <FieldLabel label="Website" input-id="company-website" :optional="true" />
        <v-text-field
          id="company-website"
          :model-value="modelValue.website"
          type="url"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-web"
          @update:model-value="updateField('website', $event)"
        />
      </v-col>
      <v-col cols="12" md="6">
        <FieldLabel label="Company Size" input-id="company-size" :optional="true" />
        <v-text-field
          id="company-size"
          :model-value="modelValue.companySize"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-account-group"
          placeholder="e.g., 50-200 employees"
          @update:model-value="updateField('companySize', $event)"
        />
      </v-col>
      <v-col cols="12" md="6">
        <FieldLabel label="Location" input-id="company-location" :optional="true" />
        <v-text-field
          id="company-location"
          :model-value="modelValue.location"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-map-marker"
          @update:model-value="updateField('location', $event)"
        />
      </v-col>
      <v-col cols="12" md="6">
        <FieldLabel label="Founded Year" input-id="company-founded-year" :optional="true" />
        <v-text-field
          id="company-founded-year"
          :model-value="modelValue.foundedYear"
          type="number"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-calendar"
          @update:model-value="updateField('foundedYear', $event)"
        />
      </v-col>
      <v-col cols="12" md="6">
        <FieldLabel label="Status" input-id="company-status" />
        <v-select
          id="company-status"
          :model-value="modelValue.status || 'INTERESTED'"
          :items="statusOptions"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          @update:model-value="updateField('status', $event)"
        >
          <template #selection="{ item }">
            <v-chip :color="item.color" :prepend-icon="item.icon" size="small" variant="tonal">
              {{ item.title }}
            </v-chip>
          </template>
          <template #item="{ item, props }">
            <v-list-item v-bind="props" title="">
              <v-chip :color="item.color" :prepend-icon="item.icon" size="small" variant="tonal">
                {{ item.title }}
              </v-chip>
            </v-list-item>
          </template>
        </v-select>
      </v-col>
      <v-col cols="12">
        <FieldLabel label="Description" input-id="company-description" :optional="true" />
        <v-textarea
          id="company-description"
          :model-value="modelValue.description"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-text"
          rows="4"
          @update:model-value="updateField('description', $event)"
        />
      </v-col>
      <v-col cols="12">
        <FieldLabel label="Note" input-id="company-note" :optional="true" />
        <v-textarea
          id="company-note"
          :model-value="modelValue.note"
          variant="outlined"
          density="compact"
          bg-color="surface"
          rounded="md"
          hide-details="auto"
          prepend-inner-icon="mdi-note-text"
          rows="3"
          placeholder="Personal note about the company"
          @update:model-value="updateField('note', $event)"
        />
      </v-col>
    </v-row>

    <template v-if="$slots.actions" #actions>
      <slot name="actions" />
    </template>
  </FormCard>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import {
  type ICompany,
  CompanyStatus,
  getCompanyStatusDisplay,
  getCompanyStatusColor,
  getCompanyStatusIcon,
} from '../../models'
import FormCard from '../forms/FormCard.vue'
import FieldLabel from '../forms/FieldLabel.vue'

export default defineComponent({
  name: 'CompanyForm',
  components: {
    FormCard,
    FieldLabel,
  },
  props: {
    modelValue: {
      type: Object as () => ICompany,
      required: true,
    },
  },
  emits: ['update:modelValue', 'change'],
  computed: {
    statusOptions() {
      return Object.values(CompanyStatus).map((status) => ({
        title: getCompanyStatusDisplay(status),
        value: status,
        color: getCompanyStatusColor(status),
        icon: getCompanyStatusIcon(status),
      }))
    },
  },
  methods: {
    updateField(field: keyof ICompany, value: string | undefined) {
      const updatedCompany = {
        ...this.modelValue,
        [field]: value,
      }
      this.$emit('update:modelValue', updatedCompany)
      this.$emit('change')
    },
  },
})
</script>

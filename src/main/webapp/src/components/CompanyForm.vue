<template>
  <v-card>
    <v-card-title class="d-flex align-center">
      <v-icon icon="mdi-domain" class="mr-2" />
      Company Details
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field
            :model-value="modelValue.name"
            label="Company Name"
            variant="outlined"
            prepend-inner-icon="mdi-domain"
            @update:model-value="updateField('name', $event)"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            :model-value="modelValue.industry"
            label="Industry"
            variant="outlined"
            prepend-inner-icon="mdi-factory"
            @update:model-value="updateField('industry', $event)"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            :model-value="modelValue.website"
            label="Website"
            type="url"
            variant="outlined"
            prepend-inner-icon="mdi-web"
            @update:model-value="updateField('website', $event)"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            :model-value="modelValue.companySize"
            label="Company Size"
            variant="outlined"
            prepend-inner-icon="mdi-account-group"
            placeholder="e.g., 50-200 employees"
            @update:model-value="updateField('companySize', $event)"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            :model-value="modelValue.location"
            label="Location"
            variant="outlined"
            prepend-inner-icon="mdi-map-marker"
            @update:model-value="updateField('location', $event)"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            :model-value="modelValue.foundedYear"
            label="Founded Year"
            type="number"
            variant="outlined"
            prepend-inner-icon="mdi-calendar"
            @update:model-value="updateField('foundedYear', $event)"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-select
            :model-value="modelValue.status || 'INTERESTED'"
            label="Status"
            :items="statusOptions"
            variant="outlined"
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
          <v-textarea
            :model-value="modelValue.description"
            label="Description"
            variant="outlined"
            prepend-inner-icon="mdi-text"
            rows="4"
            @update:model-value="updateField('description', $event)"
          />
        </v-col>
        <v-col cols="12">
          <v-textarea
            :model-value="modelValue.note"
            label="Note"
            variant="outlined"
            prepend-inner-icon="mdi-note-text"
            rows="3"
            placeholder="Personal note about the company"
            @update:model-value="updateField('note', $event)"
          />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import {
  type ICompany,
  CompanyStatus,
  getCompanyStatusDisplay,
  getCompanyStatusColor,
  getCompanyStatusIcon,
} from '../models'

export default defineComponent({
  name: 'CompanyForm',
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

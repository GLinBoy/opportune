<template>
  <v-autocomplete
    :model-value="modelValue"
    :search="search"
    :items="companies"
    item-title="name"
    item-value="id"
    :label="label"
    :variant="variant"
    :loading="isCompanyLoading"
    :prepend-inner-icon="prependInnerIcon"
    :rules="rules"
    :clearable="clearable"
    :disabled="disabled"
    return-object
    @update:model-value="handleCompanySelect"
    @update:search="handleCompanySearch"
  >
    <template v-if="$slots.prepend" #prepend>
      <slot name="prepend" />
    </template>
    <template v-if="$slots.append" #append>
      <slot name="append" />
    </template>
  </v-autocomplete>
</template>

<script lang="ts" src="./CompanyAutocomplete.ts" />

<template>
  <div>
    <!-- Page Header -->
    <div class="d-flex justify-space-between align-center mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold">Companies</h1>
        <p class="text-subtitle-1 text-medium-emphasis">Manage companies and your interest level</p>
      </div>
      <div class="d-flex align-center" style="gap: 16px">
        <v-text-field
          label="Search companies..."
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          density="compact"
          style="min-width: 300px"
          clearable
          @input="handleSearch"
        />
        <v-tooltip text="Add new company">
          <template #activator="{ props }">
            <v-btn v-bind="props" color="primary" prepend-icon="mdi-plus">
              Add Company
            </v-btn>
          </template>
        </v-tooltip>
      </div>
    </div>

    <!-- Data Table -->
    <v-card>
      <v-data-table
        v-model:items-per-page="itemsPerPage"
        v-model:page="page"
        :headers="headers"
        :items="companies"
        :items-length="totalItems"
        :loading="isFetching"
        item-value="id"
        class="elevation-1"
        @click:row="handleRowClick"
      >
      </v-data-table>
    </v-card>

    <!-- Loading overlay -->
    <v-overlay v-model="isFetching" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>
  </div>
</template>
<script lang="ts" src="./CompanyListView.ts" />

<style lang="css" scoped src="./CompanyListView.css" />

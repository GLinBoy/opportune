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
          v-model="searchQuery"
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
            <v-btn v-bind="props" color="primary" prepend-icon="mdi-plus" @click="addNewCompany">
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
        v-model:page="currentPage"
        :headers="headers"
        :items="filteredCompanies"
        :loading="loading"
        item-value="id"
        class="elevation-1"
        @click:row="handleRowClick"
      >
        <!-- Company Column with Logo -->
        <template #[`item.company`]="{ item }">
          <div class="d-flex align-center py-2">
            <v-avatar size="40" class="mr-3">
              <v-img :src="item.logo" :alt="`${item.company} logo`" />
            </v-avatar>
            <div>
              <div class="font-weight-medium">{{ item.company }}</div>
              <div class="text-caption text-medium-emphasis">{{ item.website }}</div>
            </div>
          </div>
        </template>

        <!-- Industry Column -->
        <template #[`item.industry`]="{ item }">
          <v-chip size="small" variant="tonal" color="primary">
            {{ item.industry }}
          </v-chip>
        </template>

        <!-- Status Column with Editable Select -->
        <template #[`item.status`]="{ item }">
          <v-select
            v-model="item.status"
            :items="statusOptions"
            variant="outlined"
            density="compact"
            hide-details
            @update:model-value="updateCompanyStatus(item.id, $event)"
            @click.stop
          >
            <template #selection="{ item: statusItem }">
              <v-chip :color="getStatusColor(statusItem.value)" size="small" variant="flat">
                {{ statusItem.title }}
              </v-chip>
            </template>
            <template #item="{ props, item: statusItem }">
              <v-list-item v-bind="props">
                <template #prepend>
                  <v-chip :color="getStatusColor(statusItem.value)" size="x-small" variant="flat" />
                </template>
              </v-list-item>
            </template>
          </v-select>
        </template>

        <!-- Footer with pagination info -->
        <template #bottom>
          <div class="text-center pt-2">
            <v-pagination v-model="currentPage" :length="pageCount" :total-visible="7" />
            <div class="text-caption text-medium-emphasis mt-2">
              Showing {{ startItem }} to {{ endItem }} of {{ totalItems }} companies
            </div>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <!-- Snackbar for status updates -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn color="white" variant="text" @click="snackbar.show = false"> Close </v-btn>
      </template>
    </v-snackbar>

    <!-- Add Company Dialog -->
    <v-dialog v-model="addDialog.show" max-width="600">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-plus" class="mr-2" />
          Add New Company
        </v-card-title>
        <v-card-text>
          <v-form ref="addForm" v-model="addDialog.valid">
            <v-text-field
              v-model="addDialog.companyName"
              label="Company Name"
              placeholder="Enter company name"
              variant="outlined"
              prepend-inner-icon="mdi-domain"
              :rules="[rules.required]"
              class="mb-3"
            />
            <v-text-field
              v-model="addDialog.website"
              label="Company Website"
              placeholder="https://company.com"
              variant="outlined"
              prepend-inner-icon="mdi-web"
              :rules="[rules.url]"
              class="mb-3"
            />
            <v-select
              v-model="addDialog.industry"
              label="Industry"
              :items="industryOptions"
              variant="outlined"
              prepend-inner-icon="mdi-factory"
              :rules="[rules.required]"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="closeAddDialog"> Cancel </v-btn>
          <v-btn
            color="primary"
            :loading="addDialog.loading"
            :disabled="!addDialog.valid"
            @click="addCompany"
          >
            Add Company
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>
<script lang="ts" src="./CompanyListView.ts" />

<style lang="scss" scoped src="./CompanyListView.css" />

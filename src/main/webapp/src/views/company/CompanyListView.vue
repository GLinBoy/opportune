<template>
  <div>
    <!-- Page Header -->
    <div
      class="d-flex flex-column flex-sm-row justify-space-between align-sm-center mb-4 mb-md-6 ga-3"
    >
      <div class="d-flex align-center ga-3">
        <v-icon icon="mdi-domain" size="48" />
        <div class="d-flex flex-column">
          <span class="text-headline-small font-weight-bold">Companies</span>
          <span class="text-label-medium text-medium-emphasis"
            >Manage companies and your interest level</span
          >
        </div>
      </div>
      <div class="d-flex align-center ga-3">
        <v-autocomplete
          v-model="selectedCompany"
          v-model:search="searchQuery"
          :items="searchResults"
          :loading="isSearching"
          item-title="name"
          item-value="id"
          label="Search companies..."
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          density="compact"
          class="flex-grow-1"
          style="min-width: 300px"
          hide-details
          clearable
          no-filter
          return-object
          @update:search="handleSearchInput"
          @update:model-value="handleCompanySelect"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item v-bind="props" :title="item.name" :subtitle="item.status">
              <template v-slot:prepend>
                <v-icon
                  :color="getCompanyStatusColor(item.status)"
                  :icon="getCompanyStatusIcon(item.status)"
                />
              </template>
            </v-list-item>
          </template>
          <template v-slot:no-data>
            <v-list-item>
              <v-list-item-title>
                {{ searchQuery ? 'No companies found' : 'Start typing to search...' }}
              </v-list-item-title>
            </v-list-item>
          </template>
        </v-autocomplete>
        <v-tooltip text="Add new company" location="bottom">
          <template #activator="{ props }">
            <v-btn
              v-bind="props"
              color="primary"
              icon="mdi-briefcase-plus"
              size="small"
              @click="addNewCompany"
            />
          </template>
        </v-tooltip>
      </div>
    </div>

    <!-- Data Table -->
    <v-card>
      <div style="overflow-x: auto">
        <v-data-table-server
          striped="odd"
          :show-current-page="true"
          :items-per-page="itemsPerPage"
          :page="page"
          :headers="headers"
          :items="companies"
          :items-length="totalItems"
          :loading="isFetching"
          :items-per-page-options="itemsPerPageOptions"
          @update:options="handleUpdateOptions"
          class="elevation-1 primary-header"
          @click:row="handleRowClick"
        >
          <template v-slot:[`item.name`]="{ item }">
            <v-container fluid class="pa-0">
              <v-row no-gutters align="center">
                <v-col cols="auto">
                  <CompanyLogo
                    :alt="item.name"
                    :logo="item.logo"
                    :website="item.website"
                    size="40"
                  />
                </v-col>
                <v-col class="ps-2">
                  <span class="font-weight-bold">{{ item.name }}</span>
                </v-col>
                <v-col cols="auto" v-if="item.note">
                  <v-tooltip :text="item.note" location="top">
                    <template v-slot:activator="{ props }">
                      <v-icon
                        class="ms-1"
                        color="primary"
                        size="small"
                        icon="mdi-information-outline"
                        v-bind="props"
                      />
                    </template>
                  </v-tooltip>
                </v-col>
              </v-row>
            </v-container>
          </template>

          <template v-slot:[`item.lastModifiedDate`]="{ item }">
            <v-container fluid class="pa-0">
              <v-row no-gutters align="center">
                <v-col>
                  {{ item.lastModifiedDate ? formatDate(item.lastModifiedDate) : '-' }}
                </v-col>
                <v-col
                  cols="auto"
                  v-if="
                    item.createdDate &&
                    item.lastModifiedDate &&
                    formatDate(item.createdDate) !== formatDate(item.lastModifiedDate)
                  "
                >
                  <v-tooltip :text="`Created: ${formatDate(item.createdDate)}`" location="top">
                    <template v-slot:activator="{ props }">
                      <v-icon
                        class="ms-1"
                        color="info"
                        size="small"
                        icon="mdi-update"
                        v-bind="props"
                      />
                    </template>
                  </v-tooltip>
                </v-col>
              </v-row>
            </v-container>
          </template>

          <template v-slot:[`item.status`]="{ value }">
            <v-chip
              :border="`${getCompanyStatusColor(value)} thin opacity-25`"
              :color="getCompanyStatusColor(value)"
              :text="getCompanyStatusDisplay(value)"
              size="x-small"
              :prepend-icon="getCompanyStatusIcon(value)"
            />
          </template>

          <template v-slot:[`item.actions`]="{ item }">
            <div class="d-flex justify-end" style="gap: 4px">
              <v-tooltip text="Delete" location="top">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    size="x-small"
                    variant="text"
                    color="error"
                    icon="mdi-delete-outline"
                    @click.stop="confirmDelete(item)"
                  />
                </template>
              </v-tooltip>
            </div>
          </template>
        </v-data-table-server>
      </div>
    </v-card>

    <!-- Loading overlay -->
    <v-overlay v-model="isFetching" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <v-dialog v-model="addNewCompanyDialog" transition="dialog-bottom-transition" fullscreen>
      <v-card>
        <v-toolbar color="secondary" dark>
          <v-btn icon="mdi-close" @click="closeDialog"></v-btn>
          <v-toolbar-title>Add New Company</v-toolbar-title>
        </v-toolbar>

        <v-container class="d-flex justify-center pa-8">
          <div style="max-width: 800px; width: 100%">
            <CompanyForm v-model="newCompany" class="mb-6" />

            <!-- Action buttons at the bottom -->
            <v-card>
              <v-card-actions class="justify-end pa-4">
                <v-btn
                  text="Cancel"
                  variant="outlined"
                  prepend-icon="mdi-close"
                  @click="closeDialog"
                  :disabled="isCreating"
                />
                <v-btn
                  text="Create"
                  color="primary"
                  variant="flat"
                  prepend-icon="mdi-content-save"
                  :loading="isCreating"
                  @click="createCompany"
                />
              </v-card-actions>
            </v-card>
          </div>
        </v-container>
      </v-card>
    </v-dialog>

    <!-- Confirm Delete Dialog -->
    <v-dialog v-model="confirmDeleteDialog" max-width="420">
      <v-card>
        <v-card-title class="text-h6">Confirm Deletion</v-card-title>
        <v-card-text>
          Are you sure you want to delete
          <strong>{{ companyToDelete?.name }}</strong
          >? This action cannot be undone.
        </v-card-text>
        <v-card-actions class="justify-end">
          <v-btn
            text="Cancel"
            variant="outlined"
            prepend-icon="mdi-close"
            :disabled="isDeleting"
            @click="closeDeleteDialog"
          />
          <v-btn
            text="Delete"
            color="error"
            variant="flat"
            prepend-icon="mdi-delete"
            :loading="isDeleting"
            @click="performDelete"
          />
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Success/Error Snackbar -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.message }}
    </v-snackbar>
  </div>
</template>
<script lang="ts" src="./CompanyListView.ts" />

<style lang="css" scoped src="./CompanyListView.css" />

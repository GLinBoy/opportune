<template>
  <div>
    <!-- Page Header -->
    <div
      class="d-flex flex-column flex-sm-row justify-space-between align-sm-center mb-4 mb-md-6 ga-3"
    >
      <div class="d-flex align-center ga-3">
        <v-icon icon="mdi-briefcase" size="48" />
        <div class="d-flex flex-column">
          <span class="text-headline-small font-weight-bold">Applications</span>
          <span class="text-label-medium text-medium-emphasis">Manage your job applications</span>
        </div>
      </div>
      <div class="d-flex align-center ga-3">
        <v-autocomplete
          v-model="selectedApplication"
          v-model:search="searchQuery"
          :items="searchResults"
          :loading="isSearching"
          item-title="name"
          item-value="id"
          label="Search applications..."
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
          @update:model-value="handleApplicationSelect"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item v-bind="props" :title="item.name" :subtitle="item.status">
              <template v-slot:prepend>
                <v-icon
                  :color="getSearchResultStatusColor(item.status)"
                  :icon="getSearchResultStatusIcon(item.status)"
                />
              </template>
            </v-list-item>
          </template>
          <template v-slot:no-data>
            <v-list-item>
              <v-list-item-title>
                {{ searchQuery ? 'No applications found' : 'Start typing to search...' }}
              </v-list-item-title>
            </v-list-item>
          </template>
        </v-autocomplete>
        <v-tooltip text="Add new application" location="top">
          <template #activator="{ props }">
            <v-btn
              v-bind="props"
              color="primary"
              icon="mdi-briefcase-plus"
              size="small"
              @click="addNewApplication"
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
          :items="applications"
          :items-length="totalItems"
          :loading="isFetching"
          :items-per-page-options="itemsPerPageOptions"
          @update:options="handleUpdateOptions"
          class="elevation-1 primary-header"
          @click:row="handleRowClick"
        >
          <!-- Title Column -->
          <template v-slot:[`item.title`]="{ item }">
            <v-container fluid class="pa-0">
              <v-row density="compact" align="center">
                <v-col>
                  <span class="font-weight-bold">{{ item.title }}</span>
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

          <!-- Date Applied Column -->
          <template #[`item.appliedAt`]="{ item }">
            {{ item.appliedAt ? formatDate(item.appliedAt) : '-' }}
          </template>

          <!-- Last Modified Date Column -->
          <template #[`item.lastModifiedDate`]="{ item }">
            <template v-if="item.createdDate && item.lastModifiedDate">
              <v-container fluid class="pa-0">
                <v-row density="compact" align="center">
                  <v-col>
                    {{ formatDate(item.lastModifiedDate) || '-' }}
                  </v-col>
                  <v-col
                    cols="auto"
                    v-if="formatDate(item.createdDate) !== formatDate(item.lastModifiedDate)"
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
            <template v-else>
              <v-container fluid class="pa-0">
                <v-row density="compact" align="center">
                  <v-col>
                    {{ item.lastModifiedDate ? formatDate(item.lastModifiedDate) : '-' }}
                  </v-col>
                </v-row>
              </v-container>
            </template>
          </template>

          <!-- Status Column -->
          <template v-slot:[`item.status`]="{ value }">
            <v-chip
              :border="`${getApplicationStatusColor(value)} thin opacity-25`"
              :color="getApplicationStatusColor(value)"
              :text="getApplicationStatusDisplay(value)"
              size="x-small"
              :prepend-icon="getApplicationStatusIcon(value)"
            />
          </template>

          <!-- Actions Column -->
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

    <!-- Snackbar for status updates -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn color="white" variant="text" @click="snackbar.show = false"> Close </v-btn>
      </template>
    </v-snackbar>

    <!-- Confirm Delete Dialog -->
    <ConfirmDialog
      v-model="confirmDeleteDialog"
      title="Confirm Deletion"
      variant="error"
      confirm-text="Delete"
      cancel-text="Cancel"
      :loading="isDeleting"
      @confirm="performDelete"
      @cancel="closeDeleteDialog"
    >
      Are you sure you want to delete
      <strong>{{ applicationToDelete?.title }}</strong
      >? This action cannot be undone.
    </ConfirmDialog>

    <!-- Add Application Dialog - Full Screen -->
    <v-dialog v-model="addDialog.show" fullscreen transition="dialog-bottom-transition">
      <v-card class="d-flex flex-column">
        <v-toolbar color="primary" dark>
          <v-btn icon @click="closeAddDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>Add New Application</v-toolbar-title>
          <v-spacer />
        </v-toolbar>

        <v-tabs v-model="addDialog.activeTab" bg-color="primary" class="elevation-2 flex-grow-0">
          <v-tab value="url">
            <v-icon start>mdi-link</v-icon>
            From URL
          </v-tab>
          <v-tab value="manual">
            <v-icon start>mdi-form-textbox</v-icon>
            Manual Entry
          </v-tab>
        </v-tabs>

        <v-window v-model="addDialog.activeTab" class="flex-grow-1 overflow-y-auto">
          <v-window-item value="url" class="h-100">
            <v-container class="py-8">
              <v-row justify="center">
                <v-col cols="12" md="8" lg="6">
                  <AddApplicationByUrlForm
                    ref="urlFormRef"
                    :loading="addDialog.loading"
                    @submit="handleUrlSubmit"
                    @cancel="closeAddDialog"
                  />
                </v-col>
              </v-row>
            </v-container>
          </v-window-item>

          <v-window-item value="manual" class="h-100">
            <v-container class="py-8">
              <v-row justify="center">
                <v-col cols="12" md="10" lg="8">
                  <AddApplicationManualForm
                    ref="manualFormRef"
                    :loading="addDialog.loading"
                    :initial-url="failedUrl"
                    @submit="handleManualSubmit"
                    @cancel="closeAddDialog"
                  />
                </v-col>
              </v-row>
            </v-container>
          </v-window-item>
        </v-window>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts" src="./ApplicationListView.ts" />

<style lang="css" scoped src="./ApplicationListView.css" />

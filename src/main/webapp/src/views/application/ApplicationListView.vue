<template>
  <div>
    <!-- Page Header -->
    <div class="d-flex justify-space-between align-center mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold">Applications</h1>
        <p class="text-subtitle-1 text-medium-emphasis">Manage your job applications</p>
      </div>
      <div class="d-flex align-center" style="gap: 16px">
        <v-text-field
          v-model="searchQuery"
          label="Search applications..."
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          density="compact"
          style="min-width: 300px"
          clearable
          @input="handleSearch"
        />
        <v-tooltip text="Add new application">
          <template #activator="{ props }">
            <v-btn
              v-bind="props"
              color="primary"
              prepend-icon="mdi-plus"
              @click="addNewApplication"
            >
              Add
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
        :items="filteredApplications"
        :loading="loading"
        item-value="id"
        class="elevation-1"
        @click:row="handleRowClick"
      >
        <!-- Status Column with Editable Select -->
        <template #[`item.status`]="{ item }">
          <v-select
            v-model="item.status"
            :items="statusOptions"
            variant="outlined"
            density="compact"
            hide-details
            @update:model-value="updateApplicationStatus(item.id, $event)"
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

        <!-- Date Applied Column -->
        <template #[`item.dateApplied`]="{ item }">
          {{ formatDate(item.dateApplied) }}
        </template>

        <!-- Footer with pagination info -->
        <template #bottom>
          <div class="text-center pt-2">
            <v-pagination v-model="currentPage" :length="pageCount" :total-visible="7" />
            <div class="text-caption text-medium-emphasis mt-2">
              Showing {{ startItem }} to {{ endItem }} of {{ totalItems }} applications
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

    <!-- Add Application Dialog -->
    <v-dialog v-model="addDialog.show" max-width="600">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-plus" class="mr-2" />
          Add New Application
        </v-card-title>
        <v-card-text>
          <v-form ref="addForm" v-model="addDialog.valid">
            <v-text-field
              v-model="addDialog.jobUrl"
              label="Job Posting URL"
              placeholder="https://company.com/careers/job-posting"
              variant="outlined"
              prepend-inner-icon="mdi-link"
              :rules="[rules.required, rules.url]"
              hint="Paste the URL of the job posting to automatically fetch job details"
              persistent-hint
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
            @click="fetchJobFromUrl"
          >
            Fetch Job Details
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts" src="./ApplicationListView.ts" />

<style lang="scss" scoped src="./ApplicationListView.css" />

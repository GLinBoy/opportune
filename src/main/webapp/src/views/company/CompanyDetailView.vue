<template>
  <div>
    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <div v-if="!loading && company">
      <!-- Page Header -->
      <v-row class="mb-6" align="center">
        <v-col cols="auto">
          <CompanyLogo
            :alt="company.name"
            :logo="company.logo"
            :website="company.website"
            size="64"
          />
        </v-col>
        <v-col>
          <span class="text-headline-small font-weight-bold">{{ company.name }}</span>
          <div class="d-flex align-center text-label-medium text-medium-emphasis">
            <span>{{ company.industry }}</span>
            <span v-if="company.website" class="mx-2">•</span>
            <v-btn
              v-if="company.website"
              :href="company.website"
              target="_blank"
              variant="text"
              prepend-icon="mdi-open-in-new"
              size="small"
              color="primary"
              class="pa-0"
              style="text-transform: none; justify-content: flex-start"
            >
              Visit Website
            </v-btn>
          </div>
        </v-col>
        <v-col cols="auto" class="d-flex align-center ga-3">
          <v-btn color="error" variant="flat" icon="mdi-delete" @click="confirmDelete" />
          <v-btn
            color="success"
            variant="flat"
            icon="mdi-content-save"
            :loading="saving"
            @click="saveCompany"
          />
        </v-col>
      </v-row>

      <!-- 1. Company Details -->
      <CompanyForm v-model="company" @change="markAsModified" class="mb-6" />

      <!-- 2. Meta Data -->
      <v-card class="mb-6">
        <v-card-title class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <v-icon icon="mdi-tag-multiple" class="mr-2" />
            Meta Data
          </div>
          <v-tooltip text="Add new meta data" location="bottom">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="primary"
                icon="mdi-tag-plus"
                size="small"
                @click="showAddMetaDataDialog"
              />
            </template>
          </v-tooltip>
        </v-card-title>
        <v-card-text>
          <div v-if="companyMetadata && companyMetadata.length > 0" style="overflow-x: auto">
            <v-data-table
              :headers="metaDataHeaders"
              :items="companyMetadata"
              hide-default-footer
              density="compact"
            >
              <template v-slot:[`item.metaName`]="{ item }">
                <span class="font-weight-medium">{{ item.metaName }}</span>
              </template>
              <template v-slot:[`item.value`]="{ item }">
                <div class="text-wrap" style="max-width: 400px; white-space: pre-wrap">
                  {{ item.metaValue }}
                </div>
              </template>
              <template v-slot:[`item.actions`]="{ item }">
                <v-btn
                  color="error"
                  variant="text"
                  icon="mdi-delete"
                  size="small"
                  @click="removeMetaData(item.id)"
                />
              </template>
            </v-data-table>
          </div>
          <div v-else class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-tag-hidden" size="48" class="mb-2" />
            <p>No meta data available. Click "Add Meta Data" to add key-value pairs.</p>
          </div>
        </v-card-text>
      </v-card>

      <!-- 3. Associated Applications -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-briefcase" class="mr-2" />
          Associated Applications
        </v-card-title>
        <v-card-text>
          <ApplicationTable
            v-if="associatedApplications && associatedApplications.length > 0"
            :items="associatedApplications"
            :view-all-to="`/applications?companyId=${company?.id}`"
            @delete="confirmDeleteApplication"
          />
          <div v-else class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-briefcase-outline" size="48" class="mb-2" />
            <p>No applications found for this company.</p>
          </div>
        </v-card-text>
      </v-card>
    </div>

    <!-- Add Meta Data Dialog -->
    <FormDialog
      v-model="showMetaDataDialog"
      icon="mdi-tag-plus"
      :title="metaDataDialogTitle"
      subtitle="Add a key-value pair to store additional information about this company."
      confirm-text="Save"
      cancel-text="Cancel"
      :loading="savingMetaData"
      :valid="metaDataFormValid"
      @confirm="saveMetaData"
      @cancel="cancelAddMetaData"
    >
      <v-form ref="metaDataForm" v-model="metaDataFormValid">
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="newMetaData.metaName"
              label="Key"
              variant="outlined"
              prepend-inner-icon="mdi-key"
              :rules="[rules.required]"
              placeholder="e.g., Contact Person, Revenue, Culture"
            />
          </v-col>
          <v-col cols="12">
            <v-textarea
              v-model="newMetaData.metaValue"
              label="Value"
              variant="outlined"
              prepend-inner-icon="mdi-text"
              rows="4"
              placeholder="Enter the value (supports multi-line text)"
            />
          </v-col>
        </v-row>
      </v-form>
    </FormDialog>

    <!-- Confirm Delete Application Dialog -->
    <ConfirmDialog
      v-model="confirmDeleteApplicationDialog"
      title="Confirm Deletion"
      variant="error"
      confirm-text="Delete"
      cancel-text="Cancel"
      @confirm="performApplicationDelete"
      @cancel="closeDeleteApplicationDialog"
    >
      Are you sure you want to delete
      <strong>{{ applicationToDelete?.title }}</strong
      >? This action cannot be undone.
    </ConfirmDialog>

    <!-- Confirm Delete Company Dialog -->
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
      <strong>{{ company?.name }}</strong
      >? This action cannot be undone.
    </ConfirmDialog>

    <!-- Confirm Delete Meta Data Dialog -->
    <ConfirmDialog
      v-model="confirmDeleteMetaDataDialog"
      title="Confirm Deletion"
      variant="error"
      confirm-text="Delete"
      cancel-text="Cancel"
      @confirm="performMetaDataDelete"
      @cancel="closeDeleteMetaDataDialog"
    >
      Are you sure you want to delete
      <strong>"{{ metaDataToDelete?.metaName }}"</strong>?
    </ConfirmDialog>

    <!-- Success/Error Snackbar -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.message }}
    </v-snackbar>
  </div>
</template>
<script lang="ts" src="./CompanyDetailView.ts" />

<style lang="css" scoped src="./CompanyDetailView.css" />

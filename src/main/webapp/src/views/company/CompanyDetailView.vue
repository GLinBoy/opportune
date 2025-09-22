<template>
  <div>
    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <div v-if="!loading && company">
      <!-- Breadcrumbs -->
      <v-breadcrumbs :items="breadcrumbs" class="pa-0 mb-4">
        <template #prepend>
          <v-icon icon="mdi-home" />
        </template>
      </v-breadcrumbs>

      <!-- Page Header -->
      <v-row class="mb-6" align="center">
        <v-col cols="auto">
          <v-avatar size="64" class="pa-1" rounded="0">
            <v-img :alt="company.name" :src="company.logo ? company.logo : defaultCompanyLogo" />
          </v-avatar>
        </v-col>
        <v-col>
          <h1 class="text-h4 font-weight-bold">{{ company.name }}</h1>
          <div class="d-flex align-center text-subtitle-1 text-medium-emphasis">
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
        <v-col cols="auto" class="d-flex align-center" style="gap: 12px">
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
      <CompanyForm
        v-model="company"
        @change="markAsModified"
        class="mb-6"
      />

      <!-- 2. Meta Data -->
      <v-card class="mb-6">
        <v-card-title class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <v-icon icon="mdi-database" class="mr-2" />
            Meta Data
          </div>
          <v-btn
            color="primary"
            variant="outlined"
            prepend-icon="mdi-plus"
            size="small"
            @click="showAddMetaDataDialog"
          >
            Add Meta Data
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-data-table
            v-if="companyMetadata && companyMetadata.length > 0"
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
            <template v-slot:[`item.actions`]="{ index }">
              <v-btn
                color="error"
                variant="text"
                icon="mdi-delete"
                size="small"
                @click="removeMetaData(index)"
              />
            </template>
          </v-data-table>
          <div v-else class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-information" size="48" class="mb-2" />
            <p>No meta data available. Click "Add Meta Data" to add key-value pairs.</p>
          </div>
        </v-card-text>
      </v-card>

      <!-- 3. Associated Applications -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-briefcase-variant" class="mr-2" />
          Associated Applications
        </v-card-title>
        <v-card-text>
          <v-data-table
            v-if="associatedApplications && associatedApplications.length > 0"
            :headers="applicationHeaders"
            :items="associatedApplications"
            hide-default-footer
            density="compact"
          >
          </v-data-table>
          <div v-else class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-briefcase-outline" size="48" class="mb-2" />
            <p>No applications found for this company.</p>
          </div>
        </v-card-text>
      </v-card>
    </div>

    <!-- Add Meta Data Dialog -->
    <v-dialog v-model="showMetaDataDialog" max-width="600px">
      <v-card>
        <v-card-title>Add Meta Data</v-card-title>
        <v-card-text>
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
                  :rules="[rules.required]"
                  rows="4"
                  placeholder="Enter the value (supports multi-line text)"
                />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="grey" variant="text" @click="cancelAddMetaData"> Cancel </v-btn>
          <v-btn color="primary" variant="flat" :loading="savingMetaData" @click="saveMetaData">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Success/Error Snackbar -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.message }}
    </v-snackbar>
  </div>
</template>
<script lang="ts" src="./CompanyDetailView.ts" />

<style lang="css" scoped src="./CompanyDetailView.css" />

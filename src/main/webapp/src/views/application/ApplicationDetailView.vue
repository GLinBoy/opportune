<template>
  <div>
    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <div v-if="!loading && application">
      <!-- Breadcrumbs -->
      <v-breadcrumbs :items="breadcrumbs" class="pa-0 mb-4">
        <template #prepend>
          <v-icon icon="mdi-home" />
        </template>
      </v-breadcrumbs>

      <!-- Page Header -->
      <div class="d-flex justify-space-between align-center mb-6">
        <div>
          <h1 class="text-h4 font-weight-bold">{{ application.title }}</h1>
          <div class="d-flex align-center text-subtitle-1 text-medium-emphasis">
            <span>{{ application?.company?.name }}</span>
            <span v-if="application.url" class="mx-2">•</span>
            <v-btn
              v-if="application.url"
              :href="application.url"
              target="_blank"
              variant="text"
              prepend-icon="mdi-open-in-new"
              size="small"
              color="primary"
              class="pa-0"
              style="text-transform: none; justify-content: flex-start"
            >
              Go to the Job Description
            </v-btn>
          </div>
          <div class="d-flex align-center text-subtitle-2 text-medium-emphasis">
            <span class="mr-2">Applied on: </span>
            <span class="font-weight-bold" v-if="application.appliedAt">{{formatDate(application.appliedAt) }}</span>
            <span class="font-weight-bold" v-else>( You haven't applied yet. )</span>
          </div>
        </div>
        <div class="d-flex align-center" style="gap: 12px">
          <v-tooltip text="Applied for this job" v-if="!application.appliedAt">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="secondary"
                icon="mdi-send-check"
                @click="appliedJob"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Upload resume for this application">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="info"
                variant="outlined"
                icon="mdi-file-upload"
                @click="uploadResume"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Show raw content from job posting">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="secondary"
                variant="outlined"
                icon="mdi-code-tags"
                @click="showRawContent"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Save changes">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="success"
                variant="flat"
                icon="mdi-content-save"
                :loading="saving"
                @click="saveApplication"
              />
            </template>
          </v-tooltip>
        </div>
      </div>

      <!-- 1. Application Details -->
      <v-card class="mb-6">
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-briefcase" class="mr-2" />
          Application Details
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.title"
                label="Title"
                variant="outlined"
                prepend-inner-icon="mdi-account-tie"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                label="Company"
                variant="outlined"
                prepend-inner-icon="mdi-domain"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.location"
                label="Location"
                variant="outlined"
                prepend-inner-icon="mdi-map-marker"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="application.status"
                label="Status"
                :items="statusOptions"
                variant="outlined"
                prepend-inner-icon="mdi-flag"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="application.note"
                label="Note"
                variant="outlined"
                rows="3"
                prepend-inner-icon="mdi-note-text"
                @input="markAsModified"
              />
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- 2. AI Generated Content -->
      <v-card class="mb-6">
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-robot" class="mr-2" />
          AI Generated Content
        </v-card-title>
        <v-tabs v-model="activeTab" color="primary" grow>
          <v-tab value="job-description">Job Description</v-tab>
          <v-tab value="cover-letter">Cover Letter</v-tab>
          <v-tab value="resume-enhancer">Resume Enhancer</v-tab>
          <v-tab value="interview-details">Interview Details</v-tab>
        </v-tabs>

        <v-tabs-window v-model="activeTab">
          <v-tabs-window-item value="job-description">
            <v-card-text>
              <v-textarea
                v-model="application.description"
                label="AI Job Description Analysis"
                variant="outlined"
                rows="6"
                readonly
              />
            </v-card-text>
          </v-tabs-window-item>

          <v-tabs-window-item value="cover-letter">
            <v-card-text>
              <v-textarea
                v-model="application.coverLetter"
                label="AI Generated Cover Letter"
                variant="outlined"
                rows="8"
                @input="markAsModified"
              />
            </v-card-text>
          </v-tabs-window-item>

          <v-tabs-window-item value="resume-enhancer">
            <v-card-text>
              <v-textarea
                v-model="application.resumeInsights"
                label="Resume Enhancement Suggestions"
                variant="outlined"
                rows="6"
                readonly
              />
            </v-card-text>
          </v-tabs-window-item>

          <v-tabs-window-item value="interview-details">
            <v-card-text>
              <v-textarea
                label="Interview Preparation Details"
                variant="outlined"
                rows="6"
                readonly
              />
            </v-card-text>
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card>

      <!-- 3. Meta Data -->
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
            v-if="application.metadata && application.metadata.length > 0"
            :headers="metaDataHeaders"
            :items="application.metadata"
            hide-default-footer
            density="compact"
          >
            <template v-slot:[`item.key`]="{ item }">
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

      <!-- 4. Application Timeline -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-timeline" class="mr-2" />
          Application Timeline
        </v-card-title>
        <v-card-text>
          <v-timeline side="end" density="compact">
            <v-timeline-item
              v-for="event in application.timeline"
              :key="event.id"
              :dot-color="getStatusColor(event.status)"
              size="small"
            >
              <template #opposite>
                <div class="text-caption text-medium-emphasis text-right">
                  <div class="font-weight-medium">{{ formatDate(event.occurredAt) }}</div>
                  <div v-if="event.occurredAt" class="mt-1">{{ event.occurredAt }}</div>
                </div>
              </template>
              <div>
                <div class="text-subtitle-2">{{ event.title }}</div>
                <div class="text-body-2 text-medium-emphasis">{{ event.description }}</div>
              </div>
            </v-timeline-item>
          </v-timeline>
        </v-card-text>
      </v-card>
    </div>

    <!-- Error state -->
    <div v-else-if="!loading && !application" class="text-center py-8">
      <v-icon icon="mdi-alert-circle" size="64" color="error" class="mb-4" />
      <h2 class="text-h5 mb-2">Application Not Found</h2>
      <p class="text-body-1 text-medium-emphasis mb-4">
        The application you're looking for doesn't exist or has been removed.
      </p>
      <v-btn color="primary" to="/applications"> Back to Applications </v-btn>
    </div>

    <!-- Snackbar for notifications -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
      {{ snackbar.message }}
      <template #actions>
        <v-btn color="white" variant="text" @click="snackbar.show = false"> Close </v-btn>
      </template>
    </v-snackbar>

    <!-- Add Meta Data Dialog -->
    <v-dialog v-model="metaDataDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-database-plus" class="mr-2" />
          Add Meta Data
        </v-card-title>
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
                  placeholder="e.g., Source, Recruiter, Notes"
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
  </div>
</template>

<script lang="ts" src="./ApplicationDetailView.ts" />

<style lang="css" scoped src="./ApplicationDetailView.css" />

<template>
  <div>
    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <div v-if="!loading && application">
      <!-- Page Header -->
      <div
        class="d-flex flex-column flex-md-row justify-space-between align-md-center mb-4 mb-md-6 ga-3"
      >
        <div class="app-header-info">
          <span class="text-headline-small font-weight-bold">{{ application.title }}</span>
          <div class="d-flex align-center text-label-medium text-medium-emphasis">
            <RouterLink
              v-if="application?.company?.name"
              :to="{ name: 'companies-detail', params: { id: application.company.id } }"
              class="text-primary text-decoration-none"
            >
              {{ application.company.name }}
            </RouterLink>
            <span v-else class="text-italic">( No company )</span>
            <span v-if="application.url" class="mx-2 my-1">•</span>
            <a
              v-if="application.url"
              :href="application.url"
              target="_blank"
              rel="noopener noreferrer"
              class="d-inline-flex align-center text-primary text-decoration-none text-caption pa-0"
              style="text-transform: none; justify-content: flex-start"
            >
              <v-icon size="small" start>mdi-open-in-new</v-icon>
              Go to the Job Description
            </a>
          </div>
          <div class="d-flex align-center text-label-medium text-medium-emphasis">
            <span class="mr-2">Applied on: </span>
            <span class="font-weight-bold" v-if="application.appliedAt">{{
              formatDate(application.appliedAt)
            }}</span>
            <span class="font-weight-bold" v-else>( You haven't applied yet. )</span>
          </div>
        </div>
        <div class="d-flex align-center ga-3 flex-wrap">
          <v-tooltip text="Applied for this job" location="top" v-if="!application.appliedAt">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="secondary"
                icon="mdi-send-check"
                density="compact"
                @click="appliedJob"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Upload resume for this application" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="info"
                variant="outlined"
                density="compact"
                icon="mdi-file-upload"
                @click="uploadResume"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Show original Job Description content" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="secondary"
                variant="outlined"
                density="compact"
                icon="mdi-code-tags"
                @click="showRawContent"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete application" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="error"
                variant="flat"
                density="compact"
                icon="mdi-delete"
                @click="confirmDelete"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Save changes" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                color="success"
                variant="flat"
                density="compact"
                icon="mdi-content-save"
                :loading="saving"
                @click="saveApplication"
              />
            </template>
          </v-tooltip>
        </div>
      </div>

      <!-- 1. Application Details -->
      <FormCard class="mb-6" :collapsible="true" :default-open="true">
        <template #title>
          <v-icon icon="mdi-briefcase" class="mr-2" />
          Application Details
        </template>

        <template #default>
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
              <CompanyAutocomplete
                v-model="application.company"
                :initial-company="application.company"
                @update:model-value="markAsModified"
              >
                <template v-if="application.company" #append>
                  <v-tooltip text="Remove company" location="top">
                    <template #activator="{ props }">
                      <v-btn
                        v-bind="props"
                        icon="mdi-domain-remove"
                        color="error"
                        variant="text"
                        size="small"
                        @click.stop="confirmDeleteCompany"
                      />
                    </template>
                  </v-tooltip>
                </template>
              </CompanyAutocomplete>
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
              <v-text-field
                v-model="application.salary"
                label="Salary"
                variant="outlined"
                prepend-inner-icon="mdi-currency-usd"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="application.status"
                label="Status"
                :items="statusOptions"
                variant="outlined"
                @update:model-value="markAsModified"
              >
                <template #selection="{ item }">
                  <v-chip
                    :color="item.color"
                    :prepend-icon="item.icon"
                    size="small"
                    variant="tonal"
                  >
                    {{ item.title }}
                  </v-chip>
                </template>
                <template #item="{ item, props }">
                  <v-list-item v-bind="props" title="">
                    <v-chip
                      :color="item.color"
                      :prepend-icon="item.icon"
                      size="small"
                      variant="tonal"
                    >
                      {{ item.title }}
                    </v-chip>
                  </v-list-item>
                </template>
              </v-select>
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
        </template>
      </FormCard>

      <!-- 2. AI Generated Content -->
      <FormCard class="mb-6" :collapsible="true" :default-open="true">
        <template #title>
          <v-icon icon="mdi-robot" class="mr-2" />
          AI Generated Content
        </template>

        <template #default>
          <v-tabs v-model="activeTab" color="primary" grow>
            <v-tab value="job-description">Job Description (summarized)</v-tab>
            <v-tab value="cover-letter">Cover Letter</v-tab>
            <v-tab value="resume-insights">Resume Insights</v-tab>
            <v-tab value="resume-scores">Resume Scores</v-tab>
            <v-tab value="interview-introduction">Interview Introduction</v-tab>
          </v-tabs>

          <v-tabs-window v-model="activeTab">
            <v-tabs-window-item value="job-description">
              <v-textarea
                v-model="application.description"
                label="AI Job Description Analysis"
                variant="outlined"
                rows="16"
                readonly
                class="mt-4"
              />
            </v-tabs-window-item>

            <v-tabs-window-item value="cover-letter">
              <v-textarea
                v-model="application.coverLetter"
                label="AI Generated Cover Letter"
                variant="outlined"
                rows="16"
                readonly
                class="mt-4"
                @input="markAsModified"
              />
            </v-tabs-window-item>

            <v-tabs-window-item value="resume-insights">
              <v-textarea
                v-model="application.resumeInsights"
                label="Resume Insights"
                variant="outlined"
                rows="16"
                readonly
                class="mt-4"
              />
            </v-tabs-window-item>

            <v-tabs-window-item value="resume-scores">
              <ResumeScoreCard
                :key="application.id"
                :resume-overall-score="application.resumeOverallScore"
                :skill-match-score="application.skillMatchScore"
                :skill-match-rationale="application.skillMatchRationale"
                :experience-match-score="application.experienceMatchScore"
                :experience-match-rationale="application.experienceMatchRationale"
                :education-match-score="application.educationMatchScore"
                :education-match-rationale="application.educationMatchRationale"
                :keyword-match-score="application.keywordMatchScore"
                :keyword-match-rationale="application.keywordMatchRationale"
                class="mt-4"
              />
            </v-tabs-window-item>

            <v-tabs-window-item value="interview-introduction">
              <v-textarea
                v-model="application.interviewIntroduction"
                label="Interview Introduction"
                variant="outlined"
                rows="16"
                readonly
                class="mt-4"
              />
            </v-tabs-window-item>
          </v-tabs-window>
        </template>
      </FormCard>

      <!-- 3. Meta Data -->
      <FormCard class="mb-6" :collapsible="true" :default-open="true">
        <template #title>
          <v-icon icon="mdi-tag-multiple" class="mr-2" />
          Meta Data
        </template>

        <template #default>
          <MetadataTable
            v-if="applicationMetadata && applicationMetadata.length > 0"
            :items="applicationMetadata"
            @edit="showEditMetaDataDialog"
            @delete="removeMetaData"
          />
          <div v-else class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-information" size="48" class="mb-2" />
            <p>No meta data available. Click "Add Meta Data" to add key-value pairs.</p>
          </div>
        </template>

        <template #actions>
          <v-tooltip text="Add new meta data" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                text="Add Meta Data"
                variant="text"
                color="primary"
                prepend-icon="mdi-tag-plus"
                :disabled="loading"
                min-width="120"
                @click="showAddMetaDataDialog"
              />
            </template>
          </v-tooltip>
        </template>
      </FormCard>

      <!-- 4. Interview Notes-->
      <FormCard class="mb-6" :collapsible="true" :default-open="true">
        <template #title>
          <v-icon icon="mdi-notebook-outline" class="mr-2" />
          Interview Notes
        </template>

        <template #default>
          <div class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-notebook-edit-outline" size="48" class="mb-2" />
            <p>No interview notes yet. This section will be available soon.</p>
          </div>
        </template>
      </FormCard>

      <!-- 5. Application Timeline -->
      <FormCard :collapsible="true" :default-open="true">
        <template #title>
          <v-icon icon="mdi-timeline" class="mr-2" />
          Application Timeline
        </template>

        <template #default>
          <div v-if="application.timeline && application.timeline.length > 0" class="app-timeline">
            <v-timeline side="end" density="compact" truncate-line="both">
              <v-timeline-item
                v-for="event in application.timeline"
                :key="event.id"
                :dot-color="getStatusColor(event.status)"
                size="small"
                class="app-timeline__item"
              >
                <template #opposite>
                  <div class="app-timeline__date">
                    <span class="app-timeline__date-day">{{ formatDate(event.occurredAt) }}</span>
                  </div>
                </template>

                <div class="app-timeline__card">
                  <div class="app-timeline__card-header">
                    <v-chip
                      :color="getStatusColor(event.status)"
                      size="x-small"
                      variant="tonal"
                      class="app-timeline__badge"
                    >
                      {{ event.status }}
                    </v-chip>
                  </div>
                  <div class="app-timeline__card-title">{{ event.title }}</div>
                  <div v-if="event.description" class="app-timeline__card-desc">
                    {{ event.description }}
                  </div>
                </div>
              </v-timeline-item>
            </v-timeline>
          </div>

          <div v-else class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-timeline-outline" size="48" class="mb-2" />
            <p>No timeline events yet. Status changes will appear here.</p>
          </div>
        </template>
      </FormCard>
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
    <FormDialog
      v-model="metaDataDialog"
      icon="mdi-tag-plus"
      :title="metaDataDialogTitle"
      subtitle="Add a key-value pair to store additional information about this application."
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
              placeholder="e.g., Source, Recruiter, Notes"
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
      <strong>{{ application?.title }}</strong
      >?
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
      <strong>"{{ metaDataToDelete?.metaName }}"</strong>? This action cannot be undone.
    </ConfirmDialog>

    <!-- Raw Content Dialog -->
    <RawContentDialog v-model="rawContentDialog" :content="application?.rawContent || ''" />

    <!-- Confirm Delete Company Dialog -->
    <ConfirmDialog
      v-model="confirmDeleteCompanyDialog"
      title="Remove Company"
      variant="error"
      confirm-text="Remove"
      cancel-text="Cancel"
      @confirm="performDeleteCompany"
      @cancel="closeDeleteCompanyDialog"
    >
      Are you sure you want to remove
      <strong>{{ application?.company?.name }}</strong> from this application? The change will take
      effect when you save.
    </ConfirmDialog>
  </div>
</template>

<script lang="ts" src="./ApplicationDetailView.ts" />

<style lang="css" scoped src="./ApplicationDetailView.css" />

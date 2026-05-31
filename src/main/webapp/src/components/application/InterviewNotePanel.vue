<template>
  <v-dialog
    v-model="isOpen"
    transition="slide-x-reverse-transition"
    content-class="interview-note-panel-overlay"
    :persistent="loading"
    :scrim="true"
  >
    <v-card class="interview-note-panel-card" rounded="0" elevation="8">
      <!-- Coloured top bar, matching FormDialog style -->
      <div class="interview-note-panel-status" />

      <!-- Header -->
      <v-card-title class="d-flex align-center px-6 pt-5 pb-3">
        <v-icon icon="mdi-note-edit-outline" color="primary" size="28" class="mr-3 flex-shrink-0" />
        <span class="text-h6 font-weight-bold">{{ panelTitle }}</span>
        <v-spacer />
        <v-btn icon="mdi-close" variant="text" size="small" :disabled="loading" @click="close" />
      </v-card-title>

      <v-divider />

      <!-- Scrollable body -->
      <v-card-text class="interview-note-panel-body px-6 pt-5 pb-3">
        <!-- Date / time field -->
        <v-text-field
          v-model="formDate"
          type="datetime-local"
          label="Interview Date & Time"
          variant="outlined"
          bg-color="surface"
          rounded="md"
          prepend-inner-icon="mdi-calendar-clock"
          class="mb-4"
        />

        <!-- Markdown notes editor -->
        <MdEditor
          v-model="formNotes"
          label="Notes"
          placeholder="Write interview notes here..."
          :rows="10"
        />

        <!-- ── Attachments — only shown once the note has been persisted ── -->
        <template v-if="hasId">
          <v-divider class="my-5" />

          <div class="d-flex align-center mb-3">
            <span class="text-subtitle-2 font-weight-bold">Attachments</span>
            <v-spacer />
            <v-btn
              size="small"
              variant="tonal"
              color="primary"
              prepend-icon="mdi-paperclip"
              :loading="attachmentsLoading"
              @click="triggerFileInput"
            >
              Upload
            </v-btn>
            <!-- Hidden file input — triggered programmatically -->
            <input ref="fileInputRef" type="file" class="d-none" @change="onFileSelected" />
          </div>

          <v-progress-linear v-if="attachmentsLoading" indeterminate color="primary" class="mb-2" />

          <v-list v-if="attachments.length > 0" density="compact" class="pa-0">
            <v-list-item v-for="attachment in attachments" :key="attachment.id" class="px-0">
              <template #prepend>
                <v-icon icon="mdi-file-outline" size="18" class="mr-2" />
              </template>
              <v-list-item-title class="text-body-2">
                {{ attachment.name }}
              </v-list-item-title>
              <v-list-item-subtitle class="text-caption">
                {{ formatFileSize(attachment.contentLength) }}
              </v-list-item-subtitle>
              <template #append>
                <v-btn
                  icon="mdi-delete-outline"
                  size="x-small"
                  variant="text"
                  color="error"
                  @click="deleteAttachment(attachment)"
                />
              </template>
            </v-list-item>
          </v-list>

          <div v-else-if="!attachmentsLoading" class="text-body-2 text-medium-emphasis py-2">
            No attachments yet.
          </div>
        </template>
      </v-card-text>

      <v-divider />

      <!-- Footer -->
      <v-card-actions class="px-6 py-4">
        <v-spacer />
        <v-btn
          text="Cancel"
          variant="text"
          color="secondary"
          :disabled="loading"
          min-width="120"
          @click="close"
        />
        <v-btn
          text="Save"
          color="primary"
          variant="flat"
          rounded="md"
          :loading="loading"
          min-width="120"
          @click="save"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts" src="./InterviewNotePanel.ts" />

<!--
  The dialog content is teleported to <body> by Vuetify, so the overlay
  positioning styles must be global (cannot be scoped).
-->
<style>
.interview-note-panel-overlay {
  position: fixed !important;
  right: 0 !important;
  top: 0 !important;
  bottom: 0 !important;
  margin: 0 !important;
  width: 540px !important;
  max-width: 100vw !important;
  height: 100dvh !important;
  max-height: 100dvh !important;
}
</style>

<style scoped>
.interview-note-panel-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.interview-note-panel-status {
  height: 4px;
  background-color: rgb(var(--v-theme-primary));
}

.interview-note-panel-body {
  flex: 1 1 0;
  overflow-y: auto;
  min-height: 0;
}
</style>

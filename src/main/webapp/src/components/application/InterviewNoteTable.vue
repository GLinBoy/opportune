<template>
  <div style="overflow-x: auto">
    <v-data-table
      striped="odd"
      :headers="headers"
      :items="items"
      :loading="loading"
      :sort-by="sortBy"
      hide-default-footer
      density="compact"
      class="elevation-1"
    >
      <!-- Date Column -->
      <template v-slot:[`item.date`]="{ item }">
        <span class="text-no-wrap">{{ formatDate(item.date) }}</span>
      </template>

      <!-- Notes Column -->
      <template v-slot:[`item.notes`]="{ item }">
        <span class="text-medium-emphasis">{{ truncateNotes(item.notes) }}</span>
      </template>

      <!-- Attachments Column -->
      <template v-slot:[`item.attachments`]="{ item }">
        <v-chip
          v-if="getAttachmentCount(item.id) > 0"
          size="x-small"
          color="primary"
          variant="tonal"
          prepend-icon="mdi-paperclip"
        >
          {{ getAttachmentCount(item.id) }}
        </v-chip>
      </template>

      <!-- Actions Column -->
      <template v-slot:[`item.actions`]="{ item }">
        <div class="d-flex justify-end" style="gap: 4px">
          <v-tooltip text="Edit" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                size="x-small"
                variant="text"
                color="primary"
                icon="mdi-pencil-outline"
                @click.stop="$emit('edit', item)"
              />
            </template>
          </v-tooltip>
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                size="x-small"
                variant="text"
                color="error"
                icon="mdi-delete-outline"
                @click.stop="$emit('delete', item)"
              />
            </template>
          </v-tooltip>
        </div>
      </template>

      <!-- Empty State -->
      <template v-slot:no-data>
        <div class="d-flex flex-column align-center justify-center py-10 text-medium-emphasis">
          <v-icon icon="mdi-note-off-outline" size="48" class="mb-3" />
          <span class="text-body-1">No interview notes yet.</span>
          <span class="text-body-2 mt-1">Add your first note using the button above.</span>
        </div>
      </template>
    </v-data-table>
  </div>
</template>

<script lang="ts" src="./InterviewNoteTable.ts" />

<style scoped>
:deep(.v-data-table__tr) {
  cursor: pointer;
}

:deep(.v-data-table__tr:hover) {
  background-color: rgb(var(--v-theme-grey-lighten-5)) !important;
}

:deep(thead) {
  background-color: rgb(var(--v-theme-primary)) !important;
}

:deep(thead th) {
  background-color: rgb(var(--v-theme-primary)) !important;
  color: rgb(var(--v-theme-on-primary)) !important;
}

:deep(thead .v-data-table-header__content) {
  color: rgb(var(--v-theme-on-primary)) !important;
  font-weight: 600;
}

:deep(.v-table) {
  border-bottom: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
}
</style>

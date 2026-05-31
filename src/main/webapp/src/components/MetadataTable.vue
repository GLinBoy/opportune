<template>
  <div style="overflow-x: auto">
    <v-data-table
      striped="odd"
      :headers="headers"
      :items="items"
      :loading="loading"
      hide-default-footer
      density="compact"
      class="elevation-1"
      @click:row="handleRowClick"
    >
      <!-- Key Column -->
      <template v-slot:[`item.metaName`]="{ item }">
        <span class="font-weight-bold">{{ item.metaName }}</span>
      </template>

      <!-- Value Column -->
      <template v-slot:[`item.metaValue`]="{ item }">
        <div class="text-wrap" style="max-width: 400px; white-space: pre-wrap">
          {{ item.metaValue }}
        </div>
      </template>

      <!-- Actions Column -->
      <template v-slot:[`item.actions`]="{ item }">
        <div class="d-flex justify-end">
          <v-tooltip text="Delete" location="top">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                size="x-small"
                variant="text"
                color="error"
                icon="mdi-delete-outline"
                @click.stop="$emit('delete', item.id)"
              />
            </template>
          </v-tooltip>
        </div>
      </template>
    </v-data-table>
  </div>
</template>

<script lang="ts" src="./MetadataTable.ts" />

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

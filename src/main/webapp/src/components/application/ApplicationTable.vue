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
          {{ item.lastModifiedDate ? formatDate(item.lastModifiedDate) : '-' }}
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
          <slot name="actions" :item="item">
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
          </slot>
        </div>
      </template>

      <!-- Footer slot for "View All" link or custom content -->
      <template v-if="$slots.footer || viewAllTo" #bottom>
        <slot name="footer">
          <div class="d-flex justify-end pa-2">
            <v-btn
              :to="viewAllTo"
              variant="text"
              color="primary"
              size="small"
              append-icon="mdi-arrow-right"
            >
              View all applications
            </v-btn>
          </div>
        </slot>
      </template>
    </v-data-table>
  </div>
</template>

<script lang="ts" src="./ApplicationTable.ts" />

<style scoped>
:deep(.v-data-table__tr:hover) {
  background-color: rgb(var(--v-theme-grey-lighten-5)) !important;
}

:deep(.v-data-table__tr) {
  cursor: pointer;
}

:deep(.v-data-table__td:last-child) {
  position: relative;
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
</style>

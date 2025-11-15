<template>
  <div>
    <!-- Page Header -->
    <div class="d-flex justify-space-between align-center mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold">Companies</h1>
        <p class="text-subtitle-1 text-medium-emphasis">Manage companies and your interest level</p>
      </div>
      <div class="d-flex align-self-center" style="gap: 16px">
        <v-autocomplete
          v-model="selectedCompany"
          v-model:search="searchQuery"
          :items="searchResults"
          :loading="isSearching"
          item-title="name"
          item-value="id"
          label="Search companies..."
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          density="compact"
          style="min-width: 300px"
          clearable
          no-filter
          return-object
          @update:search="handleSearchInput"
          @update:model-value="handleCompanySelect"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item
              v-bind="props"
              :title="item.raw.name"
              :subtitle="item.raw.status"
            >
              <template v-slot:prepend>
                <v-icon
                  :color="getSearchResultStatusColor(item.raw.status)"
                  :icon="getSearchResultStatusIcon(item.raw.status)"
                />
              </template>
            </v-list-item>
          </template>
          <template v-slot:no-data>
            <v-list-item>
              <v-list-item-title>
                {{ searchQuery ? 'No companies found' : 'Start typing to search...' }}
              </v-list-item-title>
            </v-list-item>
          </template>
        </v-autocomplete>
        <v-tooltip text="Add new company" location="bottom">
          <template #activator="{ props }">
            <v-btn
              v-bind="props"
              color="primary"
              icon="mdi-briefcase-plus"
              size="small"
              @click="addNewCompany" />
          </template>
        </v-tooltip>
      </div>
    </div>

    <!-- Data Table -->
    <v-card>
      <v-data-table-server
        striped="odd"
        :show-current-page="true"
        :items-per-page="itemsPerPage"
        :page="page"
        :headers="headers"
        :items="companies"
        :items-length="totalItems"
        :loading="isFetching"
        :items-per-page-options="itemsPerPageOptions"
        @update:options="handleUpdateOptions"
        class="elevation-1 primary-header"
        @click:row="handleRowClick"
      >

      <template v-slot:[`item.name`]="{ item }">
        <v-container fluid class="pa-0">
          <v-row no-gutters align="center">
            <v-col cols="auto">
              <v-avatar class="pa-1">
                <v-img :alt="item.name" :src="item.logo ? item.logo : defaultCompanyLogo" />
              </v-avatar>
            </v-col>
            <v-col class="ps-2">
              <span class="font-weight-bold">{{ item.name }}</span>
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

      <template v-slot:[`item.lastModifiedDate`]="{ item }">
        <v-container fluid class="pa-0">
          <v-row no-gutters align="center">
            <v-col>
              {{ item.lastModifiedDate ? formatDate(item.lastModifiedDate) : '-' }}
            </v-col>
            <v-col cols="auto" v-if="item.createdDate && item.lastModifiedDate && formatDate(item.createdDate) !== formatDate(item.lastModifiedDate)">
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

      <template v-slot:[`item.status`]="{ value }">
        <v-chip
          :border="`${getCompanyStatusColor(value)} thin opacity-25`"
          :color="getCompanyStatusColor(value)"
          :text="getCompanyStatusDisplay(value)"
          size="x-small"
          :prepend-icon="getCompanyStatusIcon(value)" />
      </template>
      </v-data-table-server>
    </v-card>

    <!-- Loading overlay -->
    <v-overlay v-model="isFetching" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <v-dialog
      v-model="addNewCompanyDialog"
      transition="dialog-bottom-transition"
      fullscreen
    >
      <v-card>
        <v-toolbar color="secondary" dark>
          <v-btn
            icon="mdi-close"
            @click="closeDialog"
          ></v-btn>
          <v-toolbar-title>Add New Company</v-toolbar-title>
        </v-toolbar>

        <v-container class="d-flex justify-center pa-8">
          <div style="max-width: 800px; width: 100%;">
            <CompanyForm
              v-model="newCompany"
              class="mb-6"
            />

            <!-- Action buttons at the bottom -->
            <v-card>
              <v-card-actions class="justify-end pa-4">
                <v-btn
                text="Cancel"
                  variant="outlined"
                  prepend-icon="mdi-close"
                  @click="closeDialog"
                  :disabled="isCreating"
                />
                <v-btn
                  text="Create"
                  color="primary"
                  variant="flat"
                  prepend-icon="mdi-content-save"
                  :loading="isCreating"
                  @click="createCompany"
                />
              </v-card-actions>
            </v-card>
          </div>
        </v-container>
      </v-card>
    </v-dialog>
  </div>
</template>
<script lang="ts" src="./CompanyListView.ts" />

<style lang="css" scoped src="./CompanyListView.css" />

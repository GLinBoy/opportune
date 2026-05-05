<template>
  <v-card elevation="0" border rounded="lg">
    <v-card-title class="pa-5 pb-4 text-body-1 font-weight-semibold">
      <slot name="title" />
    </v-card-title>
    <v-divider />
    <v-card-text class="pa-5">
      <slot />
    </v-card-text>
    <template v-if="$slots.actions">
      <v-divider />
      <v-card-actions class="px-5 py-4 justify-end gap-2">
        <slot name="actions" />
      </v-card-actions>
    </template>
  </v-card>
</template>

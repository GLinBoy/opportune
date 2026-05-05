<template>
  <v-row>
    <v-col cols="12" sm="6" lg="3">
      <DashboardCard
        title="Total Applications"
        icon="mdi-briefcase-variant-outline"
        :value="2"
        description="Tracked in your pipeline"
      />
    </v-col>
    <v-col cols="12" sm="6" lg="3">
      <DashboardCard
        title="In Progress"
        icon="mdi-chart-bar"
        value="1"
        description="Actively in an interview process"
      />
    </v-col>
    <v-col cols="12" sm="6" lg="3">
      <DashboardCard
        title="Offer Received"
        icon="mdi-check-circle-outline"
        value="0"
        description="You've received offers from"
      />
    </v-col>
    <v-col cols="12" sm="6" lg="3">
      <DashboardCard
        title="Rejected"
        icon="mdi-close-circle-outline"
        value="0"
        description="Applications marked as rejected"
      />
    </v-col>
    <v-col cols="12" md="6">
      <DashboardApplicationCard />
    </v-col>
    <v-col cols="12" md="6">
      <DashboardCompanyCard />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import DashboardCard from '../components/dashboard/DashboardStatusCard.vue'
import DashboardApplicationCard from '../components/dashboard/DashboardApplicationCard.vue'
import DashboardCompanyCard from '../components/dashboard/DashboardCompanyCard.vue'
</script>

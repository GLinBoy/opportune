<script setup lang="ts">
import { computed, ref } from 'vue'
import MdViewer from './markdown/MdViewer.vue'

export interface ScoreSection {
  label: string
  score: number
  rationale: string
  icon: string
}

export interface Props {
  resumeOverallScore?: number | null
  skillMatchScore?: number | null
  skillMatchRationale?: string | null
  experienceMatchScore?: number | null
  experienceMatchRationale?: string | null
  educationMatchScore?: number | null
  educationMatchRationale?: string | null
  keywordMatchScore?: number | null
  keywordMatchRationale?: string | null
}

const props = withDefaults(defineProps<Props>(), {
  resumeOverallScore: null,
  skillMatchScore: null,
  skillMatchRationale: null,
  experienceMatchScore: null,
  experienceMatchRationale: null,
  educationMatchScore: null,
  educationMatchRationale: null,
  keywordMatchScore: null,
  keywordMatchRationale: null,
})

const activeTab = ref(0)

const isReady = computed(
  () =>
    props.resumeOverallScore != null &&
    props.skillMatchScore != null &&
    props.experienceMatchScore != null &&
    props.educationMatchScore != null &&
    props.keywordMatchScore != null
)

function scoreColor(score: number | null | undefined): string {
  if (score == null) return 'grey'
  if (score >= 75) return 'success'
  if (score >= 50) return 'warning'
  return 'error'
}

const sections = computed<ScoreSection[]>(() => [
  {
    label: 'Skill Match',
    score: props.skillMatchScore ?? 0,
    rationale: props.skillMatchRationale ?? '',
    icon: 'mdi-code-tags',
  },
  {
    label: 'Experience Match',
    score: props.experienceMatchScore ?? 0,
    rationale: props.experienceMatchRationale ?? '',
    icon: 'mdi-briefcase-outline',
  },
  {
    label: 'Education Match',
    score: props.educationMatchScore ?? 0,
    rationale: props.educationMatchRationale ?? '',
    icon: 'mdi-school-outline',
  },
  {
    label: 'Keyword Match',
    score: props.keywordMatchScore ?? 0,
    rationale: props.keywordMatchRationale ?? '',
    icon: 'mdi-magnify',
  },
])
</script>

<template>
  <v-container>
    <!-- ── Empty state ── -->
    <template v-if="!isReady">
      <v-row justify="center">
        <v-col cols="12" sm="8" md="6" class="text-center py-12">
          <v-icon icon="mdi-file-search-outline" size="64" color="grey-lighten-1" class="mb-4" />
          <p class="text-h6 font-weight-medium text-medium-emphasis mb-2">No score analysis yet</p>
          <p class="text-body-2 text-disabled">
            Resume scoring will appear here once the application has been analyzed.
          </p>
        </v-col>
      </v-row>
    </template>

    <template v-else>
      <!-- ── Overall Score ── -->
      <v-sheet rounded="lg" border class="pa-4 mb-6">
        <div class="d-flex align-center justify-space-between mb-2">
          <span class="text-subtitle-1 font-weight-medium">Overall Match</span>
          <v-chip :color="scoreColor(resumeOverallScore)" variant="tonal" size="small" label>
            {{ resumeOverallScore }} / 100
          </v-chip>
        </div>
        <v-progress-linear
          :model-value="resumeOverallScore ?? undefined"
          :color="scoreColor(resumeOverallScore)"
          height="13"
          chunk-count="10"
          chunk-gap="3"
          rounded
        />
      </v-sheet>

      <!-- ── Vertical Tabs Layout ── -->
      <v-card rounded="lg" variant="outlined">
        <v-row density="compact">
          <!-- Left: Vertical Tab Headers -->
          <v-col cols="12" sm="4" md="3" class="tabs-col">
            <v-tabs v-model="activeTab" direction="vertical" color="primary" class="h-100">
              <v-tab
                v-for="(section, index) in sections"
                :key="section.label"
                :value="index"
                spaced="start"
                class="justify-start px-4 w-100"
              >
                <v-icon :icon="section.icon" size="18" class="me-2" />
                <span class="text-body-2 font-weight-medium text-start me-auto">
                  {{ section.label }}
                </span>
                <v-tooltip :text="`Score: ${section.score} / 100`" location="top">
                  <template #activator="{ props }">
                    <v-chip
                      v-bind="props"
                      :color="scoreColor(section.score)"
                      size="x-small"
                      variant="tonal"
                      label
                      class="ms-2"
                    >
                      {{ section.score }}%
                    </v-chip>
                  </template>
                </v-tooltip>
              </v-tab>
            </v-tabs>
          </v-col>

          <!-- Right: Tab Content -->
          <v-col cols="12" sm="8" md="9">
            <v-tabs-window v-model="activeTab">
              <v-tabs-window-item
                v-for="(section, index) in sections"
                :key="section.label"
                :value="index"
              >
                <div class="pa-6">
                  <!-- Section header -->
                  <div class="d-flex align-center ga-3 mb-4">
                    <v-icon :icon="section.icon" size="22" />
                    <span class="text-h6 font-weight-medium">{{ section.label }}</span>
                    <v-spacer />
                    <v-tooltip :text="`Score: ${section.score} / 100`" location="left">
                      <template #activator="{ props: tooltipProps }">
                        <v-progress-circular
                          v-bind="tooltipProps"
                          :model-value="section.score"
                          :size="40"
                          :width="4"
                          :color="scoreColor(section.score)"
                        >
                          <span class="text-caption font-weight-bold">
                            {{ section.score }}
                          </span>
                        </v-progress-circular>
                      </template>
                    </v-tooltip>
                  </div>

                  <v-divider class="mb-4" />

                  <!-- Rationale view via MdViewer -->
                  <v-sheet
                    v-if="section.rationale"
                    rounded="md"
                    color="surface"
                    class="pa-4 text-body-2"
                  >
                    <MdViewer :content="section.rationale" />
                  </v-sheet>
                  <v-alert
                    v-else
                    type="info"
                    variant="tonal"
                    density="compact"
                    text="No detailed rationale is available for this section."
                  />
                </div>
              </v-tabs-window-item>
            </v-tabs-window>
          </v-col>
        </v-row>
      </v-card>
    </template>
  </v-container>
</template>

<style scoped>
.tabs-col {
  border-right: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
}

@media (max-width: 599px) {
  .tabs-col {
    border-right: none;
    border-bottom: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
  }
}
</style>

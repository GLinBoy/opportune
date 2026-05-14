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
  height?: string
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
  height: '530px',
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
  <div class="resume-score-card" :style="{ height }">
    <!-- ── Empty state ── -->
    <template v-if="!isReady">
      <div class="resume-score-card__scroll">
        <v-row justify="center">
          <v-col cols="12" sm="8" md="6" class="text-center py-12">
            <v-icon icon="mdi-file-search-outline" size="64" color="grey-lighten-1" class="mb-4" />
            <p class="text-h6 font-weight-medium text-medium-emphasis mb-2">
              No score analysis yet
            </p>
            <p class="text-body-2 text-disabled">
              Resume scoring will appear here once the application has been analyzed.
            </p>
          </v-col>
        </v-row>
      </div>
    </template>

    <template v-else>
      <!-- ── Overall Score (fixed) ── -->
      <v-sheet rounded="lg" border class="pa-4 mb-4 mx-4 mt-4 flex-shrink-0">
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

      <!-- ── Vertical Tabs Layout (fills remaining height) ── -->
      <v-card rounded="lg" variant="outlined" class="resume-score-card__tabs-card mx-4 mb-4">
        <div class="resume-score-card__tabs-layout">
          <!-- Left: Vertical Tab Headers -->
          <div class="tabs-col">
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
          </div>

          <!-- Right: Tab Content -->
          <div class="resume-score-card__content-col">
            <v-tabs-window v-model="activeTab" class="h-100">
              <v-tabs-window-item
                v-for="(section, index) in sections"
                :key="section.label"
                :value="index"
                class="h-100"
              >
                <div class="resume-score-card__tab-pane h-100">
                  <!-- Section header (fixed) -->
                  <div class="d-flex align-center ga-3 pa-6 pb-0">
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

                  <v-divider class="mt-4 mx-6" />

                  <!-- Scrollable rationale content -->
                  <div class="resume-score-card__tab-content px-6 pb-6 pt-4">
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
                </div>
              </v-tabs-window-item>
            </v-tabs-window>
          </div>
        </div>
      </v-card>
    </template>
  </div>
</template>

<style scoped>
.resume-score-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.resume-score-card__scroll {
  height: 100%;
  overflow-y: auto;
}

.resume-score-card__tabs-card {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.resume-score-card__tabs-layout {
  display: flex;
  height: 100%;
  overflow: hidden;
}

.tabs-col {
  flex-shrink: 0;
  width: 220px;
  border-right: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
  overflow-y: auto;
}

.resume-score-card__content-col {
  flex: 1;
  overflow: hidden;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.resume-score-card__tab-pane {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.resume-score-card__tab-content {
  flex: 1;
  overflow-y: auto;
  box-sizing: border-box;
}

@media (max-width: 599px) {
  .resume-score-card__tabs-layout {
    flex-direction: column;
  }

  .tabs-col {
    width: 100%;
    border-right: none;
    border-bottom: thin solid rgba(var(--v-border-color), var(--v-border-opacity));
  }
}
</style>

<template>
  <div>
    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular color="primary" indeterminate size="64" />
    </v-overlay>

    <div v-if="!loading && application">
      <!-- Breadcrumbs -->
      <v-breadcrumbs :items="breadcrumbs" class="pa-0 mb-4">
        <template #prepend>
          <v-icon icon="mdi-home" />
        </template>
      </v-breadcrumbs>

      <!-- Page Header -->
      <div class="d-flex justify-space-between align-center mb-6">
        <div>
          <h1 class="text-h4 font-weight-bold">{{ application.position }}</h1>
          <p class="text-subtitle-1 text-medium-emphasis">{{ application.company }}</p>
          <div v-if="application.jobUrl" class="mt-1">
            <v-btn
              :href="application.jobUrl"
              target="_blank"
              variant="text"
              prepend-icon="mdi-open-in-new"
              size="small"
              color="primary"
              class="pa-0"
              style="text-transform: none; justify-content: flex-start"
            >
              View Job Description
            </v-btn>
          </div>
        </div>
        <div class="d-flex align-center" style="gap: 12px">
          <v-btn
            color="success"
            variant="flat"
            icon="mdi-content-save"
            :loading="saving"
            @click="saveApplication"
          />
        </div>
      </div>

      <!-- 1. Application Details -->
      <v-card class="mb-6">
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-briefcase" class="mr-2" />
          Application Details
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.position"
                label="Position"
                variant="outlined"
                prepend-inner-icon="mdi-account-tie"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.company"
                label="Company"
                variant="outlined"
                prepend-inner-icon="mdi-domain"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.dateApplied"
                label="Date Applied"
                type="date"
                variant="outlined"
                prepend-inner-icon="mdi-calendar"
                @input="markAsModified"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="application.status"
                :items="statusOptions"
                label="Status"
                variant="outlined"
                prepend-inner-icon="mdi-flag"
                @update:model-value="updateStatus"
              >
                <template #selection="{ item }">
                  <v-chip :color="getStatusColor(item.value)" size="small" variant="flat">
                    {{ item.title }}
                  </v-chip>
                </template>
              </v-select>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.jobUrl"
                label="Job URL"
                variant="outlined"
                prepend-inner-icon="mdi-link"
                @input="markAsModified"
              />
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
            <v-col cols="12">
              <v-textarea
                v-model="application.notes"
                label="Notes"
                variant="outlined"
                rows="3"
                prepend-inner-icon="mdi-note-text"
                @input="markAsModified"
              />
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- 2. AI Generated Content -->
      <v-card class="mb-6">
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-robot" class="mr-2" />
          AI Generated Content
        </v-card-title>
        <v-tabs v-model="activeTab" color="primary">
          <v-tab value="job-description">Job Description</v-tab>
          <v-tab value="cover-letter">Cover Letter</v-tab>
          <v-tab value="resume-enhancer">Resume Enhancer</v-tab>
          <v-tab value="interview-details">Interview Details</v-tab>
        </v-tabs>

        <v-tabs-window v-model="activeTab">
          <v-tabs-window-item value="job-description">
            <v-card-text>
              <v-textarea
                v-model="aiContent.jobDescription"
                label="AI Job Description Analysis"
                variant="outlined"
                rows="6"
                readonly
              />
            </v-card-text>
          </v-tabs-window-item>

          <v-tabs-window-item value="cover-letter">
            <v-card-text>
              <v-textarea
                v-model="aiContent.coverLetter"
                label="AI Generated Cover Letter"
                variant="outlined"
                rows="8"
                @input="markAsModified"
              />
            </v-card-text>
          </v-tabs-window-item>

          <v-tabs-window-item value="resume-enhancer">
            <v-card-text>
              <v-textarea
                v-model="aiContent.resumeEnhancer"
                label="Resume Enhancement Suggestions"
                variant="outlined"
                rows="6"
                readonly
              />
            </v-card-text>
          </v-tabs-window-item>

          <v-tabs-window-item value="interview-details">
            <v-card-text>
              <v-textarea
                v-model="aiContent.interviewDetails"
                label="Interview Preparation Details"
                variant="outlined"
                rows="6"
                readonly
              />
            </v-card-text>
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card>

      <!-- 3. Meta Data -->
      <v-card class="mb-6">
        <v-card-title class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <v-icon icon="mdi-database" class="mr-2" />
            Meta Data
          </div>
          <v-btn
            color="primary"
            variant="outlined"
            prepend-icon="mdi-plus"
            size="small"
            @click="showAddMetaDataDialog"
          >
            Add Meta Data
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-data-table
            v-if="application.metaData && application.metaData.length > 0"
            :headers="metaDataHeaders"
            :items="application.metaData"
            hide-default-footer
            density="compact"
          >
            <template v-slot:[`item.key`]="{ item }">
              <span class="font-weight-medium">{{ item.key }}</span>
            </template>
            <template v-slot:[`item.value`]="{ item }">
              <div class="text-wrap" style="max-width: 400px; white-space: pre-wrap">
                {{ item.value }}
              </div>
            </template>
            <template v-slot:[`item.actions`]="{ index }">
              <v-btn
                color="error"
                variant="text"
                icon="mdi-delete"
                size="small"
                @click="removeMetaData(index)"
              />
            </template>
          </v-data-table>
          <div v-else class="text-center py-8 text-medium-emphasis">
            <v-icon icon="mdi-information" size="48" class="mb-2" />
            <p>No meta data available. Click "Add Meta Data" to add key-value pairs.</p>
          </div>
        </v-card-text>
      </v-card>

      <!-- 4. Application Timeline -->
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-timeline" class="mr-2" />
          Application Timeline
        </v-card-title>
        <v-card-text>
          <v-timeline side="end" density="compact">
            <v-timeline-item
              v-for="event in application.timeline"
              :key="event.id"
              :dot-color="getStatusColor(event.status)"
              size="small"
            >
              <template #opposite>
                <div class="text-caption text-medium-emphasis text-right">
                  <div class="font-weight-medium">{{ formatDate(event.date) }}</div>
                  <div v-if="event.time" class="mt-1">{{ event.time }}</div>
                </div>
              </template>
              <div>
                <div class="text-subtitle-2">{{ event.title }}</div>
                <div class="text-body-2 text-medium-emphasis">{{ event.description }}</div>
              </div>
            </v-timeline-item>
          </v-timeline>
        </v-card-text>
      </v-card>
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
    <v-dialog v-model="metaDataDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-database-plus" class="mr-2" />
          Add Meta Data
        </v-card-title>
        <v-card-text>
          <v-form ref="metaDataForm" v-model="metaDataFormValid">
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="newMetaData.key"
                  label="Key"
                  variant="outlined"
                  prepend-inner-icon="mdi-key"
                  :rules="[rules.required]"
                  placeholder="e.g., Source, Recruiter, Notes"
                />
              </v-col>
              <v-col cols="12">
                <v-textarea
                  v-model="newMetaData.value"
                  label="Value"
                  variant="outlined"
                  prepend-inner-icon="mdi-text"
                  :rules="[rules.required]"
                  rows="4"
                  placeholder="Enter the value (supports multi-line text)"
                />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="grey" variant="text" @click="cancelAddMetaData"> Cancel </v-btn>
          <v-btn color="primary" variant="flat" :loading="savingMetaData" @click="saveMetaData">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

// Types
interface TimelineEvent {
  id: number
  date: string
  time?: string
  title: string
  description: string
  status: string
}

interface MetaData {
  key: string
  value: string
}

interface AIContent {
  jobDescription: string
  coverLetter: string
  resumeEnhancer: string
  interviewDetails: string
}

interface Application {
  id: number
  position: string
  company: string
  dateApplied: string
  status: string
  jobUrl?: string
  salaryRange?: string
  location?: string
  workType?: string
  notes?: string
  timeline: TimelineEvent[]
  metaData?: MetaData[]
  aiContent?: AIContent
}

interface StatusOption {
  title: string
  value: string
}

interface Snackbar {
  show: boolean
  message: string
  color: string
}

// Router
const route = useRoute()

// Reactive data
const application = ref<Application | null>(null)
const loading = ref(false)
const saving = ref(false)
const formValid = ref(false)
const hasModifications = ref(false)
const applicationForm = ref()
const activeTab = ref('job-description')

// Meta Data Dialog
const metaDataDialog = ref(false)
const metaDataForm = ref()
const metaDataFormValid = ref(false)
const savingMetaData = ref(false)
const newMetaData = ref<MetaData>({
  key: '',
  value: '',
})

// Meta Data Table Headers
const metaDataHeaders = [
  { title: 'Key', value: 'key', width: '200px' },
  { title: 'Value', value: 'value', width: 'auto' },
  { title: 'Actions', value: 'actions', width: '100px', sortable: false, align: 'center' },
]

// AI Content
const aiContent = ref<AIContent>({
  jobDescription: '',
  coverLetter: '',
  resumeEnhancer: '',
  interviewDetails: '',
})

// Snackbar for notifications
const snackbar = ref<Snackbar>({
  show: false,
  message: '',
  color: 'success',
})

// Form validation rules
const rules = {
  required: (value: string) => !!value || 'This field is required',
}

// Work type options
const workTypeOptions = ['On-site', 'Remote', 'Hybrid']

// Status options
const statusOptions: StatusOption[] = [
  { title: 'Applied', value: 'applied' },
  { title: 'Under Review', value: 'under_review' },
  { title: 'Interview Scheduled', value: 'interview_scheduled' },
  { title: 'Interview Completed', value: 'interview_completed' },
  { title: 'Offer Received', value: 'offer_received' },
  { title: 'Accepted', value: 'accepted' },
  { title: 'Rejected', value: 'rejected' },
  { title: 'Withdrawn', value: 'withdrawn' },
]

// Mock data that matches the list view - Replace with API call
const mockApplications: Application[] = [
  {
    id: 1,
    position: 'Senior Software Engineer',
    company: 'TechCorp Inc.',
    dateApplied: '2024-08-20',
    status: 'interview_scheduled',
    jobUrl: 'https://techcorp.com/careers/senior-software-engineer',
    salaryRange: '$120,000 - $150,000',
    location: 'San Francisco, CA',
    workType: 'Hybrid',
    notes:
      'Great opportunity with cutting-edge technology stack. Team seems very collaborative and focused on innovation.',
    timeline: [
      {
        id: 1,
        date: '2024-08-20',
        time: '09:30 AM',
        title: 'Application Submitted',
        description: 'Successfully submitted application through company website',
        status: 'applied',
      },
      {
        id: 2,
        date: '2024-08-22',
        time: '02:15 PM',
        title: 'Application Under Review',
        description: 'HR team confirmed receipt and started initial review',
        status: 'under_review',
      },
      {
        id: 3,
        date: '2024-08-24',
        time: '11:45 AM',
        title: 'Technical Interview Scheduled',
        description: 'Scheduled for August 26th at 2:00 PM with senior developer',
        status: 'interview_scheduled',
      },
    ],
    metaData: [
      { key: 'Source', value: 'LinkedIn' },
      {
        key: 'Recruiter',
        value: 'Jane Smith\nSenior Technical Recruiter\njane.smith@techcorp.com\n(555) 123-4567',
      },
      { key: 'Application Method', value: 'Direct Apply' },
      {
        key: 'Requirements Match',
        value:
          'Skills: React (5+ years), Node.js (4+ years), AWS (3+ years)\nEducation: Computer Science degree\nLocation: San Francisco Bay Area preferred\nSalary: Within range',
      },
      {
        key: 'Interview Notes',
        value:
          'First interview went well.\nTechnical questions focused on:\n- System design\n- React performance\n- Database optimization\n\nNext: On-site interview scheduled.',
      },
    ],
    aiContent: {
      jobDescription:
        'AI Analysis: This position requires strong expertise in React, Node.js, and cloud technologies. The role emphasizes scalable architecture and team collaboration. Key requirements include 5+ years experience and leadership capabilities.',
      coverLetter:
        'Dear Hiring Manager,\n\nI am excited to apply for the Senior Software Engineer position at TechCorp Inc. With my extensive background in full-stack development and passion for innovative technology solutions...',
      resumeEnhancer:
        'Suggestions:\n1. Highlight your React and Node.js projects more prominently\n2. Add specific metrics about scalability improvements\n3. Include leadership examples from previous roles\n4. Mention cloud platform certifications',
      interviewDetails:
        'Interview Preparation:\n\nTechnical Topics to Review:\n- System design patterns\n- React performance optimization\n- Node.js best practices\n- Cloud architecture\n\nBehavioral Questions:\n- Leadership examples\n- Team collaboration scenarios\n- Problem-solving approaches',
    },
  },
  {
    id: 2,
    position: 'Full Stack Developer',
    company: 'StartupXYZ',
    dateApplied: '2024-08-18',
    status: 'under_review',
    jobUrl: 'https://startupxyz.com/jobs/fullstack-dev',
    salaryRange: '$90,000 - $120,000',
    location: 'Remote',
    workType: 'Remote',
    notes:
      'Fast-growing startup with equity options. Looking for someone to help scale their platform.',
    timeline: [
      {
        id: 1,
        date: '2024-08-18',
        time: '03:20 PM',
        title: 'Application Submitted',
        description: 'Applied through AngelList',
        status: 'applied',
      },
      {
        id: 2,
        date: '2024-08-20',
        time: '10:00 AM',
        title: 'Initial Review',
        description: 'Founder reviewed application personally',
        status: 'under_review',
      },
    ],
    metaData: [
      { key: 'Source', value: 'AngelList' },
      {
        key: 'Equity',
        value:
          '0.1% - 0.5%\nVesting: 4 years with 1-year cliff\nStock options included\nPotential for growth as company scales',
      },
      { key: 'Team Size', value: '15 people' },
      {
        key: 'Company Stage',
        value:
          'Series A\nFunded: $5M in Series A\nRevenue: $2M ARR\nCustomers: 500+ enterprise clients',
      },
      {
        key: 'Benefits',
        value:
          'Health, dental, vision insurance\n$2000 learning budget\nFlexible PTO\nRemote work setup allowance\nStock options',
      },
    ],
    aiContent: {
      jobDescription:
        'AI Analysis: Startup environment seeking versatile developer. Strong emphasis on MVP development, rapid iteration, and wearing multiple hats. Equity compensation indicates growth potential.',
      coverLetter:
        'Dear StartupXYZ Team,\n\nI am thrilled to apply for the Full Stack Developer position. Your mission to revolutionize the industry aligns perfectly with my passion for building impactful products...',
      resumeEnhancer:
        'Startup Focus:\n1. Emphasize adaptability and learning agility\n2. Highlight MVP and rapid development experience\n3. Show examples of working in small teams\n4. Mention any startup or entrepreneurial experience',
      interviewDetails:
        'Startup Interview Prep:\n\nKey Areas:\n- Product mindset and user focus\n- Ability to work independently\n- Technical versatility\n- Growth mindset\n\nQuestions to Expect:\n- How do you prioritize features?\n- Experience with rapid prototyping\n- Handling ambiguous requirements',
    },
  },
  {
    id: 3,
    position: 'Frontend Developer',
    company: 'Design Studio',
    dateApplied: '2024-08-15',
    status: 'applied',
    jobUrl: 'https://designstudio.com/careers/frontend',
    salaryRange: '$80,000 - $100,000',
    location: 'New York, NY',
    workType: 'On-site',
    notes:
      'Creative agency focusing on user experience and modern design. Portfolio review required.',
    timeline: [
      {
        id: 1,
        date: '2024-08-15',
        time: '11:20 AM',
        title: 'Application Submitted',
        description: 'Submitted application with portfolio showcase',
        status: 'applied',
      },
    ],
  },
  {
    id: 4,
    position: 'DevOps Engineer',
    company: 'CloudSystems',
    dateApplied: '2024-08-12',
    status: 'offer_received',
    jobUrl: 'https://cloudsystems.com/jobs/devops',
    salaryRange: '$130,000 - $160,000',
    location: 'Austin, TX',
    workType: 'Hybrid',
    notes: 'Excellent cloud infrastructure team. Strong focus on automation and scalability.',
    timeline: [
      {
        id: 1,
        date: '2024-08-12',
        time: '09:30 AM',
        title: 'Application Submitted',
        description: 'Applied through LinkedIn',
        status: 'applied',
      },
      {
        id: 2,
        date: '2024-08-14',
        time: '02:15 PM',
        title: 'Phone Screening',
        description: 'Initial call with HR and hiring manager',
        status: 'under_review',
      },
      {
        id: 3,
        date: '2024-08-16',
        time: '10:00 AM',
        title: 'Technical Interview',
        description: 'Live coding session and system design discussion',
        status: 'interview_scheduled',
      },
      {
        id: 4,
        date: '2024-08-18',
        time: '01:30 PM',
        title: 'Final Interview',
        description: 'Culture fit interview with team lead',
        status: 'interview_completed',
      },
      {
        id: 5,
        date: '2024-08-20',
        time: '04:45 PM',
        title: 'Offer Received',
        description: 'Formal offer letter received with competitive package',
        status: 'offer_received',
      },
    ],
  },
  {
    id: 5,
    position: 'Backend Developer',
    company: 'DataFlow Solutions',
    dateApplied: '2024-08-10',
    status: 'rejected',
    jobUrl: 'https://dataflow.com/careers/backend',
    salaryRange: '$100,000 - $130,000',
    location: 'Boston, MA',
    workType: 'On-site',
    notes: 'Data-heavy applications with focus on performance and scalability.',
    timeline: [
      {
        id: 1,
        date: '2024-08-10',
        time: '11:45 AM',
        title: 'Application Submitted',
        description: 'Submitted through company career portal',
        status: 'applied',
      },
      {
        id: 2,
        date: '2024-08-12',
        time: '03:30 PM',
        title: 'Application Reviewed',
        description: 'Technical team reviewed application',
        status: 'under_review',
      },
      {
        id: 3,
        date: '2024-08-15',
        time: '09:15 AM',
        title: 'Application Declined',
        description: 'Position filled by internal candidate',
        status: 'rejected',
      },
    ],
  },
]

// Computed properties
const applicationId = computed(() => route.params.id as string)

const breadcrumbs = computed(() => [
  {
    title: 'Dashboard',
    disabled: false,
    href: '/dashboard',
  },
  {
    title: 'Applications',
    disabled: false,
    href: '/applications',
  },
  {
    title: application.value?.position || 'Application Detail',
    disabled: true,
  },
])

// Methods
const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

const getStatusColor = (status: string): string => {
  const colorMap: Record<string, string> = {
    applied: 'blue',
    under_review: 'orange',
    interview_scheduled: 'purple',
    interview_completed: 'indigo',
    offer_received: 'green',
    accepted: 'success',
    rejected: 'error',
    withdrawn: 'grey',
  }
  return colorMap[status] || 'default'
}

const updateStatus = async (newStatus: string) => {
  if (!application.value) return

  try {
    // Don't show loading for status updates since they're inline
    // Replace with actual API call when backend is ready
    // await updateApplicationStatusAPI(application.value.id, newStatus)

    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 300))

    // Update local data
    const oldStatus = application.value.status
    application.value.status = newStatus

    // Add timeline event for status change
    const now = new Date().toISOString().split('T')[0]
    application.value.timeline.push({
      id: application.value.timeline.length + 1,
      date: now,
      title: `Status Changed to ${statusOptions.find((s) => s.value === newStatus)?.title}`,
      description: `Status updated from ${
        statusOptions.find((s) => s.value === oldStatus)?.title
      } to ${statusOptions.find((s) => s.value === newStatus)?.title}`,
      status: newStatus,
    })

    markAsModified()

    snackbar.value = {
      show: true,
      message: 'Application status updated successfully!',
      color: 'success',
    }
  } catch (error) {
    console.error('Failed to update application status:', error)
    snackbar.value = {
      show: true,
      message: 'Failed to update application status. Please try again.',
      color: 'error',
    }
  }
}

const markAsModified = () => {
  hasModifications.value = true
}

const saveApplication = async () => {
  if (!application.value || !applicationForm.value) return

  // Validate form
  const { valid } = await applicationForm.value.validate()
  if (!valid) {
    snackbar.value = {
      show: true,
      message: 'Please fix the validation errors before saving.',
      color: 'error',
    }
    return
  }

  try {
    saving.value = true

    // Replace with actual API call when backend is ready
    // await saveApplicationAPI(application.value)

    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1000))

    hasModifications.value = false

    snackbar.value = {
      show: true,
      message: 'Application saved successfully!',
      color: 'success',
    }
  } catch (error) {
    console.error('Failed to save application:', error)
    snackbar.value = {
      show: true,
      message: 'Failed to save application. Please try again.',
      color: 'error',
    }
  } finally {
    saving.value = false
  }
}

const uploadResume = () => {
  // Function to handle resume upload for this specific application
  console.log('Upload resume clicked for application:', application.value?.id)
  snackbar.value = {
    show: true,
    message: 'Upload resume functionality will be implemented soon!',
    color: 'info',
  }
}

const showRawContent = () => {
  // Function to show raw content fetched from job posting URL
  console.log('Show raw content clicked for application:', application.value?.id)
  console.log('Job URL:', application.value?.jobUrl)
  snackbar.value = {
    show: true,
    message: 'Show raw content functionality will be implemented soon!',
    color: 'info',
  }
}

const deleteApplication = () => {
  // Show confirmation dialog and delete when implemented
  snackbar.value = {
    show: true,
    message: 'Delete application feature coming soon!',
    color: 'info',
  }
}

// AI Content Generation Methods
const regenerateJobDescription = () => {
  console.log('Regenerate job description clicked for application:', application.value?.id)
  snackbar.value = {
    show: true,
    message: 'Regenerating job description analysis...',
    color: 'info',
  }
}

const regenerateCoverLetter = () => {
  console.log('Regenerate cover letter clicked for application:', application.value?.id)
  snackbar.value = {
    show: true,
    message: 'Regenerating cover letter...',
    color: 'info',
  }
}

const regenerateResumeEnhancer = () => {
  console.log('Regenerate resume enhancer clicked for application:', application.value?.id)
  snackbar.value = {
    show: true,
    message: 'Regenerating resume enhancement suggestions...',
    color: 'info',
  }
}

const regenerateInterviewDetails = () => {
  console.log('Regenerate interview details clicked for application:', application.value?.id)
  snackbar.value = {
    show: true,
    message: 'Regenerating interview preparation details...',
    color: 'info',
  }
}

// Meta Data Management Methods
const showAddMetaDataDialog = () => {
  newMetaData.value = {
    key: '',
    value: '',
  }
  metaDataDialog.value = true
}

const saveMetaData = async () => {
  if (!metaDataForm.value || !application.value) return

  // Validate form
  const { valid } = await metaDataForm.value.validate()
  if (!valid) {
    return
  }

  try {
    savingMetaData.value = true

    // Simulate API call delay
    await new Promise((resolve) => setTimeout(resolve, 500))

    // Add to application meta data
    if (!application.value.metaData) {
      application.value.metaData = []
    }

    application.value.metaData.push({
      key: newMetaData.value.key,
      value: newMetaData.value.value,
    })

    markAsModified()
    metaDataDialog.value = false

    snackbar.value = {
      show: true,
      message: 'Meta data added successfully!',
      color: 'success',
    }
  } catch (error) {
    console.error('Failed to add meta data:', error)
    snackbar.value = {
      show: true,
      message: 'Failed to add meta data. Please try again.',
      color: 'error',
    }
  } finally {
    savingMetaData.value = false
  }
}

const cancelAddMetaData = () => {
  metaDataDialog.value = false
  newMetaData.value = {
    key: '',
    value: '',
  }
}

const removeMetaData = (index: number) => {
  if (!application.value?.metaData) return

  application.value.metaData.splice(index, 1)
  markAsModified()

  snackbar.value = {
    show: true,
    message: 'Meta data removed successfully!',
    color: 'success',
  }
}

const loadApplication = async () => {
  try {
    loading.value = true

    // Replace with actual API call when backend is ready
    // const response = await fetchApplicationById(applicationId.value)
    // application.value = response.data

    // Simulate API call delay
    await new Promise((resolve) => setTimeout(resolve, 800))

    // Find application from mock data
    const id = parseInt(applicationId.value)
    const foundApplication = mockApplications.find((app) => app.id === id)

    if (foundApplication) {
      application.value = foundApplication
      // Initialize AI content if available
      if (foundApplication.aiContent) {
        aiContent.value = foundApplication.aiContent
      }
    } else {
      // If not found in predefined mock data, create a generic one
      application.value = {
        id: id,
        position: 'Sample Position',
        company: 'Sample Company',
        dateApplied: '2024-08-25',
        status: 'applied',
        jobUrl: 'https://example.com/job',
        salaryRange: '$80,000 - $120,000',
        location: 'Remote',
        workType: 'Remote',
        notes: 'This is a sample application entry for demonstration purposes.',
        metaData: [
          { key: 'Source', value: 'Company Website' },
          { key: 'Application Method', value: 'Direct Apply' },
        ],
        timeline: [
          {
            id: 1,
            date: '2024-08-25',
            time: '12:00 PM',
            title: 'Application Created',
            description: 'Sample application entry created',
            status: 'applied',
          },
        ],
        aiContent: {
          jobDescription: 'AI analysis will be generated here...',
          coverLetter: 'AI generated cover letter will appear here...',
          resumeEnhancer: 'AI resume enhancement suggestions will appear here...',
          interviewDetails: 'AI interview preparation details will appear here...',
        },
      }

      // Initialize AI content for fallback
      aiContent.value = application.value.aiContent || {
        jobDescription: '',
        coverLetter: '',
        resumeEnhancer: '',
        interviewDetails: '',
      }
    }
  } catch (error) {
    console.error('Failed to load application:', error)
    snackbar.value = {
      show: true,
      message: 'Failed to load application details.',
      color: 'error',
    }
    application.value = null
  } finally {
    loading.value = false
  }
}

// Lifecycle
onMounted(() => {
  loadApplication()
})
</script>

<style scoped>
/* Custom timeline styling */
:deep(.v-timeline-item__body) {
  padding-top: 2px;
}

:deep(.v-timeline-item__opposite) {
  padding-top: 4px;
}
</style>

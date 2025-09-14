import { ref, computed, onMounted, defineComponent } from 'vue'
import { useRoute } from 'vue-router'

// Types
export interface TimelineEvent {
  id: number
  date: string
  time?: string
  title: string
  description: string
  status: string
}

export interface MetaData {
  key: string
  value: string
}

export interface AIContent {
  jobDescription: string
  coverLetter: string
  resumeEnhancer: string
  interviewDetails: string
}

export interface Application {
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

export interface StatusOption {
  title: string
  value: string
}

export interface Snackbar {
  show: boolean
  message: string
  color: string
}


export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ApplicationDetailView',
  setup() {
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
      { title: 'Actions', value: 'actions', width: '100px', sortable: false, align: 'center' as const },
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
          description: `Status updated from ${statusOptions.find((s) => s.value === oldStatus)?.title
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
        application.value.metaData ??= []

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

    // Return all the reactive properties and methods
    return {
      // Reactive data
      application,
      loading,
      saving,
      formValid,
      hasModifications,
      applicationForm,
      activeTab,
      metaDataDialog,
      metaDataForm,
      metaDataFormValid,
      savingMetaData,
      newMetaData,
      aiContent,
      snackbar,

      // Configuration
      rules,
      workTypeOptions,
      statusOptions,
      metaDataHeaders,

      // Computed properties
      applicationId,
      breadcrumbs,

      // Methods
      formatDate,
      getStatusColor,
      updateStatus,
      markAsModified,
      saveApplication,
      uploadResume,
      showRawContent,
      deleteApplication,
      regenerateJobDescription,
      regenerateCoverLetter,
      regenerateResumeEnhancer,
      regenerateInterviewDetails,
      showAddMetaDataDialog,
      saveMetaData,
      cancelAddMetaData,
      removeMetaData,
      loadApplication,
    }
  }
})

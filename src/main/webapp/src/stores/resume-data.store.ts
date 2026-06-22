import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import ResumeDataService from '../services/resume-data.service'
import type {
  IWorkExperience,
  IWorkExperienceBullet,
  IEducation,
  ISkillGroup,
  IResumeAggregate,
  IResumeExtractionResult,
  IResumeProject,
  IResumeCertification,
  IResumeLanguage,
  IVolunteerWork,
  IResumePublication,
  IResumeAward,
  IProfessionalAffiliation,
} from '../models/resume-data.model'
import { useToastStore } from './toast'

export const useResumeDataStore = defineStore('resumeData', () => {
  const service = new ResumeDataService()
  const toast = useToastStore()

  const aggregate = ref<IResumeAggregate>({})
  const loading = ref(false)
  const extracting = ref(false)
  const extractionResult = ref<IResumeExtractionResult | null>(null)

  const workExperiences = computed(() => {
    const list = aggregate.value.workExperiences ?? []
    return [...list].sort((a, b) => {
      const aYear = a.startYear ?? 0
      const bYear = b.startYear ?? 0
      if (bYear !== aYear) return bYear - aYear
      return (b.startMonth ?? 0) - (a.startMonth ?? 0)
    })
  })
  const education = computed(() => aggregate.value.education ?? [])
  const skillGroups = computed(() => aggregate.value.skillGroups ?? [])
  const projects = computed(() => aggregate.value.projects ?? [])
  const certifications = computed(() => aggregate.value.certifications ?? [])
  const languages = computed(() => aggregate.value.languages ?? [])
  const volunteerWork = computed(() => aggregate.value.volunteerWork ?? [])
  const publications = computed(() => aggregate.value.publications ?? [])
  const awards = computed(() => aggregate.value.awards ?? [])
  const affiliations = computed(() => aggregate.value.affiliations ?? [])

  async function fetchAggregate() {
    loading.value = true
    try {
      aggregate.value = await service.getAggregate()
    } catch (err) {
      console.error('Failed to load resume data:', err)
      toast.error('Failed to load resume data. Please try again.')
    } finally {
      loading.value = false
    }
  }

  async function extractFromResume() {
    extracting.value = true
    extractionResult.value = null
    try {
      extractionResult.value = await service.extractFromResume()
      return extractionResult.value
    } catch (err) {
      console.error('Failed to extract resume data:', err)
      toast.error('Failed to extract resume data. Please try again.')
      return null
    } finally {
      extracting.value = false
    }
  }

  async function saveAllExtracted(result: IResumeExtractionResult) {
    let saved = 0
    try {
      if (result.workExperiences) {
        for (const we of result.workExperiences) {
          await service.createWorkExperience(we)
          saved++
        }
      }
      if (result.education) {
        for (const edu of result.education) {
          await service.createEducation(edu)
          saved++
        }
      }
      if (result.skillGroups) {
        for (const sg of result.skillGroups) {
          await service.createSkillGroup(sg)
          saved++
        }
      }
      await fetchAggregate()
      extractionResult.value = null
      toast.success(`Saved ${saved} items from resume extraction.`)
    } catch (err) {
      console.error('Failed to save extracted data:', err)
      toast.error('Failed to save extracted data. Please try again.')
    }
  }

  async function createWorkExperience(dto: IWorkExperience): Promise<IWorkExperience | null> {
    try {
      const saved = await service.createWorkExperience(dto)
      await fetchAggregate()
      toast.success('Work experience added.')
      return saved
    } catch (err) {
      console.error('Failed to create work experience:', err)
      toast.error('Failed to create work experience. Please try again.')
      return null
    }
  }

  async function updateWorkExperience(id: string, dto: IWorkExperience): Promise<IWorkExperience | null> {
    try {
      const updated = await service.updateWorkExperience(id, dto)
      await fetchAggregate()
      toast.success('Work experience updated.')
      return updated
    } catch (err) {
      console.error('Failed to update work experience:', err)
      toast.error('Failed to update work experience. Please try again.')
      return null
    }
  }

  async function deleteWorkExperience(id: string) {
    try {
      await service.deleteWorkExperience(id)
      await fetchAggregate()
      toast.success('Work experience deleted.')
    } catch (err) {
      console.error('Failed to delete work experience:', err)
      toast.error('Failed to delete work experience. Please try again.')
    }
  }

  async function replaceBullets(workExperienceId: string, bullets: IWorkExperienceBullet[]) {
    try {
      await service.replaceBullets(workExperienceId, bullets)
      await fetchAggregate()
      toast.success('Bullets updated.')
    } catch (err) {
      console.error('Failed to update bullets:', err)
      toast.error('Failed to update bullets. Please try again.')
    }
  }

  async function createEducation(dto: IEducation): Promise<IEducation | null> {
    try {
      const saved = await service.createEducation(dto)
      await fetchAggregate()
      toast.success('Education added.')
      return saved
    } catch (err) {
      console.error('Failed to create education:', err)
      toast.error('Failed to create education. Please try again.')
      return null
    }
  }

  async function updateEducation(id: string, dto: IEducation): Promise<IEducation | null> {
    try {
      const updated = await service.updateEducation(id, dto)
      await fetchAggregate()
      toast.success('Education updated.')
      return updated
    } catch (err) {
      console.error('Failed to update education:', err)
      toast.error('Failed to update education. Please try again.')
      return null
    }
  }

  async function deleteEducation(id: string) {
    try {
      await service.deleteEducation(id)
      await fetchAggregate()
      toast.success('Education deleted.')
    } catch (err) {
      console.error('Failed to delete education:', err)
      toast.error('Failed to delete education. Please try again.')
    }
  }

  async function createSkillGroup(dto: ISkillGroup): Promise<ISkillGroup | null> {
    try {
      const saved = await service.createSkillGroup(dto)
      await fetchAggregate()
      toast.success('Skill group added.')
      return saved
    } catch (err) {
      console.error('Failed to create skill group:', err)
      toast.error('Failed to create skill group. Please try again.')
      return null
    }
  }

  async function updateSkillGroup(id: string, dto: ISkillGroup): Promise<ISkillGroup | null> {
    try {
      const updated = await service.updateSkillGroup(id, dto)
      await fetchAggregate()
      toast.success('Skill group updated.')
      return updated
    } catch (err) {
      console.error('Failed to update skill group:', err)
      toast.error('Failed to update skill group. Please try again.')
      return null
    }
  }

  async function deleteSkillGroup(id: string) {
    try {
      await service.deleteSkillGroup(id)
      await fetchAggregate()
      toast.success('Skill group deleted.')
    } catch (err) {
      console.error('Failed to delete skill group:', err)
      toast.error('Failed to delete skill group. Please try again.')
    }
  }

  async function createProject(dto: IResumeProject): Promise<IResumeProject | null> {
    try {
      const saved = await service.createProject(dto)
      await fetchAggregate()
      toast.success('Project added.')
      return saved
    } catch (err) {
      console.error('Failed to create project:', err)
      toast.error('Failed to create project. Please try again.')
      return null
    }
  }

  async function updateProject(id: string, dto: IResumeProject): Promise<IResumeProject | null> {
    try {
      const updated = await service.updateProject(id, dto)
      await fetchAggregate()
      toast.success('Project updated.')
      return updated
    } catch (err) {
      console.error('Failed to update project:', err)
      toast.error('Failed to update project. Please try again.')
      return null
    }
  }

  async function deleteProject(id: string) {
    try {
      await service.deleteProject(id)
      await fetchAggregate()
      toast.success('Project deleted.')
    } catch (err) {
      console.error('Failed to delete project:', err)
      toast.error('Failed to delete project. Please try again.')
    }
  }

  async function createCertification(dto: IResumeCertification): Promise<IResumeCertification | null> {
    try {
      const saved = await service.createCertification(dto)
      await fetchAggregate()
      toast.success('Certification added.')
      return saved
    } catch (err) {
      console.error('Failed to create certification:', err)
      toast.error('Failed to create certification. Please try again.')
      return null
    }
  }

  async function updateCertification(id: string, dto: IResumeCertification): Promise<IResumeCertification | null> {
    try {
      const updated = await service.updateCertification(id, dto)
      await fetchAggregate()
      toast.success('Certification updated.')
      return updated
    } catch (err) {
      console.error('Failed to update certification:', err)
      toast.error('Failed to update certification. Please try again.')
      return null
    }
  }

  async function deleteCertification(id: string) {
    try {
      await service.deleteCertification(id)
      await fetchAggregate()
      toast.success('Certification deleted.')
    } catch (err) {
      console.error('Failed to delete certification:', err)
      toast.error('Failed to delete certification. Please try again.')
    }
  }

  async function createLanguage(dto: IResumeLanguage): Promise<IResumeLanguage | null> {
    try {
      const saved = await service.createLanguage(dto)
      await fetchAggregate()
      toast.success('Language added.')
      return saved
    } catch (err) {
      console.error('Failed to create language:', err)
      toast.error('Failed to create language. Please try again.')
      return null
    }
  }

  async function updateLanguage(id: string, dto: IResumeLanguage): Promise<IResumeLanguage | null> {
    try {
      const updated = await service.updateLanguage(id, dto)
      await fetchAggregate()
      toast.success('Language updated.')
      return updated
    } catch (err) {
      console.error('Failed to update language:', err)
      toast.error('Failed to update language. Please try again.')
      return null
    }
  }

  async function deleteLanguage(id: string) {
    try {
      await service.deleteLanguage(id)
      await fetchAggregate()
      toast.success('Language deleted.')
    } catch (err) {
      console.error('Failed to delete language:', err)
      toast.error('Failed to delete language. Please try again.')
    }
  }

  async function createVolunteerWork(dto: IVolunteerWork): Promise<IVolunteerWork | null> {
    try {
      const saved = await service.createVolunteerWork(dto)
      await fetchAggregate()
      toast.success('Volunteer work added.')
      return saved
    } catch (err) {
      console.error('Failed to create volunteer work:', err)
      toast.error('Failed to create volunteer work. Please try again.')
      return null
    }
  }

  async function updateVolunteerWork(id: string, dto: IVolunteerWork): Promise<IVolunteerWork | null> {
    try {
      const updated = await service.updateVolunteerWork(id, dto)
      await fetchAggregate()
      toast.success('Volunteer work updated.')
      return updated
    } catch (err) {
      console.error('Failed to update volunteer work:', err)
      toast.error('Failed to update volunteer work. Please try again.')
      return null
    }
  }

  async function deleteVolunteerWork(id: string) {
    try {
      await service.deleteVolunteerWork(id)
      await fetchAggregate()
      toast.success('Volunteer work deleted.')
    } catch (err) {
      console.error('Failed to delete volunteer work:', err)
      toast.error('Failed to delete volunteer work. Please try again.')
    }
  }

  async function createPublication(dto: IResumePublication): Promise<IResumePublication | null> {
    try {
      const saved = await service.createPublication(dto)
      await fetchAggregate()
      toast.success('Publication added.')
      return saved
    } catch (err) {
      console.error('Failed to create publication:', err)
      toast.error('Failed to create publication. Please try again.')
      return null
    }
  }

  async function updatePublication(id: string, dto: IResumePublication): Promise<IResumePublication | null> {
    try {
      const updated = await service.updatePublication(id, dto)
      await fetchAggregate()
      toast.success('Publication updated.')
      return updated
    } catch (err) {
      console.error('Failed to update publication:', err)
      toast.error('Failed to update publication. Please try again.')
      return null
    }
  }

  async function deletePublication(id: string) {
    try {
      await service.deletePublication(id)
      await fetchAggregate()
      toast.success('Publication deleted.')
    } catch (err) {
      console.error('Failed to delete publication:', err)
      toast.error('Failed to delete publication. Please try again.')
    }
  }

  async function createAward(dto: IResumeAward): Promise<IResumeAward | null> {
    try {
      const saved = await service.createAward(dto)
      await fetchAggregate()
      toast.success('Award added.')
      return saved
    } catch (err) {
      console.error('Failed to create award:', err)
      toast.error('Failed to create award. Please try again.')
      return null
    }
  }

  async function updateAward(id: string, dto: IResumeAward): Promise<IResumeAward | null> {
    try {
      const updated = await service.updateAward(id, dto)
      await fetchAggregate()
      toast.success('Award updated.')
      return updated
    } catch (err) {
      console.error('Failed to update award:', err)
      toast.error('Failed to update award. Please try again.')
      return null
    }
  }

  async function deleteAward(id: string) {
    try {
      await service.deleteAward(id)
      await fetchAggregate()
      toast.success('Award deleted.')
    } catch (err) {
      console.error('Failed to delete award:', err)
      toast.error('Failed to delete award. Please try again.')
    }
  }

  async function createAffiliation(dto: IProfessionalAffiliation): Promise<IProfessionalAffiliation | null> {
    try {
      const saved = await service.createAffiliation(dto)
      await fetchAggregate()
      toast.success('Affiliation added.')
      return saved
    } catch (err) {
      console.error('Failed to create affiliation:', err)
      toast.error('Failed to create affiliation. Please try again.')
      return null
    }
  }

  async function updateAffiliation(id: string, dto: IProfessionalAffiliation): Promise<IProfessionalAffiliation | null> {
    try {
      const updated = await service.updateAffiliation(id, dto)
      await fetchAggregate()
      toast.success('Affiliation updated.')
      return updated
    } catch (err) {
      console.error('Failed to update affiliation:', err)
      toast.error('Failed to update affiliation. Please try again.')
      return null
    }
  }

  async function deleteAffiliation(id: string) {
    try {
      await service.deleteAffiliation(id)
      await fetchAggregate()
      toast.success('Affiliation deleted.')
    } catch (err) {
      console.error('Failed to delete affiliation:', err)
      toast.error('Failed to delete affiliation. Please try again.')
    }
  }

  return {
    aggregate,
    loading,
    extracting,
    extractionResult,
    workExperiences,
    education,
    skillGroups,
    projects,
    certifications,
    languages,
    volunteerWork,
    publications,
    awards,
    affiliations,
    fetchAggregate,
    extractFromResume,
    saveAllExtracted,
    createWorkExperience,
    updateWorkExperience,
    deleteWorkExperience,
    replaceBullets,
    createEducation,
    updateEducation,
    deleteEducation,
    createSkillGroup,
    updateSkillGroup,
    deleteSkillGroup,
    createProject,
    updateProject,
    deleteProject,
    createCertification,
    updateCertification,
    deleteCertification,
    createLanguage,
    updateLanguage,
    deleteLanguage,
    createVolunteerWork,
    updateVolunteerWork,
    deleteVolunteerWork,
    createPublication,
    updatePublication,
    deletePublication,
    createAward,
    updateAward,
    deleteAward,
    createAffiliation,
    updateAffiliation,
    deleteAffiliation,
  }
})

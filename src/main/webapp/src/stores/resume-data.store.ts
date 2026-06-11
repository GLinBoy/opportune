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
} from '../models/resume-data.model'
import { useToastStore } from './toast'

export const useResumeDataStore = defineStore('resumeData', () => {
  const service = new ResumeDataService()
  const toast = useToastStore()

  const aggregate = ref<IResumeAggregate>({})
  const loading = ref(false)
  const extracting = ref(false)
  const extractionResult = ref<IResumeExtractionResult | null>(null)

  const workExperiences = computed(() => aggregate.value.workExperiences ?? [])
  const education = computed(() => aggregate.value.education ?? [])
  const skillGroups = computed(() => aggregate.value.skillGroups ?? [])

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

  return {
    aggregate,
    loading,
    extracting,
    extractionResult,
    workExperiences,
    education,
    skillGroups,
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
  }
})

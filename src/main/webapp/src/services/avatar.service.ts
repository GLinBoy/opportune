import apiClient from './api'
import CryptoJS from 'crypto-js'

export default class AvatarService {

    /**
     * Upload avatar image file
     * @param profileId - The profile ID
     * @param file - The image file to upload
     * @returns Promise with the avatar file path
     */
    uploadAvatar(profileId: string, file: File): Promise<{ avatarPath: string }> {
        return new Promise<{ avatarPath: string }>((resolve, reject) => {
            const formData = new FormData()
            formData.append('avatar', file)
            formData.append('profileId', profileId)

            apiClient
                .post('/api/profiles/avatar', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                })
                .then(res => { resolve(res.data) })
                .catch((err: unknown) => {
                    reject(err instanceof Error ? err : new Error(String(err)))
                })
        })
    }

    /**
     * Delete current avatar
     * @param profileId - The profile ID
     * @returns Promise<void>
     */
    deleteAvatar(profileId: string): Promise<void> {
        return new Promise<void>((resolve, reject) => {
            apiClient
                .delete(`/api/profiles/${profileId}/avatar`)
                .then(() => { resolve() })
                .catch((err: unknown) => {
                    reject(err instanceof Error ? err : new Error(String(err)))
                })
        })
    }

    /**
     * Get avatar URL for a profile
     * @param avatarPath - The avatar file path from the profile
     * @returns Full URL to the avatar image
     */
    getAvatarUrl(avatarPath: string): string {
        if (avatarPath.startsWith('http')) {
            return avatarPath // External URL (like Gravatar)
        }
        return `/api/files${avatarPath}` // Internal file storage
    }

    /**
     * Generate Gravatar URL based on email
     * @param email - User email
     * @param size - Avatar size (default: 80)
     * @returns Gravatar URL
     */
    getGravatarUrl(email: string, size: number = 80): string {
        const trimmedEmail = email.trim().toLowerCase()
        const hash = CryptoJS.MD5(trimmedEmail).toString()
        return `https://www.gravatar.com/avatar/${hash}?s=${size}&d=mp&r=g`
    }
}

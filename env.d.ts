/// <reference types="vite/client" />

import 'vue-router'

declare module 'vue-router' {
    interface RouteMeta {
        /** Require the user to have ROLE_ADMIN to access this route */
        requiresAdmin?: boolean
        /** Page title shown in the AdminLayout toolbar */
        pageTitle?: string
        /** Breadcrumb items appended after the root "Admin" crumb */
        breadcrumbs?: { title: string; to?: string; disabled?: boolean }[]
        /** Tab key used by ProfileView */
        tab?: string
    }
}

import { createRouter, createWebHistory } from 'vue-router'

import DefaultLayout from '@/layouts/DefaultLayout.vue'
import BlankLayout from '@/layouts/BlankLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

import DashboardView from '@/views/DashboardView.vue'
import ApplicationList from '@/views/application/ApplicationListView.vue';
import ApplicationDetail from '@/views/application/ApplicationDetailView.vue';
import CompanyList from '@/views/company/CompanyListView.vue';
import CompanyDetail from '@/views/company/CompanyDetailView.vue';
import ProfileView from '@/views/profile/ProfileView.vue';
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import LoginView from '@/views/auth/LoginView.vue';
import RegisterView from '@/views/auth/RegisterView.vue';
import ForgotPasswordView from '@/views/auth/ForgotPasswordView.vue';
import ResetPasswordView from '@/views/auth/ResetPasswordView.vue';
import UnauthorizedView from '@/views/auth/UnauthorizedView.vue';
import EmailConfirmationView from '@/views/auth/EmailConfirmationView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/auth',
      component: BlankLayout,
      children: [
        {
          path: 'login',
          name: 'login',
          component: LoginView,
        },
        {
          path: 'register',
          name: 'register',
          component: RegisterView,
        },
        {
          path: 'forgot-password',
          name: 'forgot-password',
          component: ForgotPasswordView,
        },
        {
          path: 'unauthorized',
          name: 'unauthorized',
          component: UnauthorizedView,
        },
      ],
    },
    {
      path: '/email-validation',
      name: 'email-confirmation',
      component: BlankLayout,
      children: [
        {
          path: '',
          name: 'email-confirmation-page',
          component: EmailConfirmationView,
        },
      ],
    },
    {
      path: '/password-reset',
      name: 'password-reset',
      component: BlankLayout,
      children: [
        {
          path: '',
          name: 'password-reset-page',
          component: ResetPasswordView,
        },
      ],
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DefaultLayout,
      children: [
        {
          path: '',
          name: 'dashboard-home',
          component: DashboardView,
        },
      ],
    },
    {
      path: '/applications',
      name: 'applications',
      component: DefaultLayout,
      children: [
        {
          path: '',
          name: 'applications-list',
          component: ApplicationList,
        },
        {
          path: ':id',
          name: 'applications-detail',
          component: ApplicationDetail,
        },
      ],
    },
    {
      path: '/companies',
      name: 'companies',
      component: DefaultLayout,
      children: [
        {
          path: '',
          name: 'companies-list',
          component: CompanyList,
        },
        {
          path: ':id',
          name: 'companies-detail',
          component: CompanyDetail,
        },
      ],
    },
    {
      path: '/profile',
      name: 'profile',
      component: DefaultLayout,
      children: [
        {
          path: '',
          name: 'profile-settings',
          component: ProfileView,
          meta: { tab: 'info' }
        },
        {
          path: 'security',
          name: 'profile-security',
          component: ProfileView,
          meta: { tab: 'password' }
        },
        {
          path: 'sessions',
          name: 'profile-sessions',
          component: ProfileView,
          meta: { tab: 'sessions' }
        },
        {
          path: 'sessions/:sessionId',
          name: 'profile-session-detail',
          component: ProfileView,
          meta: { tab: 'sessions' }
        },
        {
          path: 'api',
          name: 'profile-api',
          component: ProfileView,
          meta: { tab: 'api' }
        },
      ],
    },
    {
      path: '/404',
      name: 'not-found',
      component: BlankLayout,
      children: [
        {
          path: '',
          name: 'not-found-page',
          component: NotFoundView,
        },
      ],
    },
    {
      path: '/admin',
      redirect: '/admin/dashboard',
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAdmin: true },
      children: [
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: AdminDashboardView,
          meta: {
            requiresAdmin: true,
            pageTitle: 'Dashboard',
            breadcrumbs: [{ title: 'Dashboard', disabled: true }],
          },
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/UsersView.vue'),
          meta: {
            requiresAdmin: true,
            pageTitle: 'User Management',
            breadcrumbs: [{ title: 'Users', disabled: true }],
          },
        },
        {
          path: 'users/:id',
          name: 'admin-user-detail',
          component: () => import('@/views/admin/UserDetailView.vue'),
          meta: {
            requiresAdmin: true,
            pageTitle: 'User Detail',
            breadcrumbs: [
              { title: 'Users', to: '/admin/users' },
              { title: 'Detail', disabled: true },
            ],
          },
        },
        {
          path: 'companies',
          name: 'admin-companies',
          component: () => import('@/views/admin/CompaniesView.vue'),
          meta: {
            requiresAdmin: true,
            pageTitle: 'Company Management',
            breadcrumbs: [{ title: 'Companies', disabled: true }],
          },
        },
        {
          path: 'security',
          name: 'admin-security',
          component: () => import('@/views/admin/SecurityView.vue'),
          meta: {
            requiresAdmin: true,
            pageTitle: 'Security Center',
            breadcrumbs: [{ title: 'Security', disabled: true }],
          },
        },
        {
          path: 'ai-monitor',
          name: 'admin-ai-monitor',
          component: () => import('@/views/admin/AiMonitorView.vue'),
          meta: {
            requiresAdmin: true,
            pageTitle: 'AI Monitor',
            breadcrumbs: [{ title: 'AI Monitor', disabled: true }],
          },
        },
        {
          path: 'settings',
          name: 'admin-settings',
          component: () => import('@/views/admin/SettingsView.vue'),
          meta: {
            requiresAdmin: true,
            pageTitle: 'Settings',
            breadcrumbs: [{ title: 'Settings', disabled: true }],
          },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404',
    },
  ],
})

// Navigation guard to protect routes
async function checkAdminRole(): Promise<boolean> {
  const { useAuthStore } = await import('../stores/auth.store')
  return useAuthStore().isAdmin
}

router.beforeEach(async (to, from) => {
  const accessToken = localStorage.getItem('accessToken')
  const expiresAt = localStorage.getItem('expiresAt')

  // Check if token exists and is not expired
  let isAuthenticated = accessToken && expiresAt && Date.now() < Number(expiresAt)

  // Public routes that don't require authentication
  const publicRoutes = ['login', 'register', 'forgot-password', 'password-reset', 'password-reset-page', 'not-found', 'email-confirmation', 'email-confirmation-page']

  // Check if the route requires authentication
  const requiresAuth = !publicRoutes.includes(to.name as string)

  // If access token expired but refresh token is still valid, try refreshing
  if (requiresAuth && !isAuthenticated) {
    const refreshToken = localStorage.getItem('refreshToken')
    const refreshExpiresAt = localStorage.getItem('refreshExpiresAt')
    const canRefresh = refreshToken && refreshExpiresAt && Date.now() < Number(refreshExpiresAt)

    if (canRefresh) {
      try {
        const { useAuthStore } = await import('../stores/auth.store')
        const authStore = useAuthStore()
        await authStore.refreshAccessToken()
        isAuthenticated = true
      } catch {
        isAuthenticated = false
      }
    }
  }

  if (requiresAuth && !isAuthenticated) {
    // Redirect to login if not authenticated
    return { name: 'login', query: { redirect: to.fullPath } }
  } else if (to.name === 'login' && isAuthenticated) {
    // Redirect to dashboard if already authenticated and trying to access login
    return { name: 'dashboard-home' }
  }

  // Admin route guard — check ROLE_ADMIN
  if (to.meta?.requiresAdmin && !(await checkAdminRole())) {
    return { name: 'unauthorized' }
  }
})

export default router

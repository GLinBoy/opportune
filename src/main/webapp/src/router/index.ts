import { createRouter, createWebHistory } from 'vue-router'

import DefaultLayout from '@/layouts/DefaultLayout.vue'
import BlankLayout from '@/layouts/BlankLayout.vue'

import DashboardView from '@/views/DashboardView.vue'
import ApplicationList from '@/views/application/ApplicationListView.vue';
import ApplicationDetail from '@/views/application/ApplicationDetailView.vue';
import CompanyList from '@/views/company/CompanyListView.vue';
import CompanyDetail from '@/views/company/CompanyDetailView.vue';
import ProfileView from '@/views/profile/ProfileView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import LoginView from '@/views/auth/LoginView.vue';
import RegisterView from '@/views/auth/RegisterView.vue';
import ForgotPasswordView from '@/views/auth/ForgotPasswordView.vue';
import UnauthorizedView from '@/views/auth/UnauthorizedView.vue';

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
      path: '/:pathMatch(.*)*',
      redirect: '/404',
    },
  ],
})

// Navigation guard to protect routes
router.beforeEach((to, from, next) => {
  const accessToken = localStorage.getItem('accessToken')
  const expiresAt = localStorage.getItem('expiresAt')

  // Check if token exists and is not expired
  const isAuthenticated = accessToken && expiresAt && Date.now() < Number(expiresAt)

  // Public routes that don't require authentication
  const publicRoutes = ['login', 'register', 'forgot-password', 'not-found']

  // Check if the route requires authentication
  const requiresAuth = !publicRoutes.includes(to.name as string)

  if (requiresAuth && !isAuthenticated) {
    // Redirect to login if not authenticated
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.name === 'login' && isAuthenticated) {
    // Redirect to dashboard if already authenticated and trying to access login
    next({ name: 'dashboard-home' })
  } else {
    next()
  }
})

export default router

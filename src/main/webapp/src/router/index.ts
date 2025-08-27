import { createRouter, createWebHistory } from 'vue-router'

import DefaultLayout from '@/layouts/DefaultLayout.vue'
import BlankLayout from '@/layouts/BlankLayout.vue'

import DashboardView from '@/views/DashboardView.vue'
import ApplicationList from '@/views/application/ApplicationListView.vue';
import ApplicationDetail from '@/views/application/ApplicationDetailView.vue';
import CompanyList from '@/views/company/CompanyListView.vue';
import CompanyDetail from '@/views/company/CompanyDetailView.vue';
import NotFoundView from '@/views/NotFoundView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
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

export default router

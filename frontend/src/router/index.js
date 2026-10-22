import { createRouter, createWebHistory } from 'vue-router'

export default createRouter({
  history: createWebHistory('/'),
  routes: [
    // 🏠 홈
    { path: '/', component: () => import('../pages/Home.vue'),
      meta: { section: 'home' }
    },

    // 🌱 plant
    {
      path: '/plants',
      component: () => import('../pages/plant/PlantList.vue'),
      meta: { section: 'plant' }
    },
    {
      path: '/plants/add',
      component: () => import('../pages/plant/PlantAdd.vue'),
      meta: { section: 'plant' }
    },
    {
      path: '/plants/:id',
      component: () => import('../pages/plant/PlantDetail.vue'),
      meta: { section: 'plant' }
    },
    {
      path: '/plants/edit/:id',
      component: () => import('../pages/plant/PlantEdit.vue'),
      meta: { section: 'plant' }
    },

    // 💰 account
    {
      path: '/account',
      component: () => import('../pages/account/AccountBook.vue'),
      meta: { section: 'account' }
    },
    // {
    //   path: '/account/add',
    //   component: () => import('../pages/account/AccountAdd.vue'),
    //   meta: { section: 'account' }
    // }

    {
      path: '/:pathMatch(.*)*',
      component: () => import('../pages/NotFound.vue'),
      meta: { section: 'home' }
    }
  ]
})

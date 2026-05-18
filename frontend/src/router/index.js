import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    name: 'Landing',
    component: () => import('../views/public/Landing.vue'),
  },
  {
    path: '/productos',
    name: 'Productos',
    component: () => import('../views/public/Productos.vue'),
  },
  {
    path: '/bodegas',
    name: 'Bodegas',
    component: () => import('../views/public/Bodegas.vue'),
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
  },
  {
    path: '/admin',
    component: () => import('../components/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/admin/Dashboard.vue'),
      },
      {
        path: 'productos',
        name: 'AdminProductos',
        component: () => import('../views/admin/ProductList.vue'),
      },
      {
        path: 'productos/nuevo',
        name: 'ProductoNuevo',
        component: () => import('../views/admin/ProductForm.vue'),
      },
      {
        path: 'productos/editar/:id',
        name: 'ProductoEditar',
        component: () => import('../views/admin/ProductForm.vue'),
      },
      {
        path: 'bodegas',
        name: 'AdminBodegas',
        component: () => import('../views/admin/WarehouseList.vue'),
      },
      {
        path: 'bodegas/nuevo',
        name: 'BodegaNueva',
        component: () => import('../views/admin/WarehouseForm.vue'),
      },
      {
        path: 'bodegas/editar/:id',
        name: 'BodegaEditar',
        component: () => import('../views/admin/WarehouseForm.vue'),
      },
      {
        path: 'stock',
        name: 'AdminStock',
        component: () => import('../views/admin/StockList.vue'),
      },
      {
        path: 'stock/nuevo',
        name: 'StockNuevo',
        component: () => import('../views/admin/StockForm.vue'),
      },
      {
        path: 'stock/editar/:id',
        name: 'StockEditar',
        component: () => import('../views/admin/StockForm.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && auth.isAuthenticated) {
    next('/admin')
  } else {
    next()
  }
})

export default router

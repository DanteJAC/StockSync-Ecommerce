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
    meta: { requiresAuth: true },
  },
  {
    path: '/bodegas',
    name: 'Bodegas',
    component: () => import('../views/public/Bodegas.vue'),
    meta: { requiresAuth: true },
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
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/auth/ForgotPassword.vue'),
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/auth/ResetPassword.vue'),
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    component: () => import('../views/auth/ChangePassword.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/local',
    component: () => import('../components/LocalLayout.vue'),
    meta: { requiresAuth: true },
   //
    children: [
      {
        path: '',
        name: 'LocalDashboard',
        component: () => import('../views/local/localDashboard.vue'),
      },
      {
        path: 'ventas',
        component: () => import('../views/local/localVentas.vue'),
      },
      {
        path: 'inventario',
        component: () => import('../views/local/localInventario.vue'),
      },
      {
        path: 'solicitudReposicion',
        component: () => import('../views/local/localSolicitud.vue'),
      },
      {
        path: 'seguimiento',
        component: () => import('../views/local/localSeguimiento.vue'),
      },
      {
        path: 'recepcion',
        component: () => import('../views/local/localRecepcion.vue'),
      }
    ]
  },
  {
    path: '/bodega',
    component: () => import('../components/BodegaLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'BodegaDashboard',
        component: () => import('../views/bodega/bodegaDashboard.vue'),
      },
      {
        path: 'solicitudes',
        component: () => import('../views/bodega/bodegaSolicitudes.vue'),
      },
      {
        path: 'inventario',
        component: () => import('../views/bodega/bodegaInventario.vue'),
      },
      {
        path: 'despacho',
        component: () => import('../views/bodega/bodegaDespacho.vue'),
      },
      {
        path: 'historial',
        component: () => import('../views/bodega/bodegaHistorial.vue'),
      }
    ]
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
      {
        path: 'usuarios',
        name: 'AdminUsuarios',
        component: () => import('../views/admin/UserManagement.vue'),
      },
      {
        path: 'solicitudes',
        name: 'WarehouseRequestList',
        component: () => import('../views/admin/WarehouseRequestList.vue'),
      },
      {
        path: 'recepcion',
        name: 'ProductReception',
        component: () => import('../views/admin/ProductReception.vue'),
      },
      {
        path: 'visores',
        name: 'VistasDashboard',
        component: () => import('../views/admin/VistasDashboard.vue'),
      },
      {
        path: 'visores/local',
        name: 'WarehouseViewerLocal',
        component: () => import('../views/admin/WarehouseViewer.vue'),
      },
      {
        path: 'visores/bodega',
        name: 'WarehouseViewerBodega',
        component: () => import('../views/admin/WarehouseViewer.vue'),
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
    return
  }
  
  if (to.path === '/login' && auth.isAuthenticated) {
    if (auth.isAdmin) next('/admin')
    else if (auth.isBodega) next('/bodega')
    else if (auth.isLocal) next('/local')
    else next('/')
    return
  }
  
  if (auth.isAuthenticated && auth.mustChangePassword && to.name !== 'ChangePassword') {
    next('/change-password')
    return
  }

  if (to.path.startsWith('/admin') && !auth.isAdmin) {
    if (auth.isBodega) next('/bodega')
    else if (auth.isLocal) next('/local')
    else next('/')
    return
  }

  if (to.path.startsWith('/bodega') && !auth.isAdmin && !auth.isBodega) {
    if (auth.isLocal) next('/local')
    else if (auth.isAdmin) next('/admin')
    else next('/')
    return
  }

  if (to.path.startsWith('/local') && !auth.isAdmin && !auth.isLocal) {
    if (auth.isBodega) next('/bodega')
    else if (auth.isAdmin) next('/admin')
    else next('/')
    return
  }

  next()
})

export default router
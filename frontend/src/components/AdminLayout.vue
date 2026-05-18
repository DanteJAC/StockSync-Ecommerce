<template>
  <v-layout>
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail && !mobile"
      :permanent="mdAndUp"
      :temporary="smAndDown"
      app
      width="260"
      :rail-width="64"
    >
      <template v-slot:prepend>
        <v-list-item
          class="px-4 pt-4 pb-2"
          :title="auth.userName"
          :subtitle="auth.userEmail"
          lines="two"
        >
          <template v-slot:prepend>
            <v-avatar color="primary" size="40" class="mr-3">
              <span class="text-white font-weight-bold">{{ initials }}</span>
            </v-avatar>
          </template>
        </v-list-item>
        <v-divider class="mx-4 mb-2" />
      </template>

      <v-list density="compact" nav>
        <v-list-item
          v-for="item in menuItems"
          :key="item.to"
          :to="item.to"
          :title="item.title"
          :prepend-icon="item.icon"
          :active="route.path === item.to"
          color="primary"
          rounded="lg"
          class="mx-2 my-1"
        />
      </v-list>

      <template v-slot:append>
        <v-list-item
          prepend-icon="mdi-theme-light-dark"
          title="Tema"
          class="mx-2 my-1"
          rounded="lg"
          @click="toggleTheme"
        />
        <v-list-item
          prepend-icon="mdi-logout"
          title="Cerrar Sesión"
          class="mx-2 my-1 mb-2"
          rounded="lg"
          @click="handleLogout"
        />

      </template>
    </v-navigation-drawer>

    <v-app-bar flat>
      <v-app-bar-nav-icon @click="toggleDrawer" />
      <v-app-bar-title>{{ pageTitle }}</v-app-bar-title>
      <v-spacer />
      <v-btn
        :icon="isDark ? 'mdi-white-balance-sunny' : 'mdi-weather-night'"
        variant="text"
        @click="toggleTheme"
      />
      <v-btn
        icon="mdi-logout"
        variant="text"
        @click="handleLogout"
      />
    </v-app-bar>

    <v-main>
      <v-container fluid class="pa-6">
        <router-view />
      </v-container>
    </v-main>
  </v-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme, useDisplay } from 'vuetify'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const theme = useTheme()
const auth = useAuthStore()
const { mobile, mdAndUp, smAndDown } = useDisplay()

const drawer = ref(true)
const rail = ref(false)
const isDark = computed(() => theme.global.name.value === 'dark')

const initials = computed(() => {
  const name = auth.userName || 'A'
  return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase()
})

const menuItems = [
  { title: 'Dashboard', icon: 'mdi-view-dashboard', to: '/admin' },
  { title: 'Productos', icon: 'mdi-package-variant-closed', to: '/admin/productos' },
  { title: 'Bodegas', icon: 'mdi-warehouse', to: '/admin/bodegas' },
  { title: 'Stock', icon: 'mdi-chart-box', to: '/admin/stock' },
]

const pageTitle = computed(() => {
  const map = {
    'Dashboard': 'Dashboard',
    'AdminProductos': 'Productos',
    'ProductoNuevo': 'Nuevo Producto',
    'ProductoEditar': 'Editar Producto',
    'AdminBodegas': 'Bodegas',
    'BodegaNueva': 'Nueva Bodega',
    'BodegaEditar': 'Editar Bodega',
    'AdminStock': 'Stock',
    'StockNuevo': 'Nuevo Stock',
    'StockEditar': 'Editar Stock',
  }
  return map[route.name] || 'Admin'
})

function toggleDrawer() {
  if (mobile.value) {
    drawer.value = !drawer.value
  } else {
    rail.value = !rail.value
  }
}

function toggleTheme() {
  const name = isDark.value ? 'light' : 'dark'
  theme.global.name.value = name
  localStorage.setItem('theme', name)
}

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>


<template>
  <v-app>
    <v-app-bar class="d-md-none" color="surface" elevation="1">
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>StockSync</v-app-bar-title>
    </v-app-bar>

    <v-navigation-drawer
        v-model="drawer"
        :permanent="!mobile"
        :temporary="mobile"
        width="250"
        color="surface"
        app
    >
      <div class="pa-4 d-flex align-center">
        <v-avatar color="primary">
          <span>{{ inicial }}</span>
        </v-avatar>

        <div class="ml-3">
          <div class="font-weight-bold">{{ nombre }}</div>
          <div class="text-caption">{{ email }}</div>
        </div>
      </div>

      <v-divider />

      <v-list nav>
        <v-list-item
            prepend-icon="mdi-view-dashboard"
            title="Dashboard"
            to="/local"
            exact
        />

        <v-list-item
            prepend-icon="mdi-point-of-sale"
            title="Ventas"
            to="/local/ventas"
        />

        <v-list-item
            prepend-icon="mdi-package-variant"
            title="Inventario"
            to="/local/inventario"
        />

        <v-list-item
            prepend-icon="mdi-file-document-plus"
            title="Solicitar Reposición"
            to="/local/solicitudReposicion"
        />

        <v-list-item
            prepend-icon="mdi-truck-delivery"
            title="Seguimiento"
            to="/local/seguimiento"
        />

        <v-list-item
            prepend-icon="mdi-clipboard-check"
            title="Recepciones"
            to="/local/recepcion"
        />
      </v-list>

      <template #append>
        <div class="pa-4">
          <v-btn
              v-if="auth.isAdmin"
              @click="volverAdmin"
              block
              prepend-icon="mdi-arrow-left"
              color="info"
              class="mb-4"
          >
            Volver al Admin
          </v-btn>
          <v-btn
              @click="handleLogout"
              block
              prepend-icon="mdi-logout"
              color="error"
          >
            Cerrar sesión
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useDisplay } from 'vuetify'

const auth = useAuthStore()
const router = useRouter()
const { mobile } = useDisplay()

const drawer = ref(!mobile.value)

const nombre = computed(() => auth.userName || 'Usuario Local')
const email = computed(() => auth.userEmail || 'local@stocksync.cl')

const inicial = computed(() => nombre.value.charAt(0).toUpperCase())

function handleLogout() {
  auth.logout()
  router.push('/login')
}

function volverAdmin() {
  auth.setAdminViewWarehouseId(null)
  router.push('/admin')
}
</script>

<style scoped>
</style>
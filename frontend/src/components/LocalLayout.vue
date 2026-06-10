
<template>
  <v-app>
    <v-app-bar class="d-md-none" color="surface" elevation="1">
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>StockSync</v-app-bar-title>
    </v-app-bar>

    <v-navigation-drawer
        v-model="drawer"
        width="250"
        color="surface"
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
              to="/login"
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
import { ref } from 'vue' // <-- Añadimos ref para la reactividad

// Variable inteligente que controla el menú.
// En 'null', Vuetify decide automáticamente si mostrarlo (en PC) u ocultarlo (en celular)
const drawer = ref(null)

const nombre = 'Usuario Local'
const email = 'local@stocksync.cl'

const inicial = nombre.charAt(0)
</script>

<style scoped>
</style>
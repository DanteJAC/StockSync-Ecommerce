
<template>
  <v-app>
    <v-navigation-drawer
        permanent
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
        <v-divider class="my-2" />

        <v-list-item
            prepend-icon="mdi-view-dashboard"
            title="Dashboard"
            to="/bodega"
        />

        <v-list-item
            prepend-icon="mdi-clipboard-list"
            title="Solicitudes"
            to="/bodega/solicitudes"
        />

        <v-list-item
            prepend-icon="mdi-package-variant"
            title="Inventario"
            to="/bodega/inventario"
        />

        <v-list-item
            prepend-icon="mdi-truck-delivery"
            title="Despacho"
            to="/bodega/despacho"
        />

        <v-list-item
            prepend-icon="mdi-history"
            title="Historial"
            to="/bodega/historial"
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
const nombre = 'Usuario bodega'
const email = 'bodega@stocksync.cl'

const inicial = nombre.charAt(0)
</script>


<style scoped>

</style>
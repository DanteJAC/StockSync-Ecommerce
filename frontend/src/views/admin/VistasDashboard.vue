<template>
  <v-container fluid class="pa-0">
    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Visores del Sistema
      </h1>
      <p class="text-medium-emphasis">
        Selecciona el tipo de sucursal a la que deseas acceder. Podrás gestionar operaciones como si fueses el administrador de dicho lugar.
      </p>
    </div>

    <v-row class="mx-0">
      <v-col cols="12" sm="6">
        <v-card
          class="vistas-card"
          elevation="4"
          @click="router.push('/admin/visores/local')"
        >
          <v-card-text class="text-center pa-10">
            <v-icon
              size="100"
              color="primary"
              class="mb-4"
            >
              mdi-store
            </v-icon>
            <h2 class="text-h5 font-weight-bold">Vista Local</h2>
            <p class="text-medium-emphasis mt-2">
              Ingresa al sistema como si fueses un empleado de un Local comercial. Podrás gestionar ventas, inventarios y pedir reposiciones.
            </p>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6">
        <v-card
          class="vistas-card"
          elevation="4"
          @click="router.push('/admin/visores/bodega')"
        >
          <v-card-text class="text-center pa-10">
            <v-icon
              size="100"
              color="info"
              class="mb-4"
            >
              mdi-warehouse
            </v-icon>
            <h2 class="text-h5 font-weight-bold">Vista Bodega</h2>
            <p class="text-medium-emphasis mt-2">
              Ingresa al sistema como encargado de Bodega. Podrás aprobar o rechazar solicitudes de locales, ver inventarios y despachar productos.
            </p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.vistas-card {
  transition: all 0.3s ease;
  cursor: pointer;
  border-radius: 16px;
  height: 100%;
}

.vistas-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.15) !important;
}
</style>

<template>
  <v-container fluid class="pa-6">

    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Recepciones y Observaciones
      </h1>

      <p class="text-medium-emphasis">
        Revisa las confirmaciones realizadas por los locales y las observaciones reportadas.
      </p>
    </div>

    <!-- Resumen -->
    <v-row class="mx-0 mb-6">

      <v-col cols="12" sm="4">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="success"
          >
            mdi-check-circle
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Recepciones Correctas
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ correctasCount }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="4">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="warning"
          >
            mdi-alert-circle
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Con Observaciones
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ observacionesCount }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="4">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="info"
          >
            mdi-truck-delivery
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Total Entregas
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ totalEntregasCount }}
          </div>
        </v-card>
      </v-col>

    </v-row>

    <!-- Tabla -->
    <v-card elevation="4">

      <v-card-title class="text-wrap">
        Historial de Recepciones
      </v-card-title>

      <v-divider />

      <div class="table-wrapper">

        <v-table class="text-no-wrap">

          <thead>
          <tr>
            <th>Despacho</th>
            <th>Local</th>
            <th>Fecha</th>
            <th>Estado</th>
            <th>Observación</th>
          </tr>
          </thead>

          <tbody>

          <tr
              v-for="item in recepciones"
              :key="item.id"
          >
            <td>{{ item.id }}</td>

            <td>{{ item.destinationWarehouseName }}</td>

            <td>{{ new Date(item.updatedAt || item.createdAt).toLocaleDateString() }}</td>

            <td>
              <v-chip
                  :color="'success'"
                  variant="tonal"
              >
                Recibido
              </v-chip>
            </td>

            <td>
              <span class="text-success">
                Sin observaciones
              </span>
            </td>

          </tr>

          </tbody>

        </v-table>

      </div>

    </v-card>

  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStockRequestsStore } from '../../stores/stockRequests'
import { useAuthStore } from '../../stores/auth'

const store = useStockRequestsStore()
const authStore = useAuthStore()
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await store.fetchIncoming(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})

const recepciones = computed(() => {
  return store.incomingRequests.filter(r => r.status === 'RECIBIDO')
})

const correctasCount = computed(() => recepciones.value.length)
const observacionesCount = computed(() => 0) // El backend actual no soporta observaciones en StockRequest
const totalEntregasCount = computed(() => correctasCount.value + observacionesCount.value)

</script>

<style scoped>

.table-wrapper {
  overflow-x: auto;
}

.v-card {
  border-radius: 16px;
}

</style>
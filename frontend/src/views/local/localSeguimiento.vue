<template>
  <v-container>
    <h1 class="text-h4 font-weight-bold mb-6">
      Seguimiento de Solicitudes
    </h1>

    <v-card>
      <v-card-text>
        <v-table>
          <thead>
          <tr>
            <th>ID</th>
            <th>Producto</th>
            <th>Cantidad</th>
            <th>Fecha</th>
            <th>Estado</th>
          </tr>
          </thead>

          <tbody>
          <tr
              v-for="solicitud in solicitudes"
              :key="solicitud.id"
          >
            <td>#{{ solicitud.id }}</td>
            <td>{{ solicitud.productName }}</td>
            <td>{{ solicitud.quantity }}</td>
            <td>{{ new Date(solicitud.createdAt).toLocaleDateString() }}</td>

            <td>
              <v-chip
                  :color="estadoColor(solicitud.status)"
              >
                {{ solicitud.status }}
              </v-chip>
            </td>
          </tr>
          <tr v-if="loading">
            <td colspan="5" class="text-center">Cargando...</td>
          </tr>
          <tr v-else-if="solicitudes.length === 0">
            <td colspan="5" class="text-center">No hay solicitudes</td>
          </tr>
          </tbody>
        </v-table>
      </v-card-text>
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
      await store.fetchOutgoing(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})

const solicitudes = computed(() => {
  return [...store.outgoingRequests]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

function estadoColor(estado) {
  switch (estado) {
    case 'PENDIENTE':
      return 'warning'
    case 'APROBADO':
      return 'info'
    case 'ENVIADO':
      return 'primary'
    case 'RECIBIDO':
      return 'success'
    case 'RECHAZADO':
      return 'error'
    default:
      return 'grey'
  }
}
</script>
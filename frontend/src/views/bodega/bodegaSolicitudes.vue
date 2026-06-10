<template>
  <v-container fluid class="pa-6">

    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Solicitudes de Reposición
      </h1>

      <p class="text-medium-emphasis">
        Revisa y aprueba las solicitudes enviadas por los locales.
      </p>
    </div>

    <v-row class="mx-0">

      <v-col
          v-for="solicitud in solicitudes"
          :key="solicitud.id"
          cols="12"
          md="6"
          lg="4"
      >
        <v-card
            class="solicitud-card"
            elevation="4"
        >

          <v-card-title class="d-flex align-center">

            <v-icon
                color="primary"
                class="mr-2"
            >
              mdi-clipboard-list
            </v-icon>

            Solicitud #{{ solicitud.id }}

            <v-spacer />

            <v-chip
                color="warning"
                variant="tonal"
            >
              Pendiente
            </v-chip>

          </v-card-title>

          <v-divider />

          <v-card-text>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-store
              </v-icon>

              <strong>Local:</strong>
              {{ solicitud.destinationWarehouseName }}
            </div>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-package-variant
              </v-icon>

              <strong>Producto:</strong>
              {{ solicitud.productName }}
            </div>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-counter
              </v-icon>

              <strong>Cantidad:</strong>
              {{ solicitud.quantity }}
            </div>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-calendar
              </v-icon>

              <strong>Fecha:</strong>
              {{ new Date(solicitud.createdAt).toLocaleDateString() }}
            </div>

          </v-card-text>

          <v-card-actions class="pa-4">

            <v-btn
                color="success"
                prepend-icon="mdi-check"
                @click="aprobar(solicitud)"
            >
              Aprobar
            </v-btn>

            <v-btn
                color="error"
                variant="outlined"
                prepend-icon="mdi-close"
                @click="rechazar(solicitud)"
            >
              Rechazar
            </v-btn>

          </v-card-actions>

        </v-card>
      </v-col>

    </v-row>

  </v-container>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue'
import { useStockRequestsStore } from '../../stores/stockRequests'
import { useAuthStore } from '../../stores/auth'

const store = useStockRequestsStore()
const authStore = useAuthStore()

const loading = ref(false)
const solicitudes = ref([])

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await store.fetchIncoming(authStore.assignedWarehouseId)
      solicitudes.value = store.incomingRequests.filter(r => r.status === 'PENDIENTE')
    }
  } finally {
    loading.value = false
  }
})

async function aprobar(solicitud) {
  try {
    await store.updateStatus(solicitud.id, 'APROBADO')
    solicitudes.value = solicitudes.value.filter(s => s.id !== solicitud.id)
  } catch(e) {
    alert('Error al aprobar solicitud')
  }
}

async function rechazar(solicitud) {
  try {
    await store.updateStatus(solicitud.id, 'RECHAZADO')
    solicitudes.value = solicitudes.value.filter(s => s.id !== solicitud.id)
  } catch(e) {
    alert('Error al rechazar solicitud')
  }
}

</script>

<style scoped>

.solicitud-card {
  transition: all .25s ease;
  border-radius: 18px;
}

.solicitud-card:hover {
  transform: translateY(-4px);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

@media (max-width: 600px) {

  .v-card-actions {
    flex-direction: column;
    gap: 10px;
  }

  .v-card-actions .v-btn {
    width: 100%;
  }

}

</style>
<template>
  <v-container fluid class="dashboard-container">

    <!-- Header -->
    <v-card
        class="header-card mb-6"
        elevation="0"
    >
      <div>
        <h1 class="dashboard-title">
          Dashboard de Bodega
        </h1>

        <p class="text-medium-emphasis mt-2">
          Gestiona solicitudes, inventario y despachos desde un solo lugar.
        </p>
      </div>
    </v-card>

    <!-- Cards -->
    <v-row class="mx-0">

      <v-col cols="12" sm="6" md="3">
        <v-card class="dashboard-card text-center">
          <v-card-text>
            <v-icon
                size="60"
                color="primary"
                class="mb-3"
            >
              mdi-package-variant
            </v-icon>

            <div class="text-subtitle-1">
              Total Stock
            </div>

            <div class="card-number text-primary">
              {{ totalStock }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="dashboard-card text-center">
          <v-card-text>
            <v-icon
                size="60"
                color="success"
                class="mb-3"
            >
              mdi-store
            </v-icon>

            <div class="text-subtitle-1">
              Locales Activos
            </div>

            <div class="card-number text-success">
              {{ localesActivos }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="dashboard-card text-center">
          <v-card-text>
            <v-icon
                size="60"
                color="warning"
                class="mb-3"
            >
              mdi-clipboard-list
            </v-icon>

            <div class="text-subtitle-1">
              Solicitudes Pendientes
            </div>

            <div class="card-number text-warning">
              {{ solicitudesPendientes }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="dashboard-card text-center">
          <v-card-text>
            <v-icon
                size="60"
                color="info"
                class="mb-3"
            >
              mdi-truck-delivery
            </v-icon>

            <div class="text-subtitle-1">
              Despachos Hoy
            </div>

            <div class="card-number text-info">
              {{ despachosHoy }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>

    </v-row>

    <!-- Tabla -->
    <v-card
        class="mt-8 solicitudes-card"
        elevation="4"
    >
      <v-card-title class="d-flex align-center">
        <v-icon
            color="primary"
            class="mr-2"
        >
          mdi-clipboard-text-clock
        </v-icon>

        Últimas Solicitudes
      </v-card-title>

      <v-divider />

      <div class="table-wrapper">
        <v-table class="text-no-wrap">
          <thead>
          <tr>
            <th>ID Solicitud</th>
            <th>Local</th>
            <th>Fecha</th>
            <th>Estado</th>
          </tr>
          </thead>

          <tbody>
          <tr
              v-for="solicitud in solicitudes"
              :key="solicitud.id"
          >
            <td class="font-weight-bold">
              {{ solicitud.id }}
            </td>

            <td>{{ solicitud.destinationWarehouseName }}</td>

            <td>{{ new Date(solicitud.createdAt).toLocaleDateString() }}</td>

            <td>
              <v-chip
                  :color="getEstadoColor(solicitud.status)"
                  variant="tonal"
                  size="small"
              >
                {{ solicitud.status }}
              </v-chip>
            </td>
          </tr>
          <tr v-if="loading">
            <td colspan="4" class="text-center">Cargando...</td>
          </tr>
          <tr v-else-if="solicitudes.length === 0">
            <td colspan="4" class="text-center">No hay solicitudes recientes</td>
          </tr>
          </tbody>
        </v-table>
      </div>

    </v-card>

  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useStockStore } from '../../stores/stock'
import { useStockRequestsStore } from '../../stores/stockRequests'

const authStore = useAuthStore()
const stockStore = useStockStore()
const requestsStore = useStockRequestsStore()

const loading = ref(false)
const stockData = ref([])

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await requestsStore.fetchIncoming(authStore.assignedWarehouseId)
      stockData.value = await stockStore.fetchByWarehouse(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})

const totalStock = computed(() => {
  return stockData.value.reduce((acc, curr) => acc + curr.quantity, 0)
})

const localesActivos = ref(0) // We can leave this static or fetch warehouses

const solicitudesPendientes = computed(() => {
  return requestsStore.incomingRequests.filter(r => r.status === 'PENDIENTE').length
})

const despachosHoy = computed(() => {
  const hoy = new Date().toLocaleDateString()
  return requestsStore.incomingRequests.filter(r => 
    (r.status === 'ENVIADO' || r.status === 'RECIBIDO') && 
    new Date(r.updatedAt || r.createdAt).toLocaleDateString() === hoy
  ).length
})

const solicitudes = computed(() => {
  return [...requestsStore.incomingRequests]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 5)
})

function getEstadoColor(estado) {
  switch (estado) {
    case 'PENDIENTE':
      return 'warning'
    case 'APROBADO':
      return 'success'
    case 'ENVIADO':
      return 'info'
    case 'RECHAZADO':
      return 'error'
    case 'RECIBIDO':
      return 'primary'
    default:
      return 'grey'
  }
}
</script>

<style scoped>

.dashboard-container {
  max-width: 1800px;
  margin: auto;
  padding: 24px;
}

.header-card {
  padding: 30px;
  border-radius: 20px;

  background: linear-gradient(
      135deg,
      rgba(129,166,132,0.15),
      rgba(148,176,218,0.15)
  );

  border: 1px solid rgba(255,255,255,0.08);
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: 700;
}

.dashboard-card {
  height: 100%;
  border-radius: 18px;

  transition: all .25s ease;
}

.dashboard-card:hover {
  transform: translateY(-6px);

  box-shadow: 0 10px 30px rgba(0,0,0,.2);
}

.card-number {
  font-size: 2.5rem;
  font-weight: bold;

  margin-top: 10px;
}

.solicitudes-card {
  border-radius: 18px;
  overflow: hidden;
}

.table-wrapper {
  overflow-x: auto;
}

.v-table tbody tr {
  transition: background .2s ease;
}

.v-table tbody tr:hover {
  background-color: rgba(255,255,255,0.03);
}

/* TABLET */

@media (max-width: 960px) {

  .dashboard-container {
    padding: 16px;
  }

  .header-card {
    text-align: center;
    padding: 20px;
  }

  .dashboard-title {
    font-size: 2rem;
  }

  .card-number {
    font-size: 2rem;
  }

}

/* CELULAR */

@media (max-width: 600px) {

  .dashboard-container {
    padding: 10px;
  }

  .dashboard-title {
    font-size: 1.7rem;
  }

  .card-number {
    font-size: 1.7rem;
  }

  .header-card {
    padding: 16px;
  }

}

</style>
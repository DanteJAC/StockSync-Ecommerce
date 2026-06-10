<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-6">
    <h1 class="text-h5 text-md-h4 mb-0">Dashboard Local</h1>
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn icon="mdi-dots-vertical" variant="text" v-bind="props"></v-btn>
        </template>

        <v-list>
          <v-list-item link>
            <v-list-item-title>Generar Reporte</v-list-item-title>
          </v-list-item>
          <v-list-item link>
            <v-list-item-title>Configuración</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </div>

    <v-row class="mx-0">
      <v-col cols="12" md="3">
        <v-card color="primary" dark>
          <v-card-text>
            <div class="text-h6">Ventas Hoy</div>
            <div class="text-h5 text-md-h4">{{ formatearDinero(ventasHoyTotal) }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card color="success" dark>
          <v-card-text>
            <div class="text-h6">Productos</div>
            <div class="text-h5 text-md-h4">{{ totalProductos }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card color="warning" dark>
          <v-card-text>
            <div class="text-h6">Bajo Stock</div>
            <div class="text-h5 text-md-h4">{{ bajoStock.length }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card color="info" dark>
          <v-card-text>
            <div class="text-h6">Solicitudes</div>
            <div class="text-h5 text-md-h4">{{ solicitudes.length }}</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>


    <!-- Productos con bajo stock -->
    <v-card class="mt-6">
      <v-card-title class="text-wrap">
        Productos con Bajo Stock
      </v-card-title>

      <v-table class="text-no-wrap">
        <thead>
        <tr>
          <th>Producto</th>
          <th>SKU</th>
          <th>Stock</th>
          <th>Estado</th>
        </tr>
        </thead>

        <tbody>
        <tr
            v-for="producto in bajoStock"
            :key="producto.id"
        >
          <td>{{ producto.productName }}</td>
          <td>{{ producto.sku }}</td>
          <td>{{ producto.quantity }}</td>
          <td>
            <v-chip color="error" size="small">
              Reponer
            </v-chip>
          </td>
        </tr>

        <tr v-if="!bajoStock.length">
          <td colspan="4" class="text-center">
            No hay productos con bajo stock
          </td>
        </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Últimas solicitudes -->
    <v-card class="mt-6">
      <v-card-title class="text-wrap">
        Últimas Solicitudes
      </v-card-title>

      <v-table class="text-no-wrap">
        <thead>
        <tr>
          <th>ID</th>
          <th>Fecha</th>
          <th>Estado</th>
        </tr>
        </thead>

        <tbody>
        <tr
            v-for="solicitud in ultimasSolicitudes"
            :key="solicitud.id"
        >
          <td>#{{ solicitud.id }}</td>
          <td>{{ new Date(solicitud.createdAt).toLocaleDateString() }}</td>
          <td>
            <v-chip :color="getColor(solicitud.status)">
              {{ solicitud.status }}
            </v-chip>
          </td>
        </tr>
        <tr v-if="ultimasSolicitudes.length === 0">
          <td colspan="3" class="text-center">No hay solicitudes</td>
        </tr>
        </tbody>
      </v-table>
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

const stockData = ref([])
const salesData = ref([])

onMounted(async () => {
  if (authStore.assignedWarehouseId) {
    await Promise.all([
      requestsStore.fetchIncoming(authStore.assignedWarehouseId),
      fetchStock(),
      fetchSales()
    ])
  }
})

async function fetchStock() {
  stockData.value = await stockStore.fetchByWarehouse(authStore.assignedWarehouseId)
}

async function fetchSales() {
  salesData.value = await stockStore.fetchMovements('VENTA', authStore.assignedWarehouseId)
}

const ventasHoyTotal = computed(() => {
  const hoy = new Date().toLocaleDateString()
  const ventasHoy = salesData.value.filter(v => new Date(v.createdAt).toLocaleDateString() === hoy)
  return ventasHoy.reduce((acc, v) => acc + (v.totalPrice || 0), 0)
})

function formatearDinero(valor) {
  if (!valor) return '$0'
  return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(valor)
}

const totalProductos = computed(() => stockData.value.length)

const bajoStock = computed(() =>
    stockData.value.filter(product => product.quantity <= 10)
)

const solicitudes = computed(() => requestsStore.incomingRequests)

const ultimasSolicitudes = computed(() => {
  return [...requestsStore.incomingRequests]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 5)
})

function getColor(estado) {
  switch (estado) {
    case 'PENDIENTE':
      return 'warning'
    case 'APROBADO':
      return 'success'
    case 'ENVIADO':
      return 'info'
    case 'RECIBIDO':
      return 'primary'
    case 'RECHAZADO':
      return 'error'
    default:
      return 'grey'
  }
}
</script>
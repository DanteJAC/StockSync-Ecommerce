<template>
  <v-container fluid class="pa-6">

    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Inventario de Bodega
      </h1>

      <p class="text-medium-emphasis">
        Consulta el stock disponible para abastecer los locales.
      </p>
    </div>

    <!-- Resumen -->
    <v-row class="mx-0 mb-4">

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="primary"
          >
            mdi-package-variant
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Productos
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ productos.length }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="warning"
          >
            mdi-alert
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Stock Bajo
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ stockBajo }}
          </div>
        </v-card>
      </v-col>

    </v-row>

    <!-- Buscador -->
    <v-card class="mb-4">
      <v-card-text>
        <v-text-field
            v-model="busqueda"
            label="Buscar producto"
            prepend-inner-icon="mdi-magnify"
            clearable
        />
      </v-card-text>
    </v-card>

    <!-- Tabla -->
    <v-card>

      <v-card-title class="text-wrap">
        Inventario General
      </v-card-title>

      <v-divider />

      <div class="table-wrapper">
        <v-table class="text-no-wrap">

          <thead>
          <tr>
            <th>Producto</th>
            <th>SKU</th>
            <th>Ubicación</th>
            <th>Stock</th>
            <th>Estado</th>
          </tr>
          </thead>

          <tbody>

          <tr
              v-for="producto in productosFiltrados"
              :key="producto.id"
          >
            <td>{{ producto.productName }}</td>

            <td>{{ producto.sku }}</td>

            <td>{{ producto.warehouseName }}</td>

            <td>{{ producto.quantity }}</td>

            <td>
              <v-chip
                  :color="estadoColor(producto.quantity)"
                  size="small"
                  variant="tonal"
              >
                {{ estadoTexto(producto.quantity) }}
              </v-chip>
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
import { useStockStore } from '../../stores/stock'
import { useAuthStore } from '../../stores/auth'

const stockStore = useStockStore()
const authStore = useAuthStore()

const busqueda = ref('')
const productos = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      productos.value = await stockStore.fetchByWarehouse(authStore.assignedWarehouseId)
    }
  } catch (error) {
    console.error('Error fetching warehouse stock:', error)
  } finally {
    loading.value = false
  }
})

const productosFiltrados = computed(() => {
  const search = busqueda.value || ''
  return productos.value.filter(p =>
      p.productName?.toLowerCase().includes(search.toLowerCase()) ||
      p.sku?.toLowerCase().includes(search.toLowerCase())
  )
})

const stockBajo = computed(() => {
  return productos.value.filter(p => p.quantity <= 20).length
})

function estadoColor(stock) {
  if (stock <= 10) return 'error'
  if (stock <= 20) return 'warning'
  return 'success'
}

function estadoTexto(stock) {
  if (stock <= 10) return 'Crítico'
  if (stock <= 20) return 'Bajo'
  return 'Disponible'
}
</script>

<style scoped>

.table-wrapper {
  overflow-x: auto;
}

.v-card {
  border-radius: 16px;
}

.v-table tbody tr:hover {
  background-color: rgba(255,255,255,0.04);
}

@media (max-width: 600px) {

  .text-h4 {
    font-size: 1.8rem;
  }

}

</style>
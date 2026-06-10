<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-6">
    <h1 class="text-h4 mb-0">Dashboard Local</h1>
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

    <v-row>
      <v-col cols="12" md="3">
        <v-card color="primary" dark>
          <v-card-text>
            <div class="text-h6">Ventas Hoy</div>
            <div class="text-h4">$850.000</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card color="success" dark>
          <v-card-text>
            <div class="text-h6">Productos</div>
            <div class="text-h4">{{ totalProductos }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card color="warning" dark>
          <v-card-text>
            <div class="text-h6">Bajo Stock</div>
            <div class="text-h4">{{ bajoStock.length }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card color="info" dark>
          <v-card-text>
            <div class="text-h6">Solicitudes</div>
            <div class="text-h4">3</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>


    <!-- Productos con bajo stock -->
    <v-card class="mt-6">
      <v-card-title>
        Productos con Bajo Stock
      </v-card-title>

      <v-table>
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
          <td>{{ producto.name }}</td>
          <td>{{ producto.sku }}</td>
          <td>{{ obtenerStock(producto) }}</td>
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
      <v-card-title>
        Últimas Solicitudes
      </v-card-title>

      <v-table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Fecha</th>
          <th>Estado</th>
        </tr>
        </thead>

        <tbody>
        <tr>
          <td>#SOL-001</td>
          <td>05/06/2026</td>
          <td>
            <v-chip color="warning">
              Pendiente
            </v-chip>
          </td>
        </tr>

        <tr>
          <td>#SOL-002</td>
          <td>04/06/2026</td>
          <td>
            <v-chip color="success">
              Enviada
            </v-chip>
          </td>
        </tr>
        </tbody>
      </v-table>
    </v-card>
  </v-container>
</template>




<script setup>
import { ref, computed, onMounted } from 'vue'
import { useProductsStore } from '../../stores/products'

const store = useProductsStore()

const products = ref([])

const totalProductos = computed(() => products.value.length)

const bajoStock = computed(() =>
    products.value.filter(product => obtenerStock(product) <= 10)
)

function obtenerStock(producto) {
  if (!producto.warehouseStocks?.length) return 0

  return producto.warehouseStocks.reduce(
      (acc, item) => acc + item.quantity,
      0
  )
}

onMounted(async () => {
  await store.fetchAll()
  products.value = [...store.products]
})
</script>
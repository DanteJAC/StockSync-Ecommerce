<template>
  <v-row>
    <v-col cols="12" sm="4">
      <v-card class="pa-6 text-center" elevation="2">
        <v-icon size="48" color="primary" class="mb-3">mdi-package-variant-closed</v-icon>
        <div class="text-h3 font-weight-bold">{{ totalProducts }}</div>
        <div class="text-body-1 text-medium-emphasis">Productos</div>
      </v-card>
    </v-col>
    <v-col cols="12" sm="4">
      <v-card class="pa-6 text-center" elevation="2">
        <v-icon size="48" color="primary" class="mb-3">mdi-warehouse</v-icon>
        <div class="text-h3 font-weight-bold">{{ totalWarehouses }}</div>
        <div class="text-body-1 text-medium-emphasis">Bodegas</div>
      </v-card>
    </v-col>
    <v-col cols="12" sm="4">
      <v-card class="pa-6 text-center" elevation="2">
        <v-icon size="48" color="primary" class="mb-3">mdi-chart-box</v-icon>
        <div class="text-h3 font-weight-bold">{{ totalStocks }}</div>
        <div class="text-body-1 text-medium-emphasis">Registros de Stock</div>
      </v-card>
    </v-col>
  </v-row>

  <v-row class="mt-4">
    <v-col cols="12">
      <v-card elevation="2">
        <v-card-title>Productos Recientes</v-card-title>
        <v-card-text>
          <v-table density="comfortable">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Stock</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in recentProducts" :key="p.id">
                <td>{{ p.name }}</td>
                <td>{{ p.categoryName }}</td>
                <td>${{ formatPrice(p.price) }}</td>
                <td>
                  <v-chip :color="p.stock > 0 ? 'success' : 'error'" size="small">
                    {{ p.stock }}
                  </v-chip>
                </td>
              </tr>
              <tr v-if="!recentProducts.length">
                <td colspan="4" class="text-center text-medium-emphasis">
                  No hay productos
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'
import { getStocks } from '../../api/stock'

const totalProducts = ref(0)
const totalWarehouses = ref(0)
const totalStocks = ref(0)
const recentProducts = ref([])

function formatPrice(price) {
  return Number(price).toLocaleString('es-CL')
}

onMounted(async () => {
  try {
    const [prods, whs, stks] = await Promise.all([
      getProducts(),
      getWarehouses(),
      getStocks(),
    ])
    totalProducts.value = prods.data.length
    totalWarehouses.value = whs.data.length
    totalStocks.value = stks.data.length
    recentProducts.value = prods.data.slice(0, 5)
  } catch (e) {
    console.error('Error loading dashboard:', e)
  }
})
</script>

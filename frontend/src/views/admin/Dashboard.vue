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
      <v-card elevation="2" color="red-lighten-5">
        <v-card-title class="text-error font-weight-bold">
          <v-icon class="mr-2">mdi-alert-circle</v-icon>
          Alertas de Bajo Stock
        </v-card-title>
        <v-card-text>
          <v-table density="comfortable" class="bg-transparent">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Stock Mínimo</th>
                <th>Stock Actual</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in lowStockProducts" :key="p.id">
                <td>{{ p.name }}</td>
                <td>{{ p.categoryName }}</td>
                <td>{{ p.minStockLevel || 5 }}</td>
                <td>
                  <v-chip color="error" size="small" class="font-weight-bold">
                    {{ p.stock }}
                  </v-chip>
                </td>
              </tr>
              <tr v-if="!lowStockProducts.length">
                <td colspan="4" class="text-center text-medium">
                  <v-icon color="success" class="mr-2">mdi-check-circle</v-icon>
                  No hay productos con stock bajo
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
const lowStockProducts = ref([])

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
    
    lowStockProducts.value = prods.data.filter(p => p.stock <= (p.minStockLevel || 5))
  } catch (e) {
    console.error('Error loading dashboard:', e)
  }
})
</script>

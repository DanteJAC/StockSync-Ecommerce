<template>

  <v-card>
    <h1 class="text-h5 text-md-h4 mb-6">Inventario del Local</h1>
    <v-card-text>
      <v-alert
          type="warning"
          variant="tonal"
          class="mb-4"
      >
        Productos con bajo stock: {{ bajoStock.length }}
      </v-alert>

      <!-- Bajo stock -->
      <v-card
          variant="outlined"
          class="mb-6"
      >
        <v-card-title class="text-wrap">
          Productos que requieren reposición
        </v-card-title>

        <v-table class="text-no-wrap">
          <thead>
          <tr>
            <th>Producto</th>
            <th>Stock</th>
          </tr>
          </thead>

          <tbody>
          <tr
              v-for="producto in bajoStock"
              :key="producto.id"
          >
            <td>{{ producto.productName }}</td>
            <td>
              <v-chip color="error">
                {{ producto.quantity }}
              </v-chip>
            </td>
          </tr>

          <tr v-if="!bajoStock.length">
            <td colspan="2" class="text-center">
              No hay productos críticos
            </td>
          </tr>
          </tbody>
        </v-table>
      </v-card>

      <!-- Inventario completo -->
      <v-card-title class="text-wrap">
        Inventario
      </v-card-title>
      <v-table class="text-no-wrap">
        <thead>
        <tr>
          <th>Producto</th>
          <th>SKU</th>
          <th>Categoría</th>
          <th>Stock</th>
          <th>Estado</th>
        </tr>
        </thead>

        <tbody>
        <tr
            v-for="producto in products"
            :key="producto.id"
        >
          <td>{{ producto.productName }}</td>
          <td>{{ producto.sku }}</td>
          <td>{{ producto.categoryName || 'N/A' }}</td>

          <td>
            {{ producto.quantity }}
          </td>

          <td>
            <v-chip
                :color="producto.quantity <= 10 ? 'error' : 'success'"
                size="small"
            >
              {{ producto.quantity <= 10 ? 'Bajo Stock' : 'Disponible' }}
            </v-chip>
          </td>
        </tr>
        </tbody>
      </v-table>

    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStockStore } from '../../stores/stock'
import { useAuthStore } from '../../stores/auth'

const store = useStockStore()
const authStore = useAuthStore()

const products = ref([])
const loading = ref(false)

const bajoStock = computed(() =>
    products.value.filter(product => product.quantity <= 10)
)

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      products.value = await store.fetchByWarehouse(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})
</script>
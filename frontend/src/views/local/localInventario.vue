<template>

  <v-card>
    <h1 class="text-h4 mb-6">Inventario del Local</h1>
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
        <v-card-title>
          Productos que requieren reposición
        </v-card-title>

        <v-table>
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
            <td>{{ producto.name }}</td>
            <td>
              <v-chip color="error">
                {{ obtenerStock(producto) }}
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
      <v-card-title>
        Inventario
      </v-card-title>
      <v-table>
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
          <td>{{ producto.name }}</td>
          <td>{{ producto.sku }}</td>
          <td>{{ producto.categoryName }}</td>

          <td>
            {{ obtenerStock(producto) }}
          </td>

          <td>
            <v-chip
                :color="obtenerStock(producto) <= 10 ? 'error' : 'success'"
                size="small"
            >
              {{ obtenerStock(producto) <= 10 ? 'Bajo Stock' : 'Disponible' }}
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
import { useProductsStore } from '../../stores/products'

const store = useProductsStore()

const products = ref([])

function obtenerStock(producto) {
  if (!producto.warehouseStocks?.length) return 0

  return producto.warehouseStocks.reduce(
      (acc, item) => acc + item.quantity,
      0
  )
}

const bajoStock = computed(() =>
    products.value.filter(product => obtenerStock(product) <= 10)
)

onMounted(async () => {
  await store.fetchAll()
  products.value = [...store.products]
})
</script>
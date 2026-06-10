<template>
  <v-card elevation="2">
    <v-card-title class="d-flex justify-space-between align-center">
      <span>Productos</span>
      <v-btn color="primary" to="/admin/productos/nuevo" prepend-icon="mdi-plus">
        Nuevo Producto
      </v-btn>
    </v-card-title>

    <v-card-text>
      <v-progress-linear v-if="loading" indeterminate color="primary" />

      <div class="overflow-x-auto">
        <v-table class="text-no-wrap" density="comfortable">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>SKU</th>
              <th>Categoría</th>
              <th>Precio</th>
              <th>Stock Total</th>
              <th>Stock Mínimo</th>
              <th>Bodegas</th>
              <th>Activo</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td class="font-weight-medium">{{ product.name }}</td>
              <td><code>{{ product.sku }}</code></td>
              <td>{{ product.categoryName }}</td>
              <td>${{ formatPrice(product.price) }}</td>
              <td>
                <v-chip :color="product.stock <= product.minStockLevel ? 'warning' : 'success'" size="x-small">
                  {{ product.stock }}
                </v-chip>
              </td>
              <td>{{ product.minStockLevel ?? 5 }}</td>
              <td>
                <template v-if="product.warehouseStocks && product.warehouseStocks.length">
                  <v-chip
                    v-for="ws in product.warehouseStocks"
                    :key="ws.warehouseId"
                    size="x-small"
                    class="ma-1"
                    :color="ws.quantity > 0 ? 'success' : 'error'"
                  >
                    {{ ws.warehouseName }}: {{ ws.quantity }}
                  </v-chip>
                </template>
                <span v-else class="text-medium-emphasis">—</span>
              </td>
              <td>
                <v-icon :color="product.active ? 'success' : 'grey'">
                  {{ product.active ? 'mdi-check-circle' : 'mdi-cancel' }}
                </v-icon>
              </td>
              <td>
                <v-btn
                  icon="mdi-pencil"
                  variant="text"
                  color="primary"
                  size="small"
                  :to="`/admin/productos/editar/${product.id}`"
                />
                <v-btn
                  icon="mdi-delete"
                  variant="text"
                  color="error"
                  size="small"
                  @click="confirmDelete(product)"
                />
              </td>
            </tr>
            <tr v-if="!products.length && !loading">
              <td colspan="9" class="text-center text-medium-emphasis py-6">
                No hay productos registrados
              </td>
            </tr>
          </tbody>
        </v-table>
      </div>
    </v-card-text>

    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-wrap">Eliminar Producto</v-card-title>
        <v-card-text>
          ¿Estás seguro de eliminar "{{ productToDelete?.name }}"?
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">Cancelar</v-btn>
          <v-btn color="error" variant="tonal" @click="handleDelete" :loading="deleting">
            Eliminar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useProductsStore } from '../../stores/products'

const store = useProductsStore()
const products = ref([])
const loading = ref(false)
const deleteDialog = ref(false)
const deleting = ref(false)
const productToDelete = ref(null)

function formatPrice(price) {
  return Number(price).toLocaleString('es-CL')
}

function confirmDelete(product) {
  productToDelete.value = product
  deleteDialog.value = true
}

async function handleDelete() {
  deleting.value = true
  try {
    await store.remove(productToDelete.value.id)
    products.value = [...store.products]
  } catch (e) {
    console.error('Error deleting product:', e)
  } finally {
    deleting.value = false
    deleteDialog.value = false
    productToDelete.value = null
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await store.fetchAll()
    products.value = [...store.products]
  } finally {
    loading.value = false
  }
})
</script>

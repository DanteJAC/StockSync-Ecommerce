<template>
  <v-card elevation="2">
    <v-card-title class="d-flex justify-space-between align-center">
      <span>Stock</span>
      <div class="d-flex ga-2">
        <v-btn color="primary" to="/admin/stock/nuevo" prepend-icon="mdi-plus">
          Nuevo Stock
        </v-btn>
        <v-btn color="info" variant="tonal" prepend-icon="mdi-swap-horizontal-bold" @click="showTransfer = true">
          Transferir
        </v-btn>
      </div>
    </v-card-title>

    <v-card-text>
      <v-progress-linear v-if="loading" indeterminate color="primary" />

      <div class="overflow-x-auto">
        <v-table class="text-no-wrap" density="comfortable">
          <thead>
            <tr>
              <th>Producto</th>
              <th>Bodega</th>
              <th>Cantidad</th>
              <th>Última actualización</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in stocks" :key="s.id">
              <td class="font-weight-medium">{{ s.productName }}</td>
              <td>{{ s.warehouseName }}</td>
              <td>
                <v-chip :color="chipColor(s)" size="small">
                  {{ s.quantity }}
                </v-chip>
              </td>
              <td>{{ formatDate(s.lastUpdate) }}</td>
              <td>
                <v-btn
                  icon="mdi-delete"
                  variant="text"
                  color="error"
                  size="small"
                  @click="confirmDelete(s)"
                />
              </td>
            </tr>
            <tr v-if="!stocks.length && !loading">
              <td colspan="5" class="text-center text-medium-emphasis py-6">
                No hay registros de stock
              </td>
            </tr>
          </tbody>
        </v-table>
      </div>
    </v-card-text>

    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-wrap">Eliminar Stock</v-card-title>
        <v-card-text>
          ¿Estás seguro de eliminar este registro de stock?
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

    <v-dialog v-model="showTransfer" max-width="500">
      <v-card>
        <v-card-title class="text-wrap">Transferir Stock</v-card-title>
        <v-card-text>
          <v-alert v-if="transferError" type="error" variant="tonal" class="mb-4" closable @click:close="transferError = ''">
            {{ transferError }}
          </v-alert>
          <v-select
            v-model="transferForm.productId"
            :items="products"
            item-title="name"
            item-value="id"
            label="Producto"
            class="mb-3"
          />
          <v-select
            v-model="transferForm.sourcewarehouseId"
            :items="warehouses"
            item-title="name"
            item-value="id"
            label="Bodega origen"
            class="mb-3"
          />
          <v-select
            v-model="transferForm.destinationWarehouseId"
            :items="warehouses"
            item-title="name"
            item-value="id"
            label="Bodega destino"
            class="mb-3"
          />
          <v-text-field
            v-model="transferForm.quantity"
            label="Cantidad"
            type="number"
            min="1"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showTransfer = false">Cancelar</v-btn>
          <v-btn color="primary" variant="tonal" @click="handleTransfer" :loading="transferring">
            Transferir
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStockStore } from '../../stores/stock'
import { getProducts, getLowStockProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'

const store = useStockStore()
const stocks = ref([])
const products = ref([])
const warehouses = ref([])
const loading = ref(false)
const lowStockProductIds = ref(new Set())
const deleteDialog = ref(false)
const deleting = ref(false)
const stockToDelete = ref(null)
const showTransfer = ref(false)
const transferring = ref(false)
const transferError = ref('')
const transferForm = ref({
  productId: null,
  sourcewarehouseId: null,
  destinationWarehouseId: null,
  quantity: 1,
})

function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('es-CL', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function chipColor(s) {
  if (s.quantity === 0) return 'error'
  if (lowStockProductIds.value.has(s.productId)) return 'warning'
  return 'success'
}

function confirmDelete(s) {
  stockToDelete.value = s
  deleteDialog.value = true
}

async function handleDelete() {
  deleting.value = true
  try {
    await store.remove(stockToDelete.value.id)
    stocks.value = [...store.stocks]
  } catch (e) {
    console.error('Error deleting stock:', e)
  } finally {
    deleting.value = false
    deleteDialog.value = false
    stockToDelete.value = null
  }
}

async function handleTransfer() {
  transferring.value = true
  transferError.value = ''
  try {
    await store.transfer({
      productId: Number(transferForm.value.productId),
      sourcewarehouseId: Number(transferForm.value.sourcewarehouseId),
      destinationWarehouseId: Number(transferForm.value.destinationWarehouseId),
      quantity: Number(transferForm.value.quantity),
    })
    showTransfer.value = false
    await store.fetchAll()
    stocks.value = [...store.stocks]
  } catch (e) {
    transferError.value = e.response?.data?.message || 'Error al transferir stock'
  } finally {
    transferring.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [prods, whs, lowStock] = await Promise.all([
      getProducts(),
      getWarehouses(),
      getLowStockProducts(),
    ])
    products.value = prods.data
    warehouses.value = whs.data
    lowStockProductIds.value = new Set(lowStock.data.map(p => p.id))
    await store.fetchAll()
    stocks.value = [...store.stocks]
  } finally {
    loading.value = false
  }
})
</script>

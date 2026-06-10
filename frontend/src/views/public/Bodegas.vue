<template>
  <v-main>
    <PublicHeader />

    <v-container class="py-8">
      <h1 class="text-h3 font-weight-bold mb-6">Bodegas</h1>

      <v-row class="mx-0">
        <v-col v-for="warehouse in warehouses" :key="warehouse.id" cols="12" sm="6" md="4">
          <v-card elevation="2">
            <v-card-title class="text-wrap">{{ warehouse.name }}</v-card-title>
            <v-card-subtitle>Código: {{ warehouse.code }}</v-card-subtitle>
            <v-card-text>
              <div class="text-body-2">{{ warehouse.address }}</div>
              <div class="text-body-2 mb-3">{{ warehouse.city }}</div>
            </v-card-text>
            <v-card-actions>
              <v-btn
                variant="tonal"
                color="primary"
                size="small"
                @click="selectWarehouse(warehouse)"
              >
                Ver Stock
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <v-dialog v-model="showStock" max-width="800">
        <v-card>
          <v-card-title class="text-wrap">
            Stock en {{ selectedWarehouse?.name }}
          </v-card-title>

          <v-card-text>
            <v-select
              v-model="selectedProductId"
              :items="products"
              item-title="name"
              item-value="id"
              label="Filtrar por producto"
              clearable
              class="mb-4"
            />

            <div class="overflow-x-auto">
              <v-table class="text-no-wrap" density="comfortable">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Última actualización</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in filteredStock" :key="item.id">
                    <td>{{ item.productName }}</td>
                    <td>
                      <v-chip :color="item.quantity > 0 ? 'success' : 'error'" size="small">
                        {{ item.quantity }}
                      </v-chip>
                    </td>
                    <td>{{ formatDate(item.lastUpdate) }}</td>
                  </tr>
                  <tr v-if="!filteredStock.length">
                    <td colspan="3" class="text-center text-medium-emphasis">
                      Sin stock disponible
                    </td>
                  </tr>
                </tbody>
              </v-table>
            </div>
          </v-card-text>

          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" @click="showStock = false">Cerrar</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>

    <PublicFooter />
  </v-main>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { getWarehouses } from '../../api/warehouses'
import { getProducts } from '../../api/products'
import { getStocksByWarehouse } from '../../api/stock'

const warehouses = ref([])
const products = ref([])
const selectedWarehouse = ref(null)
const selectedProductId = ref(null)
const stockData = ref([])
const showStock = ref(false)

const filteredStock = computed(() => {
  if (!selectedProductId.value) return stockData.value
  return stockData.value.filter((s) => s.productId === selectedProductId.value)
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

async function selectWarehouse(warehouse) {
  selectedWarehouse.value = warehouse
  selectedProductId.value = null
  try {
    const { data } = await getStocksByWarehouse(warehouse.id)
    stockData.value = data
  } catch {
    stockData.value = []
  }
  showStock.value = true
}

onMounted(async () => {
  try {
    const [whs, prods] = await Promise.all([
      getWarehouses(),
      getProducts(),
    ])
    warehouses.value = whs.data
    products.value = prods.data
  } catch (e) {
    console.error('Error loading bodegas:', e)
  }
})
</script>

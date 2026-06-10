<template>
  <v-container fluid>
    <v-row class="mx-0">
      <v-col cols="12">
        <h1 class="text-h5 text-md-h4 font-weight-bold mb-4">
          Solicitar Reposición
        </h1>
      </v-col>
    </v-row>

    <!-- Formulario -->
    <v-row class="mx-0">
      <v-col cols="12" md="8">
        <v-card elevation="2">
          <v-card-title class="text-wrap">
            Nueva Solicitud
          </v-card-title>

          <v-card-text>
            <v-form ref="formRef">
              <v-select
                  v-model="formulario.sourceWarehouseId"
                  label="Bodega Origen"
                  :items="bodegasDisponibles"
                  item-title="name"
                  item-value="id"
                  variant="outlined"
                  prepend-inner-icon="mdi-warehouse"
                  :rules="[v => !!v || 'Campo requerido']"
                  @update:model-value="onSourceWarehouseChange"
              />

              <v-select
                  v-model="formulario.productId"
                  label="Producto"
                  :items="productosDisponibles"
                  item-title="displayName"
                  item-value="productId"
                  variant="outlined"
                  prepend-inner-icon="mdi-package-variant"
                  :rules="[v => !!v || 'Campo requerido']"
                  class="mt-4"
                  :disabled="!formulario.sourceWarehouseId"
              />

              <v-text-field
                  v-model="formulario.quantity"
                  label="Cantidad solicitada"
                  type="number"
                  variant="outlined"
                  prepend-inner-icon="mdi-counter"
                  class="mt-4"
                  :rules="[
                    v => !!v || 'Campo requerido',
                    v => v > 0 || 'Debe ser mayor a cero'
                  ]"
              />

              <div class="d-flex justify-end mt-4">
                <v-btn
                    color="primary"
                    prepend-icon="mdi-send"
                    @click="enviarSolicitud"
                    :loading="enviando"
                >
                  Enviar Solicitud
                </v-btn>
              </div>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Productos con bajo stock -->
      <v-col cols="12" md="4">
        <v-card elevation="2">
          <v-card-title class="text-wrap">
            Productos con Bajo Stock
          </v-card-title>

          <v-list>
            <v-list-item
                v-for="producto in bajoStock"
                :key="producto.nombre"
            >
              <template #prepend>
                <v-icon color="warning">
                  mdi-alert-circle
                </v-icon>
              </template>

              <v-list-item-title>
                {{ producto.productName }}
              </v-list-item-title>

              <v-list-item-subtitle>
                Stock actual: {{ producto.quantity }}
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>

    <!-- Historial de solicitudes -->
    <v-row class="mx-0 mt-6">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title class="text-wrap">
            Estado de Solicitudes
          </v-card-title>

          <v-table class="text-no-wrap">
            <thead>
            <tr>
              <th>Producto</th>
              <th>Cantidad</th>
              <th>Fecha</th>
              <th>Estado</th>
            </tr>
            </thead>

            <tbody>
            <tr
                v-for="solicitud in solicitudes"
                :key="solicitud.id"
            >
              <td>{{ solicitud.productName }}</td>
              <td>{{ solicitud.quantity }}</td>
              <td>{{ new Date(solicitud.createdAt).toLocaleDateString() }}</td>

              <td>
                <v-chip
                    :color="getColor(solicitud.status)"
                    size="small"
                >
                  {{ solicitud.status }}
                </v-chip>
              </td>
            </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useStockStore } from '../../stores/stock'
import { useStockRequestsStore } from '../../stores/stockRequests'
import { useWarehousesStore } from '../../stores/warehouses'
import { useProductsStore } from '../../stores/products'

const authStore = useAuthStore()
const stockStore = useStockStore()
const requestsStore = useStockRequestsStore()
const warehousesStore = useWarehousesStore()
const productsStore = useProductsStore()

const formRef = ref(null)
const enviando = ref(false)
const formulario = ref({
  productId: null,
  quantity: null,
  sourceWarehouseId: null
})

const sourceWarehouseStock = ref([])

const productosDisponibles = computed(() => {
  return sourceWarehouseStock.value
    .filter(stock => stock.quantity > 0)
    .map(stock => ({
      productId: stock.productId,
      displayName: `${stock.productName} (Disponible: ${stock.quantity})`,
      quantity: stock.quantity
    }))
})

async function onSourceWarehouseChange(warehouseId) {
  formulario.value.productId = null
  if (!warehouseId) {
    sourceWarehouseStock.value = []
    return
  }
  sourceWarehouseStock.value = await stockStore.fetchByWarehouse(warehouseId)
}

const bodegasDisponibles = computed(() => 
  warehousesStore.warehouses.filter(w => w.id !== authStore.assignedWarehouseId)
)
const stockData = ref([])

onMounted(async () => {
  if (authStore.assignedWarehouseId) {
    await Promise.all([
      warehousesStore.fetchAll(),
      productsStore.fetchAll(),
      requestsStore.fetchOutgoing(authStore.assignedWarehouseId),
      fetchStock()
    ])
  }
})

async function fetchStock() {
  stockData.value = await stockStore.fetchByWarehouse(authStore.assignedWarehouseId)
}

const bajoStock = computed(() => {
  return stockData.value.filter(s => s.quantity <= 10)
})

const solicitudes = computed(() => {
  return [...requestsStore.outgoingRequests]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

async function enviarSolicitud() {
  const { valid } = await formRef.value?.validate() || { valid: true }
  if (!valid) return
  
  if (!formulario.value.productId || !formulario.value.quantity || !formulario.value.sourceWarehouseId) {
    alert('Por favor completa todos los campos requeridos.')
    return
  }

  const selectedProduct = productosDisponibles.value.find(p => p.productId === formulario.value.productId)
  if (selectedProduct && formulario.value.quantity > selectedProduct.quantity) {
    alert(`La cantidad solicitada supera el stock disponible (${selectedProduct.quantity}) en la bodega origen.`)
    return
  }

  enviando.value = true
  try {
    await requestsStore.create({
      productId: formulario.value.productId,
      quantity: formulario.value.quantity,
      sourceWarehouseId: formulario.value.sourceWarehouseId,
      destinationWarehouseId: authStore.assignedWarehouseId
    })
    formulario.value.productId = null
    formulario.value.quantity = null
    formulario.value.sourceWarehouseId = null
    sourceWarehouseStock.value = []
    await requestsStore.fetchOutgoing(authStore.assignedWarehouseId)
  } catch (error) {
    console.error('Error al enviar solicitud', error)
    alert('Error al enviar solicitud')
  } finally {
    enviando.value = false
  }
}

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
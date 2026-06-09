<template>
  <v-card elevation="2">
    <v-card-title class="d-flex justify-space-between align-center">
      <span>Solicitudes a Bodega</span>
      <v-dialog v-model="createDialog" max-width="500">
        <template v-slot:activator="{ props }">
          <v-btn color="primary" v-bind="props" prepend-icon="mdi-plus">
            Nueva Solicitud
          </v-btn>
        </template>
        <v-card>
          <v-card-title>Crear Solicitud</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="handleCreate" ref="form">
              <v-select
                v-model="newRequest.productId"
                :items="products"
                item-title="name"
                item-value="id"
                label="Producto"
                required
              />
              <v-select
                v-model="newRequest.sourceWarehouseId"
                :items="warehouses"
                item-title="name"
                item-value="id"
                label="Bodega Origen (la que provee)"
                required
              />
              <v-select
                v-if="auth.isAdmin"
                v-model="newRequest.destinationWarehouseId"
                :items="allWarehouses"
                item-title="name"
                item-value="id"
                label="Local Destino"
                required
              />
              <v-text-field
                v-model.number="newRequest.quantity"
                type="number"
                label="Cantidad"
                required
                min="1"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" @click="createDialog = false">Cancelar</v-btn>
            <v-btn color="primary" @click="handleCreate" :loading="creating">
              Solicitar
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card-title>

    <v-card-text>
      <v-progress-linear v-if="loading" indeterminate color="primary" />

      <v-table density="comfortable">
        <thead>
          <tr>
            <th>Producto (SKU)</th>
            <th>De Bodega</th>
            <th>Hacia Local</th>
            <th>Cantidad</th>
            <th>Estado</th>
            <th>Fecha</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="req in requests" :key="req.id">
            <td>{{ req.productName }} <span class="text-caption text-medium-emphasis">({{ req.sku }})</span></td>
            <td>{{ req.sourceWarehouseName }}</td>
            <td>{{ req.destinationWarehouseName }}</td>
            <td>{{ req.quantity }}</td>
            <td>
              <v-chip :color="getStatusColor(req.status)" size="small">
                {{ req.status }}
              </v-chip>
            </td>
            <td>{{ new Date(req.createdAt).toLocaleDateString() }}</td>
          </tr>
          <tr v-if="!requests.length && !loading">
            <td colspan="6" class="text-center text-medium-emphasis py-6">
              No hay solicitudes registradas.
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRequestsByDestination, createStockRequest, getAllRequests } from '../../api/stockRequests'
import { getProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const requests = ref([])
const products = ref([])
const warehouses = ref([])
const allWarehouses = ref([])
const loading = ref(false)

const createDialog = ref(false)
const creating = ref(false)
const form = ref(null)

const newRequest = ref({
  productId: null,
  sourceWarehouseId: null,
  destinationWarehouseId: null, // Se asigna automáticamente
  quantity: 1
})

function getStatusColor(status) {
  const map = {
    'PENDIENTE': 'warning',
    'ENVIADO': 'info',
    'RECIBIDO': 'success',
    'RECHAZADO': 'error'
  }
  return map[status] || 'grey'
}

async function fetchRequests() {
  if (!auth.isAdmin && !auth.user?.assignedWarehouse?.id) {
    console.warn("Usuario no tiene bodega asignada")
    return
  }
  loading.value = true
  try {
    const res = auth.isAdmin 
      ? await getAllRequests() 
      : await getRequestsByDestination(auth.user.assignedWarehouse.id)
    requests.value = res.data
  } catch (e) {
    console.error('Error fetching requests', e)
  } finally {
    loading.value = false
  }
}

async function fetchDependencies() {
  try {
    const [pRes, wRes] = await Promise.all([getProducts(), getWarehouses()])
    products.value = pRes.data
    allWarehouses.value = wRes.data
    warehouses.value = auth.isAdmin ? wRes.data : wRes.data.filter(w => w.id !== auth.user?.assignedWarehouse?.id)
  } catch (e) {
    console.error('Error fetching deps', e)
  }
}

async function handleCreate() {
  if (!newRequest.value.productId || !newRequest.value.sourceWarehouseId) return
  if (auth.isAdmin && !newRequest.value.destinationWarehouseId) return
  
  creating.value = true
  if (!auth.isAdmin) {
    newRequest.value.destinationWarehouseId = auth.user.assignedWarehouse.id
  }
  
  try {
    await createStockRequest(newRequest.value)
    createDialog.value = false
    newRequest.value = {
      productId: null,
      sourceWarehouseId: null,
      destinationWarehouseId: null,
      quantity: 1
    }
    await fetchRequests()
  } catch (e) {
    console.error('Error creating request', e)
    alert('Error: ' + (e.response?.data?.message || e.message))
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  fetchRequests()
  fetchDependencies()
})
</script>

<template>
  <v-card elevation="2" class="mx-auto" max-width="600">
    <v-card-title class="text-center pt-6 text-h5 font-weight-bold">
      <v-icon size="36" color="primary" class="mr-2">mdi-barcode-scan</v-icon>
      Recepción de Productos
    </v-card-title>

    <v-card-text class="pa-6">
      <v-alert
        v-if="!auth.isAdmin && !auth.user?.assignedWarehouse?.id"
        type="warning"
        variant="tonal"
        class="mb-4"
      >
        No tienes una bodega/local asignado para recepcionar productos.
      </v-alert>

      <v-form @submit.prevent="handleScan" v-else>
        <v-select
          v-if="auth.isAdmin"
          v-model="destinationWarehouseId"
          :items="warehouses"
          item-title="name"
          item-value="id"
          label="Local Destino"
          prepend-inner-icon="mdi-warehouse"
          required
          class="mb-4"
        />
        <v-text-field
          ref="scannerInput"
          v-model="sku"
          label="Escanea el código de barras (SKU)"
          prepend-inner-icon="mdi-barcode"
          variant="outlined"
          autofocus
          clearable
          :loading="processing"
          :disabled="processing"
          hint="Utiliza la pistola de escáner o escribe el SKU y presiona Enter"
          persistent-hint
        />
        <div class="d-flex justify-center mt-6">
          <v-btn
            color="primary"
            size="large"
            type="submit"
            :loading="processing"
            :disabled="(!sku || (auth.isAdmin && !destinationWarehouseId)) || processing"
            prepend-icon="mdi-check"
          >
            Recepcionar
          </v-btn>
        </div>
      </v-form>

      <!-- Feedback Messages -->
      <v-slide-y-transition>
        <v-alert
          v-if="successMessage"
          type="success"
          variant="tonal"
          class="mt-6 text-center"
        >
          <div class="font-weight-bold mb-1">¡Recepción Exitosa!</div>
          {{ successMessage }}
        </v-alert>
      </v-slide-y-transition>

      <v-slide-y-transition>
        <v-alert
          v-if="errorMessage"
          type="error"
          variant="tonal"
          class="mt-6 text-center"
        >
          <div class="font-weight-bold mb-1">Error de Recepción</div>
          {{ errorMessage }}
        </v-alert>
      </v-slide-y-transition>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { receiveStockByScanner } from '../../api/stockRequests'
import { useAuthStore } from '../../stores/auth'

import { getWarehouses } from '../../api/warehouses'

const auth = useAuthStore()
const sku = ref('')
const destinationWarehouseId = ref(null)
const warehouses = ref([])
const scannerInput = ref(null)
const processing = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

async function handleScan() {
  const targetWarehouse = auth.isAdmin ? destinationWarehouseId.value : auth.user?.assignedWarehouse?.id
  if (!sku.value || !targetWarehouse) return

  processing.value = true
  successMessage.value = ''
  errorMessage.value = ''

  try {
    const res = await receiveStockByScanner(sku.value.trim(), targetWarehouse)
    successMessage.value = `Se recepcionaron ${res.data.quantity} unidades de ${res.data.productName} (SKU: ${res.data.sku}).`
  } catch (e) {
    console.error('Error in reception', e)
    errorMessage.value = e.response?.data?.message || 'No se pudo procesar el código o no hay envíos pendientes.'
  } finally {
    processing.value = false
    sku.value = ''
    
    // Devolver el foco al input después de un escaneo exitoso o fallido
    nextTick(() => {
      if (scannerInput.value) {
        scannerInput.value.focus()
      }
    })
  }
}

// Asegurarse de que el input tenga el foco al entrar a la vista
onMounted(async () => {
  if (auth.isAdmin) {
    try {
      const res = await getWarehouses()
      warehouses.value = res.data
    } catch (e) {
      console.error('Error fetching warehouses', e)
    }
  }

  nextTick(() => {
    if (scannerInput.value && (auth.isAdmin || auth.user?.assignedWarehouse?.id)) {
      scannerInput.value.focus()
    }
  })
})
</script>

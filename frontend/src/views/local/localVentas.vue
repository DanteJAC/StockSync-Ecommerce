
<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold mb-0">
        Ventas del Local
      </h1>
      <v-btn
          color="primary"
          prepend-icon="mdi-file-pdf-box"
          @click="exportarPDF"
      >
        Exportar Resumen Diario
      </v-btn>
    </div>

    <v-row class="mx-0">
      <v-col cols="12" md="4">
        <v-card rounded="xl">
          <v-card-text>
            <div class="text-caption">Ventas Hoy</div>
            <div class="text-h5 text-md-h4 font-weight-bold">{{ formatearDinero(ventasHoyTotal) }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card rounded="xl">
          <v-card-text>
            <div class="text-caption">Productos Vendidos Hoy</div>
            <div class="text-h5 text-md-h4 font-weight-bold">{{ productosVendidosHoy }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card rounded="xl">
          <v-card-text>
            <div class="text-caption">Boletas Emitidas Hoy</div>
            <div class="text-h5 text-md-h4 font-weight-bold">{{ boletasEmitidasHoy }}</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-card rounded="xl" class="mt-6">
      <v-card-title class="text-wrap">Registrar Nueva Venta</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="registrarVenta">
          <v-row class="mx-0">
            <v-col cols="12" md="6">
              <v-select
                  v-model="formulario.productId"
                  :items="stockData"
                  item-title="productName"
                  item-value="productId"
                  label="Producto"
                  variant="outlined"
                  prepend-inner-icon="mdi-package-variant"
                  :rules="[v => !!v || 'Campo requerido']"
              />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                  v-model="formulario.quantity"
                  label="Cantidad a vender"
                  type="number"
                  variant="outlined"
                  prepend-inner-icon="mdi-counter"
                  :rules="[
                    v => !!v || 'Campo requerido',
                    v => v > 0 || 'Debe ser mayor a cero'
                  ]"
              />
            </v-col>
            <v-col cols="12" md="2" class="d-flex align-center">
              <v-btn
                  color="success"
                  type="submit"
                  size="large"
                  block
                  :loading="loading"
              >
                Vender
              </v-btn>
            </v-col>
          </v-row>
        </v-form>
        <v-table class="text-no-wrap mt-4">
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Producto</th>
              <th>Cantidad</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
        <tr v-for="venta in ultimasVentas" :key="venta.id">
          <td>{{ new Date(venta.createdAt).toLocaleString() }}</td>
          <td>{{ venta.productName }}</td>
          <td>{{ venta.quantity }}</td>
          <td>{{ formatearDinero(venta.totalPrice) }}</td>
        </tr>
        <tr v-if="ultimasVentas.length === 0">
          <td colspan="4" class="text-center">No hay ventas registradas</td>
        </tr>
        </tbody>
        </v-table>
      </v-card-text>
    </v-card>

  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useStockStore } from '../../stores/stock'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'

const authStore = useAuthStore()
const stockStore = useStockStore()

const loading = ref(false)
const stockData = ref([])
const salesData = ref([])

const formulario = ref({
  productId: null,
  quantity: null
})

onMounted(async () => {
  if (authStore.assignedWarehouseId) {
    await Promise.all([
      fetchStock(),
      fetchSales()
    ])
  }
})

async function fetchStock() {
  stockData.value = await stockStore.fetchByWarehouse(authStore.assignedWarehouseId)
}

async function fetchSales() {
  salesData.value = await stockStore.fetchMovements('VENTA', authStore.assignedWarehouseId)
}

const ventasHoy = computed(() => {
  const hoy = new Date().toLocaleDateString()
  return salesData.value.filter(v => new Date(v.createdAt).toLocaleDateString() === hoy)
})

const ventasHoyTotal = computed(() => {
  return ventasHoy.value.reduce((acc, v) => acc + (v.totalPrice || 0), 0)
})

const productosVendidosHoy = computed(() => {
  return ventasHoy.value.reduce((acc, v) => acc + v.quantity, 0)
})

const boletasEmitidasHoy = computed(() => {
  return ventasHoy.value.length
})

const ultimasVentas = computed(() => {
  return [...salesData.value]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 10)
})

function formatearDinero(valor) {
  if (!valor) return '$0'
  return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(valor)
}

async function registrarVenta() {
  if (!formulario.value.productId || !formulario.value.quantity) return

  loading.value = true
  try {
    await stockStore.sale({
      productId: formulario.value.productId,
      warehouseId: authStore.assignedWarehouseId,
      quantity: Number(formulario.value.quantity)
    })
    alert('Venta registrada exitosamente')
    formulario.value.productId = null
    formulario.value.quantity = null
    await Promise.all([fetchStock(), fetchSales()])
  } catch (error) {
    console.error('Error registrando venta', error)
    alert(error.response?.data?.message || 'Error registrando venta')
  } finally {
    loading.value = false
  }
}

function exportarPDF() {
  try {
    const doc = new jsPDF()

    // Título
    doc.setFontSize(18)
    doc.text('Resumen Diario de Ventas', 14, 22)

    // Subtítulo con fecha
    doc.setFontSize(11)
    doc.setTextColor(100)
    doc.text(`Fecha de emisión: ${new Date().toLocaleString()}`, 14, 30)

    // Resumen
    doc.setTextColor(0)
    doc.text(`Boletas Emitidas Hoy: ${boletasEmitidasHoy.value}`, 14, 40)
    doc.text(`Productos Vendidos: ${productosVendidosHoy.value}`, 14, 46)
    doc.text(`Total Recaudado: ${formatearDinero(ventasHoyTotal.value)}`, 14, 52)

    // Tabla
    const columnas = ['ID', 'Fecha', 'Producto', 'Cantidad', 'Total']
    const filas = ventasHoy.value.map(venta => [
      venta.id,
      new Date(venta.createdAt).toLocaleTimeString(),
      venta.productName,
      venta.quantity,
      formatearDinero(venta.totalPrice)
    ])

    autoTable(doc, {
      startY: 60,
      head: [columnas],
      body: filas,
      theme: 'striped',
      headStyles: { fillColor: [41, 128, 185] }
    })

    doc.save(`Resumen_Ventas_${new Date().toLocaleDateString().replace(/\//g, '-')}.pdf`)
  } catch (error) {
    console.error('Error al generar PDF:', error)
    alert('Error al generar PDF: ' + error.message)
  }
}
</script>

<style scoped>

</style>
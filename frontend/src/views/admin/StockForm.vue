<template>
  <v-card elevation="2">
    <v-card-title>{{ isEdit ? 'Editar Stock' : 'Nuevo Stock' }}</v-card-title>

    <v-card-text>
      <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
        {{ error }}
      </v-alert>

      <v-form @submit.prevent="handleSave">
        <v-row>
          <v-col cols="12" md="6">
            <v-select
              v-model="form.productId"
              :items="products"
              item-title="name"
              item-value="id"
              label="Producto"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-select
              v-model="form.warehouseId"
              :items="warehouses"
              item-title="name"
              item-value="id"
              label="Bodega"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.quantity"
              label="Cantidad"
              type="number"
              :rules="[rules.required, rules.minZero]"
              required
            />
          </v-col>
        </v-row>

        <v-row class="mt-4">
          <v-col cols="12" class="d-flex ga-4">
            <v-btn
              type="submit"
              color="primary"
              :loading="saving"
            >
              {{ isEdit ? 'Actualizar' : 'Agregar' }}
            </v-btn>
            <v-btn variant="outlined" to="/admin/stock">
              Cancelar
            </v-btn>
          </v-col>
        </v-row>
      </v-form>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStockStore } from '../../stores/stock'
import { getProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'

const route = useRoute()
const router = useRouter()
const store = useStockStore()

const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const error = ref('')
const products = ref([])
const warehouses = ref([])

const form = ref({
  productId: null,
  warehouseId: null,
  quantity: 0,
})

const rules = {
  required: (v) => v !== null && v !== '' || 'Campo requerido',
  minZero: (v) => Number(v) >= 0 || 'Debe ser mayor o igual a 0',
}

async function handleSave() {
  saving.value = true
  error.value = ''
  try {
    const payload = {
      productId: Number(form.value.productId),
      warehouseId: Number(form.value.warehouseId),
      quantity: Number(form.value.quantity),
    }

    if (isEdit.value) {
      payload.id = Number(route.params.id)
      await store.update(payload)
    } else {
      await store.add(payload)
    }
    router.push('/admin/stock')
  } catch (e) {
    const err = e.response?.data
    if (err?.errors) {
      error.value = Object.values(err.errors).flat().join(', ')
    } else {
      error.value = err?.message || 'Error al guardar stock'
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  try {
    const [prods, whs] = await Promise.all([
      getProducts(),
      getWarehouses(),
    ])
    products.value = prods.data
    warehouses.value = whs.data
  } catch (e) {
    console.error('Error loading form data:', e)
  }

  if (isEdit.value) {
    await store.fetchAll()
    const item = store.stocks.find((s) => s.id === Number(route.params.id))
    if (item) {
      form.value = {
        productId: item.productId,
        warehouseId: item.warehouseId,
        quantity: item.quantity,
      }
    }
  }
})
</script>

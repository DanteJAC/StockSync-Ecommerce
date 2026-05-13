<template>
  <v-card elevation="2">
    <v-card-title>{{ isEdit ? 'Editar Producto' : 'Nuevo Producto' }}</v-card-title>

    <v-card-text>
      <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
        {{ error }}
      </v-alert>

      <v-form @submit.prevent="handleSave">
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.name"
              label="Nombre"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.sku"
              label="SKU"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12">
            <v-textarea
              v-model="form.description"
              label="Descripción"
              rows="3"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="form.price"
              label="Precio"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positive]"
              required
              prefix="$"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="form.categoryId"
              :items="categories"
              item-title="name"
              item-value="id"
              label="Categoría"
              :rules="[rules.required]"
              required
            >
              <template v-slot:append-item>
                <v-divider class="mt-2" />
                <v-list-item
                  prepend-icon="mdi-plus"
                  title="Crear nueva categoría"
                  @click="showCategoryDialog = true"
                />
              </template>
            </v-select>
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="form.imageUrl"
              label="URL de Imagen"
            />
          </v-col>
          <v-col cols="12" md="6" class="d-flex align-center">
            <v-switch
              v-model="form.active"
              label="Producto activo"
              color="primary"
              inset
            />
          </v-col>
        </v-row>

        <v-divider class="my-4" />
        <v-row>
          <v-col cols="12">
            <div class="d-flex align-center mb-2">
              <span class="text-subtitle-1 font-weight-medium">Stock por Bodega</span>
              <v-spacer />
              <v-btn
                v-if="!isEdit"
                variant="tonal"
                color="primary"
                size="small"
                prepend-icon="mdi-plus"
                @click="addWarehouseRow"
              >
                Agregar bodega
              </v-btn>
            </div>
          </v-col>
        </v-row>

        <v-row v-if="isEdit && form.warehouseStocks && form.warehouseStocks.length">
          <v-col cols="12">
            <v-chip
              v-for="ws in form.warehouseStocks"
              :key="ws.warehouseId"
              class="ma-1"
              color="primary"
              variant="outlined"
            >
              {{ ws.warehouseName }}: {{ ws.quantity }}
            </v-chip>
          </v-col>
        </v-row>

        <v-row v-for="(entry, index) in form.warehouseStocks" :key="index" v-if="!isEdit">
          <v-col cols="5">
            <v-select
              v-model="entry.warehouseId"
              :items="availableWarehouses"
              item-title="name"
              item-value="id"
              label="Bodega"
              :rules="[rules.required]"
              required
            >
              <template v-slot:append-item>
                <v-divider class="mt-2" />
                <v-list-item
                  prepend-icon="mdi-plus"
                  title="Crear nueva bodega"
                  @click="openWarehouseDialog(index)"
                />
              </template>
            </v-select>
          </v-col>
          <v-col cols="5">
            <v-text-field
              v-model="entry.quantity"
              label="Cantidad"
              type="number"
              min="0"
              :rules="[rules.required, rules.minZero]"
              required
            />
          </v-col>
          <v-col cols="2" class="d-flex align-center">
            <v-btn
              icon="mdi-delete"
              variant="text"
              color="error"
              size="small"
              @click="removeWarehouseRow(index)"
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
              {{ isEdit ? 'Actualizar' : 'Crear' }}
            </v-btn>
            <v-btn variant="outlined" to="/admin/productos">
              Cancelar
            </v-btn>
          </v-col>
        </v-row>
      </v-form>
    </v-card-text>

    <v-dialog v-model="showCategoryDialog" max-width="400">
      <v-card>
        <v-card-title>Nueva Categoría</v-card-title>
        <v-card-text>
          <v-alert v-if="catError" type="error" variant="tonal" class="mb-4" closable @click:close="catError = ''">
            {{ catError }}
          </v-alert>
          <v-text-field
            v-model="catForm.name"
            label="Nombre"
            :rules="[rules.required]"
            required
          />
          <v-textarea
            v-model="catForm.description"
            label="Descripción"
            rows="2"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showCategoryDialog = false">Cancelar</v-btn>
          <v-btn color="primary" variant="tonal" :loading="catSaving" @click="handleCreateCategory">
            Crear
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showWarehouseDialog" max-width="500">
      <v-card>
        <v-card-title>Nueva Bodega</v-card-title>
        <v-card-text>
          <v-alert v-if="whError" type="error" variant="tonal" class="mb-4" closable @click:close="whError = ''">
            {{ whError }}
          </v-alert>
          <v-text-field
            v-model="whForm.code"
            label="Código"
            :rules="[rules.required]"
            required
          />
          <v-text-field
            v-model="whForm.name"
            label="Nombre"
            :rules="[rules.required]"
            required
            class="mt-3"
          />
          <v-text-field
            v-model="whForm.address"
            label="Dirección"
            :rules="[rules.required]"
            required
            class="mt-3"
          />
          <v-text-field
            v-model="whForm.city"
            label="Ciudad"
            :rules="[rules.required]"
            required
            class="mt-3"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showWarehouseDialog = false; warehouseDialogTargetIndex = -1">Cancelar</v-btn>
          <v-btn color="primary" variant="tonal" :loading="whSaving" @click="handleCreateWarehouse">
            Crear
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductsStore } from '../../stores/products'
import { useCategoriesStore } from '../../stores/categories'
import { useWarehousesStore } from '../../stores/warehouses'

const route = useRoute()
const router = useRouter()
const store = useProductsStore()
const categoriesStore = useCategoriesStore()
const warehousesStore = useWarehousesStore()

const isEdit = computed(() => !!route.params.id)
const categories = computed(() => categoriesStore.categories)
const availableWarehouses = computed(() => warehousesStore.warehouses)

const saving = ref(false)
const error = ref('')
const showCategoryDialog = ref(false)
const catSaving = ref(false)
const catError = ref('')
const catForm = ref({ name: '', description: '' })
const showWarehouseDialog = ref(false)
const whSaving = ref(false)
const whError = ref('')
const whForm = ref({ code: '', name: '', address: '', city: '' })
const warehouseDialogTargetIndex = ref(-1)

const form = ref({
  name: '',
  description: '',
  price: 0,
  sku: '',
  imageUrl: '',
  active: true,
  categoryId: null,
  warehouseStocks: [],
})

const rules = {
  required: (v) => v !== null && v !== '' || 'Campo requerido',
  positive: (v) => !v || Number(v) >= 0 || 'Debe ser mayor o igual a 0',
  minZero: (v) => !v || Number(v) >= 0 || 'Debe ser mayor o igual a 0',
}

function addWarehouseRow() {
  form.value.warehouseStocks.push({ warehouseId: null, quantity: 0 })
}

function removeWarehouseRow(index) {
  form.value.warehouseStocks.splice(index, 1)
}

async function handleCreateCategory() {
  if (!catForm.value.name) {
    catError.value = 'El nombre de la categoría es obligatorio'
    return
  }
  catSaving.value = true
  catError.value = ''
  try {
    const cat = await categoriesStore.save({ name: catForm.value.name, description: catForm.value.description })
    await categoriesStore.fetchAll()
    form.value.categoryId = cat.id
    showCategoryDialog.value = false
    catForm.value = { name: '', description: '' }
  } catch (e) {
    catError.value = e.response?.data?.message || 'Error al crear categoría'
  } finally {
    catSaving.value = false
  }
}

function openWarehouseDialog(index) {
  warehouseDialogTargetIndex.value = index
  showWarehouseDialog.value = true
}

async function handleCreateWarehouse() {
  if (!whForm.value.code || !whForm.value.name || !whForm.value.address || !whForm.value.city) {
    whError.value = 'Todos los campos son obligatorios'
    return
  }
  whSaving.value = true
  whError.value = ''
  try {
    const wh = await warehousesStore.save({ ...whForm.value })
    await warehousesStore.fetchAll()
    if (warehouseDialogTargetIndex.value >= 0) {
      form.value.warehouseStocks[warehouseDialogTargetIndex.value].warehouseId = wh.id
    }
    showWarehouseDialog.value = false
    warehouseDialogTargetIndex.value = -1
    whForm.value = { code: '', name: '', address: '', city: '' }
  } catch (e) {
    whError.value = e.response?.data?.message || 'Error al crear bodega'
  } finally {
    whSaving.value = false
  }
}

async function handleSave() {
  saving.value = true
  error.value = ''
  try {
    const payload = {
      name: form.value.name,
      description: form.value.description,
      price: Number(form.value.price),
      sku: form.value.sku,
      imageUrl: form.value.imageUrl,
      active: form.value.active,
      categoryId: Number(form.value.categoryId),
    }

    if (!isEdit.value) {
      payload.warehouseStocks = form.value.warehouseStocks
        .filter((ws) => ws.warehouseId)
        .map((ws) => ({
          warehouseId: Number(ws.warehouseId),
          quantity: Number(ws.quantity),
        }))
    }

    await store.save(payload, isEdit.value ? Number(route.params.id) : null)
    router.push('/admin/productos')
  } catch (e) {
    const err = e.response?.data
    if (err?.errors) {
      error.value = Object.values(err.errors).flat().join(', ')
    } else {
      error.value = err?.message || 'Error al guardar producto'
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    categoriesStore.fetchAll(),
    warehousesStore.fetchAll(),
  ])

  if (isEdit.value) {
    try {
      const data = await store.fetchOne(Number(route.params.id))
      form.value = {
        name: data.name,
        description: data.description || '',
        price: data.price,
        sku: data.sku,
        imageUrl: data.imageUrl || '',
        active: data.active,
        categoryId: data.categoryId || null,
        warehouseStocks: data.warehouseStocks || [],
      }
    } catch (e) {
      console.error('Error loading product:', e)
    }
  }
})
</script>

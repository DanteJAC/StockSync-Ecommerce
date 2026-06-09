<template>
  <v-card elevation="2">
    <v-card-title>{{ isEdit ? 'Editar Local/Bodega' : 'Nuevo Local/Bodega' }}</v-card-title>

    <v-card-text>
      <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
        {{ error }}
      </v-alert>

      <v-form ref="formRef" @submit.prevent="handleSave">
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.code"
              label="Código"
              :rules="[rules.required]"
              required
            />
          </v-col>
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
              v-model="form.address"
              label="Dirección"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.city"
              label="Ciudad"
              :rules="[rules.required]"
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
              {{ isEdit ? 'Actualizar' : 'Crear' }}
            </v-btn>
            <v-btn variant="outlined" to="/admin/bodegas">
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
import { useWarehousesStore } from '../../stores/warehouses'

const route = useRoute()
const router = useRouter()
const store = useWarehousesStore()
const formRef = ref(null)

const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const error = ref('')

const form = ref({
  code: '',
  name: '',
  address: '',
  city: '',
})

const rules = {
  required: (v) => !!v || 'Campo requerido',
}

async function handleSave() {
  const { valid } = await formRef.value.validate()
  if (!valid) { saving.value = false; return }
  saving.value = true
  error.value = ''
  try {
    await store.save({ ...form.value }, isEdit.value ? Number(route.params.id) : null)
    router.push('/admin/bodegas')
  } catch (e) {
    const err = e.response?.data
    if (err?.errors) {
      error.value = Object.values(err.errors).flat().join(', ')
    } else {
      error.value = err?.message || 'Error al guardar bodega'
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const data = await store.fetchOne(Number(route.params.id))
      form.value = {
        code: data.code,
        name: data.name,
        address: data.address || '',
        city: data.city || '',
      }
    } catch (e) {
      console.error('Error loading warehouse:', e)
    }
  }
})
</script>

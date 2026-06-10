<template>
  <v-card elevation="2">
    <v-card-title class="d-flex flex-column flex-sm-row justify-space-between align-sm-center ga-3">
      <span>Locales / Bodegas</span>
      <v-btn color="primary" to="/admin/bodegas/nuevo" prepend-icon="mdi-plus" class="create-btn">
        Nuevo Local/Bodega
      </v-btn>
    </v-card-title>

    <v-card-text>
      <v-progress-linear v-if="loading" indeterminate color="primary" />

      <div class="table-container">
        <v-table class="text-no-wrap" density="comfortable">
          <thead>
            <tr>
              <th>Código</th>
              <th>Nombre</th>
              <th>Dirección</th>
              <th>Ciudad</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="wh in warehouses" :key="wh.id">
              <td><code>{{ wh.code }}</code></td>
              <td class="font-weight-medium">{{ wh.name }}</td>
              <td>{{ wh.address }}</td>
              <td>{{ wh.city }}</td>
              <td>
                <v-btn
                  icon="mdi-pencil"
                  variant="text"
                  color="primary"
                  size="small"
                  :to="`/admin/bodegas/editar/${wh.id}`"
                />
                <v-btn
                  icon="mdi-delete"
                  variant="text"
                  color="error"
                  size="small"
                  @click="confirmDelete(wh)"
                />
              </td>
            </tr>
            <tr v-if="!warehouses.length && !loading">
              <td colspan="5" class="text-center text-medium-emphasis py-6">
                No hay locales ni bodegas registradas
              </td>
            </tr>
          </tbody>
        </v-table>
      </div>
    </v-card-text>

    <v-dialog v-model="deleteDialog" max-width="400" width="95%">
      <v-card>
        <v-card-title class="text-wrap">Eliminar Local/Bodega</v-card-title>
        <v-card-text>
          ¿Estás seguro de eliminar "{{ warehouseToDelete?.name }}"?
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
import { useWarehousesStore } from '../../stores/warehouses'

const store = useWarehousesStore()
const warehouses = ref([])
const loading = ref(false)
const deleteDialog = ref(false)
const deleting = ref(false)
const warehouseToDelete = ref(null)

function confirmDelete(wh) {
  warehouseToDelete.value = wh
  deleteDialog.value = true
}

async function handleDelete() {
  deleting.value = true
  try {
    await store.remove(warehouseToDelete.value.id)
    warehouses.value = [...store.warehouses]
  } catch (e) {
    console.error('Error deleting warehouse:', e)
  } finally {
    deleting.value = false
    deleteDialog.value = false
    warehouseToDelete.value = null
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await store.fetchAll()
    warehouses.value = [...store.warehouses]
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>

.table-container {
  overflow-x: auto;
  width: 100%;
}

@media (max-width: 960px) {

  .table-container {
    -webkit-overflow-scrolling: touch;
  }

  :deep(.v-table) {
    min-width: 700px;
  }

}

@media (max-width: 600px) {

  :deep(.v-card-title) {
    padding: 16px;
  }

  :deep(.v-card-text) {
    padding: 12px;
  }

  .create-btn {
    width: 100%;
  }

  :deep(.v-dialog .v-card-actions) {
    flex-direction: column;
    gap: 8px;
  }

  :deep(.v-dialog .v-btn) {
    width: 100%;
  }

}

</style>

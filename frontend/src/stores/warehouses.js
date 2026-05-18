import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '../api/warehouses'

export const useWarehousesStore = defineStore('warehouses', () => {
  const warehouses = ref([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await api.getWarehouses()
      warehouses.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchOne(id) {
    const { data } = await api.getWarehouse(id)
    return data
  }

  async function save(warehouseData, id) {
    if (id) {
      const { data } = await api.updateWarehouse(id, warehouseData)
      return data
    }
    const { data } = await api.createWarehouse(warehouseData)
    return data
  }

  async function remove(id) {
    await api.deleteWarehouse(id)
    warehouses.value = warehouses.value.filter((w) => w.id !== id)
  }

  return { warehouses, loading, fetchAll, fetchOne, save, remove }
})

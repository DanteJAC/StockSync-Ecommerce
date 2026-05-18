import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '../api/stock'

export const useStockStore = defineStore('stock', () => {
  const stocks = ref([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await api.getStocks()
      stocks.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchByWarehouse(warehouseId) {
    const { data } = await api.getStocksByWarehouse(warehouseId)
    return data
  }

  async function add(stockData) {
    const { data } = await api.addStock(stockData)
    return data
  }

  async function update(stockData) {
    const { data } = await api.updateStock(stockData)
    return data
  }

  async function transfer(transferData) {
    const { data } = await api.transferStock(transferData)
    return data
  }

  async function remove(id) {
    await api.deleteStock(id)
    stocks.value = stocks.value.filter((s) => s.id !== id)
  }

  return { stocks, loading, fetchAll, fetchByWarehouse, add, update, transfer, remove }
})

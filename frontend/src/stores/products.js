import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '../api/products'

export const useProductsStore = defineStore('products', () => {
  const products = ref([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await api.getProducts()
      products.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchByCategory(categoryId) {
    const { data } = await api.getProductsByCategory(categoryId)
    return data
  }

  async function fetchOne(id) {
    const { data } = await api.getProduct(id)
    return data
  }

  async function save(productData, id) {
    if (id) {
      const { data } = await api.updateProduct(id, productData)
      return data
    }
    const { data } = await api.createProduct(productData)
    return data
  }

  async function remove(id) {
    await api.deleteProduct(id)
    products.value = products.value.filter((p) => p.id !== id)
  }

  return { products, loading, fetchAll, fetchByCategory, fetchOne, save, remove }
})

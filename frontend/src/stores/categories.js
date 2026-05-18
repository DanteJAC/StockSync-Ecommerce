import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '../api/categories'

export const useCategoriesStore = defineStore('categories', () => {
  const categories = ref([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await api.getCategories()
      categories.value = data
    } finally {
      loading.value = false
    }
  }

  async function save(categoryData, id) {
    if (id) {
      const { data } = await api.updateCategory(id, categoryData)
      return data
    }
    const { data } = await api.createCategory(categoryData)
    return data
  }

  async function remove(id) {
    await api.deleteCategory(id)
    categories.value = categories.value.filter((c) => c.id !== id)
  }

  return { categories, loading, fetchAll, save, remove }
})

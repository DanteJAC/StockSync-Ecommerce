<template>
  <v-main>
    <PublicHeader />

    <v-container class="py-8">
      <h1 class="text-h3 font-weight-bold mb-6">Productos</h1>

      <v-row class="mb-4">
        <v-col cols="12" sm="6" md="4">
          <v-select
            v-model="selectedCategory"
            :items="categories"
            item-title="name"
            item-value="id"
            label="Filtrar por categoría"
            clearable
            @update:model-value="filterProducts"
          />
        </v-col>
        <v-col cols="12" sm="6" md="4">
          <v-text-field
            v-model="searchQuery"
            label="Buscar producto"
            prepend-inner-icon="mdi-magnify"
            clearable
            @update:model-value="debouncedSearch"
          />
        </v-col>
      </v-row>

      <v-progress-linear v-if="loading" indeterminate color="primary" />

      <v-row v-else>
        <v-col v-for="product in products" :key="product.id" cols="12" sm="6" md="4" lg="3">
          <v-card elevation="2" class="h-100 d-flex flex-column">
            <v-card-title class="text-body-1 font-weight-bold">
              {{ product.name }}
            </v-card-title>
            <v-card-subtitle>{{ product.categoryName }}</v-card-subtitle>
            <v-card-text class="flex-grow-1">
              <div class="text-h6 font-weight-bold text-primary mb-2">
                ${{ formatPrice(product.price) }}
              </div>
              <div class="text-body-2 mb-1">
                SKU: {{ product.sku }}
              </div>
              <v-chip
                :color="product.stock > 0 ? 'success' : 'error'"
                size="small"
                variant="tonal"
              >
                {{ product.stock > 0 ? `${product.stock} en stock` : 'Sin stock' }}
              </v-chip>
              <div v-if="product.description" class="text-body-2 text-medium-emphasis mt-2">
                {{ truncate(product.description, 100) }}
              </div>
            </v-card-text>
          </v-card>
        </v-col>
        <v-col v-if="!products.length" cols="12">
          <v-alert type="info" variant="tonal">
            No se encontraron productos.
          </v-alert>
        </v-col>
      </v-row>
    </v-container>

    <PublicFooter />
  </v-main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { getProducts, searchProducts, getProductsByCategory } from '../../api/products'
import { getCategories } from '../../api/categories'

const products = ref([])
const categories = ref([])
const loading = ref(false)
const selectedCategory = ref(null)
const searchQuery = ref('')
let debounceTimer = null

function formatPrice(price) {
  return Number(price).toLocaleString('es-CL')
}

function truncate(text, max) {
  return text?.length > max ? text.slice(0, max) + '...' : text
}

function debouncedSearch(val) {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    if (val) {
      searchProducts(val).then(({ data }) => { products.value = data })
    } else {
      loadProducts()
    }
  }, 300)
}

async function filterProducts() {
  loading.value = true
  try {
    if (selectedCategory.value) {
      const { data } = await getProductsByCategory(selectedCategory.value)
      products.value = data
    } else {
      await loadProducts()
    }
  } finally {
    loading.value = false
  }
}

async function loadProducts() {
  const { data } = await getProducts()
  products.value = data
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      loadProducts(),
      getCategories().then(({ data }) => { categories.value = data }),
    ])
  } finally {
    loading.value = false
  }
})
</script>

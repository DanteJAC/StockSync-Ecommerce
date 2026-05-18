import client from './client'

export function getProducts() {
  return client.get('/v1/products')
}

export function getProduct(id) {
  return client.get(`/v1/products/${id}`)
}

export function getProductsByCategory(categoryId) {
  return client.get(`/v1/products/category/${categoryId}`)
}

export function searchProducts(name) {
  return client.get('/v1/products/search', { params: { name } })
}

export function createProduct(data) {
  return client.post('/v1/products', data)
}

export function updateProduct(id, data) {
  return client.put(`/v1/products/${id}`, data)
}

export function deleteProduct(id) {
  return client.delete(`/v1/products/${id}`)
}

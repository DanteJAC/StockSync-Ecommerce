import client from './client'

export function getCategories() {
  return client.get('/v1/categories')
}

export function getCategory(id) {
  return client.get(`/v1/categories/${id}`)
}

export function createCategory(data) {
  return client.post('/v1/categories', data)
}

export function updateCategory(id, data) {
  return client.put(`/v1/categories/${id}`, data)
}

export function deleteCategory(id) {
  return client.delete(`/v1/categories/${id}`)
}

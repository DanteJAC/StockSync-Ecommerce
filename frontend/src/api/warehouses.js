import client from './client'

export function getWarehouses() {
  return client.get('/v1/warehouses')
}

export function getWarehouse(id) {
  return client.get(`/v1/warehouses/${id}`)
}

export function createWarehouse(data) {
  return client.post('/v1/warehouses', data)
}

export function updateWarehouse(id, data) {
  return client.put(`/v1/warehouses/${id}`, data)
}

export function deleteWarehouse(id) {
  return client.delete(`/v1/warehouses/${id}`)
}

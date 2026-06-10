import client from './client'

export function getStocks() {
  return client.get('/v1/stocks')
}

export function getStocksByWarehouse(warehouseId) {
  return client.get(`/v1/stocks/warehouse/${warehouseId}`)
}

export function getStocksByProduct(productId) {
  return client.get(`/v1/stocks/product/${productId}`)
}

export function getMovements(type, warehouseId) {
  return client.get('/v1/stocks/movements', { params: { type, warehouseId } })
}

export function addStock(data) {
  return client.post('/v1/stocks/add', data)
}

export function updateStock(data) {
  return client.put('/v1/stocks/update', data)
}

export function transferStock(data) {
  return client.post('/v1/stocks/transfer', data)
}

export function deleteStock(id) {
  return client.delete(`/v1/stocks/${id}`)
}

export function processSale(data) {
  return client.post('/v1/stocks/sale', data)
}

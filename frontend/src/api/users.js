import client from './client'

export function getInvitedUsers() {
  return client.get('/users/invited')
}

export function getAllUsers() {
  return client.get('/users')
}

export function inviteUser(email, role, assignedWarehouseId) {
  return client.post('/users/invite', { email, role, assignedWarehouseId })
}

export function updateUser(id, data) {
  return client.patch(`/users/${id}`, data)
}

export function deleteUser(id) {
  return client.delete(`/users/${id}`)
}

import client from './client'

export function login(email, password) {
  return client.post('/login', { email, password })
}

export function register(nombre, email, password) {
  return client.post('/register', { nombre, email, password })
}

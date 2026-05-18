import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('jwt') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const userEmail = computed(() => user.value?.email || '')
  const userName = computed(() => user.value?.nombre || '')

  async function login(email, password) {
    const { data } = await apiLogin(email, password)
    token.value = data.token
    user.value = { email: data.email, nombre: data.nombre, role: data.role }
    localStorage.setItem('jwt', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
    return data
  }

  async function register(nombre, email, password) {
    const { data } = await apiRegister(nombre, email, password)
    token.value = data.token
    user.value = { email: data.email, nombre: data.nombre, role: data.role }
    localStorage.setItem('jwt', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
    return data
  }

  function clear() {
    token.value = null
    user.value = null
    localStorage.removeItem('jwt')
    localStorage.removeItem('user')
  }

  function logout() {
    clear()
  }

  return { token, user, isAuthenticated, isAdmin, userEmail, userName, login, register, logout, clear }
})

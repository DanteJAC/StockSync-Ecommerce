import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister, changePassword as apiChangePassword } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('jwt') || null)
    const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
    // Comprobación estricta de booleano guardado como string
    const mustChangePassword = ref(localStorage.getItem('mustChangePassword') === 'true')

    const isAuthenticated = computed(() => !!token.value)
    const isAdmin = computed(() => user.value?.role === 'ADMIN')
    const isLocal = computed(() => user.value?.role === 'LOCAL')
    const isBodega = computed(() => user.value?.role === 'BODEGA')
    const userRole = computed(() => user.value?.role || '')
    const userEmail = computed(() => user.value?.email || '')
    const userName = computed(() => user.value?.nombre || '')

    // Función auxiliar interna para guardar los datos de sesión y evitar repetir código
    function setSessionData(data, forcePasswordChange = false) {
        token.value = data.token
        user.value = { email: data.email, nombre: data.nombre, role: data.role }
        mustChangePassword.value = forcePasswordChange

        localStorage.setItem('jwt', data.token)
        localStorage.setItem('user', JSON.stringify(user.value))
        localStorage.setItem('mustChangePassword', String(forcePasswordChange)) // Forzado estricto a string
    }

    async function login(email, password) {
        const { data } = await apiLogin(email, password)
        const forceChange = data.forcePasswordChange || false
        setSessionData(data, forceChange)
        return data
    }

    async function register(nombre, email, password) {
        const { data } = await apiRegister(nombre, email, password)
        setSessionData(data, false)
        return data
    }

    async function changePassword(oldPassword, newPassword) {
        await apiChangePassword(oldPassword, newPassword)
        mustChangePassword.value = false
        localStorage.setItem('mustChangePassword', 'false')
    }

    function clear() {
        token.value = null
        user.value = null
        mustChangePassword.value = false
        localStorage.removeItem('jwt')
        localStorage.removeItem('user')
        localStorage.removeItem('mustChangePassword')
    }

    function logout() {
        clear()
    }

    return {
        token, user, mustChangePassword,
        isAuthenticated, isAdmin, isLocal, isBodega, userRole, userEmail, userName,
        login, register, changePassword, logout, clear
    }
})
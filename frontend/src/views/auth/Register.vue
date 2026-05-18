<template>
  <v-main>
    <PublicHeader />

    <v-container class="py-12" max-width="480">
      <v-card class="pa-6" elevation="4">
        <v-card-title class="text-h4 font-weight-bold text-center mb-2">
          Crear Cuenta
        </v-card-title>

        <v-card-text>
          <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
            {{ error }}
          </v-alert>

          <v-form @submit.prevent="handleRegister">
            <v-text-field
              v-model="nombre"
              label="Nombre"
              prepend-inner-icon="mdi-account"
              :rules="[rules.required]"
              required
            />

            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              prepend-inner-icon="mdi-email"
              :rules="[rules.required, rules.email]"
              required
            />

            <v-text-field
              v-model="password"
              label="Contraseña"
              type="password"
              prepend-inner-icon="mdi-lock"
              :rules="[rules.required, rules.min6]"
              required
            />

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              class="mt-4"
              :loading="loading"
            >
              Registrarse
            </v-btn>
          </v-form>

          <div class="text-center mt-4">
            <span class="text-body-2 text-medium-emphasis">¿Ya tienes cuenta?</span>
            <v-btn variant="text" color="primary" to="/login" size="small">
              Iniciar Sesión
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-container>

    <PublicFooter />
  </v-main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const nombre = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const rules = {
  required: (v) => !!v || 'Campo requerido',
  email: (v) => /.+@.+/.test(v) || 'Email inválido',
  min6: (v) => !v || v.length >= 6 || 'Mínimo 6 caracteres',
}

async function handleRegister() {
  loading.value = true
  error.value = ''
  try {
    await auth.register(nombre.value, email.value, password.value)
    router.push('/admin')
  } catch (e) {
    const err = e.response?.data
    if (err?.errors) {
      error.value = Object.values(err.errors).flat().join(', ')
    } else {
      error.value = err?.message || 'Error al registrarse'
    }
  } finally {
    loading.value = false
  }
}
</script>

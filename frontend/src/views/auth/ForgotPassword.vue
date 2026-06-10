<template>
  <v-container fluid class="fill-height bg-grey-lighten-4">
    <v-row class="mx-0" align="center" justify="center">
      <v-col cols="12" sm="8" md="4" lg="3">
        <v-card class="elevation-12 rounded-xl" :loading="loading">
          <v-card-title class="text-center bg-primary text-white py-4 text-h5 font-weight-bold">
            Recuperar Contraseña
          </v-card-title>
          <v-card-text class="pa-6">
            <p class="text-body-2 mb-4 text-center">
              Ingresa tu correo electrónico y te enviaremos las instrucciones para restablecer tu contraseña.
            </p>
            <v-alert v-if="successMessage" type="success" variant="tonal" class="mb-4">
              {{ successMessage }}
            </v-alert>
            <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
              {{ error }}
            </v-alert>

            <v-form @submit.prevent="submit" v-if="!successMessage">
              <v-text-field
                v-model="email"
                label="Correo Electrónico"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                type="email"
                required
                :rules="[v => !!v || 'El correo es requerido', v => /.+@.+\..+/.test(v) || 'El correo debe ser válido']"
              ></v-text-field>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                class="mt-4 rounded-lg"
                :loading="loading"
              >
                Enviar Enlace
              </v-btn>
            </v-form>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions class="justify-center py-4 bg-grey-lighten-5">
            <v-btn variant="text" color="primary" to="/login">Volver al login</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { authService } from '../../api/auth'

const email = ref('')
const loading = ref(false)
const successMessage = ref('')
const error = ref('')

const submit = async () => {
  if (!email.value || !/.+@.+\..+/.test(email.value)) return

  loading.value = true
  error.value = ''
  successMessage.value = ''

  try {
    const response = await authService.forgotPassword(email.value)
    successMessage.value = response.data.message
  } catch (e) {
    error.value = e.response?.data?.message || 'Error al solicitar el restablecimiento. Verifica el correo.'
  } finally {
    loading.value = false
  }
}
</script>
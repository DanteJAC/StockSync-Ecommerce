<template>
  <v-container class="fill-height" fluid>
    <v-row class="mx-0" align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="elevation-12">
          <v-toolbar color="primary" dark flat>
            <v-toolbar-title>Restablecer Contraseña</v-toolbar-title>
          </v-toolbar>
          <v-card-text>
            <p v-if="message" :class="isError ? 'text-red' : 'text-green'">{{ message }}</p>
            <v-form @submit.prevent="handleResetPassword" v-if="!completed">
              <v-text-field
                label="Nueva Contraseña"
                v-model="password"
                :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPassword ? 'text' : 'password'"
                @click:append-inner="showPassword = !showPassword"
                required
                :rules="[v => !!v || 'La contraseña es requerida']"
              ></v-text-field>
              <v-text-field
                label="Confirmar Nueva Contraseña"
                v-model="confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                required
                :rules="[
                  v => !!v || 'La confirmación es requerida',
                  v => v === password || 'Las contraseñas no coinciden'
                ]"
              ></v-text-field>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn type="submit" color="primary" :loading="loading">
                  Cambiar Contraseña
                </v-btn>
              </v-card-actions>
            </v-form>
             <v-card-actions v-if="completed">
                <v-btn block color="primary" to="/login">Ir a Iniciar Sesión</v-btn>
            </v-card-actions>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

const password = ref('');
const confirmPassword = ref('');
const token = ref(null);

const loading = ref(false);
const message = ref('');
const isError = ref(false);
const completed = ref(false);
const showPassword = ref(false);


onMounted(() => {
  token.value = route.query.token;
  if (!token.value) {
    message.value = 'Token de restablecimiento no encontrado.';
    isError.value = true;
  }
});

const handleResetPassword = async () => {
  if (password.value !== confirmPassword.value) {
    message.value = 'Las contraseñas no coinciden.';
    isError.value = true;
    return;
  }
  if (!token.value) {
    message.value = 'Token inválido o expirado.';
    isError.value = true;
    return;
  }

  loading.value = true;
  isError.value = false;
  message.value = '';

  try {
    await axios.post('/api/auth/reset-password', {
      token: token.value,
      newPassword: password.value,
    });
    message.value = '¡Contraseña actualizada con éxito! Serás redirigido al login.';
    completed.value = true;
    setTimeout(() => router.push('/login'), 3000);
  } catch (error) {
    isError.value = true;
    message.value = error.response?.data?.message || 'Error al restablecer la contraseña. El token puede ser inválido o haber expirado.';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <v-container fluid class="fill-height bg-grey-lighten-4">
    <v-row class="mx-0" align="center" justify="center">
      <v-col cols="12" sm="8" md="4" lg="3">
        <v-card class="elevation-12 rounded-xl" :loading="loading">
          <v-toolbar color="primary" dark flat>
            <v-toolbar-title>Restablecer Contraseña</v-toolbar-title>
          </v-toolbar>
          <v-card-text class="pa-6">
            <v-alert v-if="message" :type="isError ? 'error' : 'success'" variant="tonal" class="mb-4">
              {{ message }}
            </v-alert>

            <v-form @submit.prevent="handleResetPassword" v-if="!completed">
              <v-text-field
                v-model="password"
                label="Nueva Contraseña"
                :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPassword ? 'text' : 'password'"
                @click:append-inner="showPassword = !showPassword"
                required
                :rules="[rules.required, rules.minLength, rules.uppercase, rules.lowercase, rules.number]"
                hint="Mínimo 8 caracteres, con mayúsculas, minúsculas y números."
                persistent-hint
              ></v-text-field>

              <v-text-field
                v-model="confirmPassword"
                label="Confirmar Nueva Contraseña"
                :type="showPassword ? 'text' : 'password'"
                required
                :rules="[rules.required, rules.match]"
                class="mt-4"
              ></v-text-field>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                class="mt-4 rounded-lg"
                :loading="loading"
              >
                Cambiar Contraseña
              </v-btn>
            </v-form>

            <v-card-actions v-if="completed" class="justify-center mt-4">
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
import { authService } from '../../api/auth'; // CORREGIDO: Usar el servicio centralizado

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

const rules = {
  required: (v) => !!v || 'Campo requerido',
  minLength: (v) => (v && v.length >= 8) || 'Mínimo 8 caracteres',
  uppercase: (v) => /[A-Z]/.test(v) || 'Debe contener al menos una mayúscula',
  lowercase: (v) => /[a-z]/.test(v) || 'Debe contener al menos una minúscula',
  number: (v) => /\d/.test(v) || 'Debe contener al menos un número',
  match: (v) => v === password.value || 'Las contraseñas no coinciden',
};

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
    // CORREGIDO: Llamada a la API a través del servicio con la URL correcta
    await authService.resetPassword(token.value, password.value);

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
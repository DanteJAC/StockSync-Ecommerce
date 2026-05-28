<template>
  <v-main>
    <PublicHeader />

    <section class="hero-section hero-background d-flex align-center">
      <v-container class="text-center">
        <v-row justify="center">
          <v-col cols="12" md="8">

            <v-icon size="80" color="primary" class="mb-6 floating-icon">mdi-package-variant-closed</v-icon>

            <h1 class="text-h1 font-weight-bold mb-6 text-white">
              StockSync
            </h1>

            <p class="text-h5 text-grey-lighten-1 mb-8">
              Inventario sin errores, negocio sin límites.
            </p>
            <p  class="text-body-1 text-grey-lighten-1 mx-auto mb-10 hero-description"
                style="max-width: 600px; margin: 0 auto;"
            >
              Sistema de gestión de inventario y E-commerce diseñado para optimizar el control de stock,
              gestionar productos y facilitar la administración en tiempo real.
            </p>
            <br>
            <v-btn
                to="/login"
                color="primary"
                size="x-large"
                variant="flat"
                rounded="xl"
                elevation="4"
                class="mr-4 mb-2 px-8"
            >
              <v-icon start>mdi-login</v-icon>
              Iniciar Sesión
            </v-btn>
            <v-btn
                to="/register"
                size="x-large"
                variant="outlined"
                rounded="xl"
                class="mb-2 px-8 text-white"
                style="border-color: white;"
            >
              <v-icon start color="white">mdi-account-plus</v-icon>
              Registrarse
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <section class="features-section py-12 bg-surface">
      <v-container>
        <h2 class="text-h3 font-weight-bold text-center mb-10">Características</h2>
        <v-row>
          <v-col v-for="feature in features" :key="feature.title" cols="12" md="4">
            <v-card class="pa-6 text-center h-100" elevation="2">
              <v-icon size="48" :color="feature.color" class="mb-4">{{ feature.icon }}</v-icon>
              <h3 class="text-h5 font-weight-bold mb-2">{{ feature.title }}</h3>
              <p class="text-body-1 text-medium-emphasis">{{ feature.description }}</p>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <section class="cta-section py-12 text-center">
      <v-container>
        <h2 class="text-h3 font-weight-bold text-white mb-4">¿Listo para gestionar tu inventario?</h2>
        <v-btn
          to="/register"
          size="x-large"
          variant="flat"
          color="white"
          class="text-primary font-weight-bold"
        >
          <v-icon start>mdi-rocket-launch</v-icon>
          Comenzar ahora
        </v-btn>
      </v-container>
    </section>

    <PublicFooter />
  </v-main>
</template>

<script setup>
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'

const features = [
  {
    icon: 'mdi-package-variant-closed',
    color: 'primary',
    title: 'Gestión de Productos',
    description: 'Administra tu catálogo con SKU, precios, categorías y control de stock centralizado.',
  },
  {
    icon: 'mdi-warehouse',
    color: 'success',
    title: 'Múltiples Bodegas',
    description: 'Organiza tu inventario en diferentes bodegas y transfiere stock entre ellas.',
  },
  {
    icon: 'mdi-chart-box',
    color: 'info',
    title: 'Trazabilidad de Stock',
    description: 'Registra cada movimiento: entradas, salidas, ajustes y transferencias.',
  },
  {
    icon: 'mdi-shield-account',
    color: 'warning',
    title: 'Control de Acceso',
    description: 'Autenticación segura con JWT y roles de usuario para administración.',
  },
  {
    icon: 'mdi-database-sync',
    color: 'secondary',
    title: 'Datos en Tiempo Real',
    description: 'Toda la información actualizada al instante con arquitectura REST API.',
  },
  {
    icon: 'mdi-responsive',
    color: 'primary',
    title: 'Diseño Adaptable',
    description: 'Interfaz moderna y responsiva accesible desde cualquier dispositivo.',
  },
]
</script>


<style scoped>

.bg-surface {
  background-color: rgb(var(--v-theme-surface));
}

.cta-section {
  background-color: rgb(var(--v-theme-primary)) !important;
  //background: linear-gradient(135deg, #166534, #4ADE80);
}


/* HERO */
.hero-background {
  background-image:
      linear-gradient(
          rgba(2, 3, 0, 0.72),
          rgba(2, 3, 0, 0.72)
      ),
      url('public/background2.jpg');

  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;

  min-height: 92vh;

  display: flex;
  align-items: center;
}

/* TEXTO */

.hero-description {
  max-width: 700px;
  line-height: 1.8;
}

h1,
.hero-description,
.text-h5 {
  //text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}

/* BOTONES */

.v-btn {
  transition: all 0.3s ease;
}

.v-btn:hover {
  transform: translateY(-3px);
}

.v-app-bar {
  backdrop-filter: blur(10px);
  background: rgba(255,255,255,0.75) !important;
}

.v-theme--dark .v-app-bar {
  background: rgba(18,18,18,0.75) !important;
}


/* ICONO */

.floating-icon {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {

  0% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-8px);
  }

  100% {
    transform: translateY(0px);
  }
}

/* CARDS */

.v-card {
  backdrop-filter: blur(8px);
  transition: 0.3s ease;
}

.v-card:hover {
  transform: translateY(-5px);
}

</style>
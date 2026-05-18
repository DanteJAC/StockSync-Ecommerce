<template>
  <v-footer class="mt-auto" app>
    <v-container class="d-flex flex-wrap justify-center justify-sm-space-between align-center py-4">
      <div class="text-body-2 text-medium-emphasis">
        &copy; {{ new Date().getFullYear() }} StockSync. Todos los derechos reservados.
      </div>
      <div class="d-flex ga-4 mt-2 mt-sm-0">
        <v-btn
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          variant="text"
          size="small"
          class="text-body-2"
        >
          {{ link.label }}
        </v-btn>
      </div>
    </v-container>
  </v-footer>
</template>

<script setup>
const links = [
  { to: '/', label: 'Inicio' },
]
</script>

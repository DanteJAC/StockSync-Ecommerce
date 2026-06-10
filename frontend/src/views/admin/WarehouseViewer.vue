<template>
  <v-card elevation="2">
    <v-card-title class="d-flex justify-space-between align-center">
      <span>{{ isLocalView ? 'Visor de Locales' : 'Visor de Bodegas' }}</span>
    </v-card-title>

    <v-card-text>
      <v-alert v-if="!warehouses.length && !loading" type="info" variant="tonal" class="mb-4">
        No hay almacenes registrados en el sistema.
      </v-alert>

      <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

      <v-row class="mx-0" v-if="warehouses.length > 0">
        <v-col v-for="wh in warehouses" :key="wh.id" cols="12" sm="6" md="4">
          <v-card variant="outlined" class="h-100 d-flex flex-column">
            <v-card-item>
              <template v-slot:prepend>
                <v-icon :icon="isLocalView ? 'mdi-store' : 'mdi-warehouse'" size="large" color="primary" class="mr-3" />
              </template>
              <v-card-title class="text-wrap">{{ wh.name }}</v-card-title>
              <v-card-subtitle>Cód: {{ wh.code }}</v-card-subtitle>
            </v-card-item>

            <v-card-text class="flex-grow-1">
              <div class="d-flex align-center mb-1">
                <v-icon icon="mdi-map-marker" size="small" class="mr-2 opacity-70" />
                <span class="text-body-2">{{ wh.address || 'Sin dirección' }}</span>
              </div>
              <div class="d-flex align-center">
                <v-icon icon="mdi-city" size="small" class="mr-2 opacity-70" />
                <span class="text-body-2">{{ wh.city || 'Sin ciudad' }}</span>
              </div>
            </v-card-text>

            <v-divider />

            <v-card-actions class="pa-3">
              <v-btn
                color="primary"
                variant="elevated"
                block
                prepend-icon="mdi-login-variant"
                @click="entrarComo(wh.id)"
              >
                Entrar como {{ isLocalView ? 'Local' : 'Bodega' }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWarehousesStore } from '../../stores/warehouses'
import { useAuthStore } from '../../stores/auth'
import { useUsersStore } from '../../stores/users'

const route = useRoute()
const router = useRouter()
const store = useWarehousesStore()
const authStore = useAuthStore()
const usersStore = useUsersStore()

const loading = ref(false)
const warehouses = ref([])

// Check if current route is for local or bodega view
const isLocalView = computed(() => route.path.includes('/visores/local'))

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      store.fetchAll(),
      usersStore.fetchAll()
    ])
    
    const requiredRole = isLocalView.value ? 'LOCAL' : 'BODEGA'
    warehouses.value = store.warehouses.filter(wh => {
      // 1. Check if there's a user explicitly assigned to this warehouse with the required role
      const hasUserWithRole = usersStore.users.some(u => u.assignedWarehouse?.id === wh.id && u.role === requiredRole)
      if (hasUserWithRole) return true
      
      // 2. Check if there is ANY user assigned with the OPPOSITE role (to exclude it)
      const oppositeRole = isLocalView.value ? 'BODEGA' : 'LOCAL'
      const hasUserWithOppositeRole = usersStore.users.some(u => u.assignedWarehouse?.id === wh.id && u.role === oppositeRole)
      if (hasUserWithOppositeRole) return false

      // 3. If no users are assigned, try to guess by its name or code
      const nameOrCode = `${wh.name} ${wh.code}`.toLowerCase()
      if (isLocalView.value) {
        return nameOrCode.includes('local') || nameOrCode.includes('sucursal') || (!nameOrCode.includes('bodega') && !nameOrCode.includes('centro'))
      } else {
        return nameOrCode.includes('bodega') || nameOrCode.includes('centro') || nameOrCode.includes('almacen')
      }
    })
  } finally {
    loading.value = false
  }
})

function entrarComo(warehouseId) {
  authStore.setAdminViewWarehouseId(warehouseId)
  if (isLocalView.value) {
    router.push('/local')
  } else {
    router.push('/bodega')
  }
}
</script>

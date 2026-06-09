<template>
  <v-container fluid>
    <h1 class="text-h4 mb-6">Despachos</h1>

    <v-row>
      <v-col
          v-for="despacho in despachos"
          :key="despacho.id"
          cols="12"
          md="6"
      >
        <v-card>
          <v-card-title>
            Solicitud #{{ despacho.id }}
          </v-card-title>

          <v-card-text>
            <p><strong>Local:</strong> {{ despacho.local }}</p>
            <p><strong>Producto:</strong> {{ despacho.producto }}</p>
            <p><strong>Cantidad:</strong> {{ despacho.cantidad }}</p>

            <v-chip :color="getColor(despacho.estado)">
              {{ despacho.estado }}
            </v-chip>
          </v-card-text>

          <v-card-actions>
            <v-btn
                v-if="despacho.estado === 'APROBADA'"
                color="warning"
                @click="despacho.estado='PREPARANDO'"
            >
              Iniciar Preparación
            </v-btn>

            <v-btn
                v-if="despacho.estado === 'PREPARANDO'"
                color="primary"
                @click="despacho.estado='EN_CAMINO'"
            >
              Despachar
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'

const despachos = ref([
  {
    id: 1,
    local: 'Providencia',
    producto: 'Coca Cola',
    cantidad: 50,
    estado: 'APROBADA'
  }
])

function getColor(estado) {
  if (estado === 'APROBADA') return 'success'
  if (estado === 'PREPARANDO') return 'warning'
  if (estado === 'EN_CAMINO') return 'primary'

  return 'grey'
}
</script>
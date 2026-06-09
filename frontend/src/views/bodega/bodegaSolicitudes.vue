<template>
  <v-container fluid>
    <h1 class="text-h4 mb-6">Solicitudes de Reposición</h1>

    <v-row>
      <v-col
          v-for="solicitud in solicitudes"
          :key="solicitud.id"
          cols="12"
      >
        <v-card>
          <v-card-title>
            Solicitud #{{ solicitud.id }}
          </v-card-title>

          <v-card-text>
            <p><strong>Local:</strong> {{ solicitud.local }}</p>
            <p><strong>Producto:</strong> {{ solicitud.producto }}</p>
            <p><strong>Cantidad:</strong> {{ solicitud.cantidad }}</p>

            <v-chip color="warning">
              Pendiente
            </v-chip>
          </v-card-text>

          <v-card-actions>
            <v-btn
                color="success"
                @click="aprobar(solicitud)"
            >
              Aprobar
            </v-btn>

            <v-btn
                color="error"
                variant="outlined"
            >
              Rechazar
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
const solicitudes = [
  {
    id: 1,
    local: 'Local Providencia',
    producto: 'Coca Cola',
    cantidad: 50
  },
  {
    id: 2,
    local: 'Local Maipú',
    producto: 'Papas Fritas',
    cantidad: 30
  }
]

function aprobar(solicitud) {
  alert(`Solicitud ${solicitud.id} aprobada`)
}
</script>
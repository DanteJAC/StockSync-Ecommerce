<template>
  <v-container fluid class="pa-6">

    <h1 class="text-h4 font-weight-bold mb-6">
      Dashboard de Bodega
    </h1>

    <!-- Tarjetas resumen -->
    <v-row class="mb-6">

      <v-col cols="12" sm="6" md="3">
        <v-card elevation="4" rounded="lg">
          <v-card-text class="text-center py-6">

            <v-icon
                size="50"
                color="primary"
            >
              mdi-package-variant
            </v-icon>

            <div class="text-subtitle-1 mt-3">
              Total Stock
            </div>

            <div class="text-h3 font-weight-bold">
              {{ totalStock }}
            </div>

          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card elevation="4" rounded="lg">
          <v-card-text class="text-center py-6">

            <v-icon
                size="50"
                color="success"
            >
              mdi-store
            </v-icon>

            <div class="text-subtitle-1 mt-3">
              Locales Activos
            </div>

            <div class="text-h3 font-weight-bold">
              {{ localesActivos }}
            </div>

          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card elevation="4" rounded="lg">
          <v-card-text class="text-center py-6">

            <v-icon
                size="50"
                color="warning"
            >
              mdi-clipboard-list
            </v-icon>

            <div class="text-subtitle-1 mt-3">
              Solicitudes Pendientes
            </div>

            <div class="text-h3 font-weight-bold">
              {{ solicitudesPendientes }}
            </div>

          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card elevation="4" rounded="lg">
          <v-card-text class="text-center py-6">

            <v-icon
                size="50"
                color="info"
            >
              mdi-truck-delivery
            </v-icon>

            <div class="text-subtitle-1 mt-3">
              Despachos Hoy
            </div>

            <div class="text-h3 font-weight-bold">
              {{ despachosHoy }}
            </div>

          </v-card-text>
        </v-card>
      </v-col>

    </v-row>

    <!-- Solicitudes -->
    <v-card
        elevation="4"
        rounded="lg"
    >
      <v-card-title class="text-h6 font-weight-bold">
        Últimas Solicitudes
      </v-card-title>

      <v-divider />

      <v-table>
        <thead>
        <tr>
          <th>ID Solicitud</th>
          <th>Local</th>
          <th>Fecha</th>
          <th>Estado</th>
        </tr>
        </thead>

        <tbody>
        <tr
            v-for="solicitud in solicitudes"
            :key="solicitud.id"
        >
          <td>{{ solicitud.id }}</td>

          <td>{{ solicitud.local }}</td>

          <td>{{ solicitud.fecha }}</td>

          <td>
            <v-chip
                :color="getEstadoColor(solicitud.estado)"
                size="small"
                variant="tonal"
            >
              {{ solicitud.estado }}
            </v-chip>
          </td>
        </tr>
        </tbody>
      </v-table>

    </v-card>

  </v-container>
</template>

<script setup>

const totalStock = 842
const localesActivos = 12
const solicitudesPendientes = 5
const despachosHoy = 3

const solicitudes = [
  {
    id: 'SOL-001',
    local: 'Local Santiago',
    fecha: '06/06/2026',
    estado: 'Pendiente'
  },
  {
    id: 'SOL-002',
    local: 'Local Maipú',
    fecha: '06/06/2026',
    estado: 'Aprobada'
  },
  {
    id: 'SOL-003',
    local: 'Local Puente Alto',
    fecha: '05/06/2026',
    estado: 'Pendiente'
  },
  {
    id: 'SOL-004',
    local: 'Local La Florida',
    fecha: '05/06/2026',
    estado: 'Despachada'
  }
]

function getEstadoColor(estado) {
  switch (estado) {
    case 'Pendiente':
      return 'warning'

    case 'Aprobada':
      return 'success'

    case 'Despachada':
      return 'info'

    case 'Rechazada':
      return 'error'

    default:
      return 'grey'
  }
}

</script>
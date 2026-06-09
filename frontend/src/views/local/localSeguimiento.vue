<template>
  <v-container>
    <h1 class="text-h4 font-weight-bold mb-6">
      Seguimiento de Solicitudes
    </h1>

    <v-card>
      <v-card-text>
        <v-table>
          <thead>
          <tr>
            <th>ID</th>
            <th>Producto</th>
            <th>Cantidad</th>
            <th>Fecha</th>
            <th>Estado</th>
          </tr>
          </thead>

          <tbody>
          <tr
              v-for="solicitud in solicitudes"
              :key="solicitud.id"
          >
            <td>#{{ solicitud.id }}</td>
            <td>{{ solicitud.producto }}</td>
            <td>{{ solicitud.cantidad }}</td>
            <td>{{ solicitud.fecha }}</td>

            <td>
              <v-chip
                  :color="estadoColor(solicitud.estado)"
              >
                {{ solicitud.estado }}
              </v-chip>
            </td>
          </tr>
          </tbody>
        </v-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
const solicitudes = [
  {
    id: 1,
    producto: 'Coca Cola',
    cantidad: 20,
    fecha: '08/06/2026',
    estado: 'Pendiente'
  },
  {
    id: 2,
    producto: 'Pepsi',
    cantidad: 50,
    fecha: '07/06/2026',
    estado: 'En preparación'
  },
  {
    id: 3,
    producto: 'Chocolate',
    cantidad: 10,
    fecha: '06/06/2026',
    estado: 'Enviado'
  }
]

function estadoColor(estado) {
  switch (estado) {
    case 'Pendiente':
      return 'warning'
    case 'En preparación':
      return 'info'
    case 'Enviado':
      return 'primary'
    case 'Recepcionada':
      return 'success'
    default:
      return 'grey'
  }
}
</script>
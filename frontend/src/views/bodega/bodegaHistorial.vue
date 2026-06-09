<template>
  <v-container fluid>
    <h1 class="text-h4 mb-6">
      Observaciones Recibidas
    </h1>

    <v-table>
      <thead>
      <tr>
        <th>Solicitud</th>
        <th>Local</th>
        <th>Observación</th>
        <th>Fecha</th>
      </tr>
      </thead>

      <tbody>
      <tr
          v-for="obs in observaciones"
          :key="obs.id"
      >
        <td>#{{ obs.id }}</td>
        <td>{{ obs.local }}</td>
        <td>{{ obs.comentario }}</td>
        <td>{{ obs.fecha }}</td>
      </tr>
      </tbody>
    </v-table>
  </v-container>
</template>

<script setup>
const observaciones = [
  {
    id: 1,
    local: 'Providencia',
    comentario: 'Faltaron 10 bebidas',
    fecha: '06/06/2026'
  },
  {
    id: 2,
    local: 'Maipú',
    comentario: 'Producto dañado',
    fecha: '06/06/2026'
  }
]
</script>
<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold mb-4">
          Solicitar Reposición
        </h1>
      </v-col>
    </v-row>

    <!-- Formulario -->
    <v-row>
      <v-col cols="12" md="8">
        <v-card elevation="2">
          <v-card-title>
            Nueva Solicitud
          </v-card-title>

          <v-card-text>
            <v-form>
              <v-select
                  label="Producto"
                  :items="productos"
                  variant="outlined"
                  prepend-inner-icon="mdi-package-variant"
              />

              <v-text-field
                  label="Cantidad solicitada"
                  type="number"
                  variant="outlined"
                  prepend-inner-icon="mdi-counter"
                  class="mt-4"
              />

              <div class="d-flex justify-end mt-4">
                <v-btn
                    color="primary"
                    prepend-icon="mdi-send"
                >
                  Enviar Solicitud
                </v-btn>
              </div>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Productos con bajo stock -->
      <v-col cols="12" md="4">
        <v-card elevation="2">
          <v-card-title>
            Productos con Bajo Stock
          </v-card-title>

          <v-list>
            <v-list-item
                v-for="producto in bajoStock"
                :key="producto.nombre"
            >
              <template #prepend>
                <v-icon color="warning">
                  mdi-alert-circle
                </v-icon>
              </template>

              <v-list-item-title>
                {{ producto.nombre }}
              </v-list-item-title>

              <v-list-item-subtitle>
                Stock actual: {{ producto.stock }}
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>

    <!-- Historial de solicitudes -->
    <v-row class="mt-6">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title>
            Estado de Solicitudes
          </v-card-title>

          <v-table>
            <thead>
            <tr>
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
              <td>{{ solicitud.producto }}</td>
              <td>{{ solicitud.cantidad }}</td>
              <td>{{ solicitud.fecha }}</td>

              <td>
                <v-chip
                    :color="getColor(solicitud.estado)"
                    size="small"
                >
                  {{ solicitud.estado }}
                </v-chip>
              </td>
            </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
const productos = [
  'Coca Cola 350ml',
  'Pepsi 350ml',
  'Papas Fritas',
  'Chocolate',
  'Agua Mineral'
]

const bajoStock = [
  {
    nombre: 'Coca Cola 350ml',
    stock: 5
  },
  {
    nombre: 'Papas Fritas',
    stock: 3
  },
  {
    nombre: 'Chocolate',
    stock: 2
  }
]

const solicitudes = [
  {
    id: 1,
    producto: 'Coca Cola 350ml',
    cantidad: 50,
    fecha: '06/06/2026',
    estado: 'Pendiente'
  },
  {
    id: 2,
    producto: 'Chocolate',
    cantidad: 30,
    fecha: '05/06/2026',
    estado: 'Aprobada'
  },
  {
    id: 3,
    producto: 'Papas Fritas',
    cantidad: 20,
    fecha: '04/06/2026',
    estado: 'En Camino'
  }
]

function getColor(estado) {
  switch (estado) {
    case 'Pendiente':
      return 'warning'

    case 'Aprobada':
      return 'success'

    case 'En Camino':
      return 'info'

    default:
      return 'grey'
  }
}
</script>
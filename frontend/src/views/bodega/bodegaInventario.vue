<template>
  <v-container fluid>
    <h1 class="text-h4 font-weight-bold mb-6">
      Inventario de Bodega
    </h1>

    <v-card>
      <v-card-text>
        <v-table>
          <thead>
          <tr>
            <th>Producto</th>
            <th>Stock</th>
            <th>Estado</th>
          </tr>
          </thead>

          <tbody>
          <tr
              v-for="producto in productos"
              :key="producto.id"
          >
            <td>{{ producto.nombre }}</td>

            <td>{{ producto.stock }}</td>

            <td>
              <v-chip
                  :color="producto.stock <= 50 ? 'error' : 'success'"
              >
                {{
                  producto.stock <= 50
                      ? 'Stock Bajo'
                      : 'Disponible'
                }}
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
const productos = [
  {
    id: 1,
    nombre: 'Coca Cola',
    stock: 500
  },
  {
    id: 2,
    nombre: 'Pepsi',
    stock: 250
  },
  {
    id: 3,
    nombre: 'Papas Fritas',
    stock: 30
  }
]
</script>
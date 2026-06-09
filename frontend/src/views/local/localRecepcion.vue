<template>
  <v-container fluid>
    <h1 class="text-h4 mb-6">
      Recepción de Productos
    </h1>

    <v-row>
      <v-col
          v-for="item in recepciones"
          :key="item.id"
          cols="12"
      >
        <v-card>
          <v-card-title>
            Solicitud #{{ item.id }}
          </v-card-title>

          <v-card-text>
            <p><strong>Producto:</strong> {{ item.producto }}</p>
            <p><strong>Cantidad:</strong> {{ item.cantidad }}</p>

            <v-chip color="primary">
              En Camino
            </v-chip>
          </v-card-text>

          <v-card-actions>
            <v-btn
                color="success"
            >
              Confirmar Recepción
            </v-btn>

            <v-btn
                color="warning"
                variant="outlined"
            >
              Agregar Observación
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
const recepciones = [
  {
    id: 1,
    producto: 'Coca Cola',
    cantidad: 50
  }
]
</script>
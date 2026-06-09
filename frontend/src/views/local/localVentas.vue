
<template>
  <v-container fluid>
    <h1 class="text-h4 font-weight-bold mb-6">
      Ventas del Local
    </h1>

    <v-row>
      <v-col cols="12" md="4">
        <v-card rounded="xl">
          <v-card-text>
            <div class="text-caption">Ventas Hoy</div>
            <div class="text-h4 font-weight-bold">$450.000</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card rounded="xl">
          <v-card-text>
            <div class="text-caption">Productos Vendidos</div>
            <div class="text-h4 font-weight-bold">84</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card rounded="xl">
          <v-card-text>
            <div class="text-caption">Boletas Emitidas</div>
            <div class="text-h4 font-weight-bold">32</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-card rounded="xl" class="mt-6">
      <v-card-title>Últimas Ventas</v-card-title>

      <v-table>
        <thead>
        <tr>
          <th>Fecha</th>
          <th>Producto</th>
          <th>Cantidad</th>
          <th>Total</th>
        </tr>
        </thead>

        <tbody>
        <tr>
          <td>06/06/2026</td>
          <td>Coca Cola 1.5L</td>
          <td>3</td>
          <td>$6.000</td>
        </tr>
        </tbody>
      </v-table>
    </v-card>
  </v-container>
</template>

<style scoped>

</style>
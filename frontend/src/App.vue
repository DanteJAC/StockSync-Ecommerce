<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script setup>
import { useTheme } from 'vuetify'
import { onMounted } from 'vue'

const theme = useTheme()

onMounted(() => {
  const saved = localStorage.getItem('theme')
  if (saved === 'dark' || (!saved && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    theme.global.name.value = 'dark'
  }
})
</script>

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

export default defineConfig({
  plugins: [vue(), vuetify()],
  base: '/api/',
  server: {
    proxy: {
      '/api/v1': { target: 'http://localhost:8080', changeOrigin: true },
      '/api/login': { target: 'http://localhost:8080', changeOrigin: true },
      '/api/register': { target: 'http://localhost:8080', changeOrigin: true },
      '/api/users': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
  build: {
    outDir: 'dist',
  },
})

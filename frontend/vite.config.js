import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
    plugins: [
      vue(), 
      vuetify(),
      VitePWA({
        registerType: 'autoUpdate',
        includeAssets: ['pwa-192x192.svg', 'pwa-512x512.svg'],
        manifest: {
          name: 'StockSync',
          short_name: 'StockSync',
          description: 'Gestión de Inventario en Tiempo Real',
          theme_color: '#121212',
          background_color: '#121212',
          display: 'standalone',
          icons: [
            {
              src: 'pwa-192x192.svg',
              sizes: '192x192',
              type: 'image/svg+xml'
            },
            {
              src: 'pwa-512x512.svg',
              sizes: '512x512',
              type: 'image/svg+xml'
            }
          ]
        }
      })
    ],
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        },
    },
    build: {
        outDir: 'dist',
        emptyOutDir: true,
    },
})
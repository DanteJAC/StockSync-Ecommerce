import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

export default defineConfig({
    plugins: [vue(), vuetify()],
    // ❌ QUITAR: base: '/api/', <-- Esto rompe la UI
    server: {
        port: 5173,
        proxy: {
            // ✅ Un solo proxy atrapa todo lo que empiece con /api y lo manda al backend
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        },
    },
    build: {
        outDir: 'dist',
    },
})

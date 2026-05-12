# StockSync Frontend

Frontend SPA del sistema de gestión de inventario StockSync, construido con Vue 3, Vuetify y Pinia.

## Stack

- **Vue 3** (Composition API + `<script setup>`)
- **Vite** (bundler + dev server con proxy al backend)
- **Vuetify 4** (UI components Material Design)
- **Pinia** (estado global)
- **Vue Router 4** (hash mode)
- **Axios** (HTTP client con interceptor JWT)

## Scripts

```bash
npm run dev        # Dev server con HMR (localhost:5173/api/)
npm run build      # Build producción en dist/
npm run preview    # Preview del build
npm run deploy     # Build + copia a backend/src/main/resources/static/
```

## Estructura

```
src/
├── api/            # Axios client + módulos por recurso
├── stores/         # Pinia stores (auth, products, warehouses, stock)
├── components/     # Layouts (PublicHeader, PublicFooter, AdminLayout)
├── views/
│   ├── public/     # Landing, Productos, Bodegas
│   ├── auth/       # Login, Register
│   └── admin/      # Dashboard, CRUD productos/bodegas/stock
├── router/         # Vue Router con auth guard
└── plugins/        # Vuetify theme config
```

## Desarrollo

El dev server proxy automáticamente las llamadas `/api/*` al backend en `localhost:8080`.

```bash
cd backend && ./mvnw spring-boot:run   # Terminal 1
cd frontend && npm run dev              # Terminal 2
```

## Convenciones

- Convencional commits en español
- Naming: PascalCase para componentes, camelCase para variables/métodos
- Vuetify props con `:` shorthand
- `v-model` para formularios
- `v-slot` template syntax para slots complejos

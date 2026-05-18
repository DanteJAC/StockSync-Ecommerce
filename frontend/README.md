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
├── api/                    # Capa HTTP
│   ├── client.js           # Axios instance + interceptors JWT y 401
│   ├── auth.js             # POST /api/login, /api/register
│   ├── products.js         # CRUD /api/v1/products
│   ├── warehouses.js       # CRUD /api/v1/warehouses
│   ├── stock.js            # CRUD + transfer /api/v1/stocks
│   └── categories.js       # CRUD /api/v1/categories
├── stores/                 # Estado global (Pinia)
│   ├── auth.js             # Token, usuario, login/logout
│   ├── products.js         # Lista de productos, CRUD
│   ├── warehouses.js       # Lista de bodegas, CRUD
│   ├── stock.js            # Lista de stock, CRUD + transferencia
│   └── categories.js       # Lista de categorías
├── components/             # Layouts compartidos
│   ├── PublicHeader.vue    # Navbar público con theme toggle + auth state
│   ├── PublicFooter.vue    # Footer con links
│   └── AdminLayout.vue     # Sidebar + topbar + router-view anidado
├── views/
│   ├── public/             # Rutas públicas (sin auth)
│   │   ├── Landing.vue     # Hero + stats cards + productos destacados
│   │   ├── Productos.vue   # Catálogo con filtros y búsqueda
│   │   └── Bodegas.vue     # Grid + modal de stock por bodega
│   ├── auth/               # Autenticación
│   │   ├── Login.vue       # Formulario con validación JWT
│   │   └── Register.vue    # Formulario con validación
│   └── admin/              # Rutas protegidas (requieren JWT)
│       ├── Dashboard.vue   # Cards totales + tabla productos recientes
│       ├── ProductList.vue # DataTable con delete dialog
│       ├── ProductForm.vue # Formulario con select de categoría
│       ├── WarehouseList.vue
│       ├── WarehouseForm.vue
│       ├── StockList.vue   # DataTable + diálogo de transferencia
│       └── StockForm.vue   # Formulario con selects producto/bodega
├── router/
│   └── index.js            # Hash mode + beforeEach guard (auth)
└── plugins/
    └── vuetify.js          # Tema light/dark, defaults globales
```

## Árbol de componentes

```
App.vue
├── PublicHeader.vue           ← pública: navbar con login/register/admin link
├── PublicFooter.vue           ← pública: footer
│
├── views/public/Landing.vue   ← ruta: /
├── views/public/Productos.vue ← ruta: /productos
├── views/public/Bodegas.vue   ← ruta: /bodegas
│
├── views/auth/Login.vue       ← ruta: /login
├── views/auth/Register.vue    ← ruta: /register
│
└── AdminLayout.vue             ← ruta: /admin (wrapper con sidebar)
    ├── Dashboard.vue          ← ruta: /admin
    ├── ProductList.vue        ← ruta: /admin/productos
    ├── ProductForm.vue        ← ruta: /admin/productos/{nuevo|editar/:id}
    ├── WarehouseList.vue      ← ruta: /admin/bodegas
    ├── WarehouseForm.vue      ← ruta: /admin/bodegas/{nuevo|editar/:id}
    ├── StockList.vue          ← ruta: /admin/stock
    └── StockForm.vue          ← ruta: /admin/stock/{nuevo|editar/:id}
```

## Flujo de datos

```
                ┌──────────────┐
                │   Usuario    │
                └──────┬───────┘
                       │
          ┌────────────┴────────────┐
          │                         │
   Login/Register              Navegación
          │                         │
          ▼                         ▼
   Auth Store (Pinia)        Vue Router
   ┌────────────────┐       ┌────────────┐
   │ token (JWT)    │       │ /admin/**  │
   │ user (role)    │       │ guard:     │
   │ localStorage  │       │ auth? sí:no│
   └────────┬───────┘       └─────┬──────┘
            │                     │
            ▼                     ▼
    Axios Interceptor       Lazy-loaded View
   ┌──────────────┐       ┌───────────────┐
   │ Request:     │       │ onMounted()   │
   │ Bearer token │       │ → fetch store │
   │ Response:    │       │ → render data │
   │ 401 → logout │       └───────┬───────┘
   └──────────────┘               │
            │                     │
            ▼                     ▼
    ┌─────────────────────────────────┐
    │     Backend (Spring Boot)       │
    │  localhost:8080/api/v1/*        │
    │  JWT Authentication Filter      │
    └─────────────────────────────────┘
```

## Autenticación

1. Usuario envía credenciales via `Login.vue` → `auth/login`
2. Backend valida y retorna `{ token, email, nombre, role }`
3. Pinia store guarda token en `localStorage['jwt']` y usuario en `localStorage['user']`
4. Axios interceptor attach `Authorization: Bearer <token>` a cada request
5. Si el backend responde 401, el interceptor limpia sesión y redirige a `/login`

### Protección de rutas

El `router.beforeEach` verifica `auth.isAuthenticated`:
- `/admin/*` → requiere token, si no → redirige a `/login`
- `/login` → si ya autenticado → redirige a `/admin`

## Tema oscuro/claro

- Configurado en `plugins/vuetify.js` con paletas originales del proyecto
- Persistencia en `localStorage['theme']`
- Detecta `prefers-color-scheme` al cargar
- Botón toggle en `PublicHeader.vue` y `AdminLayout.vue`
- Transiciones suaves vía Vuetify

## Desarrollo

El dev server proxy automáticamente las llamadas `/api/*` al backend en `localhost:8080`.

```bash
cd backend && ./mvnw spring-boot:run   # Terminal 1
cd frontend && npm run dev              # Terminal 2
```

El proxy en `vite.config.js` solo redirige endpoints reales de API (`/api/v1`, `/api/login`, etc.), las rutas del SPA son servidas directamente por Vite.

## Build producción

```bash
npm run deploy   # vite build → copia dist/ a ../backend/src/main/resources/static/
```

Spring Boot sirve el SPA en el context path `/api/` junto con los assets hasheados.

## Convenciones

- Commits: [Conventional Commits](https://www.conventionalcommits.org/) en español
- Naming: `PascalCase` para componentes, `camelCase` para variables/métodos
- Componentes: Composition API con `<script setup>`
- Props de Vuetify con `:` shorthand
- `v-model` para inputs de formularios
- `v-slot` template syntax para slots complejos
- Imports agrupados: Vue API → stores → components → views
- Variables reactivas con `ref` / `computed`
- Efectos secundarios en `onMounted` o `watch`

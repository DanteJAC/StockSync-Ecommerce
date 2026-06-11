<div align="center">
  <img src="https://github.com/user-attachments/assets/40a97c72-f0f4-403a-9af9-d5bb08a6139c" alt="StockSync Logo" width="300"/>

  # StockSync
  *Inventario sin errores, negocio sin límites.*
  
  [![Vue.js](https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vue.js&logoColor=4FC08D)](https://vuejs.org/)
  [![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
  [![PWA Support](https://img.shields.io/badge/PWA-Ready-5A0FC8?style=for-the-badge&logo=pwa&logoColor=white)](#)
</div>

---

## 📖 Sobre el Proyecto
**StockSync** es un sistema integral de gestión de inventario y E-commerce. Diseñado para optimizar el control de stock, gestionar pedidos y facilitar la administración de productos en tiempo real, garantizando trazabilidad y eficiencia a través de múltiples bodegas y sucursales.

### 🚀 Historia y Evolución
StockSync nació inicialmente como un proyecto integrador enfocado en brindar una arquitectura sólida (Java Spring Boot + Vue 3) para la gestión empresarial. Con el tiempo, el proyecto ha evolucionado para adaptarse a las necesidades operativas reales:
- **Transformación Móvil (PWA):** Se integró soporte para Aplicaciones Web Progresivas (PWA), permitiendo a los operarios instalar la aplicación directamente en sus teléfonos sin pasar por tiendas de aplicaciones, habilitando una experiencia fluida e inmersiva.
- **Escaneo Inteligente:** Se implementó `vue-qrcode-reader` para permitir el escaneo de códigos de barras y códigos QR utilizando la cámara del dispositivo móvil, agilizando drásticamente la recepción y el despacho de mercadería en bodegas.
- **Interfaz 100% Responsiva:** Se reestructuraron las tablas, cuadrículas y barras de navegación utilizando el sistema de Material Design de Vuetify para evitar desbordamientos, garantizando que el sistema sea fácil de operar incluso en las pantallas más pequeñas.

---

## ⚙️ Tecnologías (Tech Stack)

### 🎨 Frontend (Cliente)
- **Framework:** Vue 3 (Composition API + `<script setup>`)
- **Herramienta de Construcción:** Vite (optimizada para PWA con `vite-plugin-pwa`)
- **UI/Estilos:** Vuetify 3 (Material Design), CSS Vanilla y animaciones.
- **Estado Global:** Pinia
- **Navegación:** Vue Router 4
- **Herramientas de Hardware:** `vue-qrcode-reader` (Lector de cámara)
- **Peticiones HTTP:** Axios con interceptores JWT.

### 🛡️ Backend (Servidor)
- **Lenguaje / Framework:** Java 21 con Spring Boot 3.x
- **Seguridad:** Spring Security + JWT (JSON Web Tokens)
- **Persistencia:** Spring Data JPA + Hibernate
- **Base de Datos:** PostgreSQL
- **Migraciones:** Flyway
- **Mapeo de Datos:** MapStruct
- **Documentación de API:** Springdoc OpenAPI (Swagger UI)

### 🐳 Infraestructura
- **Contenedores:** Docker y Docker Compose para despliegues rápidos e independientes.

---

## 🛠️ Instalación y Uso

### Requisitos Previos
- [Node.js](https://nodejs.org/en/) (Versión 20+ recomendada)
- [Java JDK](https://adoptium.net/) (Versión 21+)
- [PostgreSQL](https://www.postgresql.org/) (Versión 15+)
- Docker (Opcional, para ejecución en contenedores)

### Opción 1: Ejecución Local

**1. Configuración del Backend:**
```bash
cd backend
# Copia el archivo de variables de entorno y ajusta las credenciales de tu BD
cp .env.example .env   
# Ejecuta el servidor (Windows usa .\mvnw.cmd, Mac/Linux usa ./mvnw)
./mvnw clean spring-boot:run
```
El backend estará disponible en `http://localhost:8080` con Swagger UI en `/api/swagger-ui.html`.

**2. Configuración del Frontend:**
```bash
cd frontend
npm install
npm run dev
```
La aplicación web se abrirá en `http://localhost:5173`. 

### Opción 2: Ejecución con Docker (Producción)
Puedes levantar todo el ecosistema (Base de datos, Backend y Frontend empaquetado) con un solo comando si dispones de Docker:
```bash
docker build -t stocksync-app .
docker run -p 8080:8080 stocksync-app
```

### 📱 Uso como PWA (Teléfonos Móviles)
1. Accede a la URL del frontend (`http://localhost:5173` o la IP local de tu servidor `http://192.168.x.x:5173`) desde el navegador de tu dispositivo móvil.
2. Abre el menú de opciones de tu navegador (Chrome/Safari) y selecciona **"Instalar aplicación"** o **"Añadir a la pantalla de inicio"**.
3. La aplicación se instalará como una app nativa en tu teléfono. Podrás utilizar el lector de cámara en la sección de **Recepciones / Entregas**.

---

## 🤝 ¿Cómo Contribuir? (Contribute)

¡Las contribuciones son bienvenidas! Si deseas mejorar StockSync, agregar una nueva funcionalidad o reportar un error, sigue estos pasos:

1. **Haz un Fork** del repositorio.
2. **Crea una nueva rama** para tu funcionalidad o corrección:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. **Realiza tus cambios** y asegúrate de probarlos localmente.
4. **Haz commit** de tus cambios usando mensajes claros (sugerimos usar [Conventional Commits](https://www.conventionalcommits.org/)):
   ```bash
   git commit -m "feat: agregar exportación de inventario a PDF"
   ```
5. **Haz push** a tu rama:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
6. **Abre un Pull Request (PR)** en este repositorio explicando detalladamente los cambios realizados. Nuestro equipo revisará el código y te dará retroalimentación.

---

## 👥 Equipo de Desarrollo

- **Allan Nuñez**
- **Dafne Mandujano**
- **Dante Escalona**
- **Felipe Segovia**
- **Renato Campos**

---
*Si encuentras útil este proyecto, ¡no olvides darle una ⭐ en GitHub!*

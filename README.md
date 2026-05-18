# LinkHogar

**Plataforma web para la gestión integral de alquileres y convivencia en el hogar.**

LinkHogar es una aplicación full-stack desarrollada como Trabajo de Fin de Grado (TFG) del Grado en Ingenieria Informatica de la Universidad de Huelva. Conecta a propietarios e inquilinos facilitando la publicacion de inmuebles, la busqueda de vivienda y la gestion diaria de la convivencia (gastos compartidos, tareas domesticas, calendario y chat en tiempo real).

---

## Tabla de Contenidos

- [Caracteristicas principales](#caracteristicas-principales)
- [Stack tecnologico](#stack-tecnologico)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Modelo de dominio](#modelo-de-dominio)
- [API REST — Endpoints](#api-rest--endpoints)
- [Requisitos previos](#requisitos-previos)
- [Instalacion y puesta en marcha](#instalacion-y-puesta-en-marcha)
- [Variables de entorno](#variables-de-entorno)
- [Migraciones de base de datos](#migraciones-de-base-de-datos)
- [Frontend (Angular)](#frontend-angular)
- [Despliegue](#despliegue)
- [Documentacion interactiva (Swagger)](#documentacion-interactiva-swagger)
- [Estructura de carpetas](#estructura-de-carpetas)

---

## Caracteristicas principales

| Modulo | Descripcion |
|---|---|
| **Autenticacion** | Registro con verificacion por email, login JWT, recuperacion de contrasena con codigo de seguridad. |
| **Gestion de inmuebles** | Publicacion, edicion, eliminacion y busqueda de viviendas. Soporte para alquiler completo o por habitaciones. |
| **Panel de administracion** | Dashboard con estadisticas, moderacion de anuncios pendientes, gestion de usuarios y reportes de viviendas. |
| **Sistema de favoritos** | Los usuarios pueden marcar viviendas como favoritas y consultarlas en una vista paginada. |
| **Chat en tiempo real** | Mensajeria instantanea entre propietario e inquilino interesado. Chat grupal del hogar. Basado en WebSocket (STOMP + SockJS). |
| **Gastos compartidos** | Registro de gastos, division automatica entre miembros del hogar, seguimiento de deudas y pagos. |
| **Tareas del hogar** | Tablon Kanban con estados (todo, inProgress, done). Asignacion de tareas a miembros. |
| **Calendario / Eventos** | Agenda compartida del hogar con creacion de eventos, recordatorios automaticos por email. |
| **Notificaciones** | Sistema de notificaciones internas con marcado de lectura. |
| **Subida de imagenes** | Integracion con Cloudinary para fotos de viviendas, habitaciones y avatares de usuario. |
| **Geolocalizacion** | Geocodificacion de direcciones mediante Nominatim (OpenStreetMap) e integracion con Google Maps en el frontend. |
| **Emails transaccionales** | Correos de verificacion, recordatorios de eventos, notificaciones de gastos y tareas via Brevo API. |

---

## Stack tecnologico

### Backend

| Tecnologia | Version | Proposito |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.2 | Framework base |
| Spring Security | — | Autenticacion y autorizacion JWT |
| Spring Data JPA / Hibernate | — | ORM y acceso a datos |
| Flyway | — | Migraciones de base de datos |
| Spring WebSocket | — | Chat en tiempo real (STOMP) |
| Spring Mail + Brevo API | — | Emails transaccionales |
| Springdoc OpenAPI | 2.3.0 | Documentacion Swagger UI |
| JJWT | 0.11.5 | Generacion y validacion de tokens JWT |
| Cloudinary | 2.0.0 | Almacenamiento de imagenes en la nube |
| Lombok | — | Reduccion de boilerplate |
| MySQL | 8.0 | Base de datos relacional |
| Maven | — | Gestion de dependencias y build |
| Docker Compose | — | Contenedorizacion de MySQL en desarrollo |

### Frontend

| Tecnologia | Version | Proposito |
|---|---|---|
| Angular | 21.1 | Framework SPA |
| TypeScript | ~5.9 | Lenguaje principal del frontend |
| Bootstrap | 5.3 | Estilos y componentes CSS |
| STOMP.js / SockJS | — | Cliente WebSocket para chat |
| FullCalendar | 6.1 | Componente de calendario |
| Google Maps Angular | — | Visualizacion de mapas |
| SweetAlert2 | — | Dialogs y alertas |
| Lucide Angular | — | Iconografia |
| FontAwesome | — | Iconografia adicional |
| ngx-image-cropper | — | Recorte de imagenes (avatar) |
| jwt-decode | — | Decodificacion de tokens JWT en cliente |
| Vitest | — | Framework de testing unitario |

---

## Arquitectura del proyecto

El proyecto sigue una **Arquitectura Hexagonal (Ports & Adapters)** combinada con el patron **CQRS** (Command Query Responsibility Segregation). Se organiza en tres capas bien diferenciadas:

```
src/main/java/com/linkhogar/
├── domain/            ← Nucleo de negocio (entidades, repositorios-interfaz, enums, errores)
├── application/       ← Casos de uso (Commands, Queries y sus Handlers)
└── infrastructure/    ← Adaptadores (REST controllers, JPA repos, servicios externos, seguridad)
```

### Capa de Dominio (`domain/`)

Contiene las **entidades JPA**, las **interfaces de repositorio** (puertos), los **enums** de dominio y los objetos de **resultado/error**. No depende de ninguna otra capa.

### Capa de Aplicacion (`application/`)

Implementa los **casos de uso** siguiendo CQRS:
- **Commands**: Operaciones de escritura (crear, actualizar, eliminar).
- **Queries**: Operaciones de lectura.
- Cada caso de uso tiene su propio paquete con un `Command`/`Query` (DTO de entrada) y un `Handler` (logica de negocio).

### Capa de Infraestructura (`infrastructure/`)

- **`rest/`**: Controladores REST (adaptadores de entrada HTTP).
- **`persistence/`**: Implementaciones JPA de los repositorios del dominio (adaptadores de salida).
- **`security/`**: Configuracion de Spring Security, filtro JWT, servicio JWT.
- **`config/`**: Configuraciones de la aplicacion (OpenAPI, WebSocket, Cloudinary, Async).
- **`externalServices/`**: Integraciones con servicios externos (Cloudinary, Nominatim, Brevo Mail).
- **`scheduler/`**: Tareas programadas (recordatorios de eventos).

### Patron Result

La aplicacion utiliza un patron `Result<T>` monádico para el manejo de errores sin excepciones, permitiendo a los controladores mapear resultados exitosos o errores a respuestas HTTP de forma limpia.

---

## Modelo de dominio

### Diagrama de entidades

```
┌──────────────┐       1:N        ┌──────────────┐       1:1        ┌──────────────┐
│     User     │─────────────────▶│    House      │────────────────▶│   Address    │
│              │                  │              │                  │              │
│ id (UUID)    │                  │ id (UUID)    │                  │ id (UUID)    │
│ firstName    │                  │ title        │                  │ street       │
│ lastName     │                  │ description  │                  │ number       │
│ mail         │                  │ houseType    │                  │ floor        │
│ password     │                  │ pubStatus    │                  │ door         │
│ phone        │                  │ status       │                  │ city         │
│ fecha_nac    │                  │ rentalMode   │                  │ cp           │
│ role         │                  │ price        │                  │ province     │
│ enabled      │                  │ size/rooms   │                  │ country      │
│ avatarUrl    │                  │ baths        │                  │ latitude     │
│ homeId       │                  │ amenities... │                  │ longitude    │
└──────────────┘                  │ images[]     │                  └──────────────┘
       │                          └──────┬───────┘
       │                                 │ 1:N
       │                          ┌──────▼───────┐
       │                          │     Room     │
       │                          │              │
       │                          │ id (UUID)    │
       │                          │ name         │
       │                          │ price        │
       │                          │ size         │
       │                          │ bedType      │
       │                          │ hasPrivBath  │
       │                          │ status       │
       │                          │ tenant(emb.) │
       │                          │ photoUrls[]  │
       │                          └──────────────┘
       │
       │         ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
       │         │   Expense    │      │  HomeTask    │      │  HomeEvent   │
       │         │              │      │              │      │              │
       │         │ homeId       │      │ homeId       │      │ homeId       │
       │         │ payerId      │      │ title        │      │ title        │
       │         │ amount       │      │ description  │      │ description  │
       │         │ category     │      │ status       │      │ startDate    │
       │         │ description  │      │ assignedUser │      │ endDate      │
       │         │ createdAt    │      │ dueDate      │      │ allDay       │
       │         └──────┬───────┘      └──────────────┘      │ reminder     │
       │                │ 1:N                                 └──────────────┘
       │         ┌──────▼───────┐
       │         │ ExpenseSplit │
       │         │              │
       │         │ expenseId    │
       │         │ debtorId     │
       │         │ amountOwed   │
       │         │ isPaid       │
       │         └──────────────┘
       │
       │         ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
       │         │    Chat      │      │   Message    │      │ Notification │
       │         │              │      │              │      │              │
       │         │ type         │      │ chatId       │      │ userId       │
       │         │ referenceId  │      │ senderId     │      │ title        │
       │         │ status       │      │ content      │      │ message      │
       │         │ createdAt    │      │ senderName   │      │ isRead       │
       │         └──────────────┘      │ createdAt    │      │ createdAt    │
       │                               └──────────────┘      └──────────────┘
       │
       │         ┌──────────────┐      ┌──────────────┐
       │         │ HouseReport  │      │HouseFavourite│
       │         │              │      │              │
       │         │ houseId      │      │ userId       │
       │         │ userId       │      │ houseId      │
       │         │ reason       │      └──────────────┘
       │         │ description  │
       │         │ status       │
       │         └──────────────┘
```

### Enumeraciones del dominio

| Enum | Valores | Descripcion |
|---|---|---|
| `Role` | `LinkHogar`, `Admin`, `Propietario`, `User` | Roles de usuario en la plataforma |
| `HouseType` | `Piso`, `Adosado`, `Estudio`, `Apartamento`, `Chalet`, `Atico`, `Loft`, `Habitacion`, `Residencia` | Tipos de inmueble |
| `HouseStatus` | `Reservada`, `Alquilada`, `Disponible` | Estado de disponibilidad del inmueble |
| `PublicationStatus` | `DRAFT`, `PENDING_REVIEW`, `PUBLISHED`, `PAUSED`, `EXPIRED`, `ARCHIVED` | Ciclo de vida de la publicacion |
| `RentalMode` | `COMPLETE`, `BY_ROOM` | Modo de alquiler (vivienda completa o por habitaciones) |
| `RoomStatus` | `AVAILABLE`, `OCCUPIED` | Estado de ocupacion de la habitacion |
| `TaskStatus` | `todo`, `inProgress`, `done` | Estados de las tareas del hogar (Kanban) |
| `ExpenseCategory` | `ALQUILER`, `SUPERMERCADO`, `SUMINISTROS`, `INTERNET`, `LIMPIEZA`, `OTROS` | Categorias de gastos compartidos |
| `ChatType` | `Alquiler`, `GrupoHogar` | Tipo de conversacion |
| `ChatStatus` | `Activo`, `Archivado` | Estado del chat |
| `ReportStatus` | `PENDIENTE`, `REVISADA` | Estado de la denuncia de un inmueble |
| `Gender` | `MALE`, `FEMALE`, `OTHER` | Genero del perfil de inquilino |
| `Occupation` | `STUDENT`, `WORKER`, `STUDY_AND_WORK`, `NOT_DEFINED` | Ocupacion del inquilino |

---

## API REST — Endpoints

La API se documenta automaticamente con Swagger UI en `/swagger-ui.html`. A continuacion se listan todos los endpoints organizados por controlador.

> Todos los endpoints autenticados requieren un header `Authorization: Bearer <token_jwt>`.

### Autenticacion (`/auth`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `POST` | `/auth/login` | No | Inicia sesion y devuelve un token JWT |
| `POST` | `/auth/register` | No | Registra un nuevo usuario y envia email de verificacion |
| `GET` | `/auth/verify/{token}` | No | Verifica la cuenta de email mediante token |
| `POST` | `/auth/request-password-code` | No | Envia un codigo de recuperacion de contrasena al email |
| `POST` | `/auth/verify-password-code` | No | Verifica el codigo de recuperacion |
| `POST` | `/auth/reset-password` | No | Restablece la contrasena con el codigo verificado |

### Usuarios (`/users`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `GET` | `/users/{id}` | No | Obtiene informacion publica de un usuario |
| `GET` | `/users/currentUser` | Si | Obtiene los datos del usuario autenticado |
| `GET` | `/users` | Admin | Lista paginada de usuarios con filtros (search, role, enabled) |
| `PUT` | `/users/update/{id}` | Si | Actualiza datos del perfil (nombre, apellidos, telefono, fecha nac.) |
| `DELETE` | `/users/{id}` | Si | Elimina un usuario |
| `PATCH` | `/users/{id}/toggle-enabled` | Admin | Activa o desactiva un usuario |
| `PUT` | `/users/change-password` | Si | Cambia la contrasena del usuario |
| `POST` | `/users/uploadAvatar/{id}` | Si | Sube un avatar de perfil (multipart) |
| `POST` | `/users/addFavourite/{userId}/{houseId}` | Si | Anade una vivienda a favoritos |
| `GET` | `/users/favourites/ids/{userId}` | Si | Obtiene los IDs de viviendas favoritas |
| `DELETE` | `/users/deleteFavourite/{userId}/{houseId}` | Si | Elimina una vivienda de favoritos |
| `GET` | `/users/getPaginatedFavourites/{userId}` | Si | Lista paginada de viviendas favoritas |

### Viviendas (`/houses`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `GET` | `/houses` | No | Lista paginada de viviendas (ordenadas por fecha de creacion) |
| `POST` | `/houses` | Si | Crea un nuevo anuncio de vivienda |
| `GET` | `/houses/{id}` | No | Detalle completo de una vivienda |
| `GET` | `/houses/city/{city}` | No | Busqueda de viviendas por ciudad (paginada) |
| `GET` | `/houses/owner/{ownerId}` | Si | Viviendas publicadas por un propietario |
| `PUT` | `/houses/{houseId}` | Si | Actualiza los datos de una vivienda |
| `PUT` | `/houses/{id}/status` | Admin | Cambia el estado de publicacion de una vivienda |
| `DELETE` | `/houses/{id}` | Si | Elimina una vivienda (verifica permisos) |
| `POST` | `/houses/{houseId}/images` | Si | Sube imagenes a una vivienda (multipart) |
| `DELETE` | `/houses/{houseId}/image?url=` | Si | Elimina una imagen de la vivienda |
| `POST` | `/houses/{houseId}/rooms/{roomId}/images` | Si | Sube imagenes a una habitacion |
| `DELETE` | `/houses/{houseId}/rooms/{roomId}/image?url=` | Si | Elimina una imagen de una habitacion |
| `PUT` | `/houses/{houseId}/rooms/{roomId}/tenant` | Si | Actualiza el perfil de inquilino de una habitacion |
| `POST` | `/houses/{houseId}/reports` | Si | Reporta/denuncia una vivienda |
| `GET` | `/houses/houseReports/getAll` | Admin | Lista paginada de reportes de viviendas |
| `POST` | `/houses/houseReport/delete/{reportId}?archiveHouse=` | Admin | Resuelve un reporte (opcionalmente archiva la vivienda) |

### Administracion (`/admin`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `GET` | `/admin/stats` | Si | Dashboard con estadisticas (usuarios, anuncios pendientes, publicados) |
| `POST` | `/admin/create-user` | Admin | Crea un usuario desde el panel de administracion |
| `GET` | `/admin/houses/pending` | Admin | Lista paginada de viviendas pendientes de revision |

### Hogar / Convivencia (`/homes`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `POST` | `/homes/{homeId}/members` | Si | Anade un miembro al hogar por email |
| `DELETE` | `/homes/{homeId}/members/{memberId}` | Si | Elimina un miembro del hogar |

### Tareas del Hogar (`/homeTasks`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `POST` | `/homeTasks/create` | Si | Crea una nueva tarea |
| `GET` | `/homeTasks/getByHome/{homeId}` | Si | Obtiene las tareas de un hogar |
| `PATCH` | `/homeTasks/{taskId}/status` | Si | Actualiza el estado de una tarea (todo/inProgress/done) |
| `DELETE` | `/homeTasks/{taskId}` | Si | Elimina una tarea |
| `GET` | `/homeTasks/{homeId}/members` | Si | Lista los miembros del hogar |

### Gastos Compartidos (`/expenses`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `POST` | `/expenses` | Si | Registra un gasto y sus divisiones |
| `GET` | `/expenses/home/{homeId}` | Si | Historial de gastos de una casa |
| `GET` | `/expenses/{expenseId}/splits` | Si | Detalle de la division de un gasto |
| `DELETE` | `/expenses/{expenseId}` | Si | Elimina un gasto y sus divisiones |
| `PATCH` | `/expenses/splits/{splitId}/pay` | Si | Marca una parte del gasto como pagada |
| `GET` | `/expenses/home/{homeId}/balances` | Si | Balance neto y deudas cruzadas del hogar |

### Calendario / Eventos (`/events`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `POST` | `/events/create` | Si | Crea un nuevo evento en el calendario |
| `GET` | `/events/home/{homeId}` | Si | Obtiene todos los eventos de un hogar |
| `PUT` | `/events/{eventId}` | Si | Edita un evento existente |

### Chat (`/chat`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `POST` | `/chat/initiate` | Si | Inicia una conversacion con el propietario de una vivienda |
| `GET` | `/chat/my-chats` | Si | Lista las conversaciones del usuario autenticado |
| `GET` | `/chat/{chatId}/messages` | Si | Mensajes de un chat (paginados) |
| `GET` | `/chat/home/{homeId}` | Si | Obtiene o crea el chat grupal de un hogar |

### Chat WebSocket

| Protocolo | Ruta | Descripcion |
|---|---|---|
| **STOMP endpoint** | `/ws-chat` | Punto de conexion WebSocket (con fallback SockJS) |
| **Envio** | `/app/chat/{chatId}/sendMessage` | Envia un mensaje al chat |
| **Suscripcion** | `/topic/chat.{chatId}` | Canal de recepcion de mensajes en tiempo real |

### Ticket WebSocket (`/chat/ticket`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `POST` | `/chat/ticket` | Si | Genera un ticket temporal para autenticar la conexion WebSocket |

### Notificaciones (`/notifications`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `GET` | `/notifications/unread` | Si | Obtiene las notificaciones no leidas |
| `PATCH` | `/notifications/{id}/read` | Si | Marca una notificacion como leida |

### Configuracion (`/AppSettings`)

| Metodo | Ruta | Auth | Descripcion |
|---|---|---|---|
| `GET` | `/AppSettings/{name}` | No | Obtiene el valor de una configuracion por nombre |

---

## Requisitos previos

- **Java 21** (JDK)
- **Maven** 3.9+
- **Node.js** 20+ y **npm** 10+
- **Angular CLI** 21+
- **Docker** y **Docker Compose** (para la base de datos en desarrollo)
- **MySQL 8.0** (o usar el contenedor Docker incluido)

---

## Instalacion y puesta en marcha

### 1. Clonar el repositorio

```bash
git clone https://github.com/Sergions1/LinkHogar.git
cd LinkHogar
```

### 2. Backend (Spring Boot)

#### 2.1 Levantar la base de datos con Docker Compose

```bash
docker-compose up -d
```

Esto inicia un contenedor MySQL 8.0 accesible en el puerto `3307` con:
- **Base de datos**: `linkhogar_bd`
- **Usuario**: `usuario_dev` / `password_dev`
- **Root password**: `Admin123!`

#### 2.2 Configurar variables de entorno

Crea un archivo `src/main/resources/application-dev.properties` (excluido de Git) con las credenciales locales:

```properties
# Base de datos local (Docker)
MYSQLHOST=localhost
MYSQLPORT=3307
MYSQLDATABASE=linkhogar_bd
MYSQLUSER=usuario_dev
MYSQLPASSWORD=password_dev

# Cloudinary (necesario para subida de imagenes)
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret

# Brevo API (necesario para emails)
BREVO_API_KEY=tu_brevo_api_key
```

#### 2.3 Ejecutar el backend

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

El servidor arranca en `http://localhost:8080`.

#### 2.4 Ejecutar tests

```bash
./mvnw test
```

### 3. Frontend (Angular)

```bash
cd linkhogar-web
npm install
ng serve
```

La aplicacion estara disponible en `http://localhost:4200`.

#### Tests del frontend

```bash
ng test
```

---

## Variables de entorno

| Variable | Obligatoria | Descripcion |
|---|---|---|
| `MYSQLHOST` | Si | Host de la base de datos MySQL |
| `MYSQLPORT` | Si | Puerto de MySQL |
| `MYSQLDATABASE` | Si | Nombre de la base de datos |
| `MYSQLUSER` | Si | Usuario de MySQL |
| `MYSQLPASSWORD` | Si | Contrasena de MySQL |
| `CLOUDINARY_CLOUD_NAME` | Si | Nombre del cloud en Cloudinary |
| `CLOUDINARY_API_KEY` | Si | API Key de Cloudinary |
| `CLOUDINARY_API_SECRET` | Si | API Secret de Cloudinary |
| `BREVO_API_KEY` | Si | API Key de Brevo para envio de emails |
| `PORT` | No | Puerto del servidor (por defecto `8080`) |

---

## Migraciones de base de datos

El proyecto utiliza **Flyway** para gestionar las migraciones de esquema de la base de datos. Las migraciones se encuentran en:

```
src/main/resources/db/migration/
```

Ademas existen migraciones adicionales en la carpeta `migration/` de la raiz:

- `V2__ImageMigration.sql` — Anade soporte para imagenes en viviendas y modifica el campo `role` de usuarios.
- `V6__profileSettings.sql` — Anade campos `avatar_url`, `verification_code` y `verification_code_expiration` a la tabla de usuarios.

Flyway ejecuta las migraciones automaticamente al arrancar la aplicacion (`spring.flyway.baseline-on-migrate=true`). Hibernate esta configurado en modo `update` para sincronizar la estructura.

---

## Frontend (Angular)

### Estructura de paginas

```
linkhogar-web/src/app/
├── pages/
│   ├── auth/          → Login, Register, Verify
│   ├── explore/       → Buscador de viviendas
│   ├── house/         → Detalle, Crear/Editar vivienda
│   ├── Announcement/  → Informacion para publicar anuncio
│   ├── user/          → Perfil, Favoritos, Mis Publicaciones
│   ├── chat/          → Mensajes directos
│   ├── home/          → Dashboard del Hogar, Tareas, Chat grupal, Gastos, Calendario
│   ├── Admin/         → Dashboard Admin, Gestion de Viviendas/Usuarios, Solicitudes
│   └── shared/        → Landing, Not Found
├── services/          → Servicios HTTP por modulo
├── Models/            → Interfaces TypeScript (DTOs)
├── guards/            → Route Guards (auth, admin, hasHome)
└── pipes/             → Pipes personalizados
```

### Rutas principales

| Ruta | Componente | Guard | Descripcion |
|---|---|---|---|
| `/` | Landing | — | Pagina de inicio |
| `/login` | Login | — | Inicio de sesion |
| `/register` | Register | — | Registro |
| `/verify` | Verify | — | Verificacion de cuenta |
| `/explore` | Explore | — | Buscador de viviendas |
| `/explore/:provincia/:municipio` | Explore | — | Busqueda filtrada por ubicacion |
| `/inmueble/:titulo/:id` | Detail | — | Detalle de un inmueble |
| `/publicar-anuncio` | InfoAnnouncement | — | Informacion para publicar |
| `/new-announcement` | Create | authGuard | Crear un anuncio |
| `/editar/:id` | Create | — | Editar un anuncio |
| `/perfil` | Profile | authGuard | Perfil del usuario |
| `/favoritos` | Favourites | authGuard | Viviendas favoritas |
| `/mis-publicaciones` | MyPublications | authGuard | Mis anuncios publicados |
| `/messages` | Messages | authGuard | Bandeja de mensajes |
| `/messages/:chatId` | Messages | authGuard | Conversacion especifica |
| `/admin/*` | Admin Layout | adminGuard | Panel de administracion |
| `/admin/dashboard` | Dashboard | adminGuard | Dashboard estadisticas |
| `/admin/houses` | AdminHouses | adminGuard | Gestion de viviendas |
| `/admin/houses/requests` | AdminRequests | adminGuard | Solicitudes pendientes |
| `/admin/users` | AdminUsers | adminGuard | Gestion de usuarios |
| `/hogar/*` | Home Layout | hasHomeGuard | Seccion del hogar |
| `/hogar/dashboard` | HomeDashboard | hasHomeGuard | Dashboard del hogar |
| `/hogar/tareas` | Tasks | hasHomeGuard | Tablon de tareas |
| `/hogar/chat` | Chat | hasHomeGuard | Chat grupal |
| `/hogar/gastos` | Expense | hasHomeGuard | Gastos compartidos |
| `/hogar/agenda` | Calendar | hasHomeGuard | Calendario compartido |

### Guards

- **`authGuard`**: Verifica que el usuario este autenticado (tenga un token JWT valido).
- **`adminGuard`**: Verifica que el usuario tenga rol `Admin` o `LinkHogar`.
- **`hasHomeGuard`**: Verifica que el usuario pertenezca a un hogar (tenga `homeId`).

---

## Despliegue

### Backend

El backend esta preparado para desplegarse en **Railway** u otros servicios PaaS. Las variables de entorno de la base de datos (`MYSQLHOST`, `MYSQLPORT`, etc.) se configuran segun el proveedor.

- URL de produccion: `https://api.linkhogar.com`

### Frontend

El frontend puede construirse para produccion con:

```bash
cd linkhogar-web
ng build --configuration production
```

Los artefactos de produccion se generan en `dist/`. Se puede servir desde cualquier CDN o servidor estatico.

- URL de produccion: `https://linkhogar.com`

### Docker (Frontend - Desarrollo)

El frontend incluye un `Dockerfile` para desarrollo:

```bash
cd linkhogar-web
docker build -t linkhogar-front .
docker run -p 4200:4200 linkhogar-front
```

---

## Documentacion interactiva (Swagger)

Una vez levantado el backend, la documentacion interactiva de la API esta disponible en:

```
http://localhost:8080/swagger-ui.html
```

Para autenticarte en Swagger UI:
1. Usa el endpoint `POST /auth/login` para obtener un token JWT.
2. Haz clic en el boton **"Authorize"** en Swagger.
3. Pega el token en el campo del esquema `bearerAuth`.

---

## Estructura de carpetas

```
LinkHogar/
├── docker-compose.yml                  # MySQL 8.0 para desarrollo
├── pom.xml                             # Dependencias Maven del backend
├── mvnw / mvnw.cmd                     # Maven Wrapper
├── migration/                          # Migraciones SQL adicionales
│   ├── V2__ImageMigration.sql
│   └── V6__profileSettings.sql
├── src/
│   ├── main/
│   │   ├── java/com/linkhogar/
│   │   │   ├── LinkHogarApplication.java
│   │   │   ├── domain/                 # Entidades, repos-interfaz, enums
│   │   │   │   ├── address/
│   │   │   │   ├── chat/
│   │   │   │   ├── common/             # Notification, Result, Error, enums
│   │   │   │   ├── event/
│   │   │   │   ├── expense/
│   │   │   │   ├── homeTasks/
│   │   │   │   ├── house/
│   │   │   │   ├── room/
│   │   │   │   ├── settings/
│   │   │   │   └── user/
│   │   │   ├── application/            # Casos de uso (CQRS)
│   │   │   │   ├── admin/
│   │   │   │   ├── chat/
│   │   │   │   ├── event/
│   │   │   │   ├── expense/
│   │   │   │   ├── home/
│   │   │   │   ├── homeTask/
│   │   │   │   ├── house/
│   │   │   │   ├── notifications/
│   │   │   │   ├── settings/
│   │   │   │   └── user/
│   │   │   └── infrastructure/         # Adaptadores
│   │   │       ├── config/             # OpenAPI, WebSocket, Cloudinary, Async
│   │   │       ├── externalServices/   # Cloudinary, Mail (Brevo), Nominatim
│   │   │       ├── persistence/        # Implementaciones JPA
│   │   │       ├── rest/               # Controllers REST
│   │   │       ├── scheduler/          # Tareas programadas
│   │   │       └── security/           # JWT, SecurityConfig
│   │   └── resources/
│   │       ├── application.properties  # Configuracion principal
│   │       └── db/migration/           # Migraciones Flyway
│   └── test/
│       └── java/com/linkhogar/         # Tests
├── linkhogar-web/                      # Frontend Angular
│   ├── Dockerfile                      # Docker para desarrollo del frontend
│   ├── package.json
│   ├── angular.json
│   ├── tsconfig.json
│   └── src/
│       └── app/
│           ├── Models/                 # Interfaces TypeScript
│           ├── guards/                 # Route Guards
│           ├── pages/                  # Componentes de pagina
│           ├── pipes/                  # Pipes personalizados
│           ├── services/               # Servicios HTTP
│           ├── app.routes.ts           # Definicion de rutas
│           ├── app.config.ts           # Configuracion de la app
│           └── app.ts                  # Componente raiz
└── .gitignore
```

---

## Servicios Externos Integrados

| Servicio | Uso | Configuracion |
|---|---|---|
| **Cloudinary** | Almacenamiento y transformacion de imagenes (viviendas, habitaciones, avatares) | Variables `CLOUDINARY_*` |
| **Brevo (Sendinblue)** | Envio de emails transaccionales (verificacion, contrasena, notificaciones) | Variable `BREVO_API_KEY` |
| **Nominatim (OpenStreetMap)** | Geocodificacion de ciudades para obtener coordenadas (latitud/longitud) | Sin configuracion (API publica) |
| **Google Maps** | Visualizacion de mapas en el frontend | Configurado en el componente Angular |

---

## Seguridad

- **Autenticacion**: JWT (JSON Web Token) stateless. Cada peticion lleva el token en el header `Authorization`.
- **Autorizacion por roles**: `Admin`/`LinkHogar` (administradores), `Propietario`, `User`.
- **Politica CORS**: Configurada para permitir origenes de desarrollo (`localhost:4200`) y produccion (`linkhogar.com`).
- **Verificacion de email**: Obligatoria para activar la cuenta tras el registro.
- **Sesiones**: Stateless — no se almacenan sesiones en el servidor.
- **Cifrado de contrasenas**: Gestionado por Spring Security (BCrypt).

---

## Tareas Programadas

| Tarea | Frecuencia | Descripcion |
|---|---|---|
| **Recordatorio de eventos** | Cada minuto | Busca eventos proximos cuyo recordatorio no haya sido enviado y envia un email a todos los miembros del hogar. |

---

## Licencia

Proyecto academico desarrollado como Trabajo de Fin de Grado.

---

*Documentacion generada para el repositorio [LinkHogar](https://github.com/Sergions1/LinkHogar).*

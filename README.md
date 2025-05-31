# Tienda Sublime

## Descripción
Tienda Sublime es una aplicación móvil Android desarrollada para gestionar una tienda de mugs. La aplicación permite a los usuarios explorar, buscar y gestionar productos relacionados con mugs, con funcionalidades de autenticación y geolocalización.

## Características Implementadas
- Interfaz de usuario moderna y atractiva
- Sistema de autenticación (Login y Registro)
- Pantalla de inicio (Splash Screen)
- Navegación entre pantallas
- Vista detallada de productos
- Interfaz adaptativa para diferentes tamaños de pantalla
- Almacenamiento local con SQLite
- Geolocalización usando Api de Google
- Gestión de sesiones de usuario

## Componentes Principales
- **Activities**:
  - MainActivity: Actividad principal de la aplicación
  - SplashActivity: Pantalla de inicio
  - LoginActivity: Gestión de inicio de sesión
  - RegisterActivity: Registro de nuevos usuarios

- **Fragments**:
  - HomeFragment: Vista principal de productos
  - ProductDetailFragment: Detalles del producto
  - ProfileFragment: Perfil de usuario
  - CartFragment: Carrito de compras
  - StoreLocatorFragment: Localización de tiendas cercanas
  - FavoritesFragment: Productos favoritos

- **Estructura del Proyecto**:
  - `/ui`: Componentes de interfaz de usuario
  - `/models`: Modelos de datos
  - `/adapters`: Adaptadores para RecyclerViews
  - `/data`: 
    - `/database`: Implementación de SQLite
    - `/repository`: Repositorios de datos
  - `/utils`: Utilidades y helpers
  - `/interfaces`: Interfaces y contratos
  - `/location`: Servicios de geolocalización
  - `/auth`: Componentes de autenticación

## Tecnologías Utilizadas
- **Lenguaje**: Kotlin
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Componentes Android**:
  - ViewBinding
  - Navigation Component
  - LiveData
  - ViewModel
  - ConstraintLayout
  - Material Design
  - Room Database (SQLite)
  - Location Services
  - SharedPreferences

## Base de Datos
- **SQLite con Room**:
  - Tabla de Usuarios
  - Tabla de Productos
  - Tabla de Carrito
  - Tabla de Favoritos
  - Tabla de Historial de Compras

## Geolocalización
- Implementación de Google Maps
- Búsqueda de tiendas cercanas
- Cálculo de distancias
- Navegación a tiendas
- Permisos de ubicación en tiempo real

## Requisitos Técnicos
- Android SDK mínimo: 24 (Android 7.0)
- Android SDK objetivo: 35
- Java 11
- Kotlin
- Google Play Services (para geolocalización)

## Dependencias Principales
- AndroidX Core KTX
- Material Design
- Navigation Component
- Lifecycle Components (LiveData, ViewModel)
- ConstraintLayout
- Room Database
- Google Maps SDK
- Location Services
- JUnit para pruebas
- Espresso para pruebas de UI

## Configuración del Proyecto
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Configura tu API Key de Google Maps en el archivo de configuración
4. Sincroniza el proyecto con Gradle
5. Ejecuta la aplicación en un emulador o dispositivo físico

## Licencia
Este proyecto es propiedad de SAR Games y está protegido por derechos de autor. 
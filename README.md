<<<<<<< HEAD
This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
=======
# MovieVerse-KMP

MovieVerse-KMP es una aplicación multiplataforma de películas desarrollada desde cero con Kotlin Multiplatform y Compose Multiplatform, acompañada de un backend propio con Ktor.

## Objetivo del proyecto

El objetivo de este proyecto es construir una aplicación moderna, escalable y mantenible para consultar películas e incorporar funciones sociales para usuarios registrados.

## Funcionalidades previstas

### v1
- Registro e inicio de sesión
- Listado de películas
- Búsqueda de películas
- Detalle de película
- Acceso restringido a detalles para usuarios autenticados
- Favoritos
- Comentarios en películas
- Perfil básico de usuario
- Trailers incrustados desde YouTube
- Backend base
- Base de datos de usuarios

### Futuras versiones
- Recomendaciones por película
- Recomendaciones entre usuarios
- Chat entre usuarios
- Blog o comunidad
- Notificaciones
- Panel de administración
- Moderación

## Stack tecnológico

- Kotlin Multiplatform
- Compose Multiplatform
- Ktor
- PostgreSQL
- SQLDelight
- Git y GitHub
- TMDB API
- YouTube embed

## Objetivos técnicos

- Diseñar una arquitectura limpia y escalable
- Separar correctamente frontend, backend y datos
- Aplicar buenas prácticas de Git y GitHub
- Trabajar con ramas por funcionalidad
- Mantener documentación clara desde el inicio

## Estado actual

Proyecto en fase inicial de preparación, definición de arquitectura y configuración del flujo de trabajo.

## Flujo de trabajo

- La rama principal será `main`
- Cada cambio se desarrollará en una rama independiente
- No se trabajará directamente sobre `main`
- Los cambios se integrarán mediante Pull Request
- Se crearán tags para hitos importantes del proyecto

## Licencia

Pendiente de definir.
>>>>>>> 5846ef933023be3b4a73ea9a954b54b6d4533ab8

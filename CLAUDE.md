# CLAUDE.md — MovieVerseKMP

## Descripción
Aplicación multiplataforma de películas (Android, iOS, Desktop) con comunidad, foro y favoritos, construida con Kotlin Multiplatform + Compose Multiplatform y backend Ktor propio.

## Stack Tecnológico
- **Cliente:** Kotlin 2.3.0 + Compose Multiplatform 1.10.0
- **Targets:** Android (minSdk 24, targetSdk 36) · iOS · Desktop (JVM)
- **DI:** Koin 4.0.4 (`koin-core`, `koin-compose`, `koin-compose-viewmodel`)
- **Networking:** Ktor Client 3.4.1 + Kotlinx Serialization
- **Estado:** Androidx Lifecycle 2.9.6 — ViewModel + StateFlow
- **Imágenes:** Coil 3.x
- **Almacenamiento local:** multiplatform-settings 1.3.0
- **Servidor:** Ktor Server 3.4.1 (Netty) + PostgreSQL + Exposed ORM 0.61.0 + HikariCP
- **Build:** Gradle + Version Catalog (`gradle/libs.versions.toml`) · AGP 8.13.2
- **Deploy:** Docker + Railway / Render / VPS

## Comandos del Proyecto

```bash
# Build completo
./gradlew build

# Servidor Ktor (desarrollo)
./gradlew :server:run

# App Android (APK debug)
./gradlew :composeApp:assembleDebug

# App Desktop
./gradlew :composeApp:run

# Tests
./gradlew test

# Clean
./gradlew clean
```

## Arquitectura del Proyecto

```
composeApp/src/commonMain/kotlin/com/albertiacob91/movieversekmp/
├── core/              # Result wrapper, constantes, utilidades
├── data/              # Repositorios impl, remote API (Ktor DTOs)
├── domain/
│   ├── model/         # Entidades de dominio
│   ├── repository/    # Interfaces de repositorio
│   └── usecase/       # Casos de uso: auth/, movies/, forum/
├── di/                # AppModule.kt — todos los módulos Koin
├── navigation/        # Compose Navigation
└── presentation/
    ├── components/    # Composables reutilizables
    ├── screens/       # Pantallas: auth/, home/, movies/, forum/, profile/
    ├── theme/         # MaterialTheme, colores, tipografía
    └── viewmodel/     # ViewModels con StateFlow

server/src/main/kotlin/
├── auth/              # JWT, hashing de contraseñas
├── config/            # Plugins de Ktor, configuración general
├── data/              # Modelos Exposed, repositorios de BD
├── dto/               # DTOs serializables request/response
└── routing/           # Rutas Ktor por dominio
```

## Convenciones de Código
- **Idioma:** código en inglés · comentarios y PRs en español
- **Commits:** Conventional Commits — `feat:` `fix:` `refactor:` `docs:` `chore:`
- **Ramas:** `feature/nombre-descriptivo` | `fix/nombre-bug` | `release/vX.X`
- **DI:** registrar todo en `AppModule.kt`; usar `viewModelOf()`, nunca `viewModel {}`
- **ViewModels:** exponen `StateFlow<UiState>` + funciones `onXxx()`; sin lógica en Composables
- **UseCases:** una clase = una responsabilidad; retornan `Result<T>` o `Flow<T>`
- **Repositorios:** interfaz en `domain/`, impl en `data/`; la UI nunca accede a la impl

## Patrones y Buenas Prácticas
- **Clean Architecture:** toda feature sigue el flujo Repository → UseCase → ViewModel → Screen
- **MVVM:** estado en ViewModel, eventos via funciones, UI reactiva con `collectAsState()`
- Variables de entorno y credenciales: siempre en `.env` o `local.properties`, nunca en el código
- DTOs de Ktor usados directamente como modelos de dominio (sin mappers — decisión pragmática)
- Código platform-specific en `androidMain/`, `iosMain/`, `jvmMain/` según corresponda

## Prohibiciones Explícitas
- NO hacer commits directos a `main` — siempre via PR
- NO instalar dependencias sin justificación en el PR
- NO escribir lógica de negocio en Composables — va en ViewModel o UseCase
- NO exponer credenciales, tokens ni API keys en el código
- NO cambiar versiones de Kotlin o Compose Multiplatform sin revisar compatibilidad
- NO añadir mappers domain↔data mientras se use el enfoque pragmático actual

## Testing y CI/CD
> **Pendiente — deuda técnica futura.** No hay tests implementados (decisión explícita).
> Cuando se aborde:
> - Framework objetivo: kotlin.test + Turbine (Flow testing)
> - Cobertura mínima objetivo: 80%
> - Pipeline previsto: lint → test → build → deploy

## Skills disponibles
- Para **revisión de PRs** → `/review`
- Para **revisión de seguridad** → `/security-review`
- Para **simplificar código** → `/simplify`
- Para **mejorar arquitectura** → skill `improve-codebase-architecture`
- Para **debugging sistemático** → skill `systematic-debugging`
- Para **cerrar branches** → skill `finishing-a-development-branch`

## MCPs configurados
- **Engram MCP** — memoria persistente entre sesiones (`mem_save`, `mem_search`, `mem_context`)

## Memory
Tienes acceso a memoria persistente via Engram (MCP tools: `mem_save`, `mem_search`, `mem_context`).
- Guarda proactivamente tras trabajo significativo — refactors, decisiones de arquitectura, bugs resueltos.
- Tras cualquier compactación o reset de contexto, llama a `mem_context` antes de continuar.

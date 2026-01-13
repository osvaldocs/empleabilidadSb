# Sistema de Gestión de Proyectos y Tareas

Aplicación web para gestión de proyectos y tareas implementada con Java 17, Spring Boot 3 y arquitectura hexagonal.

## Tecnologías

- **Backend**: Java 17, Spring Boot 3.5.9
- **Arquitectura**: Hexagonal (Ports & Adapters)
- **Base de datos**: MySQL 8.0
- **Seguridad**: Spring Security + JWT
- **Documentación API**: Swagger/OpenAPI (Springdoc)
- **Frontend**: HTML5, CSS3, JavaScript vanilla
- **Testing**: JUnit 5, Mockito
- **Containerización**: Docker, Docker Compose

## Requisitos

- Docker y Docker Compose
- (Opcional) Java 17 y Maven 3.9+ para desarrollo local

## Ejecución con Docker

### Opción 1: Docker Compose (Recomendado)

```bash
# Clonar el repositorio y navegar al directorio
cd demo

# Levantar la aplicación (backend + base de datos)
docker compose up --build

# La aplicación estará disponible en:
# - Frontend: http://localhost:8080
# - API REST: http://localhost:8080/api
# - Swagger UI: http://localhost:8080/swagger-ui.html
```

Para detener la aplicación:

```bash
docker compose down
```

Para detener y eliminar los volúmenes (datos):

```bash
docker compose down -v
```

### Opción 2: Ejecución local (sin Docker)

#### Requisitos previos

1. MySQL 8.0 corriendo en `localhost:3306`
2. Crear base de datos:

```sql
CREATE DATABASE demo_project;
CREATE USER 'demo_user'@'localhost' IDENTIFIED BY 'demo_pass';
GRANT ALL PRIVILEGES ON demo_project.* TO 'demo_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Ejecutar la aplicación

```bash
# Compilar y ejecutar
./mvnw spring-boot:run

# O compilar y ejecutar el JAR
./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Credenciales de Prueba

La aplicación no tiene usuarios precargados. Puedes:

1. **Registrar un nuevo usuario** desde el frontend (http://localhost:8080)
2. **Usar Swagger** para registrar usuarios vía API

### Ejemplo de registro vía Swagger

1. Acceder a: http://localhost:8080/swagger-ui.html
2. Endpoint: `POST /api/auth/register`
3. Body:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

## Uso de la Aplicación

### Frontend

1. Acceder a http://localhost:8080
2. **Registrarse** o **Iniciar sesión**
3. **Crear proyecto** (estado inicial: DRAFT)
4. **Ver tareas** del proyecto
5. **Crear tareas** en el proyecto
6. **Activar proyecto** (requiere al menos una tarea activa)
7. **Completar tareas**

### API REST

Documentación interactiva disponible en: http://localhost:8080/swagger-ui.html

#### Endpoints principales

**Autenticación**
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/login` - Iniciar sesión (retorna JWT)

**Proyectos** (requieren JWT)
- `GET /api/projects` - Listar proyectos del usuario
- `POST /api/projects` - Crear proyecto
- `PATCH /api/projects/{id}/activate` - Activar proyecto

**Tareas** (requieren JWT)
- `GET /api/projects/{projectId}/tasks` - Listar tareas del proyecto
- `POST /api/projects/{projectId}/tasks` - Crear tarea
- `PATCH /api/projects/{projectId}/tasks/{taskId}/complete` - Completar tarea

#### Uso del JWT en Swagger

1. Hacer login en `/api/auth/login`
2. Copiar el `token` de la respuesta
3. Clic en el botón **Authorize** (candado) en la parte superior
4. Ingresar: `Bearer {token}` o solo `{token}`
5. Probar los endpoints protegidos

## Arquitectura

### Estructura del Proyecto

```
src/main/java/com/example/demo/
├── domain/                    # Capa de dominio (sin dependencias externas)
│   ├── model/                # Entidades de dominio
│   ├── exception/            # Excepciones de negocio
│   └── port/out/             # Puertos de salida (interfaces)
├── application/              # Capa de aplicación
│   ├── port/in/              # Puertos de entrada (casos de uso)
│   └── service/              # Implementación de casos de uso
└── infrastructure/           # Capa de infraestructura
    ├── controller/           # Controladores REST
    ├── persistence/          # Adaptadores JPA
    ├── security/             # Configuración de seguridad
    ├── config/               # Configuración de beans
    ├── audit/                # Adaptador de auditoría
    └── notification/         # Adaptador de notificaciones
```

### Principios Aplicados

- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura
- **Dependency Inversion**: Las dependencias apuntan hacia el dominio
- **Ports & Adapters**: Interfaces (puertos) y sus implementaciones (adaptadores)
- **Clean Architecture**: Reglas de negocio independientes de frameworks

## Reglas de Negocio

1. Un proyecto solo puede activarse si tiene **al menos una tarea activa** (no completada ni eliminada)
2. Solo el **propietario** puede modificar un proyecto o sus tareas
3. Una **tarea completada no puede modificarse**
4. Todas las eliminaciones son **lógicas** (soft delete)
5. La **activación de proyectos** genera auditoría y notificación
6. La **finalización de tareas** genera auditoría y notificación

## Pruebas

### Ejecutar pruebas unitarias

```bash
# Con Maven
./mvnw test

# Con Docker (compilar sin ejecutar tests)
docker compose build
```

### Pruebas implementadas

- `ActivateProject_WithTasks_ShouldSucceed`
- `ActivateProject_WithoutTasks_ShouldFail`
- `ActivateProject_ByNonOwner_ShouldFail`
- `CompleteTask_AlreadyCompleted_ShouldFail`
- `CompleteTask_ShouldGenerateAuditAndNotification`

## Decisiones Técnicas

### Arquitectura Hexagonal

Se eligió arquitectura hexagonal para:
- Mantener el dominio independiente de frameworks
- Facilitar testing con mocks de puertos
- Permitir cambiar adaptadores sin afectar la lógica de negocio

### JWT sin Refresh Token

Para simplificar el assessment, se implementó JWT con:
- Token de 1 hora de duración
- Sin refresh token
- Almacenamiento en localStorage del frontend

### Auditoría y Notificaciones

Implementadas como adaptadores simples que:
- **AuditAdapter**: Registra en consola (puede extenderse a BD)
- **NotificationAdapter**: Notifica en consola (puede extenderse a email/SMS)

### Base de Datos

- MySQL con JPA/Hibernate
- DDL automático (`spring.jpa.hibernate.ddl-auto=update`)
- Sin migraciones (Flyway/Liquibase) para simplificar

### Frontend

- HTML/CSS/JS vanilla sin frameworks
- Enfoque funcional sobre diseño visual
- Integración directa con la API REST

## Troubleshooting

### Error de conexión a MySQL

Si el backend no puede conectar a MySQL:

```bash
# Verificar que MySQL esté corriendo
docker compose ps

# Ver logs de MySQL
docker compose logs mysql

# Reiniciar servicios
docker compose restart
```

### Puerto 8080 ocupado

Si el puerto 8080 está en uso:

```yaml
# Editar docker-compose.yml
services:
  backend:
    ports:
      - "8081:8080"  # Cambiar puerto externo
```

### Problemas de permisos en Maven

```bash
# Linux/Mac
chmod +x mvnw

# Windows
# Usar mvnw.cmd en lugar de ./mvnw
```

## Autor

Desarrollado como assessment técnico para demostrar:
- Arquitectura hexagonal
- Clean code
- Testing
- Integración completa (backend + frontend + BD)





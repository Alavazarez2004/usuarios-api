Requisitos: JDK 25+ y Maven (o usa el wrapper `mvnw` si lo agregas desde tu IDE).
# API de Gestión de Usuarios y Autenticación Básica — Spring Boot

Proyecto de la Unidad 1: API CRUD en Spring Boot. Implementa registro, login y
gestión (CRUD) de usuarios siguiendo una arquitectura en capas.

## Parte 1: Investigación Teórica

### 1. Arquitectura en Capas

- **Controller**: es la puerta de entrada HTTP. Recibe la petición (JSON),
  valida el formato de los datos de entrada (`@Valid`) y delega toda la
  lógica de negocio al Service. Su única responsabilidad es traducir
  HTTP ⇄ objetos Java y devolver el código de estado correcto. No debe
  contener lógica de negocio ni acceder a la base de datos directamente.
- **Service**: contiene la lógica de negocio real: reglas como "el email no
  puede repetirse", "hashear la contraseña antes de guardar", "comparar
  credenciales en el login", "desactivar en vez de borrar". Coordina al
  Repository y lanza excepciones de negocio cuando algo no cumple una regla.
- **Repository**: es la capa de acceso a datos. Se encarga únicamente de
  leer/escribir en la base de datos (a través de Spring Data JPA). No sabe
  nada de HTTP ni de reglas de negocio, solo de consultas.
- **Entity**: representa la tabla de la base de datos como un objeto Java
  (mapeo objeto-relacional). Define los campos y sus restricciones a nivel
  de persistencia (`@Column(unique = true)`, tipos, etc.).

En conjunto, esta separación permite que un cambio en una capa (por ejemplo,
cambiar de H2 a MySQL) no afecte a las demás, y facilita las pruebas
unitarias porque cada capa se puede probar de forma aislada.

### 2. Manejo de Contraseñas

Guardar contraseñas en texto plano es un riesgo crítico: si la base de datos
se filtra o alguien con acceso interno la consulta, todas las cuentas quedan
comprometidas de inmediato, y como muchas personas reutilizan contraseñas,
el daño se extiende a otros sistemas.

**BCrypt** es un algoritmo de *hashing* diseñado específicamente para
contraseñas:
- Genera un hash de un solo sentido: a partir del hash no se puede recuperar
  la contraseña original.
- Incorpora un *salt* aleatorio en cada hash, por lo que dos usuarios con la
  misma contraseña obtienen hashes distintos, evitando ataques por tablas
  precalculadas (*rainbow tables*).
- Es deliberadamente costoso computacionalmente (parámetro de "rondas"), lo
  que hace mucho más lento un ataque de fuerza bruta.
- Para validar el login no se "desencripta" el hash: se vuelve a calcular el
  hash de la contraseña recibida y se compara contra el hash guardado
  (`passwordEncoder.matches(...)`).

### 3. Registro vs. Login

- **Registro**: es una operación de **creación**. Recibe datos nuevos
  (nombre, email, contraseña), valida que el email no exista todavía,
  hashea la contraseña y **crea** una fila nueva en la base de datos.
- **Login**: es una operación de **verificación**, no crea nada. Busca al
  usuario por email y comprueba si la contraseña enviada, al hashearla,
  coincide con el hash ya almacenado. Si coincide, se confirma la identidad;
  si no, se rechaza el acceso.

### 4. Buenas Prácticas REST (verbos y códigos usados en este proyecto)

| Endpoint | Verbo | Código de éxito | Código de error |
|---|---|---|---|
| `/api/v1/auth/register` | POST | 201 Created | 400 Bad Request (campo faltante o email duplicado) |
| `/api/v1/auth/login` | POST | 200 OK | 401 Unauthorized (credenciales inválidas) |
| `/api/v1/users` | GET | 200 OK | — |
| `/api/v1/users/{id}` | GET | 200 OK | 404 Not Found (id no existe) |
| `/api/v1/users/{id}` | PUT | 200 OK | 400 Bad Request / 404 Not Found |
| `/api/v1/users/{id}` | DELETE | 200 OK | 404 Not Found |

Regla general: **POST** crea, **GET** consulta (nunca modifica), **PUT**
actualiza un recurso existente, **DELETE** elimina (o en este caso,
desactiva) un recurso.

## Parte 2: Estructura del proyecto

```
com.ejemplo.usuarios
├── entity        -> Usuario, EstadoUsuario
├── repository    -> UsuarioRepository
├── service       -> UsuarioService (lógica de negocio)
├── controller    -> AuthController, UsuarioController
├── dto           -> RegisterRequest, LoginRequest, UpdateUsuarioRequest,
│                    UsuarioResponse, MensajeResponse
├── exception     -> excepciones de negocio + GlobalExceptionHandler
└── config        -> SecurityConfig (bean de BCryptPasswordEncoder)
```

Buenas prácticas aplicadas:
- **Inyección por constructor** en `UsuarioService`, `AuthController` y
  `UsuarioController` (nada de `@Autowired` sobre atributos).
- **BCryptPasswordEncoder** para hashear contraseñas antes de guardarlas.
- **`ResponseEntity`** en todos los endpoints para controlar el código HTTP.
- **Validaciones con `@Valid`** + `GlobalExceptionHandler` para responder
  400/401/404 de forma centralizada en vez de repetir try/catch.
- **Soft delete**: `DELETE` no borra físicamente, cambia `estado` a
  `INACTIVO`.

## Cómo ejecutar el proyecto localmente

Requisitos: JDK 17+ y Maven (o usa el wrapper `mvnw` si lo agregas desde tu IDE).

```bash
# 1. Clonar el repositorio
git clone <URL-DE-TU-REPO>
cd usuarios-api

# 2. Ejecutar la aplicación
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.
Usa una base de datos H2 en memoria (no necesitas instalar nada); la consola
web está en `http://localhost:8080/h2-console` (JDBC URL:
`jdbc:h2:mem:usuariosdb`, usuario `sa`, sin contraseña).

## Ejemplos de peticiones (cURL)

**Registro**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Miguel Alavazarez","email":"miguel@example.com","password":"MiClave123"}'
```

**Login**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"miguel@example.com","password":"MiClave123"}'
```

**Login con credenciales incorrectas (debe responder 401)**
```bash
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"miguel@example.com","password":"incorrecta"}'
```

**Listar usuarios**
```bash
curl http://localhost:8080/api/v1/users
```

**Obtener usuario por id**
```bash
curl http://localhost:8080/api/v1/users/1
```

**Obtener un id que no existe (debe responder 404)**
```bash
curl -i http://localhost:8080/api/v1/users/999
```

**Actualizar usuario**
```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Miguel A. Alavazarez","email":"miguel.a@example.com"}'
```

**Eliminar (desactivar) usuario**
```bash
curl -X DELETE http://localhost:8080/api/v1/users/1
```

**Registro con campo faltante (debe responder 400)**
```bash
curl -i -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Sin Email","password":"MiClave123"}'
```

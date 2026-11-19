# PanolApp Backend

Microservicios Spring Boot (Java 21) para PanolApp. Base de datos: Firebase Firestore. Despliegue: Docker + Docker Compose en AWS EC2 con IP elástica.

| Servicio | Puerto | Endpoints |
|---|---|---|
| usuarios-service | 8081 | POST /api/usuarios/sincronizar, GET /api/usuarios, GET /api/usuarios/{id}, PUT /api/usuarios/{id}/rol |
| equipos-service | 8082 | GET /api/equipos?categoria&q, GET/POST/PUT/DELETE /api/equipos/{id} |
| prestamos-service | 8083 | POST /api/prestamos, GET /api/prestamos?estado, GET /api/prestamos/{id}, PUT /api/prestamos/{id}/devolucion |
| reportes-service | 8084 | GET /api/reportes/resumen, /top-equipos?limite, /mi-resumen |

Todos exponen `GET /health`. Errores con formato `{"mensaje": "..."}`.

## Identidad
El login se hace en SQLite dentro de la app. La app envía los headers `X-Usuario` (correo), `X-Rol` y opcionalmente `X-API-KEY` (si `API_KEY` está definida). Limitación conocida: el servidor confía en esos headers, no hay JWT.

## Configuración
1. En Firebase Console crea un proyecto, activa Firestore y descarga la clave de cuenta de servicio (JSON).
2. Copia `.env.example` a `.env` y completa `API_KEY`, `ADMIN_CORREO`.
3. Pon el JSON en `secrets/` o codifícalo en base64 en `FIREBASE_CREDENTIALS_BASE64`. Nunca subas el JSON, `.env` ni `.pem` a GitHub (están en `.gitignore`).

## Ejecutar
```
docker compose up -d --build
curl http://localhost:8081/health
```

## Tests
Cada servicio: `cd usuarios-service && ./mvnw test` (JUnit, repositorios en memoria). Colección Postman en `docs/PanolApp.postman_collection.json`; scripts `scripts_probar.sh` / `.ps1`.

## Despliegue en AWS EC2
1. Instancia Amazon Linux 2023 (t3.small), asignar IP elástica.
2. Security group: 22 (tu IP) y 8081–8084.
3. Instalar Docker y el plugin compose, clonar el repo, crear `.env` y `secrets/`.
4. `docker compose up -d --build`.
5. En la app, `ApiConfig.HOST = "http://<IP-ELASTICA>"`.

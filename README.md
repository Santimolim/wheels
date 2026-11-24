# Wheels

Aplicación de escritorio en Java (Swing) para compartir viajes entre estudiantes universitarios, con interfaz en formato de celular. Proyecto de NetBeans (Ant) con base de datos MySQL/MariaDB en XAMPP.

## Requisitos
- JDK 21 o superior y NetBeans (proyecto Ant)
- XAMPP con MySQL
- Librerías incluidas en `lib/`:
  - `mysql-connector-java-8.0.17.jar` (driver JDBC)
  - `flatlaf-3.8.jar` y `flatlaf-fonts-inter-4.1.jar` (aspecto visual moderno y fuente Inter)

## Cómo ejecutarlo
1. Iniciar MySQL en XAMPP.
2. En phpMyAdmin: **Importar** → `wheels.sql` → Continuar.
   El script crea la base `wheels` con 11 tablas y datos de prueba
   (las fechas de los viajes se calculan a partir del día de la importación).
3. Si tu MySQL **no** usa el puerto 3307, copia `wheels.properties.example`
   como `wheels.properties` (junto a `build.xml`) y cambia `db.port`.
4. Abrir la carpeta en NetBeans → Clean and Build (Shift+F11) → Run (F6).
   La clase principal es `wheels.Wheels`.

Usuario de prueba: `santiago.molina@unisabana.edu.co` / `123456`
(las contraseñas de prueba se cifran automáticamente en el primer inicio de sesión).

## Configuración de la base de datos
Se lee en este orden: propiedades `-Dwheels.db.port=...`, variables de entorno
`WHEELS_DB_PORT`, archivo `wheels.properties`, y por defecto `localhost:3307 / wheels / root`.
Claves: `db.host`, `db.port`, `db.name`, `db.user`, `db.password`.

## Funcionalidades
- Registro e inicio de sesión con correo institucional y contraseña cifrada (PBKDF2).
- Búsqueda de viajes por origen, destino y fecha; destinos populares.
- Detalle del viaje con recorrido, mapa esquemático, conductor y vehículo.
- Solicitar, cancelar, aceptar y rechazar puestos (con control atómico de cupos).
- Publicar viajes con preferencias y vista previa; cancelar viajes publicados.
- Viaje en curso con llegada estimada, chat, compartir datos y botón SOS.
- Calificaciones, notificaciones y perfil editable (incluye cambio de contraseña).

## Estructura
- `src/wheels/` — clase principal, `datos/` (conexión, seguridad y acceso a BD), `modelo/`, `vistas/` (pantallas)
- `src/wheels/vistas/` — `Aplicacion` (ventana única y navegación), `Estilo` (sistema de diseño),
  `Iconos` y `Logo` (gráficos vectoriales), `Diseno` (layouts), una clase `Ventana*` por pantalla
- `src/entidades/` — una clase por tabla de la base de datos
- `wheels.sql` — script de la base de datos
- `RESTAURAR.md` — notas detalladas de configuración y decisiones del proyecto

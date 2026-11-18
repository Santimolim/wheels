# Wheels - Como restaurar el proyecto (estado al 2026-10-07)

## Que es
- Proyecto Java (NetBeans, Ant) en `D:\Helados\wheels`, clase principal `wheels.Wheels`.
- Base de datos MySQL/MariaDB de **XAMPP en el puerto 3307**, base **`wheels`**, usuario `root` sin contrasena.
- Conexion configurada en `src/wheels/datos/Conexion.java` (HOST localhost, PUERTO 3307, DB wheels).

## Pasos
1. Descomprimir el zip en `D:\Helados\wheels`.
2. XAMPP: iniciar Apache y MySQL. MySQL de XAMPP debe escuchar en **3307**
   (`C:\xampp\mysql\bin\my.ini` -> `port=3307`; phpMyAdmin:
   `C:\xampp\phpMyAdmin\config.inc.php` -> `$cfg['Servers'][$i]['port'] = '3307';`).
   Nota: en esta PC hay otro MySQL 8 aparte en el 3306, no es el del proyecto.
3. Base de datos: phpMyAdmin -> Importar -> `wheels.sql` -> Continuar.
   (o consola: `C:\xampp\mysql\bin\mysql.exe --user=root --port=3307 --protocol=TCP < wheels.sql`)
   El script borra y recrea `wheels`. Resultado esperado: 11 tablas, 18 claves foraneas, 75 filas.
4. Driver JDBC: `mysql-connector-java-8.0.17.jar` (no va en el zip, se descarga de
   https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.17/mysql-connector-java-8.0.17.jar).
   Ruta esperada por `nbproject/project.properties`:
   `C:\Users\prestamo\Downloads\COMPLEMENTOS JAR\COMPLEMENTOS JAR\4.2 mysql-connector-java-8.0.17.jar`
   Si se pone en otra ruta, actualizar `file.reference.4.2_mysql-connector-java-8.0.17.jar-1`.
5. NetBeans: abrir el proyecto -> Clean and Build (Shift+F11).

## Decisiones tomadas
- La base original se llamaba `wheels_app`; se renombro a `wheels`.
- Regla del curso: **todas las columnas son INT o VARCHAR**.
  - Fechas `VARCHAR(10)` 'AAAA-MM-DD', horas `VARCHAR(8)` 'HH:MM:SS', timestamps `VARCHAR(19)`.
  - `trips.price` INT (pesos); `users.rating` VARCHAR(3) ('4.8'); enums -> VARCHAR; text -> VARCHAR(500); booleanos -> INT 0/1.
  - `created_at`/`sent_at`/`requested_at` ya no se llenan solos: el programa debe enviarlos.
- `src/entidades/` tiene una entidad por tabla (11): University, User, Vehicle, Trip, TripRequest,
  TripPreference, PickupPoint, Message, Notification, Rating, Report. Solo `int` y `String`, atributos en
  camelCase en el mismo orden que las columnas, constructor vacio + completo, getters/setters, toString.
  (`trips.java` se renombro a `Trip.java`.)
- En `wheels.sql` las tablas estan ordenadas por dependencias (para phpMyAdmin con revision de FKs activa).

## Cambios 2026-11-18
- Se elimino el modo demo: `BaseDatos.java` ya lee y escribe en MySQL (via `Conexion`). Si no hay conexion, la app avisa y se cierra.
- Login con usuarios reales de la BD (ej. `santiago.molina@unisabana.edu.co` / `123456`).
- `Viaje.precio` ahora es `int`. Fechas `AAAA-MM-DD`, horas `HH:MM:SS`.
- Nuevo: estilo redondeado, mapa de ruta (`PanelMapa`, `VentanaMapa`), viaje en curso con boton SOS
  (`VentanaViajeEnCurso`), chat (`DialogoChat`), notificaciones (`VentanaNotificaciones`),
  pestana Conductor (aceptar/rechazar) y calificacion al finalizar.
- `trip_requests.status` usa ademas `in_progress` y `completed`. El SOS crea un registro en `reports`
  con reason 'BOTON DE PANICO' y notifica a los usuarios con role 'admin'.
- Respaldo del codigo anterior: `..\wheels_src_respaldo_2026-11-18.zip`.

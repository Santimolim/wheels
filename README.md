# Wheels

Aplicación de escritorio en Java (Swing) para compartir viajes entre estudiantes universitarios. Proyecto de NetBeans (Ant) con base de datos MySQL/MariaDB en XAMPP.

## Requisitos
- JDK 8 o superior y NetBeans (proyecto Ant)
- XAMPP con MySQL en el puerto **3307**
- Driver JDBC incluido en `lib/mysql-connector-java-8.0.17.jar`

## Cómo ejecutarlo
1. Iniciar Apache y MySQL en XAMPP. MySQL debe escuchar en el puerto 3307
   (`C:\xampp\mysql\bin\my.ini` → `port=3307`).
2. En phpMyAdmin: **Importar** → `wheels.sql` → Continuar.
   El script crea la base `wheels` con 11 tablas y datos de prueba.
3. Abrir la carpeta en NetBeans → Clean and Build (Shift+F11) → Run (F6).
   La clase principal es `wheels.Wheels`.

La conexión se configura en `src/wheels/datos/Conexion.java`
(host `localhost`, puerto `3307`, base `wheels`, usuario `root` sin contraseña).

Usuario de prueba: `santiago.molina@unisabana.edu.co` / `123456`

## Estructura
- `src/wheels/` — clase principal, `datos/` (conexión y acceso a BD), `modelo/`, `vistas/` (ventanas)
- `src/entidades/` — una clase por tabla de la base de datos
- `wheels.sql` — script de la base de datos
- `RESTAURAR.md` — notas detalladas de configuración y decisiones del proyecto

-- =========================================================
-- Base de datos: wheels
-- Para importar en phpMyAdmin (XAMPP)
-- Pestana "Importar" -> elegir este archivo -> Continuar
-- Todos los campos son INT o VARCHAR
-- OJO: borra y recrea la base "wheels" con sus datos
-- Datos de prueba: las fechas de los viajes se calculan a partir
-- del dia de la importacion (manana, pasado manana...).
-- Las contrasenas de prueba (123456) estan en texto plano: la
-- aplicacion las cifra (PBKDF2) en el primer inicio de sesion.
-- =========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS wheels;
CREATE DATABASE wheels CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE wheels;

-- ---------- universities ----------
CREATE TABLE `universities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `created_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `universities` VALUES (1,'Universidad de La Sabana','2026-10-07 08:06:39'),(2,'Universidad de los Andes','2026-10-07 08:06:39'),(3,'Universidad Nacional de Colombia','2026-10-07 08:06:39'),(4,'Pontificia Universidad Javeriana','2026-10-07 08:06:39'),(5,'Universidad del Rosario','2026-10-07 08:06:39'),(6,'Universidad Externado de Colombia','2026-10-07 08:06:39');

-- ---------- users ----------
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `university_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `career` varchar(150) NOT NULL,
  `semester` int(11) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'student',
  `rating` varchar(3) DEFAULT '5.0',
  `created_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_users_university` (`university_id`),
  CONSTRAINT `fk_users_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `users` VALUES (1,1,'Santiago Molina','santiago.molina@unisabana.edu.co','123456','Ingenieria de Sistemas',6,'3001112233',NULL,'student','4.8','2026-10-07 08:06:39'),(2,1,'Laura Martinez','laura.martinez@unisabana.edu.co','123456','Administracion de Empresas',5,'3012223344',NULL,'student','4.7','2026-10-07 08:06:39'),(3,2,'Daniel Rodriguez','daniel.rodriguez@uniandes.edu.co','123456','Ingenieria de Software',7,'3023334455',NULL,'student','4.9','2026-10-07 08:06:39'),(4,3,'Valentina Gomez','valentina.gomez@unal.edu.co','123456','Ingenieria Industrial',4,'3034445566',NULL,'student','4.6','2026-10-07 08:06:39'),(5,4,'Juan Perez','juan.perez@javeriana.edu.co','123456','Ingenieria Electronica',8,'3045556677',NULL,'student','4.5','2026-10-07 08:06:39'),(6,5,'Camila Torres','camila.torres@urosario.edu.co','123456','Derecho',6,'3056667788',NULL,'student','4.8','2026-10-07 08:06:39'),(7,1,'Andres Castillo','andres.castillo@unisabana.edu.co','123456','Comunicacion',3,'3067778899',NULL,'student','4.4','2026-10-07 08:06:39'),(8,2,'Maria Fernandez','maria.fernandez@uniandes.edu.co','123456','Arquitectura',5,'3078889900',NULL,'student','4.7','2026-10-07 08:06:39'),(9,3,'Sebastian Rojas','sebastian.rojas@unal.edu.co','123456','Medicina',9,'3089990011',NULL,'student','4.9','2026-10-07 08:06:39'),(10,6,'Administrador Wheels','admin@wheels.edu.co','123456','Administracion del Sistema',1,'3000000000',NULL,'admin','5.0','2026-10-07 08:06:39');

-- ---------- vehicles ----------
CREATE TABLE `vehicles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `brand` varchar(50) NOT NULL,
  `model` varchar(50) NOT NULL,
  `color` varchar(30) NOT NULL,
  `plate` varchar(10) NOT NULL,
  `year` int(11) NOT NULL,
  `seats` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `plate` (`plate`),
  KEY `fk_vehicles_user` (`user_id`),
  CONSTRAINT `fk_vehicles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `vehicles` VALUES (1,1,'Renault','Logan','Blanco','ABC123',2022,4),(2,2,'Chevrolet','Onix','Gris','DEF456',2021,4),(3,3,'Mazda','2','Rojo','GHI789',2023,4),(4,4,'Kia','Picanto','Azul','JKL321',2020,4),(5,5,'Toyota','Corolla','Negro','MNO654',2022,4),(6,6,'Renault','Sandero','Plateado','PQR987',2021,4),(7,7,'Nissan','March','Blanco','STU147',2019,4);

-- ---------- trips ----------
CREATE TABLE `trips` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `origin` varchar(150) NOT NULL,
  `destination` varchar(150) NOT NULL,
  `departure_date` varchar(10) NOT NULL,
  `departure_time` varchar(8) NOT NULL,
  `available_seats` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `status` varchar(20) DEFAULT 'available',
  `created_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_trips_driver` (`driver_id`),
  KEY `fk_trips_vehicle` (`vehicle_id`),
  CONSTRAINT `fk_trips_driver` FOREIGN KEY (`driver_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_trips_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `trips` VALUES (1,1,1,'Chia','Universidad de La Sabana',DATE_FORMAT(CURDATE() + INTERVAL 1 DAY,'%Y-%m-%d'),'07:00:00',3,5000,'available','2026-10-07 08:06:39'),(2,2,2,'Bogota','Universidad de La Sabana',DATE_FORMAT(CURDATE() + INTERVAL 1 DAY,'%Y-%m-%d'),'06:30:00',2,6000,'available','2026-10-07 08:06:39'),(3,3,3,'Bogota','Universidad de los Andes',DATE_FORMAT(CURDATE() + INTERVAL 2 DAY,'%Y-%m-%d'),'07:15:00',3,5000,'available','2026-10-07 08:06:39'),(4,4,4,'Bogota','Universidad Nacional',DATE_FORMAT(CURDATE() + INTERVAL 2 DAY,'%Y-%m-%d'),'06:45:00',2,4500,'available','2026-10-07 08:06:39'),(5,5,5,'Bogota','Pontificia Universidad Javeriana',DATE_FORMAT(CURDATE() + INTERVAL 3 DAY,'%Y-%m-%d'),'07:30:00',0,5500,'full','2026-10-07 08:06:39'),(6,6,6,'Bogota','Universidad del Rosario',DATE_FORMAT(CURDATE() + INTERVAL 3 DAY,'%Y-%m-%d'),'06:50:00',3,5000,'available','2026-10-07 08:06:39'),(7,7,7,'Cajica','Universidad de La Sabana',DATE_FORMAT(CURDATE() + INTERVAL 4 DAY,'%Y-%m-%d'),'07:10:00',2,4000,'available','2026-10-07 08:06:39');

-- ---------- trip_preferences ----------
CREATE TABLE `trip_preferences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trip_id` int(11) NOT NULL,
  `music_allowed` int(11) DEFAULT 1,
  `pets_allowed` int(11) DEFAULT 0,
  `luggage_allowed` int(11) DEFAULT 1,
  `smoking_allowed` int(11) DEFAULT 0,
  `conversation_preference` varchar(20) DEFAULT 'normal',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_trip_preferences` (`trip_id`),
  CONSTRAINT `fk_preferences_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `trip_preferences` VALUES (1,1,1,0,1,0,'normal'),(2,2,1,0,1,0,'quiet'),(3,3,1,1,1,0,'talkative'),(4,4,0,0,1,0,'quiet'),(5,5,1,0,0,0,'normal'),(6,6,1,0,1,0,'talkative'),(7,7,0,1,1,0,'normal');

-- ---------- trip_requests ----------
CREATE TABLE `trip_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trip_id` int(11) NOT NULL,
  `passenger_id` int(11) NOT NULL,
  `status` varchar(20) DEFAULT 'pending',
  `requested_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_trip_passenger` (`trip_id`,`passenger_id`),
  KEY `fk_trip_requests_passenger` (`passenger_id`),
  CONSTRAINT `fk_trip_requests_passenger` FOREIGN KEY (`passenger_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_trip_requests_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `trip_requests` VALUES (1,1,2,'pending','2026-10-07 08:06:39'),(2,1,7,'accepted','2026-10-07 08:06:39'),(3,2,1,'accepted','2026-10-07 08:06:39'),(4,3,8,'pending','2026-10-07 08:06:39'),(5,4,9,'accepted','2026-10-07 08:06:39'),(6,6,8,'pending','2026-10-07 08:06:39'),(7,7,2,'rejected','2026-10-07 08:06:39');

-- ---------- pickup_points ----------
CREATE TABLE `pickup_points` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trip_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(200) NOT NULL,
  `pickup_time` varchar(8) NOT NULL,
  `created_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pickup_trip` (`trip_id`),
  CONSTRAINT `fk_pickup_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `pickup_points` VALUES (1,1,'Portal del Norte','Autopista Norte, Bogota','06:20:00','2026-10-07 08:06:39'),(2,1,'Centro Chia','Centro Comercial Centro Chia','06:40:00','2026-10-07 08:06:39'),(3,2,'Calle 170','Calle 170, Bogota','05:50:00','2026-10-07 08:06:39'),(4,2,'Portal Norte','Autopista Norte, Bogota','06:10:00','2026-10-07 08:06:39'),(5,3,'Calle 100','Calle 100, Bogota','06:30:00','2026-10-07 08:06:39'),(6,3,'Zona T','Carrera 13, Bogota','06:50:00','2026-10-07 08:06:39'),(7,4,'Portal Suba','Avenida Suba, Bogota','06:00:00','2026-10-07 08:06:39'),(8,4,'Calle 72','Calle 72, Bogota','06:25:00','2026-10-07 08:06:39'),(9,6,'Chapinero','Carrera 13, Bogota','06:20:00','2026-10-07 08:06:39');

-- ---------- messages ----------
CREATE TABLE `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_id` int(11) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `trip_id` int(11) DEFAULT NULL,
  `message` varchar(500) NOT NULL,
  `is_read` int(11) DEFAULT 0,
  `sent_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_messages_sender` (`sender_id`),
  KEY `fk_messages_receiver` (`receiver_id`),
  KEY `fk_messages_trip` (`trip_id`),
  CONSTRAINT `fk_messages_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_messages_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `messages` VALUES (1,2,1,1,'Hola Santiago, el viaje sigue disponible?',1,'2026-10-07 08:06:39'),(2,1,2,1,'Si, el viaje esta confirmado para manana a las 7 AM.',1,'2026-10-07 08:06:39'),(3,7,1,1,'En que punto de recogida debo esperar?',0,'2026-10-07 08:06:39'),(4,3,8,3,'Hola, llegare al punto de encuentro 10 minutos antes.',1,'2026-10-07 08:06:39'),(5,8,3,3,'Perfecto, estare ahi a tiempo.',1,'2026-10-07 08:06:39'),(6,9,4,4,'Puedo llevar una mochila pequena?',0,'2026-10-07 08:06:39');

-- ---------- notifications ----------
CREATE TABLE `notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `message` varchar(500) NOT NULL,
  `type` varchar(30) NOT NULL,
  `is_read` int(11) DEFAULT 0,
  `created_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notifications_user` (`user_id`),
  CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `notifications` VALUES (1,1,'Nueva solicitud de viaje','Laura Martinez solicito un cupo en tu viaje.','trip_request',0,'2026-10-07 08:06:39'),(2,7,'Solicitud aceptada','Tu solicitud de viaje fue aceptada.','request_accepted',1,'2026-10-07 08:06:39'),(3,1,'Nuevo mensaje','Recibiste un nuevo mensaje de Andres Castillo.','new_message',0,'2026-10-07 08:06:39'),(4,2,'Solicitud aceptada','Tu solicitud fue aceptada por el conductor.','request_accepted',1,'2026-10-07 08:06:39'),(5,3,'Nueva calificacion','Recibiste una nueva calificacion de un pasajero.','rating',0,'2026-10-07 08:06:39'),(6,4,'Nueva solicitud','Un pasajero solicito un cupo en tu viaje.','trip_request',0,'2026-10-07 08:06:39'),(7,5,'Viaje cancelado','Tu viaje programado fue cancelado.','trip_cancelled',1,'2026-10-07 08:06:39');

-- ---------- ratings ----------
CREATE TABLE `ratings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trip_id` int(11) NOT NULL,
  `reviewer_id` int(11) NOT NULL,
  `reviewed_user_id` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `created_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ratings_trip` (`trip_id`),
  KEY `fk_ratings_reviewer` (`reviewer_id`),
  KEY `fk_ratings_reviewed` (`reviewed_user_id`),
  CONSTRAINT `fk_ratings_reviewed` FOREIGN KEY (`reviewed_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ratings_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ratings_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE,
  CONSTRAINT `check_rating_score` CHECK (`score` between 1 and 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `ratings` VALUES (1,1,2,1,5,'Excelente conductor y muy puntual.','2026-10-07 08:06:39'),(2,1,7,1,5,'Viaje muy comodo y seguro.','2026-10-07 08:06:39'),(3,2,1,2,4,'Buena experiencia y llegamos a tiempo.','2026-10-07 08:06:39'),(4,3,8,3,5,'Excelente conductor y muy organizado.','2026-10-07 08:06:39'),(5,4,9,4,4,'Buen viaje y conduccion segura.','2026-10-07 08:06:39'),(6,6,8,6,5,'Muy buena experiencia.','2026-10-07 08:06:39');

-- ---------- reports ----------
CREATE TABLE `reports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reporter_id` int(11) NOT NULL,
  `reported_user_id` int(11) NOT NULL,
  `trip_id` int(11) DEFAULT NULL,
  `reason` varchar(150) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'pending',
  `created_at` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_reports_reporter` (`reporter_id`),
  KEY `fk_reports_reported` (`reported_user_id`),
  KEY `fk_reports_trip` (`trip_id`),
  CONSTRAINT `fk_reports_reported` FOREIGN KEY (`reported_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reports_reporter` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reports_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `reports` VALUES (1,2,1,1,'Llegada tarde','El conductor llego aproximadamente 15 minutos tarde.','reviewed','2026-10-07 08:06:39'),(2,7,2,2,'Informacion incorrecta','El punto de recogida era diferente al publicado.','pending','2026-10-07 08:06:39'),(3,8,3,3,'Otro','El usuario proporciono informacion incorrecta sobre el viaje.','dismissed','2026-10-07 08:06:39');

SET FOREIGN_KEY_CHECKS = 1;

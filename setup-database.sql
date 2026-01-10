-- Script de configuration MySQL pour le projet E-Commerce
-- À exécuter avant de lancer le backend Spring Boot

-- 1. Créer la base de données
CREATE DATABASE IF NOT EXISTS `sb-ecommerce-dev`;

-- 2. Créer l'utilisateur admin (si nécessaire)
CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY 'admin';

-- 3. Donner tous les privilèges à l'utilisateur admin
GRANT ALL PRIVILEGES ON `sb-ecommerce-dev`.* TO 'admin'@'localhost';

-- 4. Appliquer les changements
FLUSH PRIVILEGES;

-- 5. Utiliser la base de données
USE `sb-ecommerce-dev`;

-- 6. Vérifier
SELECT 'Base de données créée avec succès !' AS message;
SHOW DATABASES LIKE 'sb-ecommerce-dev';

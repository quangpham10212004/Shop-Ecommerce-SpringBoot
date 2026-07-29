/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.6-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: order_service_db
-- ------------------------------------------------------
-- Server version	11.8.6-MariaDB-0+deb13u1 from Debian

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` varchar(36) NOT NULL,
  `order_id` varchar(36) NOT NULL,
  `product_id` varchar(36) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(15,2) NOT NULL,
  `line_total` decimal(15,2) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `last_modified_at` datetime(6) DEFAULT NULL,
  `last_modified_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_items_order` (`order_id`),
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES
('0d12ccee-c4c1-42db-a6f3-eb3c3542baa7','9bf69d47-3570-4436-94c6-41204444bf11','45a3622a-f669-4799-9dbc-52d086133e20',2,150000.00,300000.00,NULL,'2026-07-29 06:03:35.302399','quang.pham','2026-07-29 06:03:35.302399','quang.pham'),
('2cfcb8c4-39d9-4b01-b91b-6f3de3493fff','7eaf50cc-97e5-48d3-8d8c-306bebdb13d6','45a3622a-f669-4799-9dbc-52d086133e20',2,150000.00,300000.00,NULL,'2026-07-28 19:42:06.247833','quang.pham','2026-07-28 19:42:06.247833','quang.pham'),
('350be631-ffb4-4a3b-8483-3aa875434017','ae55204e-4ea2-4e80-8381-16970e686581','45a3622a-f669-4799-9dbc-52d086133e20',10,150000.00,1500000.00,NULL,'2026-07-22 10:20:08.263401','quang.pham','2026-07-22 10:20:08.263401','quang.pham'),
('40a1815c-5b70-42b6-8375-e14930dafdeb','92209741-4427-4e80-a80e-0774cee70bae','45a3622a-f669-4799-9dbc-52d086133e20',2,150000.00,300000.00,NULL,'2026-07-29 05:56:45.402800','quang.pham','2026-07-29 05:56:45.402800','quang.pham'),
('5151bc73-e19c-4419-9ff8-82775450528c','a308c2c9-ebf5-4296-8d07-aedbe5a62344','45a3622a-f669-4799-9dbc-52d086133e20',10,150000.00,1500000.00,NULL,'2026-07-28 17:45:18.653259','quang.pham','2026-07-28 17:45:18.653259','quang.pham');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `status` varchar(20) NOT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `last_modified_at` datetime(6) DEFAULT NULL,
  `last_modified_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES
('7eaf50cc-97e5-48d3-8d8c-306bebdb13d6','Cus-002','CREATED',300000.00,0,'2026-07-28 19:42:06.247543','quang.pham','2026-07-28 19:42:06.247543','quang.pham'),
('92209741-4427-4e80-a80e-0774cee70bae','Cus-002','CREATED',300000.00,0,'2026-07-29 05:56:45.388017','quang.pham','2026-07-29 05:56:45.388017','quang.pham'),
('9bf69d47-3570-4436-94c6-41204444bf11','Cus-002','CONFIRMED',300000.00,0,'2026-07-29 06:03:35.301739','quang.pham','2026-07-29 06:03:35.539913','quang.pham'),
('a308c2c9-ebf5-4296-8d07-aedbe5a62344','Cus-002','CREATED',1500000.00,0,'2026-07-28 17:45:18.638381','quang.pham','2026-07-28 17:45:18.638381','quang.pham'),
('ae55204e-4ea2-4e80-8381-16970e686581','Cus-002','CREATED',1500000.00,NULL,'2026-07-22 10:20:08.245635','quang.pham','2026-07-22 10:20:08.245635','quang.pham'),
('e358fafd-edff-48b8-a143-c953e2b1f503','Cus-001','CREATED',300000.00,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-07-29 17:18:16
/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.6-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: product_service_db
-- ------------------------------------------------------
-- Server version	11.8.6-MariaDB-0+deb13u1 from Debian

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: study_mate
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `study`
--

DROP TABLE IF EXISTS `study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `leader` varchar(255) DEFAULT NULL,
  `content` text,
  `status` varchar(50) DEFAULT 'RECRUITING',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study`
--

LOCK TABLES `study` WRITE;
/*!40000 ALTER TABLE `study` DISABLE KEYS */;
INSERT INTO `study` VALUES (1,'일본어 공부 같이 하실 분 구합니다!','스시타베타이','#일본어 #비즈니스일본어 #JLPT #일상회화','RECRUITING','2026-02-07 09:11:40'),(2,'프로그래밍 공부하는 사람들 모임','우힉이','#프로그래밍 #자바 #백엔드','RECRUITING','2026-02-07 09:11:40'),(3,'토익 공부방','김토익','#영어 #토익 #취업준비','CLOSED','2026-02-07 09:11:40'),(4,'포토샵 알려주실 분 구함','디자인왕','#디자인 #포토샵 #일러스트','RECRUITING','2026-02-07 09:11:40'),(5,'일본어 공부 같이 하실 분 구합니다!','스시타베타이','#일본어 #비즈니스일본어 #JLPT #일상회화','RECRUITING','2026-02-07 09:12:27'),(6,'프로그래밍 공부하는 사람들 모임','우힉이','#프로그래밍 #자바 #백엔드','RECRUITING','2026-02-07 09:12:27'),(7,'토익 공부방','김토익','#영어 #토익 #취업준비','CLOSED','2026-02-07 09:12:27'),(8,'포토샵 알려주실 분 구함','디자인왕','#디자인 #포토샵 #일러스트','RECRUITING','2026-02-07 09:12:27'),(9,'일본어 공부 같이 하실 분 구합니다!','스시타베타이','#일본어 #비즈니스일본어 #JLPT #일상회화','RECRUITING','2026-02-07 09:12:42'),(10,'프로그래밍 공부하는 사람들 모임','우힉이','#프로그래밍 #자바 #백엔드','RECRUITING','2026-02-07 09:12:42'),(11,'토익 공부방','김토익','#영어 #토익 #취업준비','CLOSED','2026-02-07 09:12:42'),(12,'포토샵 알려주실 분 구함','디자인왕','#디자인 #포토샵 #일러스트','RECRUITING','2026-02-07 09:12:42'),(13,'일본어 공부 같이 하실 분 구합니다!','스시타베타이','#일본어 #비즈니스일본어 #JLPT #일상회화','RECRUITING','2026-02-07 09:13:11'),(14,'프로그래밍 공부하는 사람들 모임','우힉이','#프로그래밍 #자바 #백엔드','RECRUITING','2026-02-07 09:13:11'),(15,'토익 공부방','김토익','#영어 #토익 #취업준비','CLOSED','2026-02-07 09:13:11'),(16,'포토샵 알려주실 분 구함','디자인왕','#디자인 #포토샵 #일러스트','RECRUITING','2026-02-07 09:13:11'),(17,'일본어 공부 같이 하실 분 구합니다!','스시타베타이','#일본어 #비즈니스일본어 #JLPT #일상회화','RECRUITING','2026-02-07 09:13:27'),(18,'프로그래밍 공부하는 사람들 모임','우힉이','#프로그래밍 #자바 #백엔드','RECRUITING','2026-02-07 09:13:27'),(19,'토익 공부방','김토익','#영어 #토익 #취업준비','CLOSED','2026-02-07 09:13:27'),(20,'포토샵 알려주실 분 구함','디자인왕','#디자인 #포토샵 #일러스트','RECRUITING','2026-02-07 09:13:27'),(21,'일본어 공부 같이 하실 분 구합니다!','스시타베타이','#일본어 #비즈니스일본어 #JLPT #일상회화','RECRUITING','2026-02-07 09:15:41'),(22,'프로그래밍 공부하는 사람들 모임','우힉이','#프로그래밍 #자바 #백엔드','RECRUITING','2026-02-07 09:15:41'),(23,'토익 공부방','김토익','#영어 #토익 #취업준비','CLOSED','2026-02-07 09:15:41'),(24,'포토샵 알려주실 분 구함','디자인왕','#디자인 #포토샵 #일러스트','RECRUITING','2026-02-07 09:15:41'),(25,'일본어 공부 같이 하실 분!','스시타베타이','#일본어 #회화','RECRUITING','2026-02-07 09:40:04'),(26,'자바 스프링 스터디','우힉이','#프로그래밍 #백엔드','RECRUITING','2026-02-07 09:40:04');
/*!40000 ALTER TABLE `study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_post`
--

DROP TABLE IF EXISTS `study_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `creator_id` varchar(255) DEFAULT NULL,
  `creator_name` varchar(255) DEFAULT NULL,
  `current_people` int NOT NULL,
  `hashtags` varchar(255) DEFAULT NULL,
  `max_people` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_post`
--

LOCK TABLES `study_post` WRITE;
/*!40000 ALTER TABLE `study_post` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `birth_date` date NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `emailVerified` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-10  1:01:30
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: studygroup
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-10  1:01:30
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: studydb
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `study_post`
--

DROP TABLE IF EXISTS `study_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `hashtags` varchar(255) DEFAULT NULL,
  `creator_name` varchar(255) DEFAULT NULL,
  `creator_id` varchar(255) DEFAULT NULL,
  `current_people` int DEFAULT '1',
  `max_people` int DEFAULT '5',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_post`
--

LOCK TABLES `study_post` WRITE;
/*!40000 ALTER TABLE `study_post` DISABLE KEYS */;
INSERT INTO `study_post` VALUES (1,'프로그래밍 공부하는 사람들 모임','자바와 스프링 부트를 함께 공부하며 백엔드 개발자로 성장할 분들을 모십니다.','#자바 #스프링 #백엔드','개발왕','dev123',2,5),(2,'일본어 기초 회화 스터디','JLPT N3 수준의 회화를 목표로 합니다.','#일본어 #회화 #N3','스시조아','sushi',3,6),(3,'토익 900점 목표 달성방','단기간에 고득점이 필요한 취준생들을 위한 스터디입니다.','#토익 #취준 #고득점','토익커','toeic990',4,8),(4,'포토샵/일러스트 실무 과외','디자인 비전공자도 가능!','#디자인 #포토샵 #일러스트','디자이너','design',1,4),(5,'프로그래밍 공부하는 사람들 모임','자바와 스프링 부트를 함께 공부하며 백엔드 개발자로 성장할 분들을 모십니다.','#자바 #스프링 #백엔드','개발왕','dev123',2,5),(6,'일본어 기초 회화 스터디','JLPT N3 수준의 회화를 목표로 합니다.','#일본어 #회화 #N3','스시조아','sushi',3,6),(7,'토익 900점 목표 달성방','단기간에 고득점이 필요한 취준생들을 위한 스터디입니다.','#토익 #취준 #고득점','토익커','toeic990',4,8),(8,'포토샵/일러스트 실무 과외','디자인 비전공자도 가능!','#디자인 #포토샵 #일러스트','디자이너','design',1,4);
/*!40000 ALTER TABLE `study_post` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-10  1:01:31

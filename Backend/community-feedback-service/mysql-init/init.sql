-- Schéma + données de démo pour learnhub_community_db (MySQL 8)
-- Exécuté automatiquement au 1er démarrage du conteneur MySQL.
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS learnhub_community_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE learnhub_community_db;

CREATE TABLE IF NOT EXISTS course_reviews (
  id                  VARCHAR(36)   NOT NULL,
  user_id             VARCHAR(255)  NOT NULL,
  author_name         VARCHAR(255)  NOT NULL,
  course_id           VARCHAR(255)  NOT NULL,
  course_title        VARCHAR(255)  NOT NULL,
  rating              INT           NOT NULL,
  comment             VARCHAR(1000) NOT NULL,
  moderation_status   VARCHAR(20)   NOT NULL,
  verified_enrollment BIT(1)        NOT NULL,
  created_at          DATETIME(6)   NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_review_course (course_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS discussion_threads (
  id          VARCHAR(36)  NOT NULL,
  course_id   VARCHAR(255) NOT NULL,
  author_id   VARCHAR(255) NOT NULL,
  author_name VARCHAR(255) NOT NULL,
  title       VARCHAR(255) NOT NULL,
  content     TEXT         NOT NULL,
  created_at  DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_thread_course (course_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS thread_replies (
  thread_id   VARCHAR(36) NOT NULL,
  author_id   VARCHAR(255),
  author_name VARCHAR(255),
  content     TEXT,
  created_at  DATETIME(6),
  CONSTRAINT fk_reply_thread FOREIGN KEY (thread_id) REFERENCES discussion_threads (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS badges (
  id            VARCHAR(36)  NOT NULL,
  code          VARCHAR(255) NOT NULL,
  name          VARCHAR(255) NOT NULL,
  description   VARCHAR(255),
  icon          VARCHAR(50),
  trigger_event VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT uniq_badge_code UNIQUE (code)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_badges (
  id         VARCHAR(36)  NOT NULL,
  user_id    VARCHAR(255) NOT NULL,
  badge_id   VARCHAR(255) NOT NULL,
  source     VARCHAR(255),
  awarded_at DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uniq_user_badge UNIQUE (user_id, badge_id)
) ENGINE=InnoDB;

-- Données de démo (INSERT IGNORE : rejouable sans erreur)
INSERT IGNORE INTO course_reviews
  (id, user_id, author_name, course_id, course_title, rating, comment, moderation_status, verified_enrollment, created_at) VALUES
  ('rev_1','usr_1','Nawres Soua','crs_1','Architecture des Systèmes Distribués',5,'Excellent cours, le bus RabbitMQ est très bien expliqué !','APPROVED',1,NOW(6)),
  ('rev_2','usr_2','Mootez SFAIHI','crs_2','Spring Cloud & Eureka',5,'Tutorat au top, circuit breaker très clair.','APPROVED',1,NOW(6)),
  ('rev_3','usr_3','Étudiant Externe','crs_3','Docker Compose',3,'Besoin d''un exemple supplémentaire sur les volumes.','PENDING',0,NOW(6));

INSERT IGNORE INTO discussion_threads (id, course_id, author_id, author_name, title, content, created_at) VALUES
  ('thr_1','crs_1','usr_2','Mootez SFAIHI','Configurer la Dead-Letter Queue sous RabbitMQ','Comment bien configurer la DLQ sous RabbitMQ ?',NOW(6));

INSERT INTO thread_replies (thread_id, author_id, author_name, content, created_at)
  SELECT 'thr_1','usr_1','Nawres Soua','Utilise x-dead-letter-exchange sur la queue principale.',NOW(6)
  WHERE NOT EXISTS (SELECT 1 FROM thread_replies WHERE thread_id = 'thr_1');

INSERT IGNORE INTO badges (id, code, name, description, icon, trigger_event) VALUES
  ('bdg_1','AWD_ARCHITECT','Architecte AWD','Certificat AWD obtenu','🎓','CertificateIssued'),
  ('bdg_2','EVENT_MASTER','Event Master','Maîtrise de RabbitMQ','⚡','RabbitMQ');

INSERT IGNORE INTO user_badges (id, user_id, badge_id, source, awarded_at) VALUES
  ('ub_1','usr_1','bdg_1','RABBITMQ',NOW(6)),
  ('ub_2','usr_1','bdg_2','MANUAL',NOW(6));

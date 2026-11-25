-- =====================================================================
--  LearnHub — marketplace-service — Base dédiée : db_marketplace (MySQL 8)
--  Database-per-Service : AUCUNE clé étrangère vers une autre base.
--  studentId (UUID Keycloak) et courseId (course-service) = références LOGIQUES.
--
--  NB : Hibernate (ddl-auto=update) crée ces tables automatiquement au démarrage.
--  Ce script sert de documentation / de création manuelle (phpMyAdmin, Workbench).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS db_marketplace
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_marketplace;

-- ---------------------------------------------------------------------
-- 1) orders : la commande (agrégat racine).  "order" est un mot réservé SQL.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    student_id    VARCHAR(64)    NOT NULL COMMENT 'Réf. logique : UUID Keycloak (X-User-Id)',
    status        VARCHAR(20)    NOT NULL COMMENT 'PENDING | PAID | FAILED | REFUNDED',
    total_amount  DECIMAL(10,2)  NOT NULL,
    currency      VARCHAR(3)     NOT NULL DEFAULT 'TND',
    created_at    DATETIME(6)    NOT NULL,
    paid_at       DATETIME(6)    NULL,
    PRIMARY KEY (id),
    KEY idx_orders_student (student_id),
    KEY idx_orders_status (status),
    CONSTRAINT chk_orders_status CHECK (status IN ('PENDING','PAID','FAILED','REFUNDED'))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2) order_items : TABLE D'ASSOCIATION  orders  <-- N : N -->  cours du catalogue
--    order_id  = vraie FK (même base)
--    course_id = référence logique (PAS de FK : le cours vit dans db_course)
--    course_title / unit_price = SNAPSHOT figé au moment de l'achat (via OpenFeign)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    order_id      BIGINT         NOT NULL,
    course_id     BIGINT         NOT NULL COMMENT 'Réf. logique vers course-service',
    course_title  VARCHAR(255)   NOT NULL COMMENT 'Snapshot',
    unit_price    DECIMAL(10,2)  NOT NULL COMMENT 'Snapshot',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_course (order_id, course_id),
    KEY idx_items_course (course_id),
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3) payments : tentatives de paiement (1 commande -> N tentatives)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    order_id         BIGINT         NOT NULL,
    amount           DECIMAL(10,2)  NOT NULL,
    method           VARCHAR(20)    NOT NULL COMMENT 'STRIPE | PAYPAL | SIMULATOR',
    status           VARCHAR(20)    NOT NULL COMMENT 'SUCCESS | FAILED',
    transaction_ref  VARCHAR(64)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_tx (transaction_ref),
    KEY idx_payments_order (order_id),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4) invoices : facture officielle (1 commande payée -> 1 facture)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS invoices (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    order_id        BIGINT       NOT NULL,
    invoice_number  VARCHAR(30)  NOT NULL COMMENT 'Format INV-AAAA-000042',
    issued_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_invoice_order (order_id),
    UNIQUE KEY uk_invoice_number (invoice_number),
    CONSTRAINT fk_invoice_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB;

-- =====================================================================
--  Requêtes utiles pour la soutenance
-- =====================================================================

-- Détail d'une commande avec ses lignes (jointure INTERNE à la base : autorisée)
-- SELECT o.id, o.student_id, o.status, oi.course_id, oi.course_title, oi.unit_price
-- FROM orders o JOIN order_items oi ON oi.order_id = o.id WHERE o.id = 1;

-- Chiffre d'affaires et nombre de ventes par cours (commandes payées)
-- SELECT oi.course_id, MAX(oi.course_title) AS titre, COUNT(*) AS ventes, SUM(oi.unit_price) AS ca
-- FROM order_items oi JOIN orders o ON o.id = oi.order_id
-- WHERE o.status = 'PAID' GROUP BY oi.course_id ORDER BY ventes DESC;

-- L'étudiant X a-t-il acheté le cours Y ? (badge « Acheteur Vérifié »)
-- SELECT COUNT(*) > 0 FROM order_items oi JOIN orders o ON o.id = oi.order_id
-- WHERE o.student_id = 'a1b2c3d4-0000-4000-8000-000000000001' AND oi.course_id = 1 AND o.status = 'PAID';

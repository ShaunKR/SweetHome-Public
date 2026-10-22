CREATE DATABASE IF NOT EXISTS sweethome
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sweethome;

-- plants 테이블
DROP TABLE IF EXISTS plants;

CREATE TABLE plants (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        image_url VARCHAR(255) DEFAULT NULL,
                        last_watered_at DATETIME(6) DEFAULT NULL,
                        name VARCHAR(100) NOT NULL,
                        purchase_date DATE DEFAULT NULL,
                        watering_cycle INT NOT NULL,
                        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- watering_history 테이블
DROP TABLE IF EXISTS watering_history;

CREATE TABLE watering_history (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  plant_id BIGINT NOT NULL,
                                  source VARCHAR(30) DEFAULT NULL,
                                  watered_at DATETIME(6) NOT NULL,
                                  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- account_transaction 테이블
CREATE TABLE account_transaction (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     transaction_date DATE NOT NULL,
                                     transaction_type VARCHAR(20) NOT NULL,
                                     category VARCHAR(50) NOT NULL,
                                     title VARCHAR(100) NOT NULL,
                                     amount BIGINT NOT NULL,
                                     payment_method VARCHAR(20) NOT NULL,
                                     memo VARCHAR(500) NULL,
                                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     INDEX idx_account_transaction_date (transaction_date),
                                     INDEX idx_account_transaction_type (transaction_type),
                                     INDEX idx_account_transaction_category (category)
);

-- DB 생성
CREATE DATABASE IF NOT EXISTS oauth_authorization_db;
USE oauth_authorization_db;

-- 테이블 생성
CREATE TABLE auth_member (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             user_id VARCHAR(50) UNIQUE NOT NULL,
                             password VARCHAR(255) NOT NULL,
                             role VARCHAR(20) DEFAULT 'ROLE_USER',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 데이터 삽입 (비밀번호는 'password123'의 BCrypt 암호화 결과물입니다)
INSERT INTO auth_member (user_id, password, role) VALUES
                                                      ('yeong', '{bcrypt}$2a$10$vI8qO7.jBy.vEw33m.pSze4B.tH9p9OBy5vD2jF2f6C8jL4R/m2/W', 'ROLE_USER'),
                                                      ('ironman', '{bcrypt}$2a$10$vI8qO7.jBy.vEw33m.pSze4B.tH9p9OBy5vD2jF2f6C8jL4R/m2/W', 'ROLE_USER'),
                                                      ('spidey', '{bcrypt}$2a$10$vI8qO7.jBy.vEw33m.pSze4B.tH9p9OBy5vD2jF2f6C8jL4R/m2/W', 'ROLE_USER');
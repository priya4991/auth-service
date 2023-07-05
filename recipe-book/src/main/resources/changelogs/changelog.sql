-- liquibase formatted sql

-- changeset liquibase:2
DROP TABLE user_details CASCADE;
DROP TABLE role_details CASCADE;
CREATE TABLE user_details
(
    id SERIAL PRIMARY KEY,
    username VARCHAR(25),
    passw VARCHAR(100),
    first_name VARCHAR(25),
    last_name VARCHAR(25),
    email_id VARCHAR(100),
    phone_number VARCHAR(20),
    date_of_birth DATE
);
CREATE TABLE role_details
(
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(20)
);
INSERT INTO role_details (role_name) VALUES ('ROLE_ADMIN'),('ROLE_USER');

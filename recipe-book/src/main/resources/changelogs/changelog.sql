-- liquibase formatted sql

-- changeset liquibase:2
DROP TABLE test_table;
CREATE TABLE user_details
(
    id SERIAL PRIMARY KEY,
    username VARCHAR(25),
    passw VARCHAR(100),
    first_name VARCHAR(25),
    last_name VARCHAR(25),
    email_id VARCHAR(100),
    phone_number VARCHAR(20),
    dob DATE
)
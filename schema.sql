create database if not exists user_registration
character set utf8mb4
collate utf8mb4_unicode_ci;

use user_registration;

create table if not exists users (
    id bigint not null auto_increment primary key,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    email varchar(255) not null unique,
    password_hash varchar(255) not null
) engine=InnoDB
default charset=utf8mb4
collate=utf8mb4_unicode_ci;

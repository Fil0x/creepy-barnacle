create database market;
use market;
create table if not exists users(
	username varchar(128) primary key, 
	password varchar(128) not null,
	purchases integer,
	sales integer
);

create table if not exists items(
	id varchar(128) primary key,
	name varchar(128) not null,
	price integer not null,
	amount integer
);

create table if not exists account(
    name varchar(128) primary key,
    balance float not null
);

insert into users (username, password) VALUES ('filox', 'password')
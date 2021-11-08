drop schema read CASCADE;
drop schema write CASCADE;
drop schema public CASCADE;

create schema read;
create schema write;
create schema public;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

delete from write.service;
delete from read.service;

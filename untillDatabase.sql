drop schema read CASCADE;
drop schema write CASCADE;
drop schema public CASCADE;

create schema read;
create schema write;
create schema public;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

delete from write.service;
delete from read.service;

delete from write.industry;
delete from read.industry;


alter table read.connection drop column dates_collaboration;
delete from read.connection;
delete from public.databasechangelog where orderexecuted = 7

select start_collaboration, end_collaboration from "read"."connection"
select int4range(start_collaboration, end_collaboration) from read.connection

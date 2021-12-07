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

ALTER TABLE read.employee ALTER COLUMN user_job_position_ids SET DATA TYPE UUID[] USING "user_job_position_ids"::uuid[]
update "public".databasechangelog set md5sum = '8:25cbff9f73eb40051f52b268eb7dc563' where orderexecuted = 18
update "public".databasechangelog set md5sum = '8:48a1ea5fce13e2e53884a9215dd5285c' where orderexecuted = 17
select pg_typeof(user_job_position_ids) from read.employee
delete from read.employee
select * from read.employee c where c.user_job_position_ids @> array['88939030-abd3-4bda-acac-1243e804cade']::uuid[]

create index idx_connection_deleted_user_ids on read.employee using gin (deleted_user_ids)
alter table read.employee drop column deleted_user_ids
alter table read.connection drop column dates_collaboration;
delete from read.connection;
delete from read.connection_service;
delete from read.user_job_position;
delete from read.employee;
delete from public.databasechangelog where orderexecuted = 7

select data->>'slug' from read.service

select start_collaboration, end_collaboration from "read"."connection"
select int4range(start_collaboration, end_collaboration) from read.connection


select userpermis0_.id as id1_10_, userpermis0_.access_object_type as access_o2_10_, userpermis0_.role as role3_10_, userpermis0_.access_object_uuid as access_o4_10_, userpermis0_.user_id as user_id5_10_
from read.user_permission_role userpermis0_
where userpermis0_.access_object_uuid='4cfe6d05-44dd-48d7-a6fa-668b98714c11' and userpermis0_.user_id='c993e062-49de-40da-9a7a-38bea4107df5'

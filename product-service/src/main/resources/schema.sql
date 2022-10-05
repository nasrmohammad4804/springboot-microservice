
create schema if not exists product_schema;

create table if not exists product_schema.product_category_table (id bigserial not null primary key , name varchar (50));

create table if not exists product_schema.product_table(id bigserial not null  primary key , name varchar (50) not null , quantity bigint not null ,
    price double precision not null , category_id bigint references product_schema.product_category_table(id));
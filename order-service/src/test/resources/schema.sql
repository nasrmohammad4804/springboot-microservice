

create  table if not exists order_table(id bigserial not null  primary key ,
order_date timestamp ,total_price double precision not null , order_status varchar(50) );

create table if not exists order_detail_table(id bigserial not null primary key ,
    order_id bigint not null   ,
    product_id bigint not null  ,product_number bigint not null  );
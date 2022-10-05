create schema if not exists order_schema;

create table if not exists order_schema.order_table
(
    id
    bigserial
    not
    null
    primary
    key,
    order_date
    timestamp,
    total_price
    double
    precision
    not
    null,
    order_status
    varchar
(
    50
) );

create table if not exists order_schema.order_detail_table
(
    id
    bigserial
    not
    null
    primary
    key,
    order_id
    bigint
    not
    null
    references
    order_schema
    .
    order_table
(
    id
) ,
    product_id bigint not null references product_schema.product_table
(
    id
) ,product_number bigint not null );
create schema if not exists payment_schema;

create table if not exists payment_schema.transaction_table
(
    id             bigserial not null primary key,
    payment_status varchar(30) check ( payment_status in ('SUCCESS', 'FAIL', 'IN_PROGRESS') ),
    payment_mode   varchar(30) check ( payment_mode in ('CASH', 'PAYPAL', 'DEBIT_CARD', 'CREDIT_CARD', 'APPLE_PAY') ),
    payment_date timestamp ,
    cardNumber     numeric,
    cvv2           varchar(10),
    order_id       bigint unique references order_schema.order_table (id)
);


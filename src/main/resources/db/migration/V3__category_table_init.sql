SET FOREIGN_KEY_CHECKS = 0;
drop table if exists categories;
create table categories(
                           id int primary key auto_increment,
                           name varchar(100) not null,
                           total_quantity int
);
alter table products add column categories_id int null;
alter table products add foreign key (categories_id) references categories(id);
SET FOREIGN_KEY_CHECKS = 1;
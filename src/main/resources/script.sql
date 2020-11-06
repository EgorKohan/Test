create table if not exists cats
(
    id     int primary key auto_increment,
    name   varchar(45),
    gender ENUM ('MALE', 'FEMALE') NOT NULL,
    color  varchar(45)             not null,
    age    int check ( age >= 0 and age < 21)
);

create table if not exists parents(
    child_id int primary key,
    father_id int,
    mother_id int,
    foreign key (child_id) references netcracker.cats(id)
);
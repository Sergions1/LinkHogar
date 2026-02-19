-- we don't know how to generate root <with-no-name> (class Root) :(

create table addresses
(
    id        binary(16)   not null
        primary key,
    city      varchar(255) not null,
    country   varchar(255) not null,
    door      varchar(255) null,
    floor     varchar(255) null,
    latitude  double       null,
    longitude double       null,
    number    int          not null,
    province  varchar(255) not null,
    street    varchar(255) not null
);

create table flyway_schema_history
(
    installed_rank int                                 not null
        primary key,
    version        varchar(50)                         null,
    description    varchar(200)                        not null,
    type           varchar(20)                         not null,
    script         varchar(1000)                       not null,
    checksum       int                                 null,
    installed_by   varchar(100)                        not null,
    installed_on   timestamp default CURRENT_TIMESTAMP not null,
    execution_time int                                 not null,
    success        tinyint(1)                          not null
);

create index flyway_schema_history_s_idx
    on flyway_schema_history (success);

create table users
(
    id            binary(16)                          not null
        primary key,
    fecha_nac     datetime(6)                         null,
    first_name    varchar(255)                        null,
    last_name     varchar(255)                        null,
    mail          varchar(255)                        null,
    password      varchar(255)                        null,
    role          enum ('Admin', 'LinkHogar', 'User') null,
    register_date datetime(6)                         null,
    constraint UKjhck7kjdogc7yia7qamc89ypv
        unique (mail)
);

create table houses
(
    id                 binary(16)   not null
        primary key,
    air_conditioned    bit          not null,
    balcony            bit          not null,
    baths              int          not null,
    common_areas       bit          not null,
    creation_date      datetime(6)  null,
    description        varchar(255) null,
    furnished          bit          not null,
    garage             bit          not null,
    house_type         tinyint      null,
    lift               bit          not null,
    pets_allowed       bit          not null,
    pool               bit          not null,
    price              bigint       not null,
    publication_date   datetime(6)  null,
    publication_status tinyint      null,
    rooms              int          not null,
    size               int          not null,
    status             tinyint      null,
    storage            bit          not null,
    terrace            bit          not null,
    title              varchar(255) null,
    update_date        datetime(6)  null,
    address_id         binary(16)   null,
    owner_id           binary(16)   null,
    constraint UKfx43gwqyfq0gtqt6238ooym9m
        unique (address_id),
    constraint FKn9yr5cdkajfhcbtrwnvq8seyl
        foreign key (owner_id) references users (id),
    constraint FKsk9uig7cu4xs9xql0ps5d0dcm
        foreign key (address_id) references addresses (id),
    check (`house_type` between 0 and 8),
    check (`publication_status` between 0 and 5),
    check (`status` between 0 and 2)
);



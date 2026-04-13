CREATE TABLE house_favourite
(
    id       BINARY(16) NOT NULL,
    user_id  BINARY(16) NOT NULL,
    house_id BINARY(16) NOT NULL,
    added_at datetime   NULL,
    CONSTRAINT pk_housefavourite PRIMARY KEY (id)
);

ALTER TABLE house_favourite
    ADD CONSTRAINT FK_HOUSEFAVOURITE_ON_HOUSE FOREIGN KEY (house_id) REFERENCES houses (id);

ALTER TABLE house_favourite
    ADD CONSTRAINT FK_HOUSEFAVOURITE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
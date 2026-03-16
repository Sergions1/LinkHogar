CREATE TABLE IF NOT EXISTS house_images
(
    house_id  BINARY(16)   NOT NULL,
    image_url VARCHAR(255) NULL
);

ALTER TABLE house_images
    ADD CONSTRAINT fk_house_images_on_house FOREIGN KEY (house_id) REFERENCES houses (id);

ALTER TABLE houses
    DROP COLUMN images;

ALTER TABLE users
    MODIFY `role` VARCHAR(255);
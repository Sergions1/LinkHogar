CREATE TABLE house_images (
      house_id BINARY(16) NOT NULL,
      image_url VARCHAR(255),
      CONSTRAINT fk_house_images_house_id FOREIGN KEY (house_id) REFERENCES houses (id)
);

ALTER TABLE users
    MODIFY `role` VARCHAR(255);
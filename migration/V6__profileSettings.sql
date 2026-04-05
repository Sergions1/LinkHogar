ALTER TABLE users
    ADD avatar_url VARCHAR(255) NULL;

ALTER TABLE users
    ADD verification_code VARCHAR(255) NULL;

ALTER TABLE users
    ADD verification_code_expiration datetime NULL;
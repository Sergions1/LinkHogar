CREATE TABLE verification_token
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255)          NOT NULL,
    user_id     BINARY(16)            NOT NULL,
    expiry_date datetime              NOT NULL,
    CONSTRAINT pk_verificationtoken PRIMARY KEY (id)
);

ALTER TABLE users MODIFY fecha_nac DATE;
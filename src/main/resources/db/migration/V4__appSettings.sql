CREATE TABLE app_settings
(
    name          VARCHAR(255) NOT NULL,
    value         VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NULL,
    CONSTRAINT pk_appsettings PRIMARY KEY (name)
);
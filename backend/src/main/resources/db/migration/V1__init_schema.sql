CREATE TABLE roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE customers (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(40),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE genres (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE movies (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                VARCHAR(255) NOT NULL,
    description          TEXT,
    release_year         INT,
    daily_rate           DECIMAL(10, 2) NOT NULL,
    rental_days_default  INT NOT NULL DEFAULT 3,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_movies_title (title)
);

CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_movie_genres_movie FOREIGN KEY (movie_id) REFERENCES movies (id),
    CONSTRAINT fk_movie_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (id)
);

CREATE TABLE dvd_copies (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id   BIGINT NOT NULL,
    barcode    VARCHAR(64) NOT NULL UNIQUE,
    status     VARCHAR(40) NOT NULL,
    notes      VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dvd_copies_movie FOREIGN KEY (movie_id) REFERENCES movies (id),
    INDEX idx_dvd_copies_movie_status (movie_id, status),
    INDEX idx_dvd_copies_status (status)
);

CREATE TABLE rental_orders (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id  BIGINT NOT NULL,
    status       VARCHAR(40) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rental_orders_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    INDEX idx_rental_orders_customer (customer_id),
    INDEX idx_rental_orders_status (status)
);

CREATE TABLE rental_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_order_id BIGINT NOT NULL,
    dvd_copy_id     BIGINT NOT NULL,
    due_date        DATE NOT NULL,
    returned_at     TIMESTAMP NULL,
    line_amount     DECIMAL(10, 2) NOT NULL,
    overdue_amount  DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(40) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rental_items_order FOREIGN KEY (rental_order_id) REFERENCES rental_orders (id),
    CONSTRAINT fk_rental_items_copy FOREIGN KEY (dvd_copy_id) REFERENCES dvd_copies (id),
    INDEX idx_rental_items_order (rental_order_id),
    INDEX idx_rental_items_copy (dvd_copy_id),
    INDEX idx_rental_items_due (due_date),
    INDEX idx_rental_items_status (status)
);

CREATE TABLE payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_order_id BIGINT NOT NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    method          VARCHAR(40) NOT NULL DEFAULT 'CASH',
    recorded_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes           VARCHAR(500),
    CONSTRAINT fk_payments_order FOREIGN KEY (rental_order_id) REFERENCES rental_orders (id)
);

CREATE TABLE audit_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_email VARCHAR(255),
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id   BIGINT,
    details     VARCHAR(1000),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name) VALUES ('CUSTOMER'), ('STAFF'), ('ADMIN');

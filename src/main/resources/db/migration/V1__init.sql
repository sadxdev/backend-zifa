CREATE TABLE users (
                       id BINARY(16) PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(320) NOT NULL UNIQUE,
                       keycloak_id BINARY(16),
                       password_hash TEXT NOT NULL,
                       salt TEXT,
                       status SMALLINT NOT NULL DEFAULT 0,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

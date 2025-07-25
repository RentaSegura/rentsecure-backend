/* V2 – Email verification support (2025-07-24) */

-- 1. Flag de verificación en users
ALTER TABLE users
ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;

-- 2. Tabla de tokens de verificación
CREATE TABLE verification_tokens (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ NOT NULL
);

-- Índice rápido por token
CREATE INDEX idx_verif_token_token ON verification_tokens(token);
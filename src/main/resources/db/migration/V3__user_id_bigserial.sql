/* V3 – Cambiar users.user_id a BIGSERIAL (2025-07-24) */

-- 1) Soltar FKs que apuntan a users.user_id
ALTER TABLE user_profiles DROP CONSTRAINT user_profiles_user_id_fkey;
ALTER TABLE verification_tokens DROP CONSTRAINT verification_tokens_user_id_fkey;
ALTER TABLE audit_logs DROP CONSTRAINT audit_logs_user_id_fkey;

-- 2) Cambiar columna y secuencia
ALTER TABLE users ALTER COLUMN user_id TYPE BIGINT;
-- serial usa una secuencia; BIGSERIAL ≈ BIGINT + DEFAULT nextval(...).
-- Aseguramos que la secuencia continúe:
ALTER SEQUENCE users_user_id_seq AS BIGINT;

-- 3) Actualizar columnas dependientes
ALTER TABLE user_profiles ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE verification_tokens ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE audit_logs ALTER COLUMN user_id TYPE BIGINT;

-- 4) Restaurar FKs
ALTER TABLE user_profiles
  ADD CONSTRAINT user_profiles_user_id_fkey
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE verification_tokens
  ADD CONSTRAINT verification_tokens_user_id_fkey
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE audit_logs
  ADD CONSTRAINT audit_logs_user_id_fkey
  FOREIGN KEY (user_id) REFERENCES users(user_id);
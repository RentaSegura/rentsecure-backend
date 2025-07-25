/* V4 – Agrandar PK de verification_tokens a BIGSERIAL (2025-07-24) */

-- Cambiar tipo de la secuencia y la columna primaria
ALTER TABLE verification_tokens
    ALTER COLUMN id TYPE BIGINT;

-- Asegurar que la secuencia siga funcionando con BIGINT
ALTER SEQUENCE verification_tokens_id_seq AS BIGINT;

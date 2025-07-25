/* V5 – Migrar todas las PK/FK restantes a BIGINT (2025-07-25)
   -----------------------------------------------------------
   1. Eliminar FKs que bloquean el cambio de tipo
   2. Alterar columnas  (PK y FK)  a BIGINT
   3. Ajustar las secuencias       a BIGINT
   4. Recrear las FKs
*/

/* ---------- Paso 1 : soltar FKs ---------- */
ALTER TABLE properties   DROP CONSTRAINT IF EXISTS properties_owner_profile_id_fkey;
ALTER TABLE contracts    DROP CONSTRAINT IF EXISTS contracts_property_id_fkey;
ALTER TABLE contracts    DROP CONSTRAINT IF EXISTS contracts_tenant_profile_id_fkey;
ALTER TABLE payments     DROP CONSTRAINT IF EXISTS payments_contract_id_fkey;

/* ---------- Paso 2 : cambiar columnas ---------- */
-- user_profiles
ALTER TABLE user_profiles
  ALTER COLUMN profile_id TYPE BIGINT;

-- properties
ALTER TABLE properties
  ALTER COLUMN property_id       TYPE BIGINT,
  ALTER COLUMN owner_profile_id  TYPE BIGINT;

-- contracts
ALTER TABLE contracts
  ALTER COLUMN contract_id       TYPE BIGINT,
  ALTER COLUMN property_id       TYPE BIGINT,
  ALTER COLUMN tenant_profile_id TYPE BIGINT;

-- payments
ALTER TABLE payments
  ALTER COLUMN payment_id  TYPE BIGINT,
  ALTER COLUMN contract_id TYPE BIGINT;

-- audit_logs
ALTER TABLE audit_logs
  ALTER COLUMN log_id     TYPE BIGINT,
  ALTER COLUMN entity_id  TYPE BIGINT;

/* ---------- Paso 3 : secuencias ---------- */
-- (Solo si existen.  Cambia el nombre si tu secuencia es distinta)
ALTER SEQUENCE IF EXISTS user_profiles_profile_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS properties_property_id_seq  AS BIGINT;
ALTER SEQUENCE IF EXISTS contracts_contract_id_seq   AS BIGINT;
ALTER SEQUENCE IF EXISTS payments_payment_id_seq     AS BIGINT;
ALTER SEQUENCE IF EXISTS audit_logs_log_id_seq       AS BIGINT;

/* ---------- Paso 4 : recrear FKs ---------- */
ALTER TABLE properties
  ADD CONSTRAINT properties_owner_profile_id_fkey
  FOREIGN KEY (owner_profile_id)
  REFERENCES user_profiles(profile_id) ON DELETE CASCADE;

ALTER TABLE contracts
  ADD CONSTRAINT contracts_property_id_fkey
  FOREIGN KEY (property_id)
  REFERENCES properties(property_id) ON DELETE CASCADE,
  ADD CONSTRAINT contracts_tenant_profile_id_fkey
  FOREIGN KEY (tenant_profile_id)
  REFERENCES user_profiles(profile_id) ON DELETE CASCADE;

ALTER TABLE payments
  ADD CONSTRAINT payments_contract_id_fkey
  FOREIGN KEY (contract_id)
  REFERENCES contracts(contract_id) ON DELETE CASCADE;

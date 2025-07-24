-- ERD MEJORADO - RentSecure (v1.3)
-- Fecha de generación: 2025-07-22
-- Compatible con Flyway (puede renombrarse a V1__init_schema.sql si aún no hay migraciones).

/*==============================================================*/
/* TABLA: users                                                 */
/*==============================================================*/
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

/*==============================================================*/
/* TABLA: user_profiles                                         */
/*==============================================================*/
CREATE TABLE user_profiles (
    profile_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

/*==============================================================*/
/* TABLA: properties                                            */
/*==============================================================*/
CREATE TABLE properties (
    property_id SERIAL PRIMARY KEY,
    owner_profile_id INT NOT NULL REFERENCES user_profiles(profile_id) ON DELETE CASCADE,
    address TEXT NOT NULL,
    type VARCHAR(100),
    monthly_price DECIMAL(10, 2) NOT NULL,
    main_image_url TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'ARCHIVED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(owner_profile_id, address)
);

-- Index para consultar rápido las propiedades de un propietario
CREATE INDEX idx_properties_owner_profile_id ON properties(owner_profile_id);

/*==============================================================*/
/* TABLA: contracts                                             */
/*==============================================================*/
CREATE TABLE contracts (
    contract_id SERIAL PRIMARY KEY,
    property_id INT NOT NULL REFERENCES properties(property_id) ON DELETE CASCADE,
    tenant_profile_id INT NOT NULL REFERENCES user_profiles(profile_id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    monthly_amount DECIMAL(10, 2) NOT NULL,
    pdf_url TEXT,
    pdf_sha256_hash TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_SIGNATURE' CHECK (status IN ('PENDING_SIGNATURE', 'ACTIVE', 'FINISHED', 'CANCELLED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CHECK (start_date < end_date)
);

-- Garantizar que solo exista un contrato ACTIVE por propiedad
CREATE UNIQUE INDEX uq_contract_active_per_property ON contracts(property_id)
WHERE status = 'ACTIVE';

-- Índices para acelerar consultas comunes sobre contratos
CREATE INDEX idx_contracts_property_id ON contracts(property_id);
CREATE INDEX idx_contracts_tenant_profile_id ON contracts(tenant_profile_id);

/*==============================================================*/
/* TABLA: payments                                              */
/*==============================================================*/
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    contract_id INT NOT NULL REFERENCES contracts(contract_id) ON DELETE CASCADE,
    due_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'FAILED')),
    paid_at TIMESTAMP WITH TIME ZONE,
    failure_reason TEXT,
    notified_at TIMESTAMP WITH TIME ZONE
);

-- Evitar pagos duplicados para la misma cuota
CREATE UNIQUE INDEX uq_payment_unique_due ON payments(contract_id, due_date);

/*==============================================================*/
/* TABLA: audit_logs                                            */
/*==============================================================*/
CREATE TABLE audit_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id INT,
    ip_address VARCHAR(45),
    details JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

/*==============================================================*/
/* FIN DEL ERD v1.3                                             */
/*==============================================================*/
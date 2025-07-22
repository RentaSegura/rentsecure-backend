
# RentSecure MVP – User Stories (v1.1)

> Versión 1.1 · Fecha: 22 jul 2025

---
## Convenciones
- **ID**: prefijo `US-` seguido de número.
- **Formato**: _Como [rol] quiero [meta] para [beneficio]_.
- **Criterios de aceptación** (CA) en formato Given/When/Then.
- **Prioridad MoSCoW**.

---
## Epic 1 · Gestión de Usuarios y Propiedades

### US-1 · Registro de propietario
**Como** propietario potencial  
**Quiero** crear una cuenta única en RentSecure con email y contraseña  
**Para** poder administrar mis inmuebles en la plataforma.  
**Criterios de aceptación**  
1. **Given** un visitante sin sesión, **When** completa el formulario de registro y lo envía, **Then** recibe un email para verificar su cuenta.  
2. El usuario debe aceptar los Términos y Condiciones (checkbox obligatorio).  
3. El email debe ser único en la tabla de `users`.  
**Prioridad**: Must-Have

### US-2 · Alta de inmueble
**Como** propietario autenticado  
**Quiero** registrar uno o varios inmuebles (dirección, tipo, precio mensual, fotos)  
**Para** publicarlos y asociarlos a contratos individuales.  
**Criterios de aceptación**  
1. Un propietario (`landlord_profile`) puede tener múltiples `property` asociadas.  
2. La combinación de `owner_id` y `dirección` debe ser única para evitar duplicados.  
3. Se requiere al menos 1 foto por inmueble (subida a Cloudinary).  
4. El inmueble queda en estado `Draft` hasta que un contrato asociado esté `Active`.  
**Prioridad**: Must-Have

### US-3 · Invitar inquilino a una propiedad
**Como** propietario  
**Quiero** invitar a un inquilino vía email a una propiedad específica  
**Para** iniciar el flujo de firma del contrato de alquiler correspondiente.  
**Criterios de aceptación**  
1. **Given** un inmueble en estado `Draft`, **When** el propietario introduce el email del inquilino, **Then** se envía un email con un token de registro único.  
2. El token de invitación expira en 72 horas y asocia al inquilino con ese `property_id` específico.  
**Prioridad**: Must-Have

### US-4 · Registro de inquilino invitado
**Como** inquilino potencial invitado  
**Quiero** registrarme usando el enlace seguro del email  
**Para** poder aceptar los términos, firmar el contrato y gestionar mis pagos.  
**Criterios de aceptación**  
1. **Given** recibo un email de invitación, **When** hago clic en el enlace y completo mi registro, **Then** mi cuenta de usuario (`tenant_profile`) se crea y se asocia al contrato pendiente de esa propiedad.  
2. Si el email del inquilino ya existe en el sistema, el enlace lo invitará a iniciar sesión y aceptar la nueva asociación de contrato.  
**Prioridad**: Must-Have

---
## Epic 2 · Contratos Digitales

### US-5 · Generar borrador de contrato en PDF
**Como** propietario  
**Quiero** generar un contrato pre-llenado en PDF basado en una plantilla  
**Para** revisarlo antes de enviarlo para su formalización.  
**Criterios de aceptación**  
1. El sistema usa Thymeleaf + OpenHTMLToPDF para producir un PDF a partir de los datos del propietario, inquilino y propiedad.  
2. El PDF se almacena y versiona (ej. `contract_v1.pdf`).  
3. El contrato generado queda en estado `Pending Signature` hasta que se formalice.  
**Prioridad**: Must-Have

### US-6 · Formalización de contrato (MVP)
**Como** propietario  
**Quiero** subir el PDF firmado por ambas partes y marcar el contrato como activo  
**Para** formalizar el acuerdo y habilitar los cobros automáticos.  
**Criterios de aceptación**  
1. **Given** un contrato en `Pending Signature`, **When** el propietario sube un archivo PDF, **Then** el sistema reemplaza el borrador con la versión firmada.  
2. El propietario debe marcar una casilla de verificación: "Confirmo que este documento ha sido firmado por ambas partes y es legalmente vinculante".  
3. Al marcar la casilla y guardar, el estado del contrato cambia a `Active` y se registra un hash SHA-256 del PDF final para auditoría.  
4. Este proceso es manual en MVP; será reemplazado por firma digital con Culqi post-MVP.  
**Prioridad**: Must-Have

---
## Epic 3 · Cobro Automático

### US-7 · Suscripción de pago mensual por contrato
**Como** inquilino  
**Quiero** registrar mi tarjeta y autorizar un cargo recurrente para un contrato específico  
**Para** que el alquiler de esa propiedad se debite automáticamente cada mes.  
**Criterios de aceptación**  
1. Un inquilino puede tener múltiples suscripciones, una por cada contrato `Active`.  
2. Se crea un plan y una suscripción en Culqi asociados al `contract_id`.  
3. El monto del cobro es el definido en el contrato asociado.  
4. El sistema debe poder manejar webhooks de `payment_success` y `payment_failed` de Culqi y asociarlos al pago y contrato correctos.  
**Prioridad**: Must-Have

### US-8 · Notificación de pago fallido
**Como** propietario  
**Quiero** recibir una alerta si un cobro mensual falla  
**Para** contactar al inquilino y gestionar la situación.  
**Criterios de aceptación**  
1. Se envía un email al propietario y se muestra una notificación destacada en su dashboard.  
2. La alerta debe especificar qué propiedad/contrato tuvo el problema y el motivo devuelto por Culqi.  
3. Se genera un registro de `LatePayment` asociado al `payment` fallido.  
**Prioridad**: Must-Have

---
## Epic 4 · Dashboard & Reportes

### US-9 · Dashboard de rentas para el propietario
**Como** propietario  
**Quiero** ver en un panel todas mis propiedades y el estado de los pagos de cada una  
**Para** tener una visión clara y rápida de mi portafolio de rentas.  
**Criterios de aceptación**  
1. El dashboard muestra una tabla o lista de todas las propiedades del propietario.  
2. Para cada propiedad, se muestra el estado del contrato (`Active`, `Pending`, etc.) y el estado del último pago (`OK`, `Late`, `Pending`).  
3. **Given** un propietario nuevo sin propiedades, **When** visita el dashboard, **Then** ve un mensaje de bienvenida y un llamado a la acción para "Registrar tu primer inmueble".  
4. **Given** una petición al endpoint del dashboard, **Then** la respuesta debe tener un p95 < 500 ms en Railway.  
**Prioridad**: Must-Have

### US-10 · Acceso a contratos para el inquilino
**Como** inquilino  
**Quiero** poder ver y descargar todos mis contratos de alquiler firmados  
**Para** tener una copia local de mis acuerdos.  
**Criterios de aceptación**  
1. En el perfil del inquilino, hay una sección "Mis Contratos" que lista todos los contratos `Active` o `Finished`.  
2. Cada ítem de la lista permite descargar el PDF firmado correspondiente.  
3. El endpoint de descarga debe estar autenticado y solo permitir el acceso a las partes del contrato.  
**Prioridad**: Should-Have

---
## Epic 5 · Compliance & Auditoría

### US-11 · Registro de eventos críticos
**Como** administrador del sistema  
**Quiero** un log de auditoría inmutable en formato JSON para eventos clave  
**Para** cumplir con requisitos legales y poder depurar problemas de forma efectiva.  
**Criterios de aceptación**  
1. Se registran eventos como: `user_registered`, `contract_created`, `contract_activated`, `payment_processed`, `payment_failed`.  
2. Los logs se envían a Loki vía Logback JSON, incluyendo `timestamp`, `user_id`, `ip_address` y la entidad afectada (ej. `contract_id`).  
**Prioridad**: Must-Have

---

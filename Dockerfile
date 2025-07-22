# --- Etapa 1: Definir la imagen base ---
# Usamos una imagen oficial de Java 21 (JRE), que es ligera y optimizada
# solo para ejecutar aplicaciones Java, no para compilarlas.
FROM eclipse-temurin:21-jre

# --- Etapa 2: Copiar el artefacto de la aplicación ---
# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia el archivo .jar compilado de tu proyecto (desde la carpeta 'target')
# al directorio raíz del contenedor y lo renombra a 'app.jar'.
# ¡IMPORTANTE! Cambia 'rentsecure-backend-0.0.1-SNAPSHOT.jar' por el nombre exacto de tu archivo JAR.
COPY target/rentsecure-backend-0.0.1-SNAPSHOT.jar app.jar

# --- Etapa 3: Exponer el puerto ---
# Informa a Docker que el contenedor escuchará en el puerto 8080 en tiempo de ejecución.
# Railway usará esta información para exponer tu servicio.
EXPOSE 8080

# --- Etapa 4: Definir el comando de inicio ---
# Este es el comando que se ejecutará cuando el contenedor se inicie.
# Le dice a Java que ejecute el archivo .jar.
ENTRYPOINT ["java", "-jar", "app.jar"]
# =========================
# Backend build (Maven)
# =========================
FROM maven:3.9-eclipse-temurin-17 AS backend-builder
WORKDIR /app/backend
COPY ProyectoFinal/pom.xml .
RUN mvn -q -e -DskipTests=true verify --fail-never
COPY ProyectoFinal/src src
RUN mvn -q -DskipTests=true package

# =========================
# Frontend build (Angular)
# =========================
FROM node:20 AS frontend-builder
WORKDIR /app/frontend
COPY front-angular/package.json front-angular/package-lock.json ./
RUN npm ci
COPY front-angular/ .
RUN npm run build --configuration front-angular

# =========================
# Runtime (Tomcat)
# =========================
FROM tomcat:11.0-jdk17-temurin

# --- Desplegar backend como WAR en /ProyectoFinal ---
COPY --from=backend-builder /app/backend/target/ProyectoFinal-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ProyectoFinal.war

# --- Desplegar frontend en ROOT (Angular SPA) ---
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=frontend-builder /app/frontend/dist/front-angular/browser /usr/local/tomcat/webapps/ROOT/

# --- web.xml para manejar rutas SPA (opcional si ya lo tenías) ---
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF
COPY frontend-web.xml /usr/local/tomcat/webapps/ROOT/WEB-INF/web.xml

# === Almacenamiento persistente de reportes ===
ENV REPORTS_DIR=/data/reportes
RUN mkdir -p ${REPORTS_DIR}/public_docs ${REPORTS_DIR}/private_docs
VOLUME ["/data/reportes"]

# (Opcional)  exponemos como propiedad JVM  -Dreports.dir
ENV CATALINA_OPTS="$CATALINA_OPTS -Dreports.dir=/data/reportes"

EXPOSE 8080

FROM maven as backend-builder
WORKDIR /app/backend
COPY ProyectoFinal/pom.xml .
RUN mvn verify --fail-never
COPY ProyectoFinal/src src
RUN mvn package

FROM node:20 as frontend-builder
WORKDIR /app/frontend
COPY front-angular/package.json front-angular/package-lock.json ./
RUN npm ci
COPY front-angular/ .
RUN npm run build --configuration front-angular

FROM tomcat:latest

# Definir HOME fijo
ENV HOME=/home/tomcat

# Crear carpetas de reportes
RUN mkdir -p $HOME/ProyectoFinal/docs/public_docs \
    && mkdir -p $HOME/ProyectoFinal/docs/private_docs

# Deploy backend
COPY --from=backend-builder /app/backend/target/ProyectoFinal-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ProyectoFinal.war

# Deploy frontend
COPY --from=frontend-builder /app/frontend/dist/front-angular/browser /usr/local/tomcat/webapps/ROOT/
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF
COPY frontend-web.xml /usr/local/tomcat/webapps/ROOT/WEB-INF/web.xml

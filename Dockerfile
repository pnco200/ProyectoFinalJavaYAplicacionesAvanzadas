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
# Deploy backend at /ProyectoFinal context (with its own web.xml)
COPY --from=backend-builder /app/backend/target/ProyectoFinal-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ProyectoFinal.war

# Deploy frontend at ROOT context
COPY --from=frontend-builder /app/frontend/dist/front-angular/browser /usr/local/tomcat/webapps/ROOT/

# Add frontend-specific web.xml to handle Angular routing
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF
COPY frontend-web.xml /usr/local/tomcat/webapps/ROOT/WEB-INF/web.xml
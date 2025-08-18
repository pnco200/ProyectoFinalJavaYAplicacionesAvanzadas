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
COPY --from=backend-builder /app/backend/target/ProyectoFinal-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=frontend-builder /app/frontend/dist/front-angular/browser /usr/local/tomcat/webapps/ROOT/

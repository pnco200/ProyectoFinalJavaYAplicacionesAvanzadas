FROM maven as grupo10build

WORKDIR /home/ejemplo
COPY ProyectoFinal/pom.xml pom.xml
RUN mvn verify --fail-never
COPY ProyectoFinal/src src
RUN mvn package

FROM tomcat
COPY --from=grupo10build /home/ejemplo/target/ProyectoFinal-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

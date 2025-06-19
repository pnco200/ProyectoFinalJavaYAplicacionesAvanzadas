package asis.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;

@OpenAPIDefinition(
    info = @Info(
        title = "API ASIS",
        version = "1.0.0",
        description = "API para testear y documentar las funcionalidades provistas para el sistema ASIS "
    ),
    servers = {
        @Server(
            description = "Servidor local de desarrollo",
            url = "http://localhost:8080/ProyectoFinal/api"
        )
    }
)
public class OpenApiConfig {

}

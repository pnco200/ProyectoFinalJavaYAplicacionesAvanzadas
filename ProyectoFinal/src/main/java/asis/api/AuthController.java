package asis.api;

import asis.dao.IUsuarioDAO;
import asis.dto.LoginRequestDTO;
import asis.dto.LoginResponseDTO;
import asis.models.Usuario;
import asis.models.Rol;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Operaciones de autenticación")
public class AuthController {

    @Inject
    private IUsuarioDAO usuarioDAO;

    @POST
    @Path("/login")
    @Operation(summary = "Autenticar usuario",
        requestBody = @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
        }
    )
    public Response login(LoginRequestDTO request) {
        Usuario usuario = usuarioDAO.findByEmail(request.mail);

        if (usuario == null || !usuario.getPassword().equals(request.password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciales inválidas").build();
        }

        LoginResponseDTO response = new LoginResponseDTO();
        response.id = usuario.getId();
        response.mail = usuario.getMail();
        response.name = usuario.getName();
        response.is_active = usuario.isIs_active();
        response.roles = usuario.getRoles().stream()
                                .map(Rol::getNombre)
                                .map(Enum::name)
                                .collect(Collectors.toSet());

        return Response.ok(response).build();
    }

    @OPTIONS
    @Path("/login")
    @Operation(summary = "Handle preflight requests for login")
    public Response preflightLogin() {
        return Response.ok().build();
    }
}

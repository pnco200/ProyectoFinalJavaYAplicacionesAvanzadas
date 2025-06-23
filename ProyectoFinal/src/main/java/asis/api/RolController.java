package asis.api;

import asis.dao.IRolDAO;
import asis.dao.IUsuarioDAO;
import asis.dto.RolDTO;
import asis.dto.RolPostDTO;
import asis.models.Rol;
import asis.models.Rol.TipoRol;
import asis.models.Usuario;

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

import java.util.List;
import java.util.stream.Collectors;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "RolController", description = "Operaciones sobre Roles")
public class RolController {

    @Inject
    private IRolDAO rolDAO;

    @Inject
    private IUsuarioDAO usuarioDAO;

    private RolDTO toDTO(Rol rol) {
        if (rol == null) return null;
        return new RolDTO(rol.getId(), rol.getNombre().name());
    }

    @GET
    @Operation(summary = "Listar todos los roles")
    public Response findAll() {
        List<Rol> roles = rolDAO.findAll();
        List<RolDTO> dtos = roles.stream().map(this::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar rol por ID")
    public Response findById(@PathParam("id") Long id) {
        Rol rol = rolDAO.findById(id);
        if (rol == null)
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Rol no encontrado\"}").build();
        return Response.ok(toDTO(rol)).build();
    }

    @POST
    public Response create(RolPostDTO rolInput) {
        if (rolInput.getNombre() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El tipo de rol es requerido\"}")
                    .build();
        }

        TipoRol tipoRol;
        try {
            tipoRol = TipoRol.valueOf(rolInput.getNombre());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Tipo de rol inválido\"}")
                    .build();
        }

        Rol rolNuevo = new Rol();
        rolNuevo.setNombre(tipoRol);

        Rol creado = rolDAO.save(rolNuevo);
        if (creado == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Ya existe un rol de ese tipo\"}")
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(toDTO(creado)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar un rol existente")
    public Response update(@PathParam("id") Long id,
                           @RequestBody(description = "Datos para actualizar el rol", required = true,
                                        content = @Content(schema = @Schema(implementation = RolPostDTO.class)))
                           RolDTO rolInput) {

        Rol existente = rolDAO.findById(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Rol no encontrado\"}")
                           .build();
        }

        if (rolInput.getNombre() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"El tipo de rol es requerido\"}")
                           .build();
        }

        TipoRol tipoRol;
        try {
            tipoRol = TipoRol.valueOf(rolInput.getNombre());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Tipo de rol inválido\"}")
                           .build();
        }

        List<Rol> duplicados = rolDAO.findByTipoRol(tipoRol);
        if (duplicados.stream().anyMatch(r -> !r.getId().equals(id))) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("{\"error\":\"Ya existe un rol de ese tipo\"}")
                           .build();
        }

        existente.setNombre(tipoRol);
        Rol actualizado = rolDAO.update(existente);
        return Response.ok(toDTO(actualizado)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un rol por ID")
    public Response delete(@PathParam("id") Long id) {
        Rol existente = rolDAO.findById(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Rol no encontrado\"}").build();
        }
        rolDAO.delete(existente);
        return Response.ok("{\"status\":\"Rol eliminado\"}").build();
    }


    //Traje la funcionalidad de asignar y remover usuarios de un rol a este controller
    @POST
    @Path("/{id}/usuarios/{usuarioId}")
    @Operation(summary = "Asignar un rol a un usuario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuario asignado"),
            @ApiResponse(responseCode = "404", description = "Rol o usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuario ya tiene el rol")
        }
    )
    public Response addUsuario(@PathParam("id") Long rolId, @PathParam("usuarioId") Long usuarioId) {
        Rol rol = rolDAO.findById(rolId);
        Usuario usuario = usuarioDAO.findById(usuarioId);

        if (rol == null || usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Rol o usuario no encontrado\"}").build();
        }

        try {
            rolDAO.addUsuario(rolId, usuario);
            return Response.ok("{\"status\":\"Usuario asignado al rol\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/{id}/usuarios/{usuarioId}")
    @Operation(summary = "Remover rol de un usuario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuario removido"),
            @ApiResponse(responseCode = "404", description = "Rol o usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuario no tenía el rol")
        }
    )
    public Response removeUsuario(@PathParam("id") Long rolId, @PathParam("usuarioId") Long usuarioId) {
        Rol rol = rolDAO.findById(rolId);
        Usuario usuario = usuarioDAO.findById(usuarioId);

        if (rol == null || usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Rol o usuario no encontrado\"}").build();
        }

        try {
            rolDAO.removeUsuario(rolId, usuario);
            return Response.ok("{\"status\":\"Usuario removido del rol\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}

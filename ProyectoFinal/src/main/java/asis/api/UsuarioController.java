package asis.api;

import asis.dao.IUsuarioDAO;
import asis.dto.UsuarioDTO;
import asis.dto.UsuarioInputDTO;
import asis.dto.UsuarioMapper;
import asis.models.Usuario;
import asis.models.Barrio;
import asis.models.Rol.TipoRol;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuario", description = "Operaciones sobre usuarios")
public class UsuarioController {

        @Inject
    private IUsuarioDAO usuarioDAO;

    @Inject
    private asis.dao.IRolDAO rolDAO;

    @GET
    @Operation(summary = "Obtener todos los usuarios",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioDTO.class))))
        }
    )
    public Response getAll() {
        List<Usuario> usuarios = usuarioDAO.findAll();
        List<UsuarioDTO> result = usuarios.stream()
            .map(UsuarioMapper::toDTO)
            .collect(Collectors.toList());
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener un usuario por ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        }
    )
    public Response getById(@PathParam("id") Long id) {
        Usuario usuario = usuarioDAO.findById(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(UsuarioMapper.toDTO(usuario)).build();
    }

    @POST
    @Operation(summary = "Crear un nuevo usuario",
        requestBody = @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = UsuarioInputDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuario creado",
                content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un usuario con ese mail")
        }
    )
    public Response create(UsuarioInputDTO dto) {
        Usuario nuevo = UsuarioMapper.toEntity(dto);
        Usuario creado = usuarioDAO.save(nuevo);
        if (creado == null) {
            return Response.status(Response.Status.CONFLICT).entity("Ya existe un usuario con ese mail").build();
        }

        // Assign role
        if (dto.rolId != null) {
            boolean rolAssigned = usuarioDAO.assignRolToUser(creado.getId(), dto.rolId);
            if (!rolAssigned) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al asignar el rol").build();
            }
            // Re-fetch the user to ensure roles are updated in the current session
            creado = usuarioDAO.findById(creado.getId());
            if (creado == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Usuario no encontrado después de asignar rol").build();
            }
        }

        // Assign barrio if applicable
        if (dto.barrioId != null) {
            // Assuming you have a way to check if the assigned role is ORGANIZACION_SOCIAL
            // This check might need to be more robust, e.g., by fetching the role name
            // For now, we'll rely on the assignBarrioToUsuario method to handle the role check
            boolean barrioAssigned = usuarioDAO.assignBarrioToUsuario(creado.getId(), dto.barrioId);
            if (!barrioAssigned) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al asignar el barrio o el usuario no tiene el rol requerido").build();
            }
        }

        return Response.status(Response.Status.CREATED).entity(UsuarioMapper.toDTO(creado)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar un usuario",
        requestBody = @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = UsuarioInputDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado",
                content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Mail en uso por otro usuario")
        }
    )
    public Response update(@PathParam("id") Long id, UsuarioInputDTO dto) {
        Usuario existente = usuarioDAO.findById(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        UsuarioMapper.updateEntity(existente, dto);

        try {
            Usuario actualizado = usuarioDAO.update(existente);
            return Response.ok(UsuarioMapper.toDTO(actualizado)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Ya existe un usuario con ese mail").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un usuario por ID",
        responses = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        }
    )
    public Response delete(@PathParam("id") Long id) {
        Usuario usuario = usuarioDAO.findById(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        usuarioDAO.delete(usuario);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{userId}/barrio/{barrioId}")
    @Operation(summary = "Asignar un barrio a un usuario con rol ORGANIZACION_SOCIAL",
        responses = {
            @ApiResponse(responseCode = "200", description = "Barrio asignado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o barrio no encontrado"),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene el rol requerido")
        }
    )
    public Response assignBarrio(@PathParam("userId") Long userId, @PathParam("barrioId") Long barrioId) {
        boolean result = usuarioDAO.assignBarrioToUsuario(userId, barrioId);

        if (!result) {
            Usuario usuario = usuarioDAO.findById(userId);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            } else if (usuario.getRoles().stream().noneMatch(r -> r.getNombre() == TipoRol.ORGANIZACION_SOCIAL)) {
                return Response.status(Response.Status.FORBIDDEN).entity("El usuario no tiene el rol ORGANIZACION_SOCIAL").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Barrio no encontrado").build();
            }
        }

        return Response.ok("Barrio asignado correctamente").build();
    }
} 

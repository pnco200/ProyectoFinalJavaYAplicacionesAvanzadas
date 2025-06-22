package asis.api;

import asis.dao.IRolDAO;
import asis.models.Rol;
import asis.models.Rol.TipoRol;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "RolController", description = "Operaciones sobre Roles")
public class RolController {

    @Inject
    private IRolDAO rolDAO;

    @GET
    @Operation(summary = "Listar todos los roles")
    public Response findAll() {
        List<Rol> roles = rolDAO.findAll();
        return Response.ok(roles).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar rol por ID")
    public Response findById(@PathParam("id") Long id) {
        Rol rol = rolDAO.findById(id);
        if (rol == null) return Response.status(Response.Status.NOT_FOUND).entity("Rol no encontrado").build();
        return Response.ok(rol).build();
    }

    @GET
    @Path("/tipo/{tipo}")
    @Operation(summary = "Buscar roles por tipo")
    public Response findByTipo(@PathParam("tipo") String tipo) {
        try {
            TipoRol tipoRol = TipoRol.valueOf(tipo);
            List<Rol> roles = rolDAO.findByTipoRol(tipoRol);
            return Response.ok(roles).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Tipo de rol inválido").build();
        }
    }

    @POST
    @Operation(summary = "Crear un nuevo rol")
    public Response create(Rol rol) {
        if (rol.getNombre() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("El tipo de rol es requerido").build();
        }
        Rol creado = rolDAO.save(rol);
        if (creado == null) {
            return Response.status(Response.Status.CONFLICT).entity("Ya existe un rol de ese tipo").build();
        }
        return Response.status(Response.Status.CREATED).entity(creado).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar un rol existente")
    public Response update(@PathParam("id") Long id, Rol rolInput) {
        Rol existente = rolDAO.findById(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Rol no encontrado").build();
        }

        if (rolInput.getNombre() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("El tipo de rol es requerido").build();
        }

        // Evitar duplicado
        List<Rol> duplicados = rolDAO.findByTipoRol(rolInput.getNombre());
        if (duplicados.stream().anyMatch(r -> !r.getId().equals(id))) {
            return Response.status(Response.Status.CONFLICT).entity("Ya existe un rol de ese tipo").build();
        }

        existente.setNombre(rolInput.getNombre());
        Rol actualizado = rolDAO.update(existente);
        return Response.ok(actualizado).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un rol por ID")
    public Response delete(@PathParam("id") Long id) {
        Rol existente = rolDAO.findById(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Rol no encontrado").build();
        }
        rolDAO.delete(existente);
        return Response.ok("Rol eliminado").build();
    }
}

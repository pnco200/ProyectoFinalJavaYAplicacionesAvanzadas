package asis.api;

import asis.dao.CampañaDAO;
import asis.dto.CampaniaMapper;
import asis.dto.CampaniaDTO;
import asis.exceptions.CampañaException;

import asis.models.Campaña;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/campanias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Campañas", description = "Operaciones sobre campañas")
public class CampaniaController {

    @Inject
    private CampañaDAO campañaDAO;

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener campaña por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Campaña encontrada", content = @Content(schema = @Schema(implementation = CampaniaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public Response getById(@PathParam("id") Long id) {
        Campaña campaña = campañaDAO.findById(id);
        if (campaña != null) {
            return Response.ok(CampaniaMapper.toDTO(campaña)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Campaña no encontrada\"}").build();
        }
    }

    @GET
    @Path("/nombre/{name}")
    @Operation(summary = "Buscar campañas por nombre", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de campañas", content = @Content(schema = @Schema(implementation = CampaniaDTO.class)))
    })
    public Response getByName(@PathParam("name") String name) {
        List<Campaña> campañas = campañaDAO.findByName(name);
        return Response.ok(CampaniaMapper.toDTOList(campañas)).build();
    }

    @GET
    @Operation(summary = "Obtener todas las campañas", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de campañas", content = @Content(schema = @Schema(implementation = CampaniaDTO.class)))
    })
    public Response getAll() {
        List<Campaña> campañas = campañaDAO.findAll();
        return Response.ok(CampaniaMapper.toDTOList(campañas)).build();
    }

    @POST
    @Operation(summary = "Crear nueva campaña", responses = {
            @ApiResponse(responseCode = "201", description = "Campaña creada", content = @Content(schema = @Schema(implementation = CampaniaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    public Response crearCampaña(
            @Parameter(description = "Campaña a crear", required = true)
            CampaniaDTO dto) {

        if (dto.getName() == null || dto.getStartDate() == null || dto.getEndDate() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Faltan campos obligatorios\"}").build();
        }

        try {
            Campaña campaña = CampaniaMapper.toEntity(dto);
            Campaña saved = campañaDAO.save(campaña);
            return Response.status(Response.Status.CREATED).entity(CampaniaMapper.toDTO(saved)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Operation(summary = "Actualizar campaña", responses = {
            @ApiResponse(responseCode = "200", description = "Campaña actualizada", content = @Content(schema = @Schema(implementation = CampaniaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public Response actualizarCampaña(
            @Parameter(description = "Campaña con datos actualizados", required = true)
            CampaniaDTO dto) {

        if (dto.getId() == null || dto.getName() == null || dto.getStartDate() == null || dto.getEndDate() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Faltan campos obligatorios\"}").build();
        }

        Campaña existente = campañaDAO.findById(dto.getId());
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Campaña no encontrada\"}").build();
        }

        try {
            Campaña actualizada = CampaniaMapper.toEntity(dto);
            Campaña saved = campañaDAO.save(actualizada);
            return Response.ok(CampaniaMapper.toDTO(saved)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Error al actualizar campaña\"}").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar campaña por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Campaña eliminada"),
            @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public Response eliminarCampaña(@PathParam("id") Long id) {
        Campaña campaña = campañaDAO.findById(id);
        if (campaña == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Campaña no encontrada\"}").build();
        }

        campañaDAO.deleteById(id);
        return Response.ok("{\"message\": \"Campaña eliminada\"}").build();
    }
}

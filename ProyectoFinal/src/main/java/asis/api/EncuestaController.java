package asis.api;

import asis.dao.CampañaDAO;
import asis.dao.EncuestaDAO;
import asis.models.Campaña;
import asis.models.Encuesta;
import asis.models.Encuesta.TipoEncuesta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/encuesta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "EncuestaController", description = "Operaciones sobre Encuestas")
public class EncuestaController {

    @Inject
    private EncuestaDAO encuestaDAO;

    @Inject
    private CampañaDAO campañaDAO;

    @GET
    @Path("/findById/{id}")
    @Operation(summary = "Buscar encuesta por ID")
    public Response findById(@PathParam("id") Long id) {
        Encuesta encuesta = encuestaDAO.findById(id);
        if (encuesta != null) {
            return Response.ok(encuestaToJson(encuesta)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Encuesta no encontrada\"}").build();
        }
    }

    @GET
    @Path("/findByTipo/{tipo}")
    @Operation(summary = "Buscar encuestas por tipo")
    public Response findByTipo(@PathParam("tipo") String tipoStr) {
        try {
            TipoEncuesta tipo = TipoEncuesta.valueOf(tipoStr);
            List<Encuesta> encuestas = encuestaDAO.findByTipo(tipo);
            return Response.ok(encuestasToJson(encuestas)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Tipo de encuesta inválido\"}").build();
        }
    }

    @GET
    @Path("/findByViviendaId/{viviendaId}")
    @Operation(summary = "Buscar encuestas por ID de vivienda")
    public Response findByViviendaId(@PathParam("viviendaId") Long viviendaId) {
        List<Encuesta> encuestas = encuestaDAO.findByViviendaId(viviendaId);
        return Response.ok(encuestasToJson(encuestas)).build();
    }

    @GET
    @Path("/findByCampaniaId/{campaniaId}")
    @Operation(summary = "Buscar encuestas por ID de campaña")
    public Response findByCampañaId(@PathParam("campaniaId") Long campaniaId) {
        List<Encuesta> encuestas = encuestaDAO.findByCampañaId(campaniaId);
        return Response.ok(encuestasToJson(encuestas)).build();
    }

    @GET
    @Path("/all")
    @Operation(summary = "Obtener todas las encuestas")
    public Response findAll() {
        List<Encuesta> encuestas = encuestaDAO.findAll();
        return Response.ok(encuestasToJson(encuestas)).build();
    }

    @POST
    @Path("/create")
    @Operation(summary = "Crear nueva encuesta")
    public Response create(
            @Parameter(description = "Tipo de encuesta (VIVIENDA o PERSONA)", required = true)
            @QueryParam("tipo") String tipoParam,
            @Parameter(description = "En caso de ser de tipo PERSONA, vivienda asociada (opcional)")
            @QueryParam("viviendaId") Long viviendaId,
            @Parameter(description = "En caso de ser de tipo VIVIENDA, campaña asociada (opcional)")
            @QueryParam("campañaId") Long campañaId) {

        if (tipoParam == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Falta el parámetro tipo\"}").build();
        }

        try {
            TipoEncuesta tipo = TipoEncuesta.valueOf(tipoParam);
            Encuesta encuesta = new Encuesta();
            encuesta.setTipo(tipo);

            if (tipo == TipoEncuesta.PERSONA) {
                if (viviendaId == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Las encuestas PERSONA requieren viviendaId\"}").build();
                }

                Encuesta vivienda = encuestaDAO.findById(viviendaId);
                if (vivienda == null || vivienda.getTipo() != TipoEncuesta.VIVIENDA) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Vivienda no encontrada o no válida\"}").build();
                }

                encuesta.setVivienda(vivienda);
                encuesta.setCampaña(null);
            } else {
                if (campañaId == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Falta campañaId para encuestas que no son PERSONA\"}").build();
                }

                Campaña campaña = campañaDAO.findById(campañaId);
                if (campaña == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Campaña no encontrada\"}").build();
                }

                encuesta.setCampaña(campaña);
            }

            Encuesta saved = encuestaDAO.save(encuesta);
            return Response.status(Response.Status.CREATED).entity(encuestaToJson(saved)).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Tipo de encuesta inválido\"}").build();
        }
    }
/* Tecnicamente inusable
    @PUT
    @Path("/addRenglon")
    @Operation(summary = "Agregar renglón a encuesta")
    public Response addRenglon(
            @Parameter(description = "ID de la encuesta a la que asociarle el renglon", required = true)
            @QueryParam("encuestaId") Integer encuestaId,
            @Parameter(description = "ID del renglon a asociar", required = true)
            @QueryParam("renglonId") Integer renglonId) {

        if (encuestaId == null || renglonId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Faltan parámetros\"}").build();
        }

        Encuesta encuesta = encuestaDAO.addRenglonToEncuesta(encuestaId, renglonId);
        if (encuesta != null) {
            return Response.ok(encuestaToJson(encuesta)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"No se pudo agregar el renglón\"}").build();
        }
    }
*/
    @DELETE
    @Path("/delete/{id}")
    @Operation(summary = "Eliminar encuesta por ID")
    public Response delete(@PathParam("id") Long id) {
        Encuesta encuesta = encuestaDAO.findById(id);
        if (encuesta == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Encuesta no encontrada\"}").build();
        }

        if (encuesta.getTipo() == TipoEncuesta.VIVIENDA && encuesta.getPersonas() != null && !encuesta.getPersonas().isEmpty()) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"No se puede eliminar una vivienda con personas asociadas\"}").build();
        }

        encuestaDAO.delete(encuesta);
        return Response.ok("{\"message\": \"Encuesta eliminada\"}").build();
    }

    private String encuestaToJson(Encuesta encuesta) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"id\": ").append(encuesta.getId())
            .append(", \"tipo\": \"").append(encuesta.getTipo()).append("\"");
        if (encuesta.getVivienda() != null) {
            json.append(", \"viviendaId\": ").append(encuesta.getVivienda().getId());
        }
        json.append("}");
        return json.toString();
    }

    private String encuestasToJson(List<Encuesta> encuestas) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < encuestas.size(); i++) {
            sb.append(encuestaToJson(encuestas.get(i)));
            if (i < encuestas.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
package asis.api;

import asis.dao.CampañaDAO;
import asis.dao.JornadaDAO;
import asis.dto.EncuestadorDTO;
import asis.dto.EncuestadorOutDTO;
import asis.dto.JornadaDTO;
import asis.exceptions.JornadaException;
import asis.models.Campaña;
import asis.models.Encuestador;
import asis.models.Jornada;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/jornadas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Jornadas", description="Operaciones sobre Jornadas")
public class JornadaController {

    @Inject
    JornadaDAO jornadaDAO;

    @Inject
    CampañaDAO campañaDAO;

    @GET
    @Operation(summary = "Listar todas las jornadas")
    public Response getAll() {
        List<JornadaDTO> result = jornadaDAO.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar jornada por ID")
    public Response getById(@PathParam("id") Long id) {
        Jornada j = jornadaDAO.findById(id);
        if (j == null)
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No encontrada\"}").build();
        return Response.ok(toDTO(j)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar jornadas por nombre")
    public Response findByName(@QueryParam("name") String name) {
        List<JornadaDTO> result = jornadaDAO.findByName(name).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return Response.ok(result).build();
    }
    
    @GET
    @Path("/{id}/encuestadores")
    @Operation(summary = "Obtener encuestadores asignados a una jornada")
    public Response getEncuestadoresByJornada(@PathParam("id") Long id) {
        Jornada jornada = jornadaDAO.findById(id);
        if (jornada == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Jornada no encontrada\"}").build();
        }

        List<EncuestadorOutDTO> result = jornada.getEncuestadores().stream()
            .map(e -> {
                EncuestadorOutDTO dto = new EncuestadorOutDTO();
                dto.setId(e.getId());
                dto.setName(e.getName());
                dto.setDni(e.getDni());
                dto.setBirthDate(e.getBirthDate());
                dto.setSex(e.getSex());
                dto.setOccupation(e.getOccupation());
                return dto;
            })
            .collect(Collectors.toList());

        return Response.ok(result).build();
    }


    @POST
    @Operation(summary = "Crear nueva jornada")
    public Response create(JornadaDTO dto) {
        try {
            Campaña campaña = campañaDAO.findById(dto.campañaId);
            if (campaña == null)
                throw new JornadaException("Campaña no encontrada.");

            if (dto.startDate.before(campaña.getStartDate()) || dto.endDate.after(campaña.getEndDate())) {
                throw new JornadaException("Las fechas de la jornada deben estar dentro del rango de fechas de la campaña.");
            }

            Jornada j = toEntity(dto);
            j.setCampaña(campaña);
            Jornada creada = jornadaDAO.save(j);
            return Response.status(Response.Status.CREATED).entity(toDTO(creada)).build();
        } catch (JornadaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }


    @PUT
    @Operation(summary = "Actualizar jornada existente")
    public Response update(JornadaDTO dto) {
        try {
            Campaña campaña = campañaDAO.findById(dto.campañaId);
            if (campaña == null)
                throw new JornadaException("Campaña no encontrada.");

            if (dto.startDate.before(campaña.getStartDate()) || dto.endDate.after(campaña.getEndDate())) {
                throw new JornadaException("Las fechas de la jornada deben estar dentro del rango de fechas de la campaña.");
            }

            Jornada j = toEntity(dto);
            j.setCampaña(campaña);
            Jornada actualizada = jornadaDAO.save(j);
            return Response.ok(toDTO(actualizada)).build();
        } catch (JornadaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }


    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar jornada por ID")
    public Response delete(@PathParam("id") Long id) {
        jornadaDAO.deleteById(id);
        return Response.ok("{\"status\":\"Jornada eliminada\"}").build();
    }

    @POST
    @Path("/{jornadaId}/encuestadores/{encuestadorId}")
    @Operation(summary = "Agregar encuestador a jornada")
    public Response addEncuestador(@PathParam("jornadaId") Long jornadaId, @PathParam("encuestadorId") Long encuestadorId) {
        try {
            jornadaDAO.addEncuestador(jornadaId, new Encuestador(encuestadorId));
            return Response.ok("{\"status\":\"Encuestador agregado\"}").build();
        } catch (JornadaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/{jornadaId}/encuestadores/{encuestadorId}")
    @Operation(summary = "Remover encuestador de jornada")
    public Response removeEncuestador(@PathParam("jornadaId") Long jornadaId, @PathParam("encuestadorId") Long encuestadorId) {
        try {
            jornadaDAO.removeEncuestador(jornadaId, new Encuestador(encuestadorId));
            return Response.ok("{\"status\":\"Encuestador eliminado\"}").build();
        } catch (JornadaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    //helpers

    private JornadaDTO toDTO(Jornada j) {
        JornadaDTO dto = new JornadaDTO();
        dto.id = j.getId();
        dto.name = j.getName();
        dto.startDate = j.getStartDate();
        dto.endDate = j.getEndDate();
        dto.campañaId = (j.getCampaña() != null) ? j.getCampaña().getId() : null;
        return dto;
    }

    private Jornada toEntity(JornadaDTO dto) {
        Jornada j = new Jornada();
        j.setId(dto.id);
        j.setName(dto.name);
        j.setStartDate(dto.startDate);
        j.setEndDate(dto.endDate);
        return j;
    }
}

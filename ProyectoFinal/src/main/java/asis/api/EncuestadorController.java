package asis.api;

import asis.dao.EncuestadorDAO;
import asis.dao.IEncuestadorDAO;
import asis.dto.EncuestadorDTO;
import asis.dto.EncuestadorPUTDTO;
import asis.exceptions.EncuestadorException;
import asis.models.Encuestador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Path("/encuestador")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "EncuestadorController", description = "Operaciones sobre Encuestadores")
public class EncuestadorController {


    @Inject
    private IEncuestadorDAO encuestadorDAO;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GET
    @Path("/")
    @Operation(summary = "Obtener todos los encuestadores")
    public Response getAll() {
        List<Encuestador> lista = encuestadorDAO.findAll();
        return Response.ok(listToJson(lista)).build();
    }

    @GET
    @Path("/findById/{id}")
    @Operation(summary = "Buscar encuestador por ID")
    public Response findById(@PathParam("id") Long id) {
        Encuestador e = encuestadorDAO.findById(id);
        if (e != null) {
            return Response.ok(encuestadorToJson(e)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Encuestador no encontrado\"}").build();
    }

    @GET
    @Path("/findByDni/{dni}")
    @Operation(summary = "Buscar encuestador por DNI")
    public Response findByDni(@PathParam("dni") String dni) {
        Encuestador e = encuestadorDAO.findByDni(dni);
        if (e != null) {
            return Response.ok(encuestadorToJson(e)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Encuestador no encontrado\"}").build();
    }

    @POST
    @Path("/create")
    @Operation(summary = "Crear nuevo encuestador")
    public Response create(@RequestBody(description = "Encuestador a crear", required = true,
            content = @Content(schema = @Schema(implementation = EncuestadorDTO.class))) Encuestador encuestador) {
        try {
        	System.out.println("TRY");
            Encuestador creado = encuestadorDAO.save(encuestador);
        	System.out.println("Sed");
            return Response.ok(encuestadorToJson(creado)).build();
        } catch (EncuestadorException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/update")
    @Operation(summary = "Actualizar encuestador existente")
    public Response update(@RequestBody(description = "Encuestador a actualizar", required = true,
            content = @Content(schema = @Schema(implementation = EncuestadorPUTDTO.class))) EncuestadorPUTDTO dto) {
        try {
            Encuestador encuestador = new Encuestador();
            encuestador.setId(dto.getId());
            encuestador.setName(dto.getName());
            encuestador.setDni(dto.getDni());
            encuestador.setSex(dto.getSex());
            encuestador.setOccupation(dto.getOccupation());

            if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
                encuestador.setBirthDate(LocalDate.parse(dto.getBirthDate(), formatter));
            }


            Encuestador actualizado = encuestadorDAO.update(encuestador);
            return Response.ok(encuestadorToJson(actualizado)).build();
        } catch (EncuestadorException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al actualizar el encuestador\"}").build();
        }
    }


    @DELETE
    @Path("/delete/{id}")
    @Operation(summary = "Eliminar encuestador por ID")
    public Response delete(@PathParam("id") Long id) {
        try {
            encuestadorDAO.deleteById(id);
            return Response.ok("{\"status\": \"Encuestador eliminado\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error eliminando encuestador\"}").build();
        }
    }
    
    private String encuestadorToJson(Encuestador e) {
    	String birthDate = e.getBirthDate() != null ? e.getBirthDate().format(formatter) : null;

        return String.format(
            "{" +
                "\"id\": %d, " +
                "\"name\": \"%s\", " +
                "\"dni\": \"%s\", " +
                "\"birthDate\": \"%s\", " +
                "\"sex\": \"%s\", " +
                "\"occupation\": \"%s\"" +
            "}",
            e.getId(), e.getName(), e.getDni(), birthDate, e.getSex(), e.getOccupation()
        );
    }

    private String listToJson(List<Encuestador> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < lista.size(); i++) {
            sb.append(encuestadorToJson(lista.get(i)));
            if (i < lista.size() - 1) sb.append(",");
        }

        sb.append("]");
        return sb.toString();
    }
}

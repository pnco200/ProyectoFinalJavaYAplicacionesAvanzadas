package asis.api;

import asis.dao.CampañaDAO;
import asis.dao.EncuestaDAO;
import asis.dao.RenglonDAO;
import asis.dto.CampaniaMapper;
import asis.dto.CampaniaDTO;
import asis.exceptions.CampañaException;

import asis.models.Campaña;
import asis.models.Encuesta;
import asis.models.Renglon;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/campanias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Campañas", description = "Operaciones sobre campañas")
public class CampaniaController {

    @Inject
    private CampañaDAO campañaDAO;

    @Inject
    private EncuestaDAO encuestaDAO;

    @Inject
    private RenglonDAO renglonDAO;

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
        try {
            if (dto.getName() == null || dto.getStartDate() == null || dto.getEndDate() == null || dto.getBarrioId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Faltan campos obligatorios\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Error al procesar los datos de la campaña\"}").build();
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
        try {
            if (dto.getId() == null || dto.getName() == null || dto.getStartDate() == null || dto.getEndDate() == null || dto.getBarrioId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Faltan campos obligatorios\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Error al procesar los datos de la campaña\"}").build();
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

    @POST
    @Path("/{id}/upload-encuestas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir encuestas desde CSV", responses = {
            @ApiResponse(responseCode = "200", description = "Encuestas subidas"),
            @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public Response uploadEncuestas(@PathParam("id") Long id, EncuestaUploadDTO data) {
        
        try {
            
            Campaña campaña = campañaDAO.findById(id);
            if (campaña == null) {
                System.out.println("ERROR: Campaña no encontrada con ID: " + id);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Campaña no encontrada\"}").build();
            }
            
            System.out.println("Campaña encontrada: " + campaña.getName());

            // Validar que los datos no estén vacíos
            if (data.getViviendas() == null || data.getPersonas() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Faltan datos de viviendas o personas\"}").build();
            }

            Map<String, Long> viviendaIdMap = new HashMap<>();

            // Procesar viviendas
            System.out.println("Procesando " + data.getViviendas().size() + " viviendas...");
            for (Map<String, String> viviendaData : data.getViviendas()) {
                Encuesta vivienda = new Encuesta();
                vivienda.setTipo(Encuesta.TipoEncuesta.VIVIENDA);
                vivienda.setCampaña(campaña);
                Encuesta savedVivienda = encuestaDAO.save(vivienda);
                viviendaIdMap.put(viviendaData.get("ec5_uuid"), savedVivienda.getId());

                for (Map.Entry<String, String> entry : viviendaData.entrySet()) {
                    Renglon renglon = new Renglon();
                    renglon.setTipo(entry.getKey());
                    String valor = entry.getValue();
                    String resultado = (valor == null || valor.isBlank()) ? "N/C" : valor;
                    renglon.setValor(resultado);
                    renglon.setEncuesta(savedVivienda);
                    renglonDAO.save(renglon);
                }
            }

            // Procesar personas
            System.out.println("Procesando " + data.getPersonas().size() + " personas...");
            for (Map<String, String> personaData : data.getPersonas()) {
                Encuesta persona = new Encuesta();
                persona.setTipo(Encuesta.TipoEncuesta.PERSONA);
                persona.setCampaña(campaña); // Agregar esta línea que faltaba
                Encuesta savedPersona = encuestaDAO.save(persona);

                for (Map.Entry<String, String> entry : personaData.entrySet()) {
                    if (entry.getKey().equals("ec5_branch_owner_uuid")) {
                        Long viviendaId = viviendaIdMap.get(entry.getValue());
                        if (viviendaId != null) {
                            Encuesta vivienda = encuestaDAO.findById(viviendaId);
                            persona.setVivienda(vivienda);
                            encuestaDAO.save(persona);
                        }
                    } else {
                        Renglon renglon = new Renglon();
                        renglon.setTipo(entry.getKey());
                        renglon.setValor(entry.getValue());
                        renglon.setEncuesta(savedPersona);
                        renglonDAO.save(renglon);
                    }
                }
            }

            System.out.println("Proceso completado exitosamente");
            return Response.ok("{\"message\": \"Encuestas subidas correctamente\"}").build();
            
        } catch (Exception e) {
            System.out.println("ERROR en uploadEncuestas: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno: " + e.getMessage() + "\"}").build();
        }
    }

    public static class EncuestaUploadDTO {
        private List<Map<String, String>> viviendas;
        private List<Map<String, String>> personas;

        // Constructor por defecto (OBLIGATORIO para JAX-RS)
        public EncuestaUploadDTO() {}

        // Constructor con parámetros
        public EncuestaUploadDTO(List<Map<String, String>> viviendas, List<Map<String, String>> personas) {
            this.viviendas = viviendas;
            this.personas = personas;
        }

        public List<Map<String, String>> getViviendas() {
            return viviendas;
        }

        public void setViviendas(List<Map<String, String>> viviendas) {
            this.viviendas = viviendas;
        }

        public List<Map<String, String>> getPersonas() {
            return personas;
        }

        public void setPersonas(List<Map<String, String>> personas) {
            this.personas = personas;
        }

        @Override
        public String toString() {
            return "EncuestaUploadDTO{" +
                    "viviendas=" + (viviendas != null ? viviendas.size() : 0) + " items" +
                    ", personas=" + (personas != null ? personas.size() : 0) + " items" +
                    '}';
        }
    }
}
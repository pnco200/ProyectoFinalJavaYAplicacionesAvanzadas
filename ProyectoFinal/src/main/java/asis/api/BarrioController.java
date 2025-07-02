package asis.api;

import asis.dao.IBarrioDAO;
import asis.dao.ICoordenadasDAO;
import asis.dao.IZonaDAO;
import asis.dto.BarrioDTO;
import asis.dto.CoordenadasDTO;
import asis.models.Barrio;
import asis.models.Coordenadas;
import asis.models.Zona;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/barrios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "BarrioController", description = "Operaciones sobre Barrios")
public class BarrioController {

    @Inject
    private IBarrioDAO barrioDAO;
    
    @Inject
    private IZonaDAO zonaDao;


    @Inject
    private ICoordenadasDAO coordenadasDAO;

    @GET
    @Path("/")
    @Operation(summary = "Obtener todos los barrios o uno por ID")
    public Response getAll(@QueryParam("id") Long id) {
        try {
            if (id == null) {
                List<Barrio> barrios = barrioDAO.findAll();
                String json = serializeBarriosToJson(barrios);
                return Response.ok(json).build();
            } else {
                Barrio barrio = barrioDAO.findById(id);
                if (barrio == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\": \"Barrio no encontrado\"}").build();
                }
                List<Barrio> singleBarrio = new ArrayList<>();
                singleBarrio.add(barrio);
                String json = serializeBarriosToJson(singleBarrio);
                return Response.ok(json).build();
            }
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @SuppressWarnings("unused")
	@POST
    @Path("/create")
    @Operation(summary = "Crear nuevo barrio con coordenadas")
    public Response create(@RequestBody(
            description = "Barrio con coordenadas",
            required = true,
            content = @Content(schema = @Schema(implementation = BarrioDTO.class))) BarrioDTO dto) {

        try {
            if (dto == null || !dto.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Se requiere un barrio válido y al menos 3 coordenadas\"}").build();
            }

            Barrio savedBarrio = barrioDAO.save(new Barrio(dto.getNombre()));
            if (savedBarrio == null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Ya existe un barrio con ese nombre\"}").build();
            }

            List<Coordenadas> savedCoords = new ArrayList<>();
            for (CoordenadasDTO c : dto.getCoordenadas()) {
                Coordenadas coord = new Coordenadas(c.getLatitud(), c.getLongitud(), savedBarrio);
                coordenadasDAO.save(coord);
                savedCoords.add(coord);
            }
            savedBarrio.setCoordenadas(savedCoords);
            List<Barrio> singleBarrio = new ArrayList<>();
            singleBarrio.add(savedBarrio);
            String json = serializeBarriosToJson(singleBarrio);
            return Response.status(Response.Status.CREATED).entity(json).build();

        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/update/{id}")
    @Operation(summary = "Actualizar barrio y sus coordenadas")
    public Response update(@PathParam("id") Long id,
                           @RequestBody(
                                   description = "Barrio actualizado",
                                   required = true,
                                   content = @Content(schema = @Schema(implementation = BarrioDTO.class))) BarrioDTO dto) {

        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Se requiere ID del barrio\"}").build();
            }

            Barrio updatedBarrio = barrioDAO.findById(id);
            if (updatedBarrio == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"ID Invalida\"}").build();
            }
            if(dto.getNombre() != null) {
            	updatedBarrio.setName(dto.getNombre());
            }

            if (dto.getCoordenadas() != null && dto.getCoordenadas().size() > 2) {
                coordenadasDAO.deleteByBarrioId(id);

                for (CoordenadasDTO c : dto.getCoordenadas()) {
                    Coordenadas coord = new Coordenadas(c.getLatitud(), c.getLongitud(), updatedBarrio);
                    coordenadasDAO.save(coord);
                }
            }

            Barrio saved = barrioDAO.update(updatedBarrio);
            List<Barrio> singleBarrio = new ArrayList<>();
            singleBarrio.add(saved);
            String json = serializeBarriosToJson(singleBarrio);
            return Response.ok(json).build();

        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Operation(summary = "Eliminar barrio y sus coordenadas")
    public Response delete(@PathParam("id") Long id) {
    	System.out.println(id);
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Se requiere ID del barrio\"}").build();
            }

            if (!barrioDAO.existsById(id)) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Barrio no encontrado\"}").build();
            }

            coordenadasDAO.deleteByBarrioId(id);
            List<Zona> zonas;
            zonas = zonaDao.findByBarrioId(id);
            for (Zona zona : zonas) {
                coordenadasDAO.deleteByZonaId(zona.getId());
                zonaDao.deleteById(zona.getId());

			}
            barrioDAO.deleteById(id);

            return Response.noContent().build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    private String serializeBarriosToJson(List<Barrio> barrios) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < barrios.size(); i++) {
            Barrio b = barrios.get(i);
            json.append("{")
                .append("\"id\":").append(b.getId()).append(",")
                .append("\"name\":\"").append(b.getName()).append("\",");
            
            json.append("\"coordenadas\":[");
            
            List<Coordenadas> coordenadasList = b.getCoordenadas();
            if (coordenadasList != null) {
                for (int j = 0; j < coordenadasList.size(); j++) {
                    Coordenadas c = coordenadasList.get(j);
                    json.append("{")
                        .append("\"latitud\":").append(c.getLatitud()).append(",")
                        .append("\"longitud\":").append(c.getLongitud())
                        .append("}");
                    
                    if (j < coordenadasList.size() - 1) {
                        json.append(",");
                    }
                }
            }
            
            json.append("]")
                .append("}");
            
            if (i < barrios.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }
}

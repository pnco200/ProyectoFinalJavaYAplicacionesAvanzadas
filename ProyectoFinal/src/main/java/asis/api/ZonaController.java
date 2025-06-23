package asis.api;

import asis.dao.BarrioDAO;
import asis.dao.CoordenadasDAO;
import asis.dao.ZonaDAO;
import asis.dto.CoordenadasDTO;
import asis.dto.ZonaDTO;
import asis.models.Barrio;
import asis.models.Coordenadas;
import asis.models.Zona;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/zonas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "ZonaController", description = "Operaciones sobre Zonas")
public class ZonaController {

    @Inject
    private ZonaDAO zonaDAO;

    @Inject
    private BarrioDAO barrioDAO;

    @Inject
    private CoordenadasDAO coordenadasDAO;

    @GET
    @Path("/findById/{id}")
    @Operation(summary = "Buscar zona por ID")
    public Response findById(@PathParam("id") Long id) {
        Zona zona = zonaDAO.findById(id);
        if (zona == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Zona no encontrada\"}").build();
        }
        return Response.ok(serializeZonasToJson(List.of(zona))).build();
    }

    @GET
    @Path("/findByBarrioId/{barrioId}")
    @Operation(summary = "Buscar zonas por ID de barrio")
    public Response findByBarrioId(@PathParam("barrioId") Long barrioId) {
        List<Zona> zonas = zonaDAO.findByBarrioId(barrioId);
        return Response.ok(serializeZonasToJson(zonas)).build();
    }

    @GET
    @Path("/all")
    @Operation(summary = "Obtener todas las zonas")
    public Response findAll() {
        List<Zona> zonas = zonaDAO.findAll();
        return Response.ok(serializeZonasToJson(zonas)).build();
    }

    @POST
    @Path("/create")
    @Operation(summary = "Crear nueva zona")
    public Response create(ZonaDTO zonaDTO) {
        if (zonaDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Zona inválida\"}").build();
        }

        Barrio barrio = barrioDAO.findById(zonaDTO.getBarrioId());
        if (barrio == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Barrio no encontrado\"}").build();
        }

        Zona savedZona = zonaDAO.save(new Zona(zonaDTO.getColor(), barrio));

        List<Coordenadas> savedCoords = new ArrayList<>();
        if(zonaDTO.getCoordenadas() != null && zonaDTO.getCoordenadas().size() > 2) {
            for (CoordenadasDTO coordDTO : zonaDTO.getCoordenadas()) {
                Coordenadas coord = new Coordenadas(coordDTO.getLatitud(), coordDTO.getLongitud(), savedZona);
                Coordenadas savedCoord = coordenadasDAO.save(coord);
                savedCoords.add(savedCoord);
            }
        }
        savedZona.setCoordenadas(savedCoords);
        List<Zona> singleZona = new ArrayList<>();
        singleZona.add(savedZona);
        String json = serializeZonasToJson(singleZona);
        
        return Response.status(Response.Status.CREATED).entity(json).build();
    }

    @PUT
    @Path("/update/{id}")
    @Operation(summary = "Actualizar zona existente")
    public Response update(@PathParam("id") Long id, ZonaDTO zonaDTO) {
        try {
            Zona zona = zonaDAO.findById(id);
            if(zona == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Zona no encontrado\"}").build();
            }
            if(zonaDTO.getBarrioId() != null) {
                Barrio barrio = barrioDAO.findById(zonaDTO.getBarrioId());
                if (barrio == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Barrio no encontrado\"}").build();
                }
                zona.setBarrio(barrio);
            }	


            if (zonaDTO.getCoordenadas() != null && zonaDTO.getCoordenadas().size() > 2) {
                coordenadasDAO.deleteByZonaId(id);
                for (CoordenadasDTO coordDTO : zonaDTO.getCoordenadas()) {
                    Coordenadas coord = new Coordenadas(coordDTO.getLatitud(), coordDTO.getLongitud(), zona);
                    coordenadasDAO.save(coord);
                }
            }

            zonaDAO.update(zona);
            return Response.ok("{\"message\":\"Zona actualizada\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Operation(summary = "Eliminar zona por ID")
    public Response delete(@PathParam("id") Long id) {
        if (!zonaDAO.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Zona no encontrada\"}").build();
        }
        coordenadasDAO.deleteByZonaId(id);
        zonaDAO.deleteById(id);
        return Response.noContent().build();
    }

    private String serializeZonasToJson(List<Zona> zonas) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < zonas.size(); i++) {
            Zona z = zonas.get(i);
            json.append("{")
                .append("\"id\":").append(z.getId()).append(",")
                .append("\"color\":\"").append(z.getColor()).append("\",")
            	.append("\"barrioId\":\"").append(z.getBarrio().getId()).append("\",");

            json.append("\"coordenadas\":[");
            
            List<Coordenadas> coordenadasList = z.getCoordenadas();
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
            
            if (i < zonas.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }
}

package asis.api;

import asis.dao.BarrioDAO;
import asis.dao.CoordenadasDAO;
import asis.dao.ZonaDAO;
import asis.dto.CoordenadasDTO;
import asis.models.Barrio;
import asis.models.Coordenadas;
import asis.models.Zona;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/coordenadas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "CoordenadasController", description = "Operaciones sobre Coordenadas")
public class CoordenadasController {


    @Inject
    private CoordenadasDAO coordenadasDAO;

    @Inject
    private BarrioDAO barrioDAO;

    @Inject
    private ZonaDAO zonaDAO;

    @GET
    @Path("/find")
    @Operation(summary = "Buscar coordenadas por ID, barrioId o zonaId")
    public Response find(@QueryParam("id") Long id,
                         @QueryParam("barrioId") Long barrioId,
                         @QueryParam("zonaId") Long zonaId) {
        try {
            if (id != null) {
                Coordenadas c = coordenadasDAO.findById(id);
                if (c == null) return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Coordenada no encontrada\"}").build();
                return Response.ok(coordenadaToJson(c)).build();
            } else if (barrioId != null) {
                List<Coordenadas> list = coordenadasDAO.findByBarrioId(barrioId);
                return Response.ok(coordenadasToJson(list)).build();
            } else if (zonaId != null) {
                List<Coordenadas> list = coordenadasDAO.findByZonaId(zonaId);
                return Response.ok(coordenadasToJson(list)).build();
            } else {
                List<Coordenadas> all = coordenadasDAO.findAll();
                return Response.ok(coordenadasToJson(all)).build();
            }
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/create")
    @Operation(summary = "Crear nuevas coordenadas")
    public Response create(CoordenadasDTO coordenadas) {
        try {
            if (coordenadas == null || coordenadas.getLatitud() == null || coordenadas.getLongitud() == null)
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Datos de coordenadas inválidos\"}").build();

            if ((coordenadas.getBarrioId() == null && coordenadas.getZonaId() == null) ||
                (coordenadas.getBarrioId() != null && coordenadas.getZonaId() != null)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Una coordenada debe pertenecer a un barrio o una zona, pero no a ambos\"}").build();
            }
            
            Coordenadas _coord;

            if (coordenadas.getBarrioId() != null) {
                Barrio barrio = barrioDAO.findById(coordenadas.getBarrioId());
                if (barrio == null) return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Barrio no encontrado\"}").build();
                _coord = new Coordenadas(coordenadas.getLatitud(), coordenadas.getLongitud(), barrio);
            } else {
                Zona zona = zonaDAO.findById(coordenadas.getZonaId());
                if (zona == null) return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Zona no encontrada\"}").build();
                _coord = new Coordenadas(coordenadas.getLatitud(), coordenadas.getLongitud(), zona);
            }

            Coordenadas saved = coordenadasDAO.save(_coord);
            return Response.status(Response.Status.CREATED).entity(coordenadaToJson(saved)).build();

        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/update")
    @Operation(summary = "Actualizar coordenadas")
    public Response update(@QueryParam("id") Long id, CoordenadasDTO coordenadas) {
        try {
            if (id == null || coordenadas == null)
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"ID o coordenadas faltantes\"}").build();

            Coordenadas _coord = coordenadasDAO.findById(id);

            if ((coordenadas.getBarrioId() == null && coordenadas.getZonaId() == null) ||
               (coordenadas.getBarrioId() != null && coordenadas.getZonaId() != null)) {
               return Response.status(Response.Status.BAD_REQUEST)
                      .entity("{\"error\": \"Una coordenada debe pertenecer a un barrio o una zona, pero no a ambos\"}").build();
            }

            if (coordenadas.getBarrioId() != null) {
                Barrio barrio = barrioDAO.findById(coordenadas.getBarrioId());
                if (barrio == null) return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Barrio no encontrado\"}").build();
                _coord.setBarrio(barrio);
                _coord.setZona(null);
            } else {
                Zona zona = zonaDAO.findById(coordenadas.getZonaId());
                if (zona == null) return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Zona no encontrada\"}").build();
                _coord.setBarrio(null);
                _coord.setZona(zona);
            }
            
            if(coordenadas.getLatitud() != null) {
            	_coord.setLatitud(coordenadas.getLatitud());
            }
            if(coordenadas.getLongitud() != null) {
            	_coord.setLongitud(coordenadas.getLongitud());
            }

            Coordenadas updated = coordenadasDAO.update(_coord);
            return Response.ok(coordenadaToJson(updated)).build();

        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/delete")
    @Operation(summary = "Eliminar coordenadas por ID, barrioId o zonaId")
    public Response delete(@QueryParam("id") Long id,
                           @QueryParam("barrioId") Long barrioId,
                           @QueryParam("zonaId") Long zonaId) {
        try {
            if (id != null) {
                if (!coordenadasDAO.existsById(id))
                    return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Coordenada no encontrada\"}").build();
                coordenadasDAO.deleteById(id);
            } else if (barrioId != null) {
                coordenadasDAO.deleteByBarrioId(barrioId);
            } else if (zonaId != null) {
                coordenadasDAO.deleteByZonaId(zonaId);
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Se requiere ID de coordenada, barrio o zona\"}").build();
            }
            return Response.noContent().build();

        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    private String coordenadaToJson(Coordenadas coordenada) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"id\": ").append(coordenada.getId())
            .append(", \"latitud\": ").append(coordenada.getLatitud())
            .append(", \"longitud\": ").append(coordenada.getLongitud());

        if (coordenada.getZona() != null) {
            json.append(", \"zonaId\": ").append(coordenada.getZona().getId());
        } else if (coordenada.getBarrio() != null) {
            json.append(", \"barrioId\": ").append(coordenada.getBarrio().getId());
        }

        json.append("}");
        return json.toString();
    }

    private String coordenadasToJson(List<Coordenadas> coordenadasList) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < coordenadasList.size(); i++) {
            sb.append(coordenadaToJson(coordenadasList.get(i)));
            if (i < coordenadasList.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

}
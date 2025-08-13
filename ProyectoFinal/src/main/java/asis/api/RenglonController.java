package asis.api;

import asis.dao.IRenglonDAO;
import asis.dao.IEncuestaDAO;
import asis.models.Encuesta;
import asis.models.Renglon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/renglon")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "RenglonController", description = "Operaciones sobre Renglones")
public class RenglonController {

    @Inject
    private IRenglonDAO renglonDAO;

    @Inject
    private IEncuestaDAO encuestaDAO;

    @GET
    @Path("/")
    @Operation(summary = "Obtener todos los renglones")
    public Response getAll() {
        List<Renglon> lista = renglonDAO.findAll();
        return Response.ok(renglonesToJson(lista)).build();
    }

    @GET
    @Path("/findById/{id}")
    @Operation(summary = "Buscar renglon por ID")
    public Response findById(@PathParam("id") Long id) {
        Renglon renglon = renglonDAO.findById(id);
        if (renglon != null) {
            return Response.ok(renglonToJson(renglon)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Renglon no encontrado\"}").build();
    }

    @GET
    @Path("/findByEncuestaId/{id}")
    @Operation(summary = "Buscar renglones por ID de encuesta")
    public Response findByEncuesta(@PathParam("id") Long id) {
        return Response.ok(renglonesToJson(renglonDAO.findByEncuestaId(id))).build();
    }

    @GET
    @Path("/findByTipo/{tipo}")
    @Operation(summary = "Buscar renglones por tipo")
    public Response findByTipo(@PathParam("tipo") String tipo) {
        return Response.ok(renglonesToJson(renglonDAO.findByTipo(tipo))).build();
    }

    @POST
    @Path("/create")
    @Operation(summary = "Crear nuevo renglon")
    public Response create(
        @Parameter(description = "Tipo del renglon", required = true)
        @QueryParam("tipo") String tipo,
    	@Parameter(description = "Valor del renglon", required = true)
        @QueryParam("valor") String valor,
    	@Parameter(description = "ID de la encuesta para asociar el renglon", required = true)
        @QueryParam("encuestaId") Long encuestaId) {

        if (tipo == null || valor == null || encuestaId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Faltan parámetros requeridos (tipo, valor, encuestaId)\"}").build();
        }

        Encuesta encuesta = encuestaDAO.findById(encuestaId);
        if (encuesta == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Encuesta no encontrada\"}").build();
        }

        Renglon renglon = new Renglon();
        renglon.setTipo(tipo);
        renglon.setValor(valor);
        renglon.setEncuesta(encuesta);

        Renglon creado = renglonDAO.save(renglon);

        if (creado != null) {
            return Response.status(Response.Status.CREATED).entity(renglonToJson(creado)).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("{\"error\": \"Error al guardar el renglon\"}").build();
    }

    @PUT
    @Path("/update")
    @Operation(summary = "Actualizar renglon existente")
    public Response update(
    	@Parameter(description = "ID del renglon a actualizar", required = true)
        @QueryParam("id") Long id,
        @Parameter(description = "Nuevo valor del renglon (opcional)")
        @QueryParam("valor") String valor,
        @Parameter(description = "Nuevo tipo del renglon (opcional)")
        @QueryParam("tipo") String tipo,
        @Parameter(description = "ID de la encuesta asociada (opcional)")
        @QueryParam("encuestaId") Long encuestaId) {

        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Falta el ID del renglon\"}").build();
        }

        Renglon renglon = renglonDAO.findById(id);
        if (renglon == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Renglon no encontrado\"}").build();
        }

        if (valor != null) {
            renglon.setValor(valor);
        }

        if (tipo != null) {
            renglon.setTipo(tipo);
        }

        if (encuestaId != null) {
            Encuesta encuesta = encuestaDAO.findById(encuestaId);
            if (encuesta == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Encuesta no encontrada\"}").build();
            }
            renglon.setEncuesta(encuesta);
        }

        Renglon actualizado = null;
		try {
			actualizado = renglonDAO.update(renglon);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return Response.ok(renglonToJson(actualizado)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @Operation(summary = "Eliminar renglon por ID")
    public Response delete(@PathParam("id") Long id) {
        Renglon renglon = renglonDAO.findById(id);
        if (renglon != null) {
            renglonDAO.delete(renglon);
            return Response.ok("{\"message\": \"Renglon eliminado\"}").build();
        }
        return Response.status(Response.Status.NOT_FOUND)
            .entity("{\"error\": \"Renglon no encontrado\"}").build();
    }
    
    // Métodos auxiliares para convertir a JSON
    private String renglonToJson(Renglon renglon) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"id\": ").append(renglon.getId())
            .append(", \"valor\": \"").append(renglon.getValor()).append("\"")
            .append(", \"tipo\": \"").append(renglon.getTipo()).append("\"");
            
        if (renglon.getEncuesta() != null) {
            json.append(", \"encuestaId\": ").append(renglon.getEncuesta().getId());
        }
        
        json.append("}");
        return json.toString();
    }

    private String renglonesToJson(List<Renglon> renglones) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < renglones.size(); i++) {
            json.append(renglonToJson(renglones.get(i)));
            if (i < renglones.size() - 1) {
                json.append(", ");
            }
        }
        json.append("]");
        return json.toString();
    }

    @GET
    @Path("/tipos")
    @Operation(summary = "Obtener todos los tipos de renglones distintos")
    public Response getDistinctTipos() {
        List<Object[]> tipos = renglonDAO.findDistinctTipos();
        return Response.ok(tipos).build();
    }

    @GET
    @Path("/valores/{tipo}")
    @Operation(summary = "Obtener todos los valores distintos para un tipo de renglon")
    public Response getDistinctValoresByTipo(@PathParam("tipo") String tipo) {
        List<String> valores = renglonDAO.findDistinctValoresByTipo(tipo);
        return Response.ok(valores).build();
    }
    
    @POST
    @Path("/filtrar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Filtrar encuestas por filtros de vivienda y/o persona (sin respuestas)")
    public Response filtrarEncuestasSinRespuestas(Map<String, List<Map<String, String>>> filtrosMap) {
        try {
            List<Map<String, String>> filtrosVivienda = filtrosMap.get("filtros_vivienda");
            List<Map<String, String>> filtrosPersona = filtrosMap.get("filtros_persona");

            List<Object[]> resultados = renglonDAO.findEncuestasByFiltros(filtrosVivienda, filtrosPersona);
            
            // Convertir a formato más amigable
            List<Map<String, Object>> encuestas = new ArrayList<>();
            for (Object[] row : resultados) {
                Map<String, Object> encuesta = new HashMap<>();
                
                encuesta.put("encuesta_id", row[0]);
                encuesta.put("tipo", row[1]);
                encuesta.put("vivienda_id", row[2]);
                encuesta.put("latitud", row[3]);
                encuesta.put("longitud", row[4]);
                encuestas.add(encuesta);
            }
            
            return Response.ok(encuestas).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error procesando filtros\"}")
                    .build();
        }
    }
    @GET
    @Path("/respuestas/{encuestaId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener respuestas de una encuesta específica")
    public Response getRespuestasByEncuestaId(@PathParam("encuestaId") Long encuestaId) {
        try {
            List<Object[]> resultados = renglonDAO.findRespuestasByEncuestaId(encuestaId);
            
            // Convertir a formato más amigable
            List<Map<String, String>> respuestas = new ArrayList<>();
            for (Object[] row : resultados) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("tipo", (String) row[0]);
                respuesta.put("valor", (String) row[1]);
                respuestas.add(respuesta);
            }
            
            return Response.ok(respuestas).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error obteniendo respuestas de la encuesta\"}")
                    .build();
        }
    }
    
    @POST
    @Path("/chart-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener datos para gráficos basados en múltiples encuestas")
    public Response getChartDataByEncuestaIds(List<Long> encuestaIds) {
        try {
            if (encuestaIds == null || encuestaIds.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Lista de IDs de encuestas no puede estar vacía\"}")
                        .build();
            }
            
            List<Map<String, Object>> chartData = renglonDAO.findChartDataByEncuestaIds(encuestaIds);
            return Response.ok(chartData).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error obteniendo datos para gráficos\"}")
                    .build();
        }
    }

}


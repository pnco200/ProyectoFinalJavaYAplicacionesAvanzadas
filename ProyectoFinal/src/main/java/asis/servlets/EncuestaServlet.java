package asis.servlets;

import asis.dao.EncuestaDAO;
import asis.dao.CampañaDAO;
import asis.models.Encuesta;
import asis.models.Encuesta.TipoEncuesta;
import asis.models.Campaña;
import asis.models.Renglon;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/encuestaOLD")
public class EncuestaServlet extends HttpServlet {

    private EncuestaDAO encuestaDAO;
    private CampañaDAO campañaDAO;

    @Override
    public void init() throws ServletException {
        encuestaDAO = new EncuestaDAO();
        campañaDAO = new CampañaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String tipoParam = request.getParameter("tipo");
        String viviendaIdParam = request.getParameter("viviendaId");
        String campañaIdParam = request.getParameter("campañaId");
        
        response.setContentType("application/json");

        if (idParam != null) {
            // Buscar por ID
            Long id = Long.parseLong(idParam);
            Encuesta encuesta = encuestaDAO.findById(id);
            if (encuesta != null) {
                response.getWriter().write(encuestaToJson(encuesta));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Encuesta no encontrada\"}");
            }
        } else if (tipoParam != null) {
            // Buscar por tipo
            TipoEncuesta tipo = TipoEncuesta.valueOf(tipoParam);
            List<Encuesta> encuestas = encuestaDAO.findByTipo(tipo);
            response.getWriter().write(encuestasToJson(encuestas));
        } else if (viviendaIdParam != null) {
            // Buscar por vivienda ID
            Long viviendaId = Long.parseLong(viviendaIdParam);
            List<Encuesta> encuestas = encuestaDAO.findByViviendaId(viviendaId);
            response.getWriter().write(encuestasToJson(encuestas));
        } else if (campañaIdParam != null) {
            // Buscar por campaña ID
            Long campañaId = Long.parseLong(campañaIdParam);
            List<Encuesta> encuestas = encuestaDAO.findByCampañaId(campañaId);
            response.getWriter().write(encuestasToJson(encuestas));
        } else {
            // Obtener todas las encuestas
            List<Encuesta> encuestas = encuestaDAO.findAll();
            response.getWriter().write(encuestasToJson(encuestas));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tipoParam = request.getParameter("tipo");
        String viviendaIdParam = request.getParameter("viviendaId");
        String campañaIdParam = request.getParameter("campañaId");

        if (tipoParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros obligatorios (tipo)\"}");
            return;
        }

        try {
        	Campaña campaña;
            TipoEncuesta tipo = TipoEncuesta.valueOf(tipoParam);
            
            if (tipo == TipoEncuesta.PERSONA) {
            	campaña = null;
            }else {
            	Long campañaId = Long.parseLong(campañaIdParam);
                campaña = campañaDAO.findById(campañaId);
                if (campaña == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Campaña no encontrada\"}");
                    return;
                }
            }
            

            Encuesta encuesta = new Encuesta();
            encuesta.setTipo(tipo);
            encuesta.setCampaña(campaña);

            // Si es tipo PERSONA, debe tener una vivienda asociada
            if (tipo == TipoEncuesta.PERSONA) {
                if (viviendaIdParam == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Las encuestas de tipo PERSONA requieren viviendaId\"}");
                    return;
                }
                
                Long viviendaId = Long.parseLong(viviendaIdParam);
                Encuesta vivienda = encuestaDAO.findById(viviendaId);
                
                if (vivienda == null || vivienda.getTipo() != TipoEncuesta.VIVIENDA) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Vivienda no encontrada o no es de tipo VIVIENDA\"}");
                    return;
                }
                
                encuesta.setVivienda(vivienda);
            }

            Encuesta savedEncuesta = encuestaDAO.save(encuesta);

            if (savedEncuesta != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(encuestaToJson(savedEncuesta));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Error al guardar la encuesta\"}");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Tipo de encuesta inválido\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Falta el ID de la encuesta\"}");
            return;
        }

        Long id = Long.parseLong(idParam);
        Encuesta encuesta = encuestaDAO.findById(id);

        if (encuesta != null) {
            // Verificar si es una vivienda con personas asociadas
            if (encuesta.getTipo() == TipoEncuesta.VIVIENDA && !encuesta.getPersonas().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\": \"No se puede eliminar una vivienda con personas asociadas\"}");
                return;
            }
            
            encuestaDAO.delete(encuesta);
            response.getWriter().write("{\"message\": \"Encuesta eliminada\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Encuesta no encontrada\"}");
        }
    }
@Override
protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String encuestaId = request.getParameter("encuestaId");
    String renglonIdParam = request.getParameter("renglonId");
    response.setContentType("application/json");

    if (renglonIdParam != null && encuestaId != null) {

        try {
            int _encuestaId = Integer.parseInt(encuestaId);
            int renglonId = Integer.parseInt(renglonIdParam);
            
            Encuesta encuesta = encuestaDAO.addRenglonToEncuesta(_encuestaId, renglonId);
            
            if (encuesta != null) {
                response.getWriter().write(encuestaToJson(encuesta));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"No se pudo agregar el renglón a la encuesta\"}");
            }
            return;
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID inválido\"}");
            return;
        }
    }
    
}

    // Métodos auxiliares para convertir a JSON
    private String encuestaToJson(Encuesta encuesta) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"id\": ").append(encuesta.getId())
            .append(", \"tipo\": \"").append(encuesta.getTipo()).append("\"");            
        if (encuesta.getTipo() == TipoEncuesta.PERSONA && encuesta.getVivienda() != null) {
            json.append(", \"viviendaId\": ").append(encuesta.getVivienda().getId());
        }
        
        json.append("}");
        return json.toString();
    }

    private String encuestasToJson(List<Encuesta> encuestas) {
        StringBuilder json = new StringBuilder("[");
        for (Encuesta encuesta : encuestas) {
            json.append(encuestaToJson(encuesta)).append(", ");
        }
        if (!encuestas.isEmpty()) {
            json.deleteCharAt(json.length() - 1).deleteCharAt(json.length() - 1);
        }
        json.append("]");
        return json.toString();
    }
}
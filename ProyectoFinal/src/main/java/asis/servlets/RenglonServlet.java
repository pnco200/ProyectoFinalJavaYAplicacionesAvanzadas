package asis.servlets;

import asis.dao.RenglonDAO;
import asis.dao.EncuestaDAO;
import asis.models.Renglon;
import asis.models.Encuesta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/renglon")
public class RenglonServlet extends HttpServlet {

    private RenglonDAO renglonDAO;
    private EncuestaDAO encuestaDAO;

    @Override
    public void init() throws ServletException {
        renglonDAO = new RenglonDAO();
        encuestaDAO = new EncuestaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String encuestaIdParam = request.getParameter("encuestaId");
        String tipoParam = request.getParameter("tipo");
        
        response.setContentType("application/json");

        if (idParam != null) {
            // Buscar por ID
            Long id = Long.parseLong(idParam);
            Renglon renglon = renglonDAO.findById(id);
            if (renglon != null) {
                response.getWriter().write(renglonToJson(renglon));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Renglon no encontrado\"}");
            }
        } else if (encuestaIdParam != null) {
            // Buscar por encuesta ID
            Long encuestaId = Long.parseLong(encuestaIdParam);
            List<Renglon> renglones = renglonDAO.findByEncuestaId(encuestaId);
            response.getWriter().write(renglonesToJson(renglones));
        } else if (tipoParam != null) {
            // Buscar por tipo
            List<Renglon> renglones = renglonDAO.findByTipo(tipoParam);
            response.getWriter().write(renglonesToJson(renglones));
        } else {
            // Obtener todos los renglones
            List<Renglon> renglones = renglonDAO.findAll();
            response.getWriter().write(renglonesToJson(renglones));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String valor = request.getParameter("valor");
        String tipo = request.getParameter("tipo");
        String encuestaIdParam = request.getParameter("encuestaId");

        if (valor == null || tipo == null || encuestaIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros obligatorios (valor, tipo, encuestaId)\"}");
            return;
        }

        try {
            Long encuestaId = Long.parseLong(encuestaIdParam);
            Encuesta encuesta = encuestaDAO.findById(encuestaId);
            
            if (encuesta == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Encuesta no encontrada\"}");
                return;
            }

            Renglon renglon = new Renglon();
            renglon.setValor(valor);
            renglon.setTipo(tipo);
            renglon.setEncuesta(encuesta);

            Renglon savedRenglon = renglonDAO.save(renglon);

            if (savedRenglon != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(renglonToJson(savedRenglon));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Error al guardar el renglon\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID de encuesta inválido\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String valor = request.getParameter("valor");
        String tipo = request.getParameter("tipo");
        String encuestaIdParam = request.getParameter("encuestaId");

        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Falta el ID del renglon\"}");
            return;
        }

        Long id = Long.parseLong(idParam);
        Renglon renglon = renglonDAO.findById(id);

        if (renglon == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Renglon no encontrado\"}");
            return;
        }

        if (valor != null) {
            renglon.setValor(valor);
        }
        
        if (tipo != null) {
            renglon.setTipo(tipo);
        }

        if (encuestaIdParam != null) {
            try {
                Long encuestaId = Long.parseLong(encuestaIdParam);
                Encuesta encuesta = encuestaDAO.findById(encuestaId);
                
                if (encuesta == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Encuesta no encontrada\"}");
                    return;
                }
                
                renglon.setEncuesta(encuesta);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"ID de encuesta inválido\"}");
                return;
            }
        }

        Renglon updatedRenglon = renglonDAO.update(renglon);
        response.getWriter().write(renglonToJson(updatedRenglon));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Falta el ID del renglon\"}");
            return;
        }

        Long id = Long.parseLong(idParam);
        Renglon renglon = renglonDAO.findById(id);

        if (renglon != null) {
            renglonDAO.delete(renglon);
            response.getWriter().write("{\"message\": \"Renglon eliminado\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Renglon no encontrado\"}");
        }
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
}
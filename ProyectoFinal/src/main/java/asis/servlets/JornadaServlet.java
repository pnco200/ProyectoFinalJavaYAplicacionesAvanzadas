package asis.servlets;

import asis.dao.CampañaDAO;
import asis.dao.JornadaDAO;
import asis.exceptions.JornadaException;
import asis.models.Campaña;
import asis.models.Encuestador;
import asis.models.Jornada;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/jornadas")
public class JornadaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final JornadaDAO jornadaDAO = new JornadaDAO();
    private final CampañaDAO campañaDAO = new CampañaDAO(); 
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String nameParam = req.getParameter("name");

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            if (idParam != null) {
                Long id = Long.parseLong(idParam);
                Jornada jornada = jornadaDAO.findById(id);
                if (jornada != null) {
                    out.println(jornadaToJson(jornada));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Jornada no encontrada\"}");
                }
            } else if (nameParam != null) {
                List<Jornada> jornadas = jornadaDAO.findByName(nameParam);
                out.println(listToJson(jornadas));
            } else {
                List<Jornada> jornadas = jornadaDAO.findAll();
                out.println(listToJson(jornadas));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar GET\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");

        try {
            switch (action) {
                case "create":
                    Jornada nueva = parseJornadaFromRequest(req);
                    Long campañaIdCreate = Long.parseLong(req.getParameter("campañaId"));
                    Campaña campañaCreate = campañaDAO.findById(campañaIdCreate);
                    if (campañaCreate == null) {
                        throw new JornadaException("Campaña no encontrada con ID: " + campañaIdCreate);
                    }
                    nueva.setCampaña(campañaCreate);
                    Jornada creada = jornadaDAO.save(nueva);
                    out.println(jornadaToJson(creada));
                    break;

                case "update":
                    Jornada actualizada = parseJornadaFromRequest(req);
                    Long campañaIdUpdate = Long.parseLong(req.getParameter("campañaId"));
                    Campaña campañaUpdate = campañaDAO.findById(campañaIdUpdate);
                    if (campañaUpdate == null) {
                        throw new JornadaException("Campaña no encontrada con ID: " + campañaIdUpdate);
                    }
                    actualizada.setCampaña(campañaUpdate);
                    Jornada resultado = jornadaDAO.save(actualizada);
                    out.println(jornadaToJson(resultado));
                    break;

                case "addEncuestador":
                    Long jornadaId1 = Long.parseLong(req.getParameter("jornadaId"));
                    Long encuestadorId1 = Long.parseLong(req.getParameter("encuestadorId"));
                    jornadaDAO.addEncuestador(jornadaId1, new Encuestador(encuestadorId1));
                    out.println("{\"status\": \"Encuestador agregado\"}");
                    break;

                case "removeEncuestador":
                    Long jornadaId2 = Long.parseLong(req.getParameter("jornadaId"));
                    Long encuestadorId2 = Long.parseLong(req.getParameter("encuestadorId"));
                    jornadaDAO.removeEncuestador(jornadaId2, new Encuestador(encuestadorId2));
                    out.println("{\"status\": \"Encuestador eliminado\"}");
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Parametro 'action' invalido\"}");
            }
        } catch (JornadaException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error en POST\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        PrintWriter out = resp.getWriter();

        try {
            Long id = Long.parseLong(idParam);
            jornadaDAO.deleteById(id);
            out.println("{\"status\": \"Jornada eliminada\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"No se pudo eliminar la jornada\"}");
        }
    }

    private Jornada parseJornadaFromRequest(HttpServletRequest req) throws Exception {
        Jornada j = new Jornada();

        if (req.getParameter("id") != null && !req.getParameter("id").isEmpty()) {
            j.setId(Long.parseLong(req.getParameter("id")));
        }

        j.setName(req.getParameter("name"));
        j.setStartDate(sdf.parse(req.getParameter("startDate")));
        j.setEndDate(sdf.parse(req.getParameter("endDate")));
        return j;
    }

    private String jornadaToJson(Jornada j) {
        Long campañaId = (j.getCampaña() != null) ? j.getCampaña().getId() : null;
        return String.format(
            "{\"id\": %d, \"name\": \"%s\", \"startDate\": \"%s\", \"endDate\": \"%s\", \"campañaId\": %s}",
            j.getId(),
            j.getName(),
            sdf.format(j.getStartDate()),
            sdf.format(j.getEndDate()),
            campañaId != null ? campañaId.toString() : "null"
        );
    }


    private String listToJson(List<Jornada> jornadas) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < jornadas.size(); i++) {
            sb.append(jornadaToJson(jornadas.get(i)));
            if (i < jornadas.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}

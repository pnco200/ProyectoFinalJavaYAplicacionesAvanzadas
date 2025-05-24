package asis.servlets;

import asis.dao.CampañaDAO;
import asis.exceptions.CampañaException;
import asis.models.Campaña;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/campania")
public class CampañaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CampañaDAO campañaDAO;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        campañaDAO = new CampañaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String idParam = request.getParameter("id");
        String nameParam = request.getParameter("name");

        try {
            if (idParam != null) {
                Long id = Long.valueOf(idParam);
                Campaña campaña = campañaDAO.findById(id);
                if (campaña != null) {
                    response.getWriter().write(campañaToJson(campaña));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Campaña no encontrada\"}");
                }
            } else if (nameParam != null) {
                List<Campaña> campañas = campañaDAO.findByName(nameParam);
                response.getWriter().write(campañasToJson(campañas));
            } else {
                List<Campaña> campañas = campañaDAO.findAll();
                response.getWriter().write(campañasToJson(campañas));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID inválido\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String name = request.getParameter("name");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        if (name == null || startDateStr == null || endDateStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros obligatorios: name, startDate, endDate\"}");
            return;
        }

        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            Campaña campaña = new Campaña();
            campaña.setName(name);
            campaña.setStartDate(startDate);
            campaña.setEndDate(endDate);

            Campaña saved = campañaDAO.save(campaña);
            if (saved != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(campañaToJson(saved));
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\": \"Ya existe una campaña con ese nombre y fechas solapadas\"}");
            }
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Formato de fecha inválido. Use yyyy-MM-dd\"}");
        } catch (CampañaException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
		}
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        if (idParam == null || name == null || startDateStr == null || endDateStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros obligatorios: id, name, startDate, endDate\"}");
            return;
        }

        try {
            Long id = Long.valueOf(idParam);
            Campaña campaña = campañaDAO.findById(id);

            if (campaña == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Campaña no encontrada\"}");
                return;
            }

            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            campaña.setName(name);
            campaña.setStartDate(startDate);
            campaña.setEndDate(endDate);

            Campaña updated = campañaDAO.save(campaña);
            if (updated != null) {
                response.getWriter().write(campañaToJson(updated));
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\": \"Conflicto con otra campaña (nombre/fechas)\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID inválido\"}");
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Formato de fecha inválido. Usar yyyy-MM-dd\"}");
        } catch (CampañaException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Falta parámetro 'id'\"}");
            return;
        }

        try {
            Long id = Long.valueOf(idParam);
            Campaña campaña = campañaDAO.findById(id);

            if (campaña == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Campaña no encontrada\"}");
                return;
            }

            campañaDAO.deleteById(id);
            response.getWriter().write("{\"message\": \"Campaña eliminada\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID inválido\"}");
        } 
    }

    
    private String campañaToJson(Campaña c) {
        String start = c.getStartDate() != null ? sdf.format(c.getStartDate()) : null;
        String end = c.getEndDate() != null ? sdf.format(c.getEndDate()) : null;
        return "{"
            + "\"id\": " + c.getId() + ", "
            + "\"name\": \"" + escapeJson(c.getName()) + "\", "
            + "\"startDate\": \"" + start + "\", "
            + "\"endDate\": \"" + end + "\""
            + "}";
    }

    private String campañasToJson(List<Campaña> list) {
        StringBuilder sb = new StringBuilder("[");
        for (Campaña c : list) {
            sb.append(campañaToJson(c)).append(", ");
        }
        if (!list.isEmpty()) sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}

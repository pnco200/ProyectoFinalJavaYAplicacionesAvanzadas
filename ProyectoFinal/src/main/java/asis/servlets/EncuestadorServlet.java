package asis.servlets;

import asis.dao.EncuestadorDAO;
import asis.dao.IEncuestadorDAO;
import asis.exceptions.EncuestadorException;
import asis.models.Encuestador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/encuestadorOLD")
public class EncuestadorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final IEncuestadorDAO encuestadorDAO = new EncuestadorDAO();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");

        try {
            if (action == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Falta el parametro 'action'\"}");
                return;
            }

            switch (action) {
                case "findAll":
                    List<Encuestador> todos = encuestadorDAO.findAll();
                    out.println(listToJson(todos));
                    break;

                case "findById":
                    Long id = Long.parseLong(req.getParameter("id"));
                    Encuestador encontrado = encuestadorDAO.findById(id);
                    if (encontrado != null) {
                        out.println(encuestadorToJson(encontrado));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("{\"error\": \"Encuestador no encontrado\"}");
                    }
                    break;

                case "findByDni":
                    String dni = req.getParameter("dni");
                    Encuestador porDni = encuestadorDAO.findByDni(dni);
                    if (porDni != null) {
                        out.println(encuestadorToJson(porDni));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("{\"error\": \"Encuestador no encontrado\"}");
                    }
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Accion invalida\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error en GET\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");

        try {
            if (action == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Falta el parámetro 'action'\"}");
                return;
            }

            switch (action) {
                case "create":
                case "update":
                    Encuestador encuestador = parseEncuestadorFromRequest(req);
                    Encuestador guardado = encuestadorDAO.save(encuestador);
                    out.println(encuestadorToJson(guardado));
                    break;

                case "delete":
                    Long id = Long.parseLong(req.getParameter("id"));
                    encuestadorDAO.deleteById(id);
                    out.println("{\"status\": \"Encuestador eliminado\"}");
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Accion invalida\"}");
            }
        } catch (EncuestadorException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error en POST\"}");
            e.printStackTrace();
        }
    }

    private Encuestador parseEncuestadorFromRequest(HttpServletRequest req) throws Exception {
        Encuestador e = new Encuestador();

        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            e.setId(Long.parseLong(idStr));
        }

        e.setName(req.getParameter("name"));
        e.setDni(req.getParameter("dni"));
        e.setSex(req.getParameter("sex"));
        e.setOccupation(req.getParameter("occupation"));

        String birthDateStr = req.getParameter("birthDate");
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            e.setBirthDate(sdf.parse(birthDateStr));
        }

        return e;
    }

    private String encuestadorToJson(Encuestador e) {
        String birthDate = e.getBirthDate() != null ? sdf.format(e.getBirthDate()) : null;

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

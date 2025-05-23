package asis.servlets;

import asis.dao.RolDAO;
import asis.dao.UsuarioDAO;
import asis.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/usuarioRol")
public class RolUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RolDAO rolDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        rolDAO = new RolDAO();
        usuarioDAO = new UsuarioDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String action = request.getParameter("action");

        if ("addRoleToUser".equals(action)) {
            handleAddRoleToUser(request, response);
        } else if ("removeRoleFromUser".equals(action)) {
            handleRemoveRoleFromUser(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Parametro 'action' invalido\"}");
        }
    }

    private void handleAddRoleToUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rolIdParam = request.getParameter("rolId");
        String userIdParam = request.getParameter("userId");

        if (rolIdParam == null || userIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parametros\"}");
            return;
        }

        try {
            Long rolId = Long.valueOf(rolIdParam);
            Long userId = Long.valueOf(userIdParam);

            Usuario usuario = usuarioDAO.findById(userId);
            if (usuario == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Usuario inexistente\"}");
                return;
            }

            rolDAO.addUsuario(rolId, usuario);
            response.getWriter().write("{\"message\": \"Rol añadido al usuario\"}");
        } catch (Exception e) {
        	if (e instanceof IllegalArgumentException) {
        		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        		response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}"); //Maneja el caso en que el rol no exista o ya lo tenga asignado
        	} else {
        		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        		response.getWriter().write("{\"error\": \"Error al añadir el rol al usuario\"}");
        	}
        }
    }

    private void handleRemoveRoleFromUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rolIdParam = request.getParameter("rolId");
        String userIdParam = request.getParameter("userId");

        if (rolIdParam == null || userIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parametros\"}");
            return;
        }

        try {
            Long rolId = Long.valueOf(rolIdParam);
            Long userId = Long.valueOf(userIdParam);

            Usuario usuario = usuarioDAO.findById(userId);
            if (usuario == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Usuario inexistente\"}");
                return;
            }

            rolDAO.removeUsuario(rolId, usuario);
            response.getWriter().write("{\"message\": \"Rol eliminado del usuario\"}");
        } catch (Exception e) {
        	if (e instanceof IllegalArgumentException) {
        		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        		response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}"); //Maneja el caso en que el rol no exista o no lo tenga asignado
        	} else {
        		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        		response.getWriter().write("{\"error\": \"Error al eliminar el rol del usuario \"}");
        	}
        }
    }
}

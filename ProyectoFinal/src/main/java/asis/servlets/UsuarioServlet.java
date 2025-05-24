package asis.servlets;

import asis.dao.UsuarioDAO;
import asis.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/usuario")
public class UsuarioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        response.setContentType("application/json");

        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            Usuario usuario = usuarioDAO.findById(id);
            if (usuario != null) {
                response.getWriter().write("{\"id\": " + usuario.getId() + ", \"mail\": \"" + usuario.getMail() + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
            }
        } else {
            List<Usuario> usuarios = usuarioDAO.findAll();
            StringBuilder json = new StringBuilder("[");
            for (Usuario usuario : usuarios) {
                json.append("{\"id\": ").append(usuario.getId())
                    .append(", \"mail\": \"").append(usuario.getMail()).append("\"},");
            }
            if (!usuarios.isEmpty()) {
                json.deleteCharAt(json.length() - 1); 
            }
            json.append("]");
            response.getWriter().write(json.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String mail = request.getParameter("mail");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String domicilio = request.getParameter("address");
        String opt_extra = request.getParameter("extra");
        String opt_activity = request.getParameter("activity");

        System.out.println(mail + password + name);
        if (mail == null || password == null || name == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros\"}");
            return;
        }

        Usuario usuario = new Usuario(mail, password, name, true, domicilio, opt_activity, opt_extra);
        Usuario savedUsuario = usuarioDAO.save(usuario);

        if (savedUsuario != null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"id\": " + savedUsuario.getId() + ", \"mail\": \"" + savedUsuario.getMail() + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("{\"error\": \"El usuario ya existe\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String action = request.getParameter("action");

        if (action == null || (!action.equals("update") && !action.equals("activate") && !action.equals("barrio"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Parámetro 'action' inválido (debe ser 'update', 'activate' o 'barrio')\"}");
            return;
        }

        if (action.equals("activate")) {
            doActivate(request, response);
        } else if (action.equals("update")) {
            doUpdate(request, response);
        } else if (action.equals("barrio")) {
			doAsociarBarrio(request, response);
        }
    }
    
    
    private void doAsociarBarrio(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userIdParam = request.getParameter("id");
        String barrioIdParam = request.getParameter("barrioId");

        if (userIdParam == null || barrioIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros: 'id' y 'barrioId' son obligatorios\"}");
            return;
        }

        try {
            Long userId = Long.parseLong(userIdParam);
            Long barrioId = Long.parseLong(barrioIdParam);

            boolean result = usuarioDAO.assignBarrioToUsuario(userId, barrioId);

            if (result) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Barrio asignado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"No se pudo asignar el barrio. Verificá que el usuario y el barrio existan, y que el usuario tenga el rol adecuado.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Error al asignar el barrio\"}");
        }
    }

    private void doActivate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String isActiveParam = request.getParameter("is_active");

        if (idParam == null || isActiveParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros\"}");
            return;
        }

        Long id = Long.parseLong(idParam);
        boolean isActive = Boolean.parseBoolean(isActiveParam);

        boolean updated = usuarioDAO.updateIsActive(id, isActive);

        if (updated) {
            response.getWriter().write("{\"message\": \"Estado actualizado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
        }
    }
    
    
    private void doUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Falta el parámetro 'id'\"}");
                return;
            }

            Long id = Long.parseLong(idParam);
            Usuario usuario = usuarioDAO.findById(id);

            if (usuario == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
                return;
            }

            // Campos opcionales
            String mail = request.getParameter("mail");
            String password = request.getParameter("password");
            String name = request.getParameter("name");
            String domicilio = request.getParameter("domicilio");
            String activity = request.getParameter("activity");
            String extra = request.getParameter("extra");

            if (mail != null) usuario.setMail(mail);
            if (password != null) usuario.setPassword(password);
            if (name != null) usuario.setName(name);
            if (domicilio != null) usuario.setDomicilio(domicilio);
            if (activity != null) usuario.setActivity(activity);
            if (extra != null) usuario.setExtra(extra);

            Usuario updatedUsuario = usuarioDAO.update(usuario);
            if (updatedUsuario != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Usuario actualizado\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"No se pudo actualizar el usuario\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Entro");
    	String idParam = request.getParameter("id");

        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros\"}");
            return;
        }

        Long id = Long.parseLong(idParam);
        Usuario usuario = usuarioDAO.findById(id);

        if (usuario != null) {
            usuarioDAO.delete(usuario);
            response.getWriter().write("{\"message\": \"Usuario eliminado\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
        }
    }
    
    
}

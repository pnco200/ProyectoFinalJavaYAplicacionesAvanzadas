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
        String idParam = request.getParameter("id");
        String mail = request.getParameter("mail");
        String password = request.getParameter("password");
        String name = request.getParameter("name");

        if (idParam == null || mail == null || password == null || name == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parámetros\"}");
            return;
        }

        Long id = Long.parseLong(idParam);
        Usuario usuario = usuarioDAO.findById(id);

        if (usuario != null) {
            usuario.setMail(mail);
            usuario.setPassword(password);
            usuario.setName(name);
            Usuario updatedUsuario = usuarioDAO.update(usuario);
            response.getWriter().write("{\"id\": " + updatedUsuario.getId() + ", \"mail\": \"" + updatedUsuario.getMail() + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

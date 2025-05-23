package asis.servlets;

import asis.dao.RolDAO;
import asis.models.Rol;
import asis.models.Rol.TipoRol;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/rol")
public class RolServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RolDAO rolDAO;

    @Override
    public void init() throws ServletException {
        rolDAO = new RolDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String idParam = request.getParameter("id");
        String tipoParam = request.getParameter("tipo");
        System.out.println("ID: " + idParam + ", Tipo: " + tipoParam);
        try {
            if (idParam != null) {
                Long id = Long.valueOf(idParam);
                Rol rol = rolDAO.findById(id);
                if (rol != null) {
                    response.getWriter().write(rolToJson(rol));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Ese ID no pertenece a un Rol existente\"}");
                }
            } else if (tipoParam != null) {
      
            	System.out.println("Tipo: " + tipoParam);
                TipoRol tipo = TipoRol.valueOf(tipoParam);
                System.out.println("Tipo Rol: " + tipo);
                List<Rol> roles = rolDAO.findByTipoRol(tipo);
                response.getWriter().write(rolesToJson(roles));
            } else {
                List<Rol> roles = rolDAO.findAll();
                response.getWriter().write(rolesToJson(roles));
            }
        } catch (Exception e) {
        	if (e instanceof IllegalArgumentException) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("{\"error\": \"El 'tipo' de rol no pertenece a un rol valido\"}");
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().write("{\"error\": \"Internal server error\"}");
			}
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String tipoParam = request.getParameter("tipo");

        if (tipoParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing 'tipo' parameter\"}");
            return;
        }

        try {
            TipoRol tipo = TipoRol.valueOf(tipoParam);
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(tipo);
            Rol savedRol = rolDAO.save(nuevoRol);

            if (savedRol != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(rolToJson(savedRol));
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\": \"El rol ya existe\"}");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\": \"El 'tipo' de rol no pertenece a un rol valido\"}");
        } 
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String idParam = request.getParameter("id");
        String tipoParam = request.getParameter("tipo");

        if (idParam == null || tipoParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parametros\"}");
            return;
        }

        try {
            Long id = Long.valueOf(idParam);
            TipoRol tipo = TipoRol.valueOf(tipoParam);
            Rol rol = rolDAO.findById(id);

            if (rol == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ese rol no existe\"}");
                return;
            }

            // Chequeo para no permitir tipos duplicados
            Rol existingRol = rolDAO.findByTipoRol(tipo).stream()
                .filter(r -> !r.getId().equals(id)) 
                .findFirst()
                .orElse(null);

            if (existingRol != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\": \"Ya hay un rol de ese tipo\"}");
                return;
            }

            rol.setNombre(tipo);
            Rol updatedRol = rolDAO.update(rol);
            if (updatedRol != null) {
                response.getWriter().write(rolToJson(updatedRol));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Error al actualizar el rol\"}");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"El 'tipo' de rol no pertenece a un rol valido\"}");
        }
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan parametros\"}");
            return;
        }

        Long id = Long.valueOf(idParam);
        Rol rol = rolDAO.findById(id);
        if (rol == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Rol no encontrado\"}");
            return;
        }

        rolDAO.delete(rol);
        response.getWriter().write("{\"message\": \"Rol eliminado\"}");
    }
    
    private String rolToJson(Rol rol) {
        return "{\"id\": " + rol.getId() + ", \"tipo\": \"" + rol.getNombre() + "\"}";
    }

    private String rolesToJson(List<Rol> roles) {
        StringBuilder sb = new StringBuilder("[");
        for (Rol rol : roles) {
            sb.append(rolToJson(rol)).append(", ");
        }
        if (!roles.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}

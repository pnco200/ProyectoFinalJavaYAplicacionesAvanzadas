package asis.servlets;

import asis.models.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/usuario")
public class UsuarioServlet extends HttpServlet {
    
    private EntityManagerFactory emf;
    
    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("miUP");
    }
    
    // CREATE - POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        EntityManager em = emf.createEntityManager();
        
        try {
            String mail = request.getParameter("mail");
            String name = request.getParameter("name");
            String password = request.getParameter("password");
            String domicilio = request.getParameter("domicilio");
            String activity = request.getParameter("activity");
            
            em.getTransaction().begin();
            Usuario usuario = new Usuario();
            usuario.setMail(mail);
            usuario.setName(name);
            usuario.setPassword(password);
            usuario.setIs_active(true);
            usuario.setDomicilio(domicilio);
            usuario.setActivity(activity);
            
            em.persist(usuario);
            em.getTransaction().commit();
            
            out.println("<h2>Usuario creado exitosamente</h2>");
            out.println("<p>ID: " + usuario.getId() + "</p>");
            out.println("<p>Email: " + usuario.getMail() + "</p>");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<h2>Error al crear usuario</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
        } finally {
            em.close();
        }
    }
    
    // READ - GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        EntityManager em = emf.createEntityManager();
        
        try {
            String idParam = request.getParameter("id");
            String email = request.getParameter("email");
            
            if (idParam != null) {
                // Buscar por ID
                Long id = Long.parseLong(idParam);
                Usuario usuario = em.find(Usuario.class, id);
                
                if (usuario != null) {
                    out.println("<h2>Usuario encontrado</h2>");
                    out.println("<p>ID: " + usuario.getId() + "</p>");
                    out.println("<p>Nombre: " + usuario.getName() + "</p>");
                    out.println("<p>Email: " + usuario.getMail() + "</p>");
                    out.println("<p>Domicilio: " + usuario.getDomicilio() + "</p>");
                    out.println("<p>Actividad: " + usuario.getActivity() + "</p>");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("<h2>Usuario no encontrado</h2>");
                }
            } else if (email != null) {
                // Buscar por email
                TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.mail = :email", Usuario.class);
                query.setParameter("email", email);
                try {
                    Usuario usuario = query.getSingleResult();
                    out.println("<h2>Usuario encontrado</h2>");
                    out.println("<p>ID: " + usuario.getId() + "</p>");
                    out.println("<p>Nombre: " + usuario.getName() + "</p>");
                    out.println("<p>Email: " + usuario.getMail() + "</p>");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("<h2>Usuario no encontrado</h2>");
                }
            } else {
                // Listar todos
                TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u", Usuario.class);
                out.println("<h2>Lista de Usuarios</h2>");
                out.println("<ul>");
                for (Usuario usuario : query.getResultList()) {
                    out.println("<li>ID: " + usuario.getId() + 
                                ", Nombre: " + usuario.getName() + 
                                ", Email: " + usuario.getMail() + "</li>");
                }
                out.println("</ul>");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<h2>Error al consultar usuarios</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
        } finally {
            em.close();
        }
    }
    
    // UPDATE - PUT
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        EntityManager em = emf.createEntityManager();
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                Long id = Long.parseLong(idParam);
                Usuario usuario = em.find(Usuario.class, id);
                
                if (usuario != null) {
                    em.getTransaction().begin();
                    
                    String name = request.getParameter("name");
                    if (name != null) usuario.setName(name);
                    
                    String mail = request.getParameter("mail");
                    if (mail != null) usuario.setMail(mail);
                    
                    String password = request.getParameter("password");
                    if (password != null) usuario.setPassword(password);
                    
                    String domicilio = request.getParameter("domicilio");
                    if (domicilio != null) usuario.setDomicilio(domicilio);
                    
                    String activity = request.getParameter("activity");
                    if (activity != null) usuario.setActivity(activity);
                    
                    em.merge(usuario);
                    em.getTransaction().commit();
                    
                    out.println("<h2>Usuario actualizado</h2>");
                    out.println("<p>ID: " + usuario.getId() + "</p>");
                    out.println("<p>Nombre: " + usuario.getName() + "</p>");
                    out.println("<p>Email: " + usuario.getMail() + "</p>");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("<h2>Usuario no encontrado</h2>");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("<h2>Error: Se requiere el ID</h2>");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<h2>Error al actualizar usuario</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
        } finally {
            em.close();
        }
    }
    
    // DELETE
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        EntityManager em = emf.createEntityManager();
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                Long id = Long.parseLong(idParam);
                Usuario usuario = em.find(Usuario.class, id);
                
                if (usuario != null) {
                    em.getTransaction().begin();
                    em.remove(usuario);
                    em.getTransaction().commit();
                    
                    out.println("<h2>Usuario eliminado exitosamente</h2>");
                    out.println("<p>ID eliminado: " + id + "</p>");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("<h2>Usuario no encontrado</h2>");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("<h2>Error: Se requiere el ID</h2>");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<h2>Error al eliminar usuario</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
        } finally {
            em.close();
        }
    }
    
    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

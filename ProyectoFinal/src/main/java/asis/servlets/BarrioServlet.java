package asis.servlets;

import asis.dao.BarrioDAO;
import asis.dao.IBarrioDAO;
import asis.models.Barrio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/barrios")
public class BarrioServlet extends HttpServlet {
    
    private IBarrioDAO barrioDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        barrioDAO = new BarrioDAO();
        gson = new GsonBuilder().create();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");
        
        try {
            if (idParam == null) {
                List<Barrio> barrios = barrioDAO.findAll();
                out.print(gson.toJson(barrios));
            } else {
                Long id = Long.parseLong(idParam);
                Barrio barrio = barrioDAO.findById(id);
                
                if (barrio == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Barrio no encontrado");
                    return;
                }
                
                out.print(gson.toJson(barrio));
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            BufferedReader reader = request.getReader();
            Barrio barrio = gson.fromJson(reader, Barrio.class);
            
            if (barrio == null || barrio.getName() == null || barrio.getName().trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de barrio inválidos");
                return;
            }
            
            Barrio savedBarrio = barrioDAO.save(barrio);
            
            if (savedBarrio == null) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Ya existe un barrio con ese nombre");
                return;
            }
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(savedBarrio));
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");
        
        try {
            if (idParam == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere ID del barrio");
                return;
            }
            
            Long id = Long.parseLong(idParam);
            BufferedReader reader = request.getReader();
            Barrio barrio = gson.fromJson(reader, Barrio.class);
            
            if (barrio == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de barrio inválidos");
                return;
            }
            
            barrio.setId(id);
            
            Barrio updatedBarrio = barrioDAO.update(barrio);
            out.print(gson.toJson(updatedBarrio));
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        try {
            if (idParam == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere ID del barrio");
                return;
            }
            
            Long id = Long.parseLong(idParam);
            
            if (!barrioDAO.existsById(id)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Barrio no encontrado");
                return;
            }
            
            barrioDAO.deleteById(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
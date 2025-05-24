package asis.servlets;

import asis.dao.BarrioDAO;
import asis.dao.IBarrioDAO;
import asis.dao.IZonaDAO;
import asis.dao.ZonaDAO;
import asis.models.Barrio;
import asis.models.Zona;
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

@WebServlet("/zonas")
public class ZonaServlet extends HttpServlet {
    
    private IZonaDAO zonaDAO;
    private IBarrioDAO barrioDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        zonaDAO = new ZonaDAO();
        barrioDAO = new BarrioDAO();
        gson = new GsonBuilder().create();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");
        String barrioIdParam = request.getParameter("barrioId");
        
        try {
            if (idParam != null) {
                // Get zona by ID
                Long id = Long.parseLong(idParam);
                Zona zona = zonaDAO.findById(id);
                
                if (zona == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Zona no encontrada");
                    return;
                }
                
                out.print(gson.toJson(zona));
            } else if (barrioIdParam != null) {
                // Get zonas by barrio ID
                Long barrioId = Long.parseLong(barrioIdParam);
                List<Zona> zonas = zonaDAO.findByBarrioId(barrioId);
                out.print(gson.toJson(zonas));
            } else {
                // Get all zonas
                List<Zona> zonas = zonaDAO.findAll();
                out.print(gson.toJson(zonas));
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
            Zona zona = gson.fromJson(reader, Zona.class);
            
            if (zona == null || zona.getColor() == null || zona.getColor().trim().isEmpty() 
                    || zona.getBarrio() == null || zona.getBarrio().getId() == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de zona inválidos");
                return;
            }
            
            // Verify that the barrio exists
            Long barrioId = zona.getBarrio().getId();
            Barrio barrio = barrioDAO.findById(barrioId);
            if (barrio == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El barrio especificado no existe");
                return;
            }
            
            // Set the complete barrio object
            zona.setBarrio(barrio);
            
            Zona savedZona = zonaDAO.save(zona);
            
            if (savedZona == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar la zona");
                return;
            }
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(savedZona));
            
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere ID de la zona");
                return;
            }
            
            Long id = Long.parseLong(idParam);
            BufferedReader reader = request.getReader();
            Zona zona = gson.fromJson(reader, Zona.class);
            
            if (zona == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de zona inválidos");
                return;
            }
            
            zona.setId(id);
            
            // Verify that the barrio exists if it's being updated
            if (zona.getBarrio() != null && zona.getBarrio().getId() != null) {
                Long barrioId = zona.getBarrio().getId();
                Barrio barrio = barrioDAO.findById(barrioId);
                if (barrio == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El barrio especificado no existe");
                    return;
                }
                zona.setBarrio(barrio);
            }
            
            Zona updatedZona = zonaDAO.update(zona);
            out.print(gson.toJson(updatedZona));
            
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere ID de la zona");
                return;
            }
            
            Long id = Long.parseLong(idParam);
            
            if (!zonaDAO.existsById(id)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Zona no encontrada");
                return;
            }
            
            zonaDAO.deleteById(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
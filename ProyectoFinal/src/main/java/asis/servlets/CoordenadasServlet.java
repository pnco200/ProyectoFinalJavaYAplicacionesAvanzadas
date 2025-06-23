package asis.servlets;

import asis.dao.BarrioDAO;
import asis.dao.CoordenadasDAO;
import asis.dao.IBarrioDAO;
import asis.dao.ICoordenadasDAO;
import asis.dao.IZonaDAO;
import asis.dao.ZonaDAO;
import asis.models.Barrio;
import asis.models.Coordenadas;
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

@WebServlet("/coordenadasOLD")
public class CoordenadasServlet extends HttpServlet {
    
    private ICoordenadasDAO coordenadasDAO;
    private IBarrioDAO barrioDAO;
    private IZonaDAO zonaDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        coordenadasDAO = new CoordenadasDAO();
        barrioDAO = new BarrioDAO();
        zonaDAO = new ZonaDAO();
        gson = new GsonBuilder().create();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");
        String barrioIdParam = request.getParameter("barrioId");
        String zonaIdParam = request.getParameter("zonaId");
        
        try {
            if (idParam != null) {
                // Get coordenada by ID
                Long id = Long.parseLong(idParam);
                Coordenadas coordenadas = coordenadasDAO.findById(id);
                
                if (coordenadas == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Coordenada no encontrada");
                    return;
                }
                
                out.print(gson.toJson(coordenadas));
            } else if (barrioIdParam != null) {
                // Get coordenadas by barrio ID
                Long barrioId = Long.parseLong(barrioIdParam);
                List<Coordenadas> coordenadasList = coordenadasDAO.findByBarrioId(barrioId);
                out.print(gson.toJson(coordenadasList));
            } else if (zonaIdParam != null) {
                // Get coordenadas by zona ID
                Long zonaId = Long.parseLong(zonaIdParam);
                List<Coordenadas> coordenadasList = coordenadasDAO.findByZonaId(zonaId);
                out.print(gson.toJson(coordenadasList));
            } else {
                // Get all coordenadas
                List<Coordenadas> coordenadasList = coordenadasDAO.findAll();
                out.print(gson.toJson(coordenadasList));
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
            Coordenadas coordenadas = gson.fromJson(reader, Coordenadas.class);
            
            if (coordenadas == null || coordenadas.getLatitud() == null || coordenadas.getLongitud() == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de coordenadas inválidos");
                return;
            }
            
            // Ensure that either barrio or zona is set, but not both
            if ((coordenadas.getBarrio() == null && coordenadas.getZona() == null) || 
                (coordenadas.getBarrio() != null && coordenadas.getZona() != null)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Una coordenada debe pertenecer a un barrio o a una zona, pero no a ambos.");
                return;
            }
            
            // Verify that the referenced entity exists
            if (coordenadas.getBarrio() != null) {
                Long barrioId = coordenadas.getBarrio().getId();
                Barrio barrio = barrioDAO.findById(barrioId);
                if (barrio == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El barrio especificado no existe");
                    return;
                }
                coordenadas.setBarrio(barrio);
            } else if (coordenadas.getZona() != null) {
                Long zonaId = coordenadas.getZona().getId();
                Zona zona = zonaDAO.findById(zonaId);
                if (zona == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La zona especificada no existe");
                    return;
                }
                coordenadas.setZona(zona);
            }
            
            Coordenadas savedCoordenadas = coordenadasDAO.save(coordenadas);
            
            if (savedCoordenadas == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar las coordenadas");
                return;
            }
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(savedCoordenadas));
            
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere ID de la coordenada");
                return;
            }
            
            Long id = Long.parseLong(idParam);
            BufferedReader reader = request.getReader();
            Coordenadas coordenadas = gson.fromJson(reader, Coordenadas.class);
            
            if (coordenadas == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de coordenadas inválidos");
                return;
            }
            
            coordenadas.setId(id);
            
            // Ensure that either barrio or zona is set, but not both
            if ((coordenadas.getBarrio() == null && coordenadas.getZona() == null) || 
                (coordenadas.getBarrio() != null && coordenadas.getZona() != null)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Una coordenada debe pertenecer a un barrio o a una zona, pero no a ambos.");
                return;
            }
            
            // Verify that the referenced entity exists
            if (coordenadas.getBarrio() != null) {
                Long barrioId = coordenadas.getBarrio().getId();
                Barrio barrio = barrioDAO.findById(barrioId);
                if (barrio == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El barrio especificado no existe");
                    return;
                }
                coordenadas.setBarrio(barrio);
            } else if (coordenadas.getZona() != null) {
                Long zonaId = coordenadas.getZona().getId();
                Zona zona = zonaDAO.findById(zonaId);
                if (zona == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La zona especificada no existe");
                    return;
                }
                coordenadas.setZona(zona);
            }
            
            Coordenadas updatedCoordenadas = coordenadasDAO.update(coordenadas);
            out.print(gson.toJson(updatedCoordenadas));
            
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
        String barrioIdParam = request.getParameter("barrioId");
        String zonaIdParam = request.getParameter("zonaId");
        
        try {
            if (idParam != null) {
                // Delete a single coordenada by ID
                Long id = Long.parseLong(idParam);
                
                if (!coordenadasDAO.existsById(id)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Coordenada no encontrada");
                    return;
                }
                
                coordenadasDAO.deleteById(id);
            } else if (barrioIdParam != null) {
                // Delete all coordenadas for a barrio
                Long barrioId = Long.parseLong(barrioIdParam);
                coordenadasDAO.deleteByBarrioId(barrioId);
            } else if (zonaIdParam != null) {
                // Delete all coordenadas for a zona
                Long zonaId = Long.parseLong(zonaIdParam);
                coordenadasDAO.deleteByZonaId(zonaId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Se requiere ID de coordenada, ID de barrio o ID de zona");
                return;
            }
            
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
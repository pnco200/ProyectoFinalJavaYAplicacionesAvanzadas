package asis.servlets;

import asis.dao.BarrioDAO;
import asis.dao.CoordenadasDAO;
import asis.dao.IBarrioDAO;
import asis.dao.ICoordenadasDAO;
import asis.dao.IZonaDAO;
import asis.dao.ZonaDAO;
import asis.dto.CoordenadasDTO;
import asis.dto.ZonaDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/zonas")
public class ZonaServlet extends HttpServlet {
    
    private IZonaDAO zonaDAO;
    private IBarrioDAO barrioDAO;
    private ICoordenadasDAO coordenadasDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        zonaDAO = new ZonaDAO();
        barrioDAO = new BarrioDAO();
        coordenadasDAO = new CoordenadasDAO();
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
            ZonaDTO zonaDTO = gson.fromJson(reader, ZonaDTO.class);
            if (zonaDTO == null || !zonaDTO.isValid()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Se requiere una zona válida y al menos 3 coordenadas");
                return;
            }
            
            Long barrioId = zonaDTO.getZona().getBarrio().getId();
            Barrio barrio = barrioDAO.findById(barrioId);
            if (barrio == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El barrio especificado no existe");
                return;
            }
            
            zonaDTO.getZona().setBarrio(barrio);
            
            Zona savedZona = zonaDAO.save(zonaDTO.getZona());
            if (savedZona == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar la zona");
                return;
            }

            List<Coordenadas> savedCoordenadas = new ArrayList<>();
            for (CoordenadasDTO coordDTO : zonaDTO.getCoordenadas()) {
                Coordenadas coordenada = new Coordenadas(coordDTO.getLatitud(), coordDTO.getLongitud(), savedZona);
                Coordenadas savedCoordenada = coordenadasDAO.save(coordenada);
                
                if (savedCoordenada != null) {
                    savedCoordenadas.add(savedCoordenada);
                }
            }

            ZonaDTO responseDTO = new ZonaDTO();
            responseDTO.setZona(savedZona);

            List<CoordenadasDTO> coordenadasDTOs = new ArrayList<>();
            for (Coordenadas coord : savedCoordenadas) {
                CoordenadasDTO dto = new CoordenadasDTO(coord.getLatitud(), coord.getLongitud());
                coordenadasDTOs.add(dto);
            }

            responseDTO.setCoordenadas(coordenadasDTOs);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("zonaId", savedZona.getId());
            responseMap.put("color", savedZona.getColor());
            responseMap.put("barrioId", savedZona.getBarrio().getId());
            responseMap.put("barrioName", savedZona.getBarrio().getName());
            responseMap.put("coordenadas", coordenadasDTOs);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(responseMap));
            
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
            ZonaDTO zonaDTO = gson.fromJson(reader, ZonaDTO.class);
            
            Zona savedZona = zonaDTO.getZona();

            if (savedZona == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de zona inválidos");
                return;
            }
            
            savedZona.setId(id);
            
            // Verify that the barrio exists if it's being updated
            if (savedZona.getBarrio() != null && savedZona.getBarrio().getId() != null) {
                Long barrioId = savedZona.getBarrio().getId();
                Barrio barrio = barrioDAO.findById(barrioId);
                if (barrio == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El barrio especificado no existe");
                    return;
                }
                savedZona.setBarrio(barrio);
            }
            
            zonaDAO.update(savedZona);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("zonaId", savedZona.getId());
            responseMap.put("color", savedZona.getColor());
            responseMap.put("barrioId", savedZona.getBarrio().getId());
            responseMap.put("barrioName", savedZona.getBarrio().getName());
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(responseMap));
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
            
            // Delete associated coordenadas first
            coordenadasDAO.deleteByZonaId(id);
            
            // Then delete the zona
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
package asis.servlets;

import asis.dao.BarrioDAO;
import asis.dao.CoordenadasDAO;
import asis.dao.IBarrioDAO;
import asis.dao.ICoordenadasDAO;
import asis.dto.BarrioDTO;
import asis.dto.CoordenadasDTO;
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
import java.util.List;

@WebServlet("/barrios")
public class BarrioServlet extends HttpServlet {
    
    private IBarrioDAO barrioDAO;
    private ICoordenadasDAO coordenadasDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
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
        
        try {
            if (idParam == null) {
                List<Barrio> barrios = barrioDAO.findAll();
                StringBuilder json = new StringBuilder();
                out.print(serializeBarriosToJson(barrios));
            } else {
                Long id = Long.parseLong(idParam);
                Barrio b = barrioDAO.findById(id);
                
                if (b == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Barrio no encontrado");
                    return;
                }
                
                List<Barrio> singleBarrio = new ArrayList<>();
                singleBarrio.add(b);
                String json = serializeBarriosToJson(singleBarrio);
                out.print(json);

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
            BarrioDTO barrioDTO = gson.fromJson(reader, BarrioDTO.class);
            
            if (barrioDTO == null || !barrioDTO.isValid()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Se requiere un barrio válido y al menos 3 coordenadas");
                return;
            }
            
            Barrio savedBarrio = barrioDAO.save(barrioDTO.getBarrio());
            if (savedBarrio == null) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Ya existe un barrio con ese nombre");
                return;
            }
            
            List<Coordenadas> savedCoordenadas = new ArrayList<>();
            for (CoordenadasDTO coordDTO : barrioDTO.getCoordenadas()) {
                Coordenadas coordenada = new Coordenadas(coordDTO.getLatitud(), coordDTO.getLongitud(), savedBarrio);
                Coordenadas savedCoordenada = coordenadasDAO.save(coordenada);
                
                if (savedCoordenada != null) {
                    savedCoordenadas.add(savedCoordenada);
                }
            }
            
            BarrioDTO responseDTO = new BarrioDTO();
            responseDTO.setBarrio(savedBarrio);
            
            List<CoordenadasDTO> coordenadasDTOs = new ArrayList<>();
            for (Coordenadas coord : savedCoordenadas) {
                CoordenadasDTO dto = new CoordenadasDTO(coord.getLatitud(), coord.getLongitud());
                coordenadasDTOs.add(dto);
            }
            responseDTO.setCoordenadas(coordenadasDTOs);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(responseDTO));
            
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
            BarrioDTO barrioDTO = gson.fromJson(reader, BarrioDTO.class);
            
            Barrio savedBarrio = barrioDTO.getBarrio();

            savedBarrio.setId(id);
            List<Coordenadas> savedCoordenadas = new ArrayList<>();
            if(barrioDTO.getCoordenadas() != null && barrioDTO.getCoordenadas().size() > 2) {
                coordenadasDAO.deleteByBarrioId(id);
                for (CoordenadasDTO coordDTO : barrioDTO.getCoordenadas()) {
                    Coordenadas coordenada = new Coordenadas(coordDTO.getLatitud(), coordDTO.getLongitud(), savedBarrio);
                    Coordenadas savedCoordenada = coordenadasDAO.save(coordenada);
                    
                    if (savedCoordenada != null) {
                        savedCoordenadas.add(savedCoordenada);
                    }
                }
            }
            
            Barrio updatedBarrio = barrioDAO.update(savedBarrio);
            StringBuilder json = new StringBuilder();
            json.append("[");

                json.append("{")
                    .append("\"id\":").append(updatedBarrio.getId()).append(",")
                    .append("\"name\":\"").append(updatedBarrio.getName()).append("\"")
                    .append("}");

            json.append("]");
            out.print(json.toString());
            
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
            
            coordenadasDAO.deleteByBarrioId(id);
            
            barrioDAO.deleteById(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
    private String serializeBarriosToJson(List<Barrio> barrios) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < barrios.size(); i++) {
            Barrio b = barrios.get(i);
            json.append("{")
                .append("\"id\":").append(b.getId()).append(",")
                .append("\"name\":\"").append(b.getName()).append("\",");
            
            json.append("\"coordenadas\":[");
            
            List<Coordenadas> coordenadasList = b.getCoordenadas();
            if (coordenadasList != null) {
                for (int j = 0; j < coordenadasList.size(); j++) {
                    Coordenadas c = coordenadasList.get(j);
                    json.append("{")
                        .append("\"latitud\":").append(c.getLatitud()).append(",")
                        .append("\"longitud\":").append(c.getLongitud())
                        .append("}");
                    
                    if (j < coordenadasList.size() - 1) {
                        json.append(",");
                    }
                }
            }
            
            json.append("]")
                .append("}");
            
            if (i < barrios.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }
}
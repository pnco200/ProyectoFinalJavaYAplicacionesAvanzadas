package asis.dto;

import asis.models.Zona;
import java.util.List;

public class ZonaDTO {
    private Zona zona;
    private List<CoordenadasDTO> coordenadas;
    
    public ZonaDTO() {
    }
    
    public Zona getZona() {
        return zona;
    }
    
    public void setZona(Zona zona) {
        this.zona = zona;
    }
    
    public List<CoordenadasDTO> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<CoordenadasDTO> coordenadas) {
        this.coordenadas = coordenadas;
    }
    
    public boolean isValid() {
        return zona != null && zona.getColor() != null && !zona.getColor().trim().isEmpty() 
            && zona.getBarrio() != null && zona.getBarrio().getId() != null
            && coordenadas != null && coordenadas.size() >= 3;
    }
}
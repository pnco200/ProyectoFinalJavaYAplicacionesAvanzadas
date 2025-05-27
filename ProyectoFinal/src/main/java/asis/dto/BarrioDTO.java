package asis.dto;

import asis.models.Barrio;
import java.util.List;

public class BarrioDTO {
    private Barrio barrio;
    private List<CoordenadasDTO> coordenadas;
    
    public BarrioDTO() {
    }
    
    public Barrio getBarrio() {
        return barrio;
    }
    
    public void setBarrio(Barrio barrio) {
        this.barrio = barrio;
    }
    
    public List<CoordenadasDTO> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<CoordenadasDTO> coordenadas) {
        this.coordenadas = coordenadas;
    }
    
    public boolean isValid() {
        return barrio != null && barrio.getName() != null && !barrio.getName().trim().isEmpty() 
            && coordenadas != null && coordenadas.size() >= 3;
    }
}
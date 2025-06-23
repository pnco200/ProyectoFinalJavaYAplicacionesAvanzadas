package asis.dto;

import java.util.List;

public class ZonaDTO {
    private String color;
    private Long barrioId;
    private List<CoordenadasDTO> coordenadas;
    
    public ZonaDTO() {
    }
    
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
public Long getBarrioId() {
        return barrioId;
    }
public void setBarrioId(Long barrioId) {
        this.barrioId = barrioId;
    }
    
    public List<CoordenadasDTO> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<CoordenadasDTO> coordenadas) {
        this.coordenadas = coordenadas;
    }
    
    public boolean isValid() {
        return color != null && color.trim().isEmpty() 
            && barrioId != null
            && coordenadas != null && coordenadas.size() >= 3;
    }
}
package asis.dto;

import java.util.List;

public class BarrioDTO {
    private String nombre;
    private List<CoordenadasDTO> coordenadas;
    
    public BarrioDTO() {
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public List<CoordenadasDTO> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<CoordenadasDTO> coordenadas) {
        this.coordenadas = coordenadas;
    }
    
    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty() 
            && coordenadas != null && coordenadas.size() >= 3;
    }
}
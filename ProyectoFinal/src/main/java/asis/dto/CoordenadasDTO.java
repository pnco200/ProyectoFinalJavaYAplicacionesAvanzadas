package asis.dto;

public class CoordenadasDTO {
    private Float latitud;
    private Float longitud;
    private Long zonaId; 
    private Long barrioId; 

    
    public CoordenadasDTO() {
    }
    
    public CoordenadasDTO(Float latitud, Float longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
    public Float getLatitud() {
        return latitud;
    }
    
    public void setLatitud(Float latitud) {
        this.latitud = latitud;
    }
    
    public Float getLongitud() {
        return longitud;
    }
    
    public void setLongitud(Float longitud) {
        this.longitud = longitud;
    }

    public Long getZonaId() {
        return zonaId;
    }
    public void setZonaId(Long zonaId) {
        this.zonaId = zonaId;
    }
    public Long getBarrioId() {
        return barrioId;
    }
    public void setBarrioId(Long barrioId) {
        this.barrioId = barrioId;
    }
}
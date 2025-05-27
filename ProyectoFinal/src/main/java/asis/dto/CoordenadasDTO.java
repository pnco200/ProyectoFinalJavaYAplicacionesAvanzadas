package asis.dto;

public class CoordenadasDTO {
    private Float latitud;
    private Float longitud;
    
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
}
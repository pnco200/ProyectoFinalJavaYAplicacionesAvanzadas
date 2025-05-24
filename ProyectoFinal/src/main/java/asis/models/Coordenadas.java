package asis.models;
import jakarta.persistence.*;
@Entity
public class Coordenadas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float latitud;
    private Float longitud;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;
    
    @ManyToOne
    @JoinColumn(name = "barrio_id")
    private Barrio barrio;
    
    public Coordenadas() {
    }
    
    // Constructor for Barrio
    public Coordenadas(Float latitud, Float longitud, Barrio barrio) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.barrio = barrio;
        this.zona = null;
    }
    
    // Constructor for Zona
    public Coordenadas(Float latitud, Float longitud, Zona zona) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.zona = zona;
        this.barrio = null;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Zona getZona() {
        return zona;
    }
    
    public void setZona(Zona zona) {
        this.zona = zona;
        if (zona != null) {
            this.barrio = null;
        }
    }
    
    public Barrio getBarrio() {
        return barrio;
    }
    
    public void setBarrio(Barrio barrio) {
        this.barrio = barrio;
        if (barrio != null) {
            this.zona = null;
        }
    }
}
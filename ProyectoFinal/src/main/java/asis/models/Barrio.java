package asis.models;
import java.util.List;
import jakarta.persistence.*;
@Entity
public class Barrio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "barrio")
    private List<Zona> zonas;
    
    @OneToMany(mappedBy = "barrio")
    private List<Coordenadas> coordenadas;

    @OneToMany(mappedBy = "barrio")
    private List<Usuario> organizaciones;
    
    public Barrio() {
    }
    
    public Barrio(String name) {
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Zona> getZonas() {
        return zonas;
    }
    
    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }
    
    public List<Coordenadas> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<Coordenadas> coordenadas) {
        this.coordenadas = coordenadas;
    }
    
    public List<Usuario> getOrganizaciones() {
        return organizaciones;
    }
    
    public void setOrganizaciones(List<Usuario> organizaciones) {
        this.organizaciones = organizaciones;
    }
}
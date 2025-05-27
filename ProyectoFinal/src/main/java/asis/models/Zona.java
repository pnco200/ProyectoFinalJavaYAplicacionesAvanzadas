package asis.models;
import java.util.List;
import jakarta.persistence.*;
@Entity
public class Zona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String color;

    @ManyToOne
    @JoinColumn(name = "barrio_id")
    private Barrio barrio;

    @OneToMany(mappedBy = "zona", fetch = FetchType.LAZY)
    private List<Coordenadas> coordenadas;
    
    public Zona() {
    }
    
    public Zona(String color, Barrio barrio) {
        this.color = color;
        this.barrio = barrio;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public Barrio getBarrio() {
        return barrio;
    }
    
    public void setBarrio(Barrio barrio) {
        this.barrio = barrio;
    }
    
    public List<Coordenadas> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<Coordenadas> coordenadas) {
        this.coordenadas = coordenadas;
    }
}
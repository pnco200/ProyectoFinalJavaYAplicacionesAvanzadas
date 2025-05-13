package asis.models;
import java.util.List;
import javax.persistence.*;
@Entity
public class Barrio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "barrio")
    private List<Zona> zonas;
    
    @OneToMany(mappedBy = "zona")
    private List<Coordenadas> coordenadas;
    
}
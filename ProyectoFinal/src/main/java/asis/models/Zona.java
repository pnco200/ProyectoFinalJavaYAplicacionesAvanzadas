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

    @OneToMany(mappedBy = "zona")
    private List<Coordenadas> coordenadas;
}
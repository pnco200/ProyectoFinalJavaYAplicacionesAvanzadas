package asis.models;
import java.util.*;
import jakarta.persistence.*;

@Entity
public class Jornada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "campaña_id")
    private Campaña campaña;

    @ManyToMany
    @JoinTable(
        name = "jornada_encuestador",
        joinColumns = @JoinColumn(name = "jornada_id"),
        inverseJoinColumns = @JoinColumn(name = "encuestador_id")
    )
    private Set<Encuestador> encuestadores = new HashSet<>();
    
}
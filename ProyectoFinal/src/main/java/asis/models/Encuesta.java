package asis.models;
import java.util.List;

import jakarta.persistence.*;
@Entity
public class Encuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "encuesta")
    private List<Renglon> renglones;

    @OneToMany(mappedBy = "vivienda")
    private List<Encuesta> personas;
    
    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Encuesta vivienda;

    @ManyToOne
    @JoinColumn(name = "campaña_id")
    private Campaña campaña;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEncuesta tipo;

    public enum TipoEncuesta {
        PERSONA,
        VIVIENDA,
    }
}
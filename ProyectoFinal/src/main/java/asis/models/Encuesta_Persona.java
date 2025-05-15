package asis.models;
import java.util.List;

import javax.persistence.*;
@Entity
public class Encuesta_Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "vivienda")
    private List<Renglon> renglones;

    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Encuesta_Vivienda viviendas;

    @ManyToOne
    @JoinColumn(name = "campaña_id")
    private Campaña campaña;
}
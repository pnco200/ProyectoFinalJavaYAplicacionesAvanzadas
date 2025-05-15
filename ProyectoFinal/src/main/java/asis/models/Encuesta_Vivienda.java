package asis.models;
import java.util.List;

import javax.persistence.*;
@Entity
public class Encuesta_Vivienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "vivienda")
    private List<Renglon> renglones;

    @OneToMany(mappedBy = "viviendas")
    private List<Encuesta_Persona> personas;

}
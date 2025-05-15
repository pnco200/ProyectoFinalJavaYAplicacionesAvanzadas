package asis.models;
import javax.persistence.*;
@Entity
public class Encuesta_Vivienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "vivienda")
    private Renglon renglones;

    @OneToMany(mappedBy = "viviendas")
    private Encuesta_Vivienda personas;
}
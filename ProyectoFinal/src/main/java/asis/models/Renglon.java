package asis.models;
import javax.persistence.*;
@Entity
public class Renglon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String valor;

    @Column(unique = true, nullable = true)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    private Encuesta_Persona persona;

    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Encuesta_Vivienda vivienda;
}
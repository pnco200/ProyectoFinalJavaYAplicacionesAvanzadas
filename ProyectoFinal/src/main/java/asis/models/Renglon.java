package asis.models;
import javax.persistence.*;
@Entity
public class Renglon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) //No tiene sentido crear el renglon si el campo esta vacio directamente no se crea, no es que se crea y lo dejamos nulo
    private String valor;

    @Column(nullable = false)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "encuesta_id")
    private Encuesta encuesta;

}
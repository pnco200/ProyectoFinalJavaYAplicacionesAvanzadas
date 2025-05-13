package asis.models;
import javax.persistence.*;
@Entity
public class Encuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "campaña_id")
    private Campaña campaña;
}
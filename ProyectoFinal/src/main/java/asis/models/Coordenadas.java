package asis.models;
import javax.persistence.*;
@Entity
public class Coordenadas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float latitud;
    private Float longitud;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;
    
    @ManyToOne
    @JoinColumn(name = "barrio_id")
    private Barrio barrio;
}


package asis.models;
import java.util.*;
import java.util.Date;
import javax.persistence.*;

@Entity
public class Encuestador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String dni;

    private Date birthDate;
    private String sex;
    private String occupation;


    @ManyToMany(mappedBy = "encuestadores")
    private Set<Jornada> jornadas = new HashSet<>();
    
}


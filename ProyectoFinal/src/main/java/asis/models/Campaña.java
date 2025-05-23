package asis.models;
import jakarta.persistence.*;
import java.util.List;
import java.util.Date;
@Entity
public class Campaña {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;

    @OneToMany(mappedBy = "campaña")
    private List<Jornada> jornadas;
    
    @OneToMany(mappedBy = "campaña")
    private List<Encuesta> encuestas;
    
    public Long getId() {
		return id;
	}
}
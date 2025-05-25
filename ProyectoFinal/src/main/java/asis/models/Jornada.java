package asis.models;
import java.util.*;
import jakarta.persistence.*;

@Entity
public class Jornada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "campaña_id")
    private Campaña campaña;

    @ManyToMany
    @JoinTable(
        name = "jornada_encuestador",
        joinColumns = @JoinColumn(name = "jornada_id"),
        inverseJoinColumns = @JoinColumn(name = "encuestador_id")
    )
    private Set<Encuestador> encuestadores = new HashSet<>();
    public Jornada() {
	}
    public Jornada(String name, Date startDate, Date endDate) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Campaña getCampaña() {
        return campaña;
    }

    public void setCampaña(Campaña campaña) {
        this.campaña = campaña;
    }

    public Set<Encuestador> getEncuestadores() {
        return encuestadores;
    }

    public void setEncuestadores(Set<Encuestador> encuestadores) {
        this.encuestadores = encuestadores;
    }

}
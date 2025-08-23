package asis.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Campaña {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Date startDate;
    private Date endDate;

    @OneToMany(mappedBy = "campaña", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Jornada> jornadas = new ArrayList<>();

    @OneToMany(mappedBy = "campaña", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Encuesta> encuestas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "barrio_id")
    private Barrio barrio;

    // --- Getters escalares
    public Long getId() { return id; }
    public String getName() { return name; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public Barrio getBarrio() { return barrio; }

    // --- Setters escalares
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public void setBarrio(Barrio barrio) { this.barrio = barrio; }

    // --- Colecciones: solo getter (sin setters) y helpers
    public List<Jornada> getJornadas() { return jornadas; }
    public List<Encuesta> getEncuestas() { return encuestas; }

    public void addJornada(Jornada j) {
        jornadas.add(j);
        j.setCampaña(this);
    }
    public void removeJornada(Jornada j) {
        jornadas.remove(j);
        j.setCampaña(null);
    }
    public void addEncuesta(Encuesta e) {
        encuestas.add(e);
        e.setCampaña(this);
    }
    public void removeEncuesta(Encuesta e) {
        encuestas.remove(e);
        e.setCampaña(null);
    }
}

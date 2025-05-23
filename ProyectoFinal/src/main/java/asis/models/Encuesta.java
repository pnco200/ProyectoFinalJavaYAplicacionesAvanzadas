package asis.models;
import java.util.List;

import jakarta.persistence.*;
@Entity
public class Encuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "encuesta")
    private List<Renglon> renglones;

    @OneToMany(mappedBy = "vivienda")
    private List<Encuesta> personas;
    
    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Encuesta vivienda;

    @ManyToOne
    @JoinColumn(name = "campaña_id")
    private Campaña campaña;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEncuesta tipo;

    public enum TipoEncuesta {
        PERSONA,
        VIVIENDA,
    }
    
    public Long getId() {
		return id;
	}

    public void setTipo(TipoEncuesta tipo) {
        this.tipo = tipo;
    }
    
    public TipoEncuesta getTipo() {
        return tipo;
    }

    public void setVivienda(Encuesta vivienda) {
        this.vivienda = vivienda;
    }

    public Encuesta getVivienda() {
        return vivienda;
    }

    public void setCampaña(Campaña campaña) {
        this.campaña = campaña;
    }

    public Campaña getCampaña() {
        return campaña;
    }
    
    public void setRenglones(List<Renglon> renglones) {
        this.renglones = renglones;
    }

    public List<Renglon> getRenglones() {
        return renglones;
    }

    public void setPersonas(List<Encuesta> personas) {
        this.personas = personas;
    }

    public List<Encuesta> getPersonas() {
        return personas;
    }


    public void addPersona(Encuesta persona) {
        if (this.tipo != TipoEncuesta.VIVIENDA) {
            throw new IllegalStateException("Solo encuestas de tipo VIVIENDA pueden tener personas");
        }
        if (persona.getTipo() != TipoEncuesta.PERSONA) {
            throw new IllegalStateException("Solo se pueden agregar encuestas de tipo PERSONA");
        }
        
        this.personas.add(persona);
        persona.setVivienda(this);
    }

    public void removePersona(Encuesta persona) {
        this.personas.remove(persona);
        persona.setVivienda(null);
    }

    public void addRenglon(Renglon renglon) {
        this.renglones.add(renglon);
        renglon.setEncuesta(this);
    }

    public void removeRenglon(Renglon renglon) {
        this.renglones.remove(renglon);
        renglon.setEncuesta(null);
    }

}
package asis.models;
import java.time.LocalDate;
import java.util.*;

import jakarta.persistence.*;

@Entity
public class Encuestador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String dni;

    private LocalDate birthDate;
    private String sex;
    private String occupation;


    @ManyToMany(mappedBy = "encuestadores")
    private Set<Jornada> jornadas = new HashSet<>();
    
    public Encuestador() {
	}

    public Encuestador(Long id) { //Usado para pasarle al DAO el id direcatamente
        this.id = id;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Set<Jornada> getJornadas() {
        return jornadas;
    }

    public void setJornadas(Set<Jornada> jornadas) {
        this.jornadas = jornadas;
    }

}


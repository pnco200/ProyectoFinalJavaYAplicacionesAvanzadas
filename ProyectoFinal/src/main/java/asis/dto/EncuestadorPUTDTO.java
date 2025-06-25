package asis.dto;

import java.io.Serializable;

public class EncuestadorPUTDTO implements Serializable {
    private Long id;
    private String name;
    private String dni;
    private String birthDate; // Formato yyyy-MM-dd como texto
    private String sex;
    private String occupation;

    public EncuestadorPUTDTO() {
    }

    public EncuestadorPUTDTO(Long id, String name, String dni, String birthDate, String sex, String occupation) {
        this.id = id;
        this.name = name;
        this.dni = dni;
        this.birthDate = birthDate;
        this.sex = sex;
        this.occupation = occupation;
    }

    // Getters y setters

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
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
}

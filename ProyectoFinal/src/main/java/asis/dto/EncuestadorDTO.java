package asis.dto;

import java.time.LocalDate;
import java.util.Date;

public class EncuestadorDTO {
    private String name;
    private String dni;
    private LocalDate birthDate;
    private String sex;
    private String occupation;

    public EncuestadorDTO() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
}

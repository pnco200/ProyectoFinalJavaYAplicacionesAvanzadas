package asis.dto;

import java.time.LocalDate;
import java.util.Date;



public class EncuestadorOutDTO {
	public String name;
	public String dni;
    public LocalDate birthDate;
    public String sex;
    public String occupation;
    public Long id;
    
    public EncuestadorOutDTO() {
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
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}
    
}

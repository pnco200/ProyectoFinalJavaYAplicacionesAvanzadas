package asis.models;
import javax.persistence.*;

@Entity
@DiscriminatorValue("PERSONAL_SALUD")
public class PersonalSalud extends Usuario {
}

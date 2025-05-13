package asis.models;
import javax.persistence.*;

@Entity
@DiscriminatorValue("ORGANIZACION_SOCIAL")
public class OrganizacionSocial extends Usuario {
    private String domicilio;
    private String activity;
    private String extra;


}
package asis.dto;

import java.util.Set;

public class UsuarioDTO {
    public Long id;
    public String mail;
    public String name;
    public boolean is_active;
    public String domicilio;
    public String activity;
    public String extra;
    public String barrio;           // nombre del barrio o null
    public Set<String> roles;       // nombres de los roles
}

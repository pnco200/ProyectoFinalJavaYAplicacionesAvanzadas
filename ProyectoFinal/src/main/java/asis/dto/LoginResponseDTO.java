package asis.dto;

import java.util.Set;

public class LoginResponseDTO {
    public Long id;
    public String mail;
    public String name;
    public Set<String> roles;
    public boolean is_active;
}
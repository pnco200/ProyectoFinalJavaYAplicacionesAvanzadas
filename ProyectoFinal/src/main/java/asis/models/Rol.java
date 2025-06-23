package asis.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRol nombre;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore 
    private Set<Usuario> usuarios = new HashSet<>();

    public enum TipoRol {
        ADMINISTRADOR,
        ORGANIZACION_SOCIAL,
        PERSONAL_SALUD
    }

    public Rol() {}

    public Rol(TipoRol nombre) {
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoRol getNombre() { return nombre; }
    public void setNombre(TipoRol nombre) { this.nombre = nombre; }

    public Set<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(Set<Usuario> usuarios) { this.usuarios = usuarios; }

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
        usuario.getRoles().add(this);
    }

    public void removeUsuario(Usuario usuario) {
        this.usuarios.remove(usuario);
        usuario.getRoles().remove(this);
    }
}

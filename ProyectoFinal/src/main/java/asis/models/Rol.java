package asis.models;

import javax.persistence.*;

import java.util.Set;

@Entity
public abstract class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRol nombre;

    @ManyToMany(mappedBy = "roles")
    private Set<Usuario> usuarios;

    public enum TipoRol {
        ADMINISTRADOR,
        ORGANIZACION_SOCIAL,
        PERSONAL_SALUD   
    }
}
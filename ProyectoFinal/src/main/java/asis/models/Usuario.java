package asis.models;

import javax.persistence.*;

@Entity
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String mail;

    private String password;
    private String name;

    @Column(nullable = false)
    private boolean is_active = false;

    @Column(nullable = true)
    private String domicilio;
    @Column(nullable = true)
    private String activity;
    @Column(nullable = true)
    private String extra;

    @ManyToOne
    @JoinColumn(name = "barrio_id")
    private Barrio barrio;

    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

}
package asis.models;

import java.util.Set;
import java.util.HashSet;

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
    
    @ManyToMany //Agregado el ManyToMany roles
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();
    
    public Usuario() {
	
    }
    
	public Usuario(String mail, String password, String name, boolean is_active, String domicilio, String activity, String extra) {
		this.mail = mail;
		this.password = password;
		this.name = name;
		this.is_active = is_active;
		this.domicilio = domicilio;
		this.activity = activity;
		this.extra = extra;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}
	public String getDomicilio() {
		return domicilio;
	}
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public Barrio getBarrio() {
	    return barrio;
	}

	public void setBarrio(Barrio barrio) {
	    this.barrio = barrio;
	}

	public Set<Rol> getRoles() {
	    return roles;
	}

	public void setRoles(Set<Rol> roles) {
	    this.roles = roles;
	}

	public void addRol(Rol rol) {
	    this.roles.add(rol);
	}

	public void removeRol(Rol rol) {
	    this.roles.remove(rol);
	}
}
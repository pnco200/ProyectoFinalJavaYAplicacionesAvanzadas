package asis.dto;


public class RolDTO {
    private Long id;
    private String nombre; // TipoRol a String para chequeo en el put

    public RolDTO(Long id2, String name) {
        this.id = id2;
        this.nombre = name;
	}
    public RolDTO() {}
	public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

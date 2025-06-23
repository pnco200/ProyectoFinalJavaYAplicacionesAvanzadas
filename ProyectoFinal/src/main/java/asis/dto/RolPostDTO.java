package asis.dto;

import asis.models.Rol.TipoRol;

// DTO usado para el cuerpo de la solicitud (request body) al crear o actualizar un Rol.
// Solo contiene los campos que el cliente debe proveer.
public class RolPostDTO {
    private String nombre; // String, no TipoRol para poder chequear en la deserialziacion

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

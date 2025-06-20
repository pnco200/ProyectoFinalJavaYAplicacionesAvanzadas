package asis.dto;

import asis.models.Rol.TipoRol;

public class RolDTO {
    private Long id;
    private TipoRol tipo;

    public RolDTO() {}

    public RolDTO(Long id, TipoRol tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public TipoRol getTipo() {
        return tipo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipo(TipoRol tipo) {
        this.tipo = tipo;
    }
}

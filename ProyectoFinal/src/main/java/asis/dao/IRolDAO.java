package asis.dao;

import asis.models.Rol;
import asis.models.Usuario;
import java.util.List;

public interface IRolDAO {
    Rol save(Rol rol);
    Rol findById(Long id);
    List<Rol> findAll();
    Rol update(Rol rol);
    void delete(Rol rol);
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Rol> findByTipoRol(Rol.TipoRol tipoRol);
    List<Usuario> findUsersByRolId(Long rolId);
    void addUsuario(Long rolId, Usuario usuario);
    void removeUsuario(Long rolId, Usuario usuario);
}
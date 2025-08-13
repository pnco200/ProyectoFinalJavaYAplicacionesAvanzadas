package asis.dao;

import asis.models.Usuario;
import java.util.List;

public interface IUsuarioDAO {
    Usuario save(Usuario usuario);
    
    Usuario findById(Long id);
    List<Usuario> findAll();
    Usuario findByEmail(String email);
    
    Usuario update(Usuario usuario) throws Exception;
    
    void delete(Usuario usuario);
    void deleteById(Long id);
    
    boolean existsById(Long id);
    boolean existsByEmail(String email);

	    boolean assignBarrioToUsuario(Long userId, Long barrioId);
    boolean assignRolToUser(Long userId, Long rolId);
    boolean updateIsActive(Long userId, boolean isActive);
}

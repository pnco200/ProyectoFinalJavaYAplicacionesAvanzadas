package asis.dao;

import asis.models.Zona;
import java.util.List;

public interface IZonaDAO {
    
    Zona save(Zona zona);
    
    Zona findById(Long id);
    
    List<Zona> findAll();
    
    List<Zona> findByBarrioId(Long barrioId);
    
    Zona update(Zona zona) throws Exception;
    
    void delete(Zona zona);
    
    void deleteById(Long id);

    void deleteByBarrioId(Long barrioId);
    
    boolean existsById(Long id);
}

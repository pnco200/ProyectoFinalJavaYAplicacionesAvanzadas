package asis.dao;

import asis.models.Barrio;
import java.util.List;

public interface IBarrioDAO {
    
    Barrio save(Barrio barrio);
    
    Barrio findById(Long id);
    
    List<Barrio> findAll();
    
    Barrio update(Barrio barrio) throws Exception;
    
    void delete(Barrio barrio);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByName(String name);
}
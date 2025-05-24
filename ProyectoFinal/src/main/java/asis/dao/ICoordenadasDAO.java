package asis.dao;

import asis.models.Coordenadas;
import java.util.List;

public interface ICoordenadasDAO {
    
    Coordenadas save(Coordenadas coordenadas);
    
    Coordenadas findById(Long id);
    
    List<Coordenadas> findAll();
    
    List<Coordenadas> findByBarrioId(Long barrioId);
    
    List<Coordenadas> findByZonaId(Long zonaId);
    
    Coordenadas update(Coordenadas coordenadas) throws Exception;
    
    void delete(Coordenadas coordenadas);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    void deleteByBarrioId(Long barrioId);
    
    void deleteByZonaId(Long zonaId);
}
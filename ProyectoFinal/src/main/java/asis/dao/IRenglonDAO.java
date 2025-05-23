package asis.dao;

import asis.models.Renglon;
import java.util.List;

public interface IRenglonDAO {
    Renglon save(Renglon renglon);
    
    Renglon findById(Long id);
    List<Renglon> findAll();
    List<Renglon> findByEncuestaId(Long encuestaId);
    List<Renglon> findByTipo(String tipo);
    
    Renglon update(Renglon renglon);
    
    void delete(Renglon renglon);
    void deleteById(Long id);
    
    boolean existsById(Long id);
}
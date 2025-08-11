package asis.dao;

import asis.models.Renglon;
import java.util.List;

public interface IRenglonDAO {
    
    Renglon save(Renglon renglon);
    
    Renglon findById(Long id);
    
    List<Renglon> findAll();
    
    Renglon update(Renglon renglon) throws Exception;
    
    void delete(Renglon renglon);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);

    List<String> findDistinctTipos();

    List<String> findDistinctValoresByTipo(String tipo);
}

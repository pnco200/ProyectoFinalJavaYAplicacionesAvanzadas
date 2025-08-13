package asis.dao;

import asis.models.Renglon;
import java.util.List;
import java.util.Map;

public interface IRenglonDAO {
    
    Renglon save(Renglon renglon);
    
    Renglon findById(Long id);
    
    List<Renglon> findAll();
    
    Renglon update(Renglon renglon) throws Exception;
    
    void delete(Renglon renglon);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);

    List<Object[]> findEncuestasByFiltros(List<Map<String, String>> filtrosVivienda,
            List<Map<String, String>> filtrosPersona);
    
    List<Object[]> findRespuestasByEncuestaId(Long encuestaId);

    List<Map<String, Object>> findChartDataByEncuestaIds(List<Long> encuestaIds);
    
    List<Object[]> findDistinctTipos();
    

    List<String> findDistinctValoresByTipo(String tipo);

	List<Renglon> findByTipo(String tipo);

	List<Renglon> findByEncuestaId(Long encuestaId);
}

package asis.dao;

import asis.models.Encuesta;
import asis.models.Encuesta.TipoEncuesta;
import java.util.List;

public interface IEncuestaDAO {
    Encuesta save(Encuesta encuesta);
    
    Encuesta findById(Long id);
    List<Encuesta> findAll();
    List<Encuesta> findByTipo(TipoEncuesta tipo);
    List<Encuesta> findByViviendaId(Long viviendaId);
    List<Encuesta> findByCampañaId(Long campañaId);
    void deleteAllByCampañaId(Long id);
    Encuesta update(Encuesta encuesta);
    
    void delete(Encuesta encuesta);
    void deleteById(Long id);
    
    Encuesta addRenglonToEncuesta(int encuestaId, int renglonId);
    
    boolean existsById(Long id);
}
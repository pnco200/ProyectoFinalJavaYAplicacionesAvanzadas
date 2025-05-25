package asis.dao;

import asis.exceptions.JornadaException;
import asis.models.Encuestador;
import asis.models.Jornada;

import java.util.Date;
import java.util.List;

public interface IJornadaDAO {
    Jornada save(Jornada jornada) throws JornadaException;
    Jornada findById(Long id);
    List<Jornada> findAll();
    void deleteById(Long id);
    List<Jornada> findByName(String name);
    List<Jornada> findByStartDateBetween(Date startDate, Date endDate);
    void addEncuestador(Long jornadaId, Encuestador encuestador) throws JornadaException;
    void removeEncuestador(Long jornadaId, Encuestador encuestador) throws JornadaException;
    List<Encuestador> getEncuestadoresByJornada(Long jornadaId) throws JornadaException;

}

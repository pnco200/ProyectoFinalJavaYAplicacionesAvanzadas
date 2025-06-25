package asis.dao;

import asis.exceptions.EncuestadorException;
import asis.models.Encuestador;

import java.util.List;

public interface IEncuestadorDAO {
    Encuestador save(Encuestador encuestador) throws EncuestadorException;
    Encuestador findById(Long id);
    List<Encuestador> findAll();
    void deleteById(Long id) throws EncuestadorException;
    //List<Encuestador> findByName(String name);
    Encuestador findByDni(String dni);
    Encuestador update(Encuestador encuestador) throws EncuestadorException;
}

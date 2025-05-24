package asis.dao;

import asis.exceptions.CampañaException;
import asis.models.Campaña;
import java.util.List;

public interface ICampañaDAO {
    Campaña save(Campaña campaña) throws CampañaException;

    Campaña findById(Long id);

    List<Campaña> findAll();

    void deleteById(Long id);

    List<Campaña> findByName(String name);

    List<Campaña> findByStartDateBetween(java.util.Date startDate, java.util.Date endDate);
}

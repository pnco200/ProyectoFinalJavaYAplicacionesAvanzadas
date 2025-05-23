package asis.dao;

import asis.models.Campaña;
import java.util.List;

public interface ICampañaDAO {
	Campaña save(Campaña campaña);
    
    Campaña findById(Long id);

}
package asis.dao;

import asis.exceptions.EncuestadorException;
import asis.models.Encuestador;
import asis.utils.JPAUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class EncuestadorDAO implements IEncuestadorDAO {
	@Inject
	private EntityManager em;
	
	@Override
	public Encuestador save(Encuestador encuestador) throws EncuestadorException {
	    if (encuestador.getDni() == null || encuestador.getDni().trim().isEmpty()) {
	        throw new EncuestadorException("El DNI del encuestador no puede estar vacio.");
	    }
	    EntityTransaction tx = em.getTransaction();

	    try {
	        tx.begin();
	        TypedQuery<Encuestador> query = em.createQuery(
	            "SELECT e FROM Encuestador e WHERE e.dni = :dni", Encuestador.class);
	        query.setParameter("dni", encuestador.getDni());
	        List<Encuestador> existentes = query.getResultList();

	        if (!existentes.isEmpty()) {
	            if (encuestador.getId() == null || !existentes.get(0).getId().equals(encuestador.getId())) {
	                throw new EncuestadorException("Ya existe un encuestador con ese DNI.");
	            }
	        }

	        if (encuestador.getId() == null) {
	            em.persist(encuestador);
	        } else {
	            encuestador = em.merge(encuestador);
	        }

	        tx.commit();
	        return encuestador;
	    } catch (EncuestadorException e) {
	        if (tx.isActive()) tx.rollback();
	        throw e;
	    } catch (Exception e) {
	        if (tx.isActive()) tx.rollback();
	        e.printStackTrace();
	        throw new EncuestadorException("Error al guardar el encuestador.");
	    } finally {
	        em.close();
	    }
	}


    @Override
    public Encuestador findById(Long id) {
        try {
            return em.find(Encuestador.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Encuestador> findAll() {
        try {
            TypedQuery<Encuestador> query = em.createQuery(
                "SELECT e FROM Encuestador e", Encuestador.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) throws EncuestadorException {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Encuestador encuestador = em.find(Encuestador.class, id);
            if (encuestador != null) {
                encuestador.getJornadas().forEach(j -> j.getEncuestadores().remove(encuestador));
                em.remove(encuestador);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new EncuestadorException("Error al eliminar el encuestador.");
        } finally {
            em.close();
        }
    }

    /*@Override
    public List<Encuestador> findByName(String name) {
        try {
            TypedQuery<Encuestador> query = em.createQuery(
                "SELECT e FROM Encuestador e WHERE e.name LIKE :name", Encuestador.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }*/

    @Override
    public Encuestador findByDni(String dni) {
        try {
            TypedQuery<Encuestador> query = em.createQuery(
                "SELECT e FROM Encuestador e WHERE e.dni = :dni", Encuestador.class);
            query.setParameter("dni", dni);
            List<Encuestador> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }
}

package asis.dao;

import asis.models.Zona;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class ZonaDAO implements IZonaDAO {

    @Inject
    private EntityManager em;

    @Override
    public Zona save(Zona zona) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(zona);
            tx.commit();
            return zona;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Zona findById(Long id) {
        return em.find(Zona.class, id);
    }

    @Override
    public List<Zona> findAll() {
        TypedQuery<Zona> query = em.createQuery("SELECT z FROM Zona z", Zona.class);
        return query.getResultList();
    }

    @Override
    public List<Zona> findByBarrioId(Long barrioId) {
        TypedQuery<Zona> query = em.createQuery(
            "SELECT z FROM Zona z WHERE z.barrio.id = :barrioId", Zona.class);
        query.setParameter("barrioId", barrioId);
        return query.getResultList();
    }

    @Override
    public Zona update(Zona zona) throws Exception {
        EntityTransaction tx = em.getTransaction();
        try {
            Zona existente = em.find(Zona.class, zona.getId());
            if (existente == null) {
                throw new Exception("Zona no encontrada.");
            }

            tx.begin();
            Zona merged = em.merge(zona);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public void delete(Zona zona) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Zona managed = zona;
            if (!em.contains(zona)) {
                managed = em.merge(zona);
            }
            em.remove(managed);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Zona zona = em.find(Zona.class, id);
            if (zona != null) {
                em.remove(zona);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }
}

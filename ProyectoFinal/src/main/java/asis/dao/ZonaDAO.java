package asis.dao;

import asis.models.Zona;
import asis.models.Barrio;
import asis.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ZonaDAO implements IZonaDAO {

    @Override
    public Zona save(Zona zona) {
        EntityManager em = JPAUtil.getEntityManager();
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
        } finally {
            em.close();
        }
    }

    @Override
    public Zona findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Zona.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Zona> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Zona> query = em.createQuery("SELECT z FROM Zona z", Zona.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Zona> findByBarrioId(Long barrioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Zona> query = em.createQuery(
                    "SELECT z FROM Zona z WHERE z.barrio.id = :barrioId", Zona.class);
            query.setParameter("barrioId", barrioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Zona update(Zona zona) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        Zona merged = null;
        try {
            Zona existente = em.find(Zona.class, zona.getId());
            if (existente == null) {
                throw new Exception("Zona no encontrada.");
            }

            tx.begin();
            merged = em.merge(zona);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Zona zona) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Zona managedZona = zona;
            if (!em.contains(zona)) {
                managedZona = em.merge(zona);
            }
            em.remove(managedZona);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
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
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }
}
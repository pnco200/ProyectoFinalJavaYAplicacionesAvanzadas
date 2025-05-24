package asis.dao;

import asis.models.Barrio;
import asis.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class BarrioDAO implements IBarrioDAO {

    @Override
    public Barrio save(Barrio barrio) {
        if (this.existsByName(barrio.getName())) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(barrio);
            tx.commit();
            return barrio;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Barrio findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Barrio.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Barrio> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Barrio> query = em.createQuery("SELECT b FROM Barrio b", Barrio.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Barrio update(Barrio barrio) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        Barrio merged = null;
        try {
            Barrio existente = em.find(Barrio.class, barrio.getId());
            if (existente == null) {
                throw new Exception("Barrio no encontrado.");
            }

            if (barrio.getName() != null && !barrio.getName().equals(existente.getName())) {
                if (isNameUsedByAnother(barrio.getId(), barrio.getName())) {
                    throw new Exception("El nombre ya está en uso por otro barrio.");
                }
            }

            tx.begin();
            merged = em.merge(barrio);
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
    public void delete(Barrio barrio) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Barrio managedBarrio = barrio;
            if (!em.contains(barrio)) {
                managedBarrio = em.merge(barrio);
            }
            em.remove(managedBarrio);
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
            Barrio barrio = em.find(Barrio.class, id);
            if (barrio != null) {
                em.remove(barrio);
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

    @Override
    public boolean existsByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(b) FROM Barrio b WHERE b.name = :name", Long.class);
            query.setParameter("name", name);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    private boolean isNameUsedByAnother(Long barrioId, String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Barrio b WHERE b.name = :name AND b.id != :id", Long.class);
            query.setParameter("name", name);
            query.setParameter("id", barrioId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}
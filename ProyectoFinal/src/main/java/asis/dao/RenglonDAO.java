package asis.dao;

import asis.models.Renglon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import asis.utils.JPAUtil;

public class RenglonDAO implements IRenglonDAO {

    @Override
    public Renglon save(Renglon renglon) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(renglon);
            tx.commit();
            return renglon;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Renglon findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Renglon.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Renglon> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Renglon> query = em.createQuery("SELECT r FROM Renglon r", Renglon.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Renglon> findByEncuestaId(Long encuestaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Renglon> query = em.createQuery(
                "SELECT r FROM Renglon r WHERE r.encuesta.id = :encuestaId", Renglon.class);
            query.setParameter("encuestaId", encuestaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Renglon> findByTipo(String tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Renglon> query = em.createQuery(
                "SELECT r FROM Renglon r WHERE r.tipo = :tipo", Renglon.class);
            query.setParameter("tipo", tipo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Renglon update(Renglon renglon) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Renglon merged = em.merge(renglon);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Renglon renglon) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Renglon managedRenglon = renglon;
            if (!em.contains(renglon)) {
                managedRenglon = em.merge(renglon);
            }
            em.remove(managedRenglon);
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
            Renglon renglon = em.find(Renglon.class, id);
            if (renglon != null) {
                em.remove(renglon);
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
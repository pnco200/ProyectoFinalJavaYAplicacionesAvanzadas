package asis.dao;

import asis.models.Campaña;
import asis.models.Encuesta;
import asis.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class CampañaDAO implements ICampañaDAO {

    @Override
    public Campaña save(Campaña campaña) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(campaña);
            tx.commit();
            return campaña;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Campaña findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Campaña.class, id);
        } finally {
            em.close();
        }
    }
}
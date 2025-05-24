package asis.dao;

import asis.exceptions.CampañaException;
import asis.models.Campaña;
import asis.utils.JPAUtil; 

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class CampañaDAO implements ICampañaDAO {

    @Override
    public Campaña save(Campaña campaña) throws CampañaException {
        // Valida que inicio no puede ser despues del fin
        if (campaña.getStartDate() != null && campaña.getEndDate() != null) {
            if (campaña.getStartDate().after(campaña.getEndDate())) {
                throw new CampañaException("La fecha de inicio es posterior a la fecha de fin.");
            }
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            String jpql = "SELECT c FROM Campaña c WHERE c.name = :name AND " +
                          "((c.startDate BETWEEN :startDate AND :endDate) OR " +
                          "(c.endDate BETWEEN :startDate AND :endDate) OR " +
                          "(:startDate BETWEEN c.startDate AND c.endDate))";

            // Si la campaña ya tiene id (update), excluir esa campaña de la busqueda
            if (campaña.getId() != null) {
                jpql += " AND c.id <> :id";
            }

            TypedQuery<Campaña> query = em.createQuery(jpql, Campaña.class);
            query.setParameter("name", campaña.getName());
            query.setParameter("startDate", campaña.getStartDate());
            query.setParameter("endDate", campaña.getEndDate());

            if (campaña.getId() != null) {
                query.setParameter("id", campaña.getId());
            }

            List<Campaña> conflictos = query.getResultList();
            if (!conflictos.isEmpty()) {
                throw new CampañaException("Ya existe una campaña con ese nombre y fechas solapadas.");
            }

            tx.begin();
            if (campaña.getId() == null) {
                em.persist(campaña);
            } else {
                campaña = em.merge(campaña);
            }
            tx.commit();
            return campaña;
        } catch (CampañaException e) {
            if (tx.isActive()) tx.rollback();
            throw e; 
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new CampañaException("Error interno al guardar la campaña.");
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

    @Override
    public List<Campaña> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Campaña> query = em.createQuery("SELECT c FROM Campaña c", Campaña.class);
            return query.getResultList();
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
            Campaña campaña = em.find(Campaña.class, id);
            if (campaña != null) {
                em.remove(campaña);
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
    public List<Campaña> findByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Campaña> query = em.createQuery(
                "SELECT c FROM Campaña c WHERE c.name LIKE :name", Campaña.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Campaña> findByStartDateBetween(Date startDate, Date endDate) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Campaña> query = em.createQuery(
                "SELECT c FROM Campaña c WHERE c.startDate BETWEEN :startDate AND :endDate", Campaña.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

package asis.dao;

import asis.models.Encuesta;
import asis.models.Encuesta.TipoEncuesta;
import asis.models.Renglon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import asis.utils.JPAUtil;

public class EncuestaDAO implements IEncuestaDAO {

    @Override
    public Encuesta save(Encuesta encuesta) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(encuesta);
            tx.commit();
            return encuesta;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Encuesta findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Encuesta.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Encuesta> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Encuesta> query = em.createQuery("SELECT e FROM Encuesta e", Encuesta.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Encuesta> findByTipo(TipoEncuesta tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Encuesta> query = em.createQuery(
                "SELECT e FROM Encuesta e WHERE e.tipo = :tipo", Encuesta.class);
            query.setParameter("tipo", tipo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Encuesta> findByViviendaId(Long viviendaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Encuesta> query = em.createQuery(
                "SELECT e FROM Encuesta e WHERE e.vivienda.id = :viviendaId", Encuesta.class);
            query.setParameter("viviendaId", viviendaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Encuesta> findByCampañaId(Long campañaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Encuesta> query = em.createQuery(
                "SELECT e FROM Encuesta e WHERE e.campaña.id = :campañaId", Encuesta.class);
            query.setParameter("campañaId", campañaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Encuesta update(Encuesta encuesta) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Encuesta merged = em.merge(encuesta);
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
    public void delete(Encuesta encuesta) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Encuesta managedEncuesta = encuesta;
            if (!em.contains(encuesta)) {
                managedEncuesta = em.merge(encuesta);
            }
            em.remove(managedEncuesta);
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
            Encuesta encuesta = em.find(Encuesta.class, id);
            if (encuesta != null) {
                em.remove(encuesta);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Encuesta addRenglonToEncuesta(int encuestaId, int renglonId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            Encuesta encuesta = em.find(Encuesta.class, encuestaId);
            Renglon renglon = em.find(Renglon.class, renglonId);

            if (encuesta == null || renglon == null) {
                return null;
            }
            
            renglon.setEncuesta(encuesta);
            encuesta.getRenglones().add(renglon);
            
            tx.commit();
            return encuesta;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }
}
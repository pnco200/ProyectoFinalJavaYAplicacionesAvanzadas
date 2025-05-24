package asis.dao;

import asis.models.Coordenadas;
import asis.models.Barrio;
import asis.models.Zona;
import asis.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CoordenadasDAO implements ICoordenadasDAO {

    @Override
    public Coordenadas save(Coordenadas coordenadas) {
        // Validate that either barrio or zona is set, but not both
        if ((coordenadas.getBarrio() == null && coordenadas.getZona() == null) || 
            (coordenadas.getBarrio() != null && coordenadas.getZona() != null)) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(coordenadas);
            tx.commit();
            return coordenadas;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Coordenadas findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Coordenadas.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Coordenadas> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Coordenadas> query = em.createQuery("SELECT c FROM Coordenadas c", Coordenadas.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Coordenadas> findByBarrioId(Long barrioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Coordenadas> query = em.createQuery(
                    "SELECT c FROM Coordenadas c WHERE c.barrio.id = :barrioId", Coordenadas.class);
            query.setParameter("barrioId", barrioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Coordenadas> findByZonaId(Long zonaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Coordenadas> query = em.createQuery(
                    "SELECT c FROM Coordenadas c WHERE c.zona.id = :zonaId", Coordenadas.class);
            query.setParameter("zonaId", zonaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Coordenadas update(Coordenadas coordenadas) throws Exception {
        // Validate that either barrio or zona is set, but not both
        if ((coordenadas.getBarrio() == null && coordenadas.getZona() == null) || 
            (coordenadas.getBarrio() != null && coordenadas.getZona() != null)) {
            throw new Exception("Una coordenada debe pertenecer a un barrio o a una zona, pero no a ambos.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        Coordenadas merged = null;
        try {
            Coordenadas existente = em.find(Coordenadas.class, coordenadas.getId());
            if (existente == null) {
                throw new Exception("Coordenada no encontrada.");
            }

            tx.begin();
            merged = em.merge(coordenadas);
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
    public void delete(Coordenadas coordenadas) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Coordenadas managedCoordenadas = coordenadas;
            if (!em.contains(coordenadas)) {
                managedCoordenadas = em.merge(coordenadas);
            }
            em.remove(managedCoordenadas);
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
            Coordenadas coordenadas = em.find(Coordenadas.class, id);
            if (coordenadas != null) {
                em.remove(coordenadas);
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
    public void deleteByBarrioId(Long barrioId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM Coordenadas c WHERE c.barrio.id = :barrioId")
                    .setParameter("barrioId", barrioId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteByZonaId(Long zonaId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM Coordenadas c WHERE c.zona.id = :zonaId")
                    .setParameter("zonaId", zonaId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
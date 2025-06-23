package asis.dao;

import asis.models.Coordenadas;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class CoordenadasDAO implements ICoordenadasDAO {

    @Inject
    private EntityManager em;

    @Override
    public Coordenadas save(Coordenadas coordenadas) {
        if ((coordenadas.getBarrio() == null && coordenadas.getZona() == null) ||
            (coordenadas.getBarrio() != null && coordenadas.getZona() != null)) {
            return null;
        }

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
        }
    }

    @Override
    public Coordenadas findById(Long id) {
        return em.find(Coordenadas.class, id);
    }

    @Override
    public List<Coordenadas> findAll() {
        TypedQuery<Coordenadas> query = em.createQuery("SELECT c FROM Coordenadas c", Coordenadas.class);
        return query.getResultList();
    }

    @Override
    public List<Coordenadas> findByBarrioId(Long barrioId) {
        TypedQuery<Coordenadas> query = em.createQuery(
            "SELECT c FROM Coordenadas c WHERE c.barrio.id = :barrioId", Coordenadas.class);
        query.setParameter("barrioId", barrioId);
        return query.getResultList();
    }

    @Override
    public List<Coordenadas> findByZonaId(Long zonaId) {
        TypedQuery<Coordenadas> query = em.createQuery(
            "SELECT c FROM Coordenadas c WHERE c.zona.id = :zonaId", Coordenadas.class);
        query.setParameter("zonaId", zonaId);
        return query.getResultList();
    }

    @Override
    public Coordenadas update(Coordenadas coordenadas) throws Exception {
        if ((coordenadas.getBarrio() == null && coordenadas.getZona() == null) ||
            (coordenadas.getBarrio() != null && coordenadas.getZona() != null)) {
            throw new Exception("Una coordenada debe pertenecer a un barrio o a una zona, pero no a ambos.");
        }

        EntityTransaction tx = em.getTransaction();
        try {
            Coordenadas existente = em.find(Coordenadas.class, coordenadas.getId());
            if (existente == null) {
                throw new Exception("Coordenada no encontrada.");
            }

            tx.begin();
            Coordenadas merged = em.merge(coordenadas);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public void delete(Coordenadas coordenadas) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Coordenadas managed = coordenadas;
            if (!em.contains(coordenadas)) {
                managed = em.merge(coordenadas);
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
            Coordenadas coordenadas = em.find(Coordenadas.class, id);
            if (coordenadas != null) {
                em.remove(coordenadas);
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

    @Override
    public void deleteByBarrioId(Long barrioId) {
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
        }
    }

    @Override
    public void deleteByZonaId(Long zonaId) {
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
        }
    }
}

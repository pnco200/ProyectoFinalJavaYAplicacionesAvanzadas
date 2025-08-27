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

    @Inject
    private ICampañaDAO campañaDAO;

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
            if (existente == null) throw new Exception("Zona no encontrada.");

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
        deleteById(zona.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (campañaDAO.isZonaAsociadaACampana(id)) {
            throw new RuntimeException("No podes borrar una zona si está asociada a una campaña");
        }

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 1) Borrar coordenadas de la zona
            em.createQuery(
                "DELETE FROM Coordenadas c WHERE c.zona.id = :zonaId"
            ).setParameter("zonaId", id).executeUpdate();

            // 2) Borrar la zona
            em.createQuery(
                "DELETE FROM Zona z WHERE z.id = :zonaId"
            ).setParameter("zonaId", id).executeUpdate();

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

            // 1) Borrar coordenadas de todas las zonas del barrio
            em.createQuery(
                "DELETE FROM Coordenadas c WHERE c.zona.id IN (" +
                "  SELECT z.id FROM Zona z WHERE z.barrio.id = :barrioId" +
                ")"
            ).setParameter("barrioId", barrioId).executeUpdate();

            // 2) Borrar todas las zonas del barrio
            em.createQuery(
                "DELETE FROM Zona z WHERE z.barrio.id = :barrioId"
            ).setParameter("barrioId", barrioId).executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
}

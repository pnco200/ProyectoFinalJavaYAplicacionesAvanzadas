package asis.dao;

import asis.models.Barrio;
import asis.models.Zona;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class BarrioDAO implements IBarrioDAO {

    @Inject
    private EntityManager em;

    @Inject
    private ICampañaDAO campañaDAO;

    @Inject
    private IZonaDAO zonaDAO;

    @Inject
    private ICoordenadasDAO coordenadasDAO;

    @Override
    public Barrio save(Barrio barrio) {
        if (existsByName(barrio.getName())) {
            return null;
        }

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
        }
    }

    @Override
    public Barrio findById(Long id) {
        return em.find(Barrio.class, id);
    }

    @Override
    public List<Barrio> findAll() {
        TypedQuery<Barrio> query = em.createQuery("SELECT b FROM Barrio b", Barrio.class);
        List<Barrio> barrios = query.getResultList();
        // Inicializar colecciones para evitar LazyInitializationException si es necesario
        for (Barrio barrio : barrios) {
            barrio.getZonas().size();
            barrio.getCoordenadas().size();
            barrio.getOrganizaciones().size();
        }
        return barrios;
    }

    @Override
    public Barrio update(Barrio barrio) throws Exception {
        EntityTransaction tx = em.getTransaction();
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
            Barrio merged = em.merge(barrio);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public void delete(Barrio barrio) {
        deleteById(barrio.getId());
    }

@Override
    public void deleteById(Long id) {
        if (campañaDAO.isBarrioAsociadoACampana(id)) {
            throw new RuntimeException("No podes borrar un barrio si esta asociado a una campaña");
        }

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 1) Borrar coordenadas de las zonas del barrio
            em.createQuery(
                "DELETE FROM Coordenadas c WHERE c.zona.id IN (" +
                "  SELECT z.id FROM Zona z WHERE z.barrio.id = :barrioId" +
                ")"
            ).setParameter("barrioId", id).executeUpdate();

            // 2) Borrar coordenadas del barrio
            em.createQuery(
                "DELETE FROM Coordenadas c WHERE c.barrio.id = :barrioId"
            ).setParameter("barrioId", id).executeUpdate();

            // 3) Borrar zonas del barrio
            em.createQuery(
                "DELETE FROM Zona z WHERE z.barrio.id = :barrioId"
            ).setParameter("barrioId", id).executeUpdate();

            // 4) Finalmente, borrar el barrio
            em.createQuery(
                "DELETE FROM Barrio b WHERE b.id = :barrioId"
            ).setParameter("barrioId", id).executeUpdate();

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
    public boolean existsByName(String name) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(b) FROM Barrio b WHERE b.name = :name", Long.class);
        query.setParameter("name", name);
        return query.getSingleResult() > 0;
    }

    private boolean isNameUsedByAnother(Long barrioId, String name) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(b) FROM Barrio b WHERE b.name = :name AND b.id != :id", Long.class);
        query.setParameter("name", name);
        query.setParameter("id", barrioId);
        return query.getSingleResult() > 0;
    }
}

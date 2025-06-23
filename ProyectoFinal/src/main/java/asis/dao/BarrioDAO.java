package asis.dao;

import asis.models.Barrio;
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
        }
    }

    @Override
    public void deleteById(Long id) {
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

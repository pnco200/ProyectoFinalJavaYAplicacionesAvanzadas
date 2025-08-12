package asis.dao;

import asis.models.Renglon;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class RenglonDAO implements IRenglonDAO {

    @Inject
    private EntityManager em;

    @Override
    public Renglon save(Renglon renglon) {
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
        }
    }

    @Override
    public Renglon findById(Long id) {
        return em.find(Renglon.class, id);
    }

    @Override
    public List<Renglon> findAll() {
        TypedQuery<Renglon> query = em.createQuery("SELECT r FROM Renglon r", Renglon.class);
        return query.getResultList();
    }

    @Override
    public List<Renglon> findByEncuestaId(Long encuestaId) {
        TypedQuery<Renglon> query = em.createQuery(
            "SELECT r FROM Renglon r WHERE r.encuesta.id = :encuestaId", Renglon.class);
        query.setParameter("encuestaId", encuestaId);
        return query.getResultList();
    }

    @Override
    public List<Renglon> findByTipo(String tipo) {
        TypedQuery<Renglon> query = em.createQuery(
            "SELECT r FROM Renglon r WHERE r.tipo = :tipo", Renglon.class);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }

    @Override
    public Renglon update(Renglon renglon) {
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
        }
    }

    @Override
    public void delete(Renglon renglon) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (!em.contains(renglon)) {
                renglon = em.merge(renglon);
            }
            em.remove(renglon);
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
            Renglon renglon = em.find(Renglon.class, id);
            if (renglon != null) {
                em.remove(renglon);
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
    public List<Object[]> findDistinctTipos() {
        String sql = """
            SELECT DISTINCT r.tipo, 'VIVIENDA' AS tipo_encuesta
            FROM Renglon r
            INNER JOIN Encuesta e ON e.id = r.encuesta_id
            WHERE e.vivienda_id IS NULL
            UNION
            SELECT DISTINCT r.tipo, 'PERSONA' AS tipo_encuesta
            FROM Renglon r
            INNER JOIN Encuesta e ON e.id = r.encuesta_id
            WHERE e.vivienda_id IS NOT NULL
        """;

        Query query = em.createNativeQuery(sql);
        return query.getResultList(); // each Object[] -> { tipo, tipo_encuesta }
    }

    @Override
    public List<String> findDistinctValoresByTipo(String tipo) {
        TypedQuery<String> query = em.createQuery("SELECT DISTINCT r.valor FROM Renglon r WHERE r.tipo = :tipo", String.class);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }
}

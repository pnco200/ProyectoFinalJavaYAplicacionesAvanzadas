package asis.dao;

import asis.models.Encuesta;
import asis.models.Encuesta.TipoEncuesta;
import asis.models.Renglon;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class EncuestaDAO implements IEncuestaDAO {

    @Inject
    private EntityManager em;

    @Override
    public Encuesta save(Encuesta encuesta) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (encuesta.getId() == null) {
                em.persist(encuesta);
            } else {
                encuesta = em.merge(encuesta);
            }
            tx.commit();
            return encuesta;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Encuesta update(Encuesta encuesta) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Encuesta updated = em.merge(encuesta);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Encuesta findById(Long id) {
        return em.find(Encuesta.class, id);
    }

    @Override
    public List<Encuesta> findAll() {
        TypedQuery<Encuesta> query = em.createQuery("SELECT e FROM Encuesta e", Encuesta.class);
        return query.getResultList();
    }

    @Override
    public List<Encuesta> findByTipo(TipoEncuesta tipo) {
        TypedQuery<Encuesta> query = em.createQuery("SELECT e FROM Encuesta e WHERE e.tipo = :tipo", Encuesta.class);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }

    @Override
    public List<Encuesta> findByViviendaId(Long viviendaId) {
        TypedQuery<Encuesta> query = em.createQuery("SELECT e FROM Encuesta e WHERE e.vivienda.id = :viviendaId", Encuesta.class);
        query.setParameter("viviendaId", viviendaId);
        return query.getResultList();
    }

    @Override
    public List<Encuesta> findByCampañaId(Long campañaId) {
        TypedQuery<Encuesta> query = em.createQuery("SELECT e FROM Encuesta e WHERE e.campaña.id = :campañaId", Encuesta.class);
        System.out.println(query);
        query.setParameter("campañaId", campañaId);
        return query.getResultList();
    }

    @Override
    public void delete(Encuesta encuesta) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (!em.contains(encuesta)) {
                encuesta = em.merge(encuesta);
            }
            em.remove(encuesta);
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
            Encuesta encuesta = em.find(Encuesta.class, id);
            if (encuesta != null) {
                em.remove(encuesta);
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

    public Encuesta addRenglonToEncuesta(int encuestaId, int renglonId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Encuesta encuesta = em.find(Encuesta.class, encuestaId);
            Renglon renglon = em.find(Renglon.class, renglonId);

            if (encuesta == null || renglon == null) {
                tx.rollback();
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
        }
    }
}

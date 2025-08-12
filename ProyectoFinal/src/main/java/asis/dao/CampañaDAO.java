package asis.dao;

import asis.exceptions.CampañaException;
import asis.models.Campaña;
import asis.models.Jornada;
import asis.utils.JPAUtil;
import jakarta.enterprise.context.ApplicationScoped;
import asis.models.Barrio;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class CampañaDAO implements ICampañaDAO {
	
	@Inject
    private EntityManager em;
    @Inject
    private BarrioDAO barrioDAO;
    @Override
    public Campaña save(Campaña campaña) throws CampañaException {
        if (campaña.getStartDate() != null && campaña.getEndDate() != null) {
            if (campaña.getStartDate().after(campaña.getEndDate())) {
                throw new CampañaException("La fecha de inicio es posterior a la fecha de fin.");
            }
        }


        EntityTransaction tx = em.getTransaction();
        try {
            String jpql = "SELECT c FROM Campaña c WHERE c.name = :name AND " +
                          "((c.startDate BETWEEN :startDate AND :endDate) OR " +
                          "(c.endDate BETWEEN :startDate AND :endDate) OR " +
                          "(:startDate BETWEEN c.startDate AND c.endDate))";

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
            if (campaña.getBarrio() != null && campaña.getBarrio().getId() != null) {
                Barrio barrio = barrioDAO.findById(campaña.getBarrio().getId());
                if (barrio == null) {
                    throw new CampañaException("Barrio no encontrado con ID: " + campaña.getBarrio().getId());
                }
                campaña.setBarrio(barrio);
            }

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
        } 
    }


    @Override
    public Campaña findById(Long id) {

        try {
            return em.find(Campaña.class, id);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null; // Retorna null en caso de error
        }
    }
    @Override
    public List<Campaña> findAll() {

        try {
            TypedQuery<Campaña> query = em.createQuery("SELECT c FROM Campaña c", Campaña.class);
            return query.getResultList();
        } catch (Exception e) {
			e.printStackTrace();
			return List.of(); // Retorna una lista vacía en caso de error
		} 
    }

    @Override
    public void deleteById(Long id) { //Esto esta mas o menos, me gustaria llamar el DAO de jornada pero maneja su propio EM

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Campaña campaña = em.find(Campaña.class, id);
            if (campaña != null) {
                // Forzar carga de jornadas si la colección es LAZY
                campaña.getJornadas().size();

                for (Jornada jornada : campaña.getJornadas()) {
                    // Eliminar asociaciones con encuestadores
                    jornada.getEncuestadores().clear();
                    em.merge(jornada); // Persistir el cambio

                    // Eliminar la jornada
                    em.remove(em.contains(jornada) ? jornada : em.merge(jornada));
                }

                // Finalmente eliminar la campaña
                em.remove(campaña);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } 
    }


    @Override
    public List<Campaña> findByName(String name) {

        try {
            TypedQuery<Campaña> query = em.createQuery(
                "SELECT c FROM Campaña c WHERE c.name LIKE :name", Campaña.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception e) {
        	e.printStackTrace();
        	return List.of(); // Retorna una lista vacía en caso de error
        }
    }

    @Override
    public List<Campaña> findByStartDateBetween(Date startDate, Date endDate) {

        try {
            TypedQuery<Campaña> query = em.createQuery(
                "SELECT c FROM Campaña c WHERE c.startDate BETWEEN :startDate AND :endDate", Campaña.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
			e.printStackTrace();
			return List.of(); // Retorna una lista vacía en caso de error
		} 
    }

    @Override
    public boolean isBarrioAsociadoACampana(Long barrioId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(c) FROM Campaña c WHERE c.barrio.id = :barrioId", Long.class);
        query.setParameter("barrioId", barrioId);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean isZonaAsociadaACampana(Long zonaId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(c) FROM Campaña c JOIN c.barrio b JOIN b.zonas z WHERE z.id = :zonaId", Long.class);
        query.setParameter("zonaId", zonaId);
        return query.getSingleResult() > 0;
    }
}

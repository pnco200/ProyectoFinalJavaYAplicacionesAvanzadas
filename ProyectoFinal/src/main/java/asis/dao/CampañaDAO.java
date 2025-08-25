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
    public Campaña save(Campaña data) throws CampañaException {
        if (data.getStartDate() != null && data.getEndDate() != null) {
            if (data.getStartDate().after(data.getEndDate())) {
                throw new CampañaException("La fecha de inicio es posterior a la fecha de fin.");
            }
        }

        EntityTransaction tx = em.getTransaction();
        try {
            // validación de solapamiento (igual que antes)
            String jpql = "SELECT c FROM Campaña c WHERE c.name = :name AND " +
                          "((c.startDate BETWEEN :startDate AND :endDate) OR " +
                          "(c.endDate BETWEEN :startDate AND :endDate) OR " +
                          "(:startDate BETWEEN c.startDate AND c.endDate))";
            if (data.getId() != null) {
                jpql += " AND c.id <> :id";
            }
            TypedQuery<Campaña> query = em.createQuery(jpql, Campaña.class);
            query.setParameter("name", data.getName());
            query.setParameter("startDate", data.getStartDate());
            query.setParameter("endDate", data.getEndDate());
            if (data.getId() != null) {
                query.setParameter("id", data.getId());
            }
            if (!query.getResultList().isEmpty()) {
                throw new CampañaException("Ya existe una campaña con ese nombre y fechas solapadas.");
            }

            tx.begin();

            // Resolver barrio (si viene)
            Barrio barrio = null;
            if (data.getBarrio() != null && data.getBarrio().getId() != null) {
                barrio = barrioDAO.findById(data.getBarrio().getId());
                if (barrio == null) throw new CampañaException("Barrio no encontrado con ID: " + data.getBarrio().getId());
            }

            Campaña managed;
            if (data.getId() == null) {
                // CREAR: persistir una NUEVA entidad vacía y setear solo escalares
                managed = new Campaña();
                em.persist(managed);
            } else {
                // ACTUALIZAR: traer la administrada
                managed = em.find(Campaña.class, data.getId());
                if (managed == null) throw new CampañaException("Campaña no encontrada");
            }

            // Copiar SOLO escalares/asociaciones many-to-one
            managed.setName(data.getName());
            managed.setStartDate(data.getStartDate());
            managed.setEndDate(data.getEndDate());
            managed.setBarrio(barrio);

            // MUY IMPORTANTE:
            // No tocar managed.getEncuestas() ni managed.getJornadas() acá.
            // No usar merge(managed) ni merge(data).

            tx.commit();
            return managed;
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

    public List<Campaña> findByDateRange(Date startDate, Date endDate) {
        try {
            TypedQuery<Campaña> query = em.createQuery(
                "SELECT c FROM Campaña c WHERE c.startDate <= :endDate AND c.endDate >= :startDate", Campaña.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}

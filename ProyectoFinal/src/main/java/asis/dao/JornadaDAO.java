package asis.dao;

import asis.exceptions.JornadaException;
import asis.models.Encuestador;
import asis.models.Jornada;
import asis.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;

public class JornadaDAO implements IJornadaDAO {

    @Override
    public Jornada save(Jornada jornada) throws JornadaException {
        if (jornada.getStartDate() != null && jornada.getEndDate() != null) {
            if (jornada.getStartDate().after(jornada.getEndDate())) {
                throw new JornadaException("La fecha de inicio es posterior a la fecha de fin.");
            }
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
        	String jpql = "SELECT j FROM Jornada j WHERE j.name = :name AND j.campaña.id = :campañaId AND " +
                    "((j.startDate BETWEEN :startDate AND :endDate) OR " +
                    "(j.endDate BETWEEN :startDate AND :endDate) OR " +
                    "(:startDate BETWEEN j.startDate AND j.endDate))";


            if (jornada.getId() != null) {
                jpql += " AND j.id <> :id";
            }

            TypedQuery<Jornada> query = em.createQuery(jpql, Jornada.class);
            if (jornada.getCampaña() == null || jornada.getCampaña().getId() == null) {
                throw new JornadaException("ID de jornada invalido.");
            }
            query.setParameter("campañaId", jornada.getCampaña().getId());
            query.setParameter("name", jornada.getName());
            query.setParameter("startDate", jornada.getStartDate());
            query.setParameter("endDate", jornada.getEndDate());

            if (jornada.getId() != null) {
                query.setParameter("id", jornada.getId());
            }

            List<Jornada> conflictos = query.getResultList();
            if (!conflictos.isEmpty()) {
                throw new JornadaException("Ya existe una jornada con ese nombre y fechas solapadas para esa campaña.");
            }

            tx.begin();
            if (jornada.getId() == null) {
                em.persist(jornada);
            } else {
                jornada = em.merge(jornada);
            }
            tx.commit();
            return jornada;
        } catch (JornadaException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new JornadaException("Error interno al guardar la jornada.");
        } finally {
            em.close();
        }
    }

    @Override
    public Jornada findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Jornada.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Jornada> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Jornada> query = em.createQuery("SELECT j FROM Jornada j", Jornada.class);
            return query.getResultList();
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
            Jornada jornada = em.find(Jornada.class, id);
            if (jornada != null) {
                jornada.getEncuestadores().clear(); //Saco a los encuestadores de la jornada
                em.merge(jornada); 
                em.remove(jornada);
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
    public List<Jornada> findByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Jornada> query = em.createQuery(
                "SELECT j FROM Jornada j WHERE j.name LIKE :name", Jornada.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Jornada> findByStartDateBetween(Date startDate, Date endDate) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Jornada> query = em.createQuery(
                "SELECT j FROM Jornada j WHERE j.startDate BETWEEN :startDate AND :endDate", Jornada.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void addEncuestador(Long jornadaId, Encuestador encuestador) throws JornadaException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Jornada jornada = em.find(Jornada.class, jornadaId);
            if (jornada == null) {
                throw new JornadaException("Jornada no encontrada.");
            }
            
            Encuestador managedEncuestador = em.find(Encuestador.class, encuestador.getId());
            if (managedEncuestador == null) {
                throw new JornadaException("Encuestador no encontrado.");
            }

            if (jornada.getEncuestadores().contains(managedEncuestador)) {
                throw new JornadaException("El encuestador ya esta asignado a esta jornada.");
            }

            jornada.getEncuestadores().add(managedEncuestador);
            em.merge(jornada);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new JornadaException("Error al asignar el encuestador a la jornada.");
        } finally {
            em.close();
        }
    }

    @Override
    public void removeEncuestador(Long jornadaId, Encuestador encuestador) throws JornadaException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Jornada jornada = em.find(Jornada.class, jornadaId);
            if (jornada == null) {
                throw new JornadaException("Jornada no encontrada.");
            }

            Encuestador managedEncuestador = em.find(Encuestador.class, encuestador.getId());
            if (managedEncuestador == null) {
                throw new JornadaException("Encuestador no encontrado.");
            }

            if (!jornada.getEncuestadores().contains(managedEncuestador)) {
                throw new JornadaException("El encuestador no esta asignado a esta jornada.");
            }

            jornada.getEncuestadores().remove(managedEncuestador);
            em.merge(jornada);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new JornadaException("Error al remover el encuestador de la jornada.");
        } finally {
            em.close();
        }
    }

    @Override
    public List<Encuestador> getEncuestadoresByJornada(Long jornadaId) throws JornadaException {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            Jornada jornada = em.find(Jornada.class, jornadaId);
            if (jornada == null) {
                throw new JornadaException("Jornada no encontrada.");
            }
            return List.copyOf(jornada.getEncuestadores());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JornadaException("Error al obtener los encuestadores de la jornada.");
        } finally {
            em.close();
        }
    }

}

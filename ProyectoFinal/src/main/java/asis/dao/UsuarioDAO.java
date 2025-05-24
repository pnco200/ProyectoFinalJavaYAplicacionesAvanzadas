package asis.dao;

import asis.models.Barrio;
import asis.models.Rol;
import asis.models.Rol.TipoRol;
import asis.models.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import java.util.List;
import asis.utils.JPAUtil;

public class UsuarioDAO implements IUsuarioDAO {

    @Override
    public Usuario save(Usuario usuario) {
        if (this.existsByEmail(usuario.getMail())) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(usuario);
            tx.commit();
            return usuario;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.mail = :email", Usuario.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario update(Usuario usuario) throws Exception{
    	 EntityManager em = JPAUtil.getEntityManager();
    	    EntityTransaction tx = em.getTransaction();
    	    Usuario merged = null;
    	    try {
    	        Usuario existente = em.find(Usuario.class, usuario.getId());
    	        if (existente == null) {
    	            throw new Exception("Usuario no encontrado.");
    	        }

    	        if (usuario.getMail() != null && !usuario.getMail().equals(existente.getMail())) {
    	            if (isEmailUsedByAnother(usuario.getId(), usuario.getMail())) {
    	                throw new Exception("El email ya está en uso por otro usuario.");
    	            }
    	        }

    	        tx.begin();
    	        merged = em.merge(usuario);
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
    public void delete(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario managedUsuario = usuario;
            if (!em.contains(usuario)) {
                managedUsuario = em.merge(usuario);
            }
            em.remove(managedUsuario);
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
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null) {
                // Eliminar relaciones con roles
                for (Rol rol : usuario.getRoles()) {
                    rol.getUsuarios().remove(usuario); // sincroniza el otro lado
                }
                usuario.getRoles().clear(); // limpia la colección del usuario

                em.remove(usuario);
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
    public boolean existsByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.mail = :email", Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    public boolean updateIsActive(Long userId, boolean isActive) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario usuario = em.find(Usuario.class, userId);
            if (usuario != null) {
                usuario.setIs_active(isActive);
                em.merge(usuario);
                tx.commit();
                return true;
            }
            return false; // Usuario no encontrado
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    public boolean assignBarrioToUsuario(Long userId, Long barrioId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario usuario = em.find(Usuario.class, userId);
            if (usuario == null) {
                return false; // Usuario no encontrado
            }

            // Check if the user has the ORGANIZACION_SOCIAL role
            boolean hasOrganizacionSocialRole = usuario.getRoles().stream()
            	.anyMatch(rol -> rol.getNombre() == TipoRol.ORGANIZACION_SOCIAL);
            if (!hasOrganizacionSocialRole) {
                return false; // El usuario no tiene el rol ORGANIZACION_SOCIAL
            }

            Barrio barrio = em.find(Barrio.class, barrioId);
            if (barrio == null) {
                return false; // Barrio no encontrado
            }

            // Asignar el barrio al usuario
            usuario.setBarrio(barrio);
            em.merge(usuario);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    private boolean isEmailUsedByAnother(Long userId, String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.mail = :email AND u.id != :id", Long.class);
            query.setParameter("email", email);
            query.setParameter("id", userId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}
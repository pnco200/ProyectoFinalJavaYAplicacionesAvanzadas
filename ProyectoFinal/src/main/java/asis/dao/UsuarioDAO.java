package asis.dao;

import asis.models.Barrio;
import asis.models.Rol;
import asis.models.Rol.TipoRol;
import asis.models.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import java.util.List;
import asis.utils.JPAUtil;

@ApplicationScoped
public class UsuarioDAO implements IUsuarioDAO {
	
	@Inject
	private EntityManager em;
	
    @Override
    public Usuario save(Usuario usuario) {
        if (this.existsByEmail(usuario.getMail())) {
            return null;
        }

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
        } 
    }

    @Override
    public Usuario findById(Long id) {
        try {
            return em.find(Usuario.class, id);
        } catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    @Override
    public List<Usuario> findAll() {
 
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
            return query.getResultList();
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

    @Override
    public Usuario findByEmail(String email) {

        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.mail = :email", Usuario.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } 
    }

    public Usuario update(Usuario usuario) throws Exception{

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
                existente.setMail(usuario.getMail());
                existente.setName(usuario.getName());
                existente.setDomicilio(usuario.getDomicilio());
                existente.setActivity(usuario.getActivity());
                existente.setExtra(usuario.getExtra());
                existente.setIs_active(usuario.isIs_active());

    	        tx.begin();
    	        merged = em.merge(existente);
    	        tx.commit();
    	        return merged;
    	    } catch (Exception e) {
    	        if (tx.isActive()) tx.rollback();
    	        throw e; 
    	    } 
    }

    @Override
    public void delete(Usuario usuario) {
        
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
        } 
    }

    @Override
    public void deleteById(Long id) {
        
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
        } 
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public boolean existsByEmail(String email) {

        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.mail = :email", Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
        	e.printStackTrace();
			return false;
        }
    }
    
    public boolean updateIsActive(Long userId, boolean isActive) {
        
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
        } 
    }
    
    public boolean assignBarrioToUsuario(Long userId, Long barrioId) {
        
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario usuario = em.find(Usuario.class, userId);
            if (usuario == null) {
                return false; // Usuario no encontrado
            }

            // Refresh the user object to ensure roles are up-to-date
            em.refresh(usuario);

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
        } 
    }
    public boolean assignRolToUser(Long userId, Long rolId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario usuario = em.find(Usuario.class, userId);
            if (usuario == null) {
                return false; // Usuario no encontrado
            }

            Rol rol = em.find(Rol.class, rolId);
            if (rol == null) {
                return false; // Rol no encontrado
            }

            // Add the role to the user's roles set
            usuario.addRol(rol);
            em.merge(usuario);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    private boolean isEmailUsedByAnother(Long userId, String email) {
        
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.mail = :email AND u.id != :id", Long.class);
            query.setParameter("email", email);
            query.setParameter("id", userId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
    }
}
package asis.dao;

import asis.models.Rol;
import asis.models.Usuario;
import asis.utils.JPAUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
@ApplicationScoped
public class RolDAO implements IRolDAO {
	@Inject
	private EntityManager em;
    @Override
    public Rol save(Rol rol) {
        EntityTransaction tx = em.getTransaction();
        try {
            // Validar duplicados por nombre
            TypedQuery<Rol> query = em.createQuery(
                "SELECT r FROM Rol r WHERE r.nombre = :nombre", Rol.class);
            query.setParameter("nombre", rol.getNombre());
            if (!query.getResultList().isEmpty()) {
                System.out.println("Rol con nombre '" + rol.getNombre() + "' ya existe.");
                return null;
            }

            tx.begin();
            em.persist(rol);
            tx.commit();
            return rol;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } 
    }

    @Override
    public Rol findById(Long id) {
        try {
            return em.find(Rol.class, id);
        } catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
    }

    @Override
    public List<Rol> findAll() {
        try {
            TypedQuery<Rol> query = em.createQuery("SELECT r FROM Rol r", Rol.class);
            return query.getResultList();
        } catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
    }

    @Override
    public Rol update(Rol rol) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Rol mergedRol = em.merge(rol);
            tx.commit();
            return mergedRol;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } 
    }

    @Override
    public void delete(Rol rol) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Rol managed = em.contains(rol) ? rol : em.merge(rol);

            // Eliminar la relación con los usuarios
            managed.getUsuarios().forEach(usuario -> usuario.getRoles().remove(managed));
            managed.getUsuarios().clear();

            em.remove(managed);
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
            Rol rol = em.find(Rol.class, id);
            if (rol != null) {
                // Eliminar la relación con los usuarios
                rol.getUsuarios().forEach(usuario -> usuario.getRoles().remove(rol));
                rol.getUsuarios().clear();

                em.remove(rol);
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
    public List<Rol> findByTipoRol(Rol.TipoRol tipoRol) {
        try {
            TypedQuery<Rol> query = em.createQuery(
                "SELECT r FROM Rol r WHERE r.nombre = :rolTipo", Rol.class);
            query.setParameter("rolTipo", tipoRol);
            return query.getResultList();
        } catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
    }

    @Override
    public List<Usuario> findUsersByRolId(Long rolId) {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u JOIN u.roles r WHERE r.id = :rolId", Usuario.class);
            query.setParameter("rolId", rolId);
            return query.getResultList();
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

    @Override
    public void addUsuario(Long rolId, Usuario usuario) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Rol rol = em.find(Rol.class, rolId);

            if (rol == null) {
                throw new IllegalArgumentException("El rol no existe.");
            }

            usuario = em.merge(usuario);

            if (rol.getUsuarios().contains(usuario)) {
                throw new IllegalArgumentException("El usuario ya tiene este rol asignado.");
            }

            rol.getUsuarios().add(usuario);
            usuario.getRoles().add(rol);
            em.merge(rol);
            em.merge(usuario);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e; 
        } 
    }

    @Override
    public void removeUsuario(Long rolId, Usuario usuario) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Rol rol = em.find(Rol.class, rolId);

            if (rol == null) {
                throw new IllegalArgumentException("El rol no existe.");
            }

            usuario = em.merge(usuario);

            if (!rol.getUsuarios().contains(usuario)) {
                throw new IllegalArgumentException("El usuario no tiene este rol asignado.");
            }

            rol.getUsuarios().remove(usuario);
            usuario.getRoles().remove(rol);
            em.merge(rol);
            em.merge(usuario);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e; 
        } 
    }

}

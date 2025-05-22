package asis.dao;

import asis.models.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import java.util.List;

public class UsuarioDAO implements IUsuarioDAO {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Usuario save(Usuario usuario) {
    	if (this.existsByEmail(usuario.getMail())) {
    		return null;
    	}
        entityManager.persist(usuario);
        return usuario;
    }
    
    @Override
    public Usuario findById(Long id) {
        return entityManager.find(Usuario.class, id);
    }
    
    @Override
    public List<Usuario> findAll() {
        TypedQuery<Usuario> query = entityManager.createQuery(
            "SELECT u FROM Usuario u", Usuario.class);
        return query.getResultList();
    }
    
    @Override
    public Usuario findByEmail(String email) {
        TypedQuery<Usuario> query = entityManager.createQuery(
            "SELECT u FROM Usuario u WHERE u.mail = :email", Usuario.class);
        query.setParameter("email", email);
        
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public Usuario update(Usuario usuario) {
        return entityManager.merge(usuario);
    }
    
    @Override
    public void delete(Usuario usuario) {
        if (entityManager.contains(usuario)) {
            entityManager.remove(usuario);
        } else {
            entityManager.remove(entityManager.merge(usuario));
        }
    }
    
    @Override
    public void deleteById(Long id) {
        Usuario usuario = findById(id);
        if (usuario != null) {
            delete(usuario);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM Usuario u WHERE u.mail = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}

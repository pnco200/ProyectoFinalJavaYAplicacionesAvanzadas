package asis.servlets;

import asis.models.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class TestDB {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUP");
        EntityManager em = emf.createEntityManager();

        try {
            System.out.println("========== CREATE OPERATION ==========");
            em.getTransaction().begin();
            
            Usuario usuario = new Usuario();
            usuario.setMail("test@example.com");
            usuario.setName("Usuario Prueba");
            usuario.setPassword("password123");
            usuario.setIs_active(true);
            usuario.setDomicilio("Calle Test 123");
            usuario.setActivity("Testing");
            
            em.persist(usuario);
            em.getTransaction().commit();
            
            Long userId = usuario.getId();
            System.out.println("CREATE: Usuario creado con éxito con ID: " + userId);
            System.out.println("CREATE: Email: " + usuario.getMail());
            
            System.out.println("\n========== READ OPERATION ==========");
            Usuario foundUser = em.find(Usuario.class, userId);
            System.out.println("READ: Usuario encontrado por ID: " + foundUser.getId());
            System.out.println("READ: Nombre: " + foundUser.getName());
            System.out.println("READ: Email: " + foundUser.getMail());
            
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.mail = :email", Usuario.class);
            query.setParameter("email", "test@example.com");
            Usuario foundByEmail = query.getSingleResult();
            System.out.println("READ: Usuario encontrado por email: " + foundByEmail.getMail());
            
            System.out.println("\n========== UPDATE OPERATION ==========");
            em.getTransaction().begin();
            foundUser.setName("Nombre Actualizado");
            foundUser.setActivity("Testing Updated");
            em.merge(foundUser);
            em.getTransaction().commit();
            
            foundUser = em.find(Usuario.class, userId);
            System.out.println("UPDATE: Usuario actualizado - ID: " + foundUser.getId());
            System.out.println("UPDATE: Nombre nuevo: " + foundUser.getName());
            System.out.println("UPDATE: Actividad nueva: " + foundUser.getActivity());
            
            System.out.println("\n========== DELETE OPERATION ==========");
            em.getTransaction().begin();
            em.remove(foundUser);
            em.getTransaction().commit();
            System.out.println("DELETE: Usuario eliminado con ID: " + userId);
            
            Usuario deletedUser = em.find(Usuario.class, userId);
            if (deletedUser == null) {
                System.out.println("DELETE: Verificación exitosa - Usuario no encontrado después de eliminación");
            } else {
                System.out.println("DELETE: Error - Usuario todavía existe después de eliminación");
            }
            
            System.out.println("\nOperaciones CRUD completadas exitosamente!");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error durante operaciones CRUD: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}

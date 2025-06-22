package asis.utils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory("miUP");

        @Produces
        @RequestScoped
        public EntityManager getEntityManager() {
            return emf.createEntityManager();
        }
}

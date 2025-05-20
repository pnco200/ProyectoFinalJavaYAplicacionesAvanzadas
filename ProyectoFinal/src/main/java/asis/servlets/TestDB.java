package asis.servlets;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;



public class TestDB {
	public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUP");
        javax.persistence.EntityManager em = emf.createEntityManager();

        System.out.println("Database tables created successfully!");
        em.close();
        emf.close();
    }
}

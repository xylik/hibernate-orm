package com.miniorm.example;

import com.miniorm.*;

/**
 * Example demonstrating the usage of Mini-ORM framework.
 * 
 * This example shows all the key patterns extracted from Hibernate:
 * 1. Configuration and bootstrapping
 * 2. Session management and unit of work
 * 3. Entity lifecycle operations
 * 4. Transaction management
 * 5. Query execution
 */
public class MiniOrmExample {
    
    public static void main(String[] args) {
        // Configuration and bootstrapping (Builder pattern)
        Configuration config = new Configuration()
            .setProperty("database.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
            .setProperty("database.username", "sa")
            .setProperty("database.password", "")
            .addAnnotatedClass(User.class);
        
        // Build SessionFactory (Factory pattern)
        SessionFactory sessionFactory = config.buildSessionFactory();
        
        try {
            // Create the table (in a real app, this would be done via schema tools)
            createUserTable(sessionFactory);
            
            // Demonstrate basic operations
            demonstrateSaveAndLoad(sessionFactory);
            demonstrateQuery(sessionFactory);
            demonstrateTransactions(sessionFactory);
            
        } finally {
            sessionFactory.close();
        }
    }
    
    private static void createUserTable(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            // Create table (normally done by schema generation tools)
            session.createQuery(
                "CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100), " +
                "age INTEGER" +
                ")"
            ).executeUpdate();
            
            tx.commit();
            System.out.println("Created users table");
        }
    }
    
    private static void demonstrateSaveAndLoad(SessionFactory sessionFactory) {
        System.out.println("\n=== Save and Load Demo ===");
        
        // Session-per-operation pattern
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            // Create and save entities
            User user1 = new User("john_doe", "john@example.com", 25);
            User user2 = new User("jane_smith", "jane@example.com", 30);
            
            session.save(user1);
            session.save(user2);
            
            tx.commit();
            System.out.println("Saved users: " + user1 + ", " + user2);
        }
        
        // Load entities (demonstrating identity map)
        try (Session session = sessionFactory.openSession()) {
            User loadedUser = session.get(User.class, 1L);
            if (loadedUser != null) {
                System.out.println("Loaded user: " + loadedUser);
            }
            
            // Load same entity again - should come from identity map
            User sameUser = session.get(User.class, 1L);
            System.out.println("Same instance? " + (loadedUser == sameUser));
        }
    }
    
    private static void demonstrateQuery(SessionFactory sessionFactory) {
        System.out.println("\n=== Query Demo ===");
        
        try (Session session = sessionFactory.openSession()) {
            // Simple query with parameter
            Query query = session.createQuery("SELECT username FROM users WHERE age > ?")
                .setParameter(1, 25);
            
            System.out.println("Users older than 25: " + query.list());
        }
    }
    
    private static void demonstrateTransactions(SessionFactory sessionFactory) {
        System.out.println("\n=== Transaction Demo ===");
        
        // Successful transaction
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            User user = new User("admin", "admin@example.com", 35);
            session.save(user);
            
            tx.commit();
            System.out.println("Transaction committed successfully");
        }
        
        // Failed transaction (rollback)
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            try {
                User user = new User("test", "test@example.com", 40);
                session.save(user);
                
                // Simulate an error
                throw new RuntimeException("Simulated error");
                
            } catch (RuntimeException e) {
                tx.rollback();
                System.out.println("Transaction rolled back due to error: " + e.getMessage());
            }
        }
    }
}
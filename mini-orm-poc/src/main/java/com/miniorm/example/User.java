package com.miniorm.example;

import com.miniorm.annotations.*;

/**
 * Example entity class demonstrating the annotation-based mapping.
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "age")
    private Integer age;
    
    // Default constructor required for entity creation
    public User() {
    }
    
    public User(String username, String email, Integer age) {
        this.username = username;
        this.email = email;
        this.age = age;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
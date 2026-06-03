package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parents")
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String kid_name;
    
    @Column(name = "pairing_code")
    private String pairingCode;

    public Parent() {}

    // Getters și Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getKid_name() { return kid_name; }
    public void setKid_name(String kid_name) { this.kid_name = kid_name; }
    
    public String getPairingCode() { return pairingCode; }
    public void setPairingCode(String pairingCode) { this.pairingCode = pairingCode; }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fop_assignment_2025;

/**
 *
 * @author Asus
 */


public class User {
    private String username; // Acts as Employee ID (e.g., C6001)
    private String password;
    private String role;     // "manager" or "employee"
    private String ID;     // Real Name (e.g., Tan Guan Han)

    public User(String ID, String username, String role, String password) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.ID = ID;
    }

    // ===== Getters =====
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getID() { return ID; }

    // ===== Setters =====
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setID(String ID) { this.ID = ID; }
}

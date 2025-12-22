/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fop_assignment_2025;
import java.io.IOException;
import java.util.List;

public class LoginSystem {

    public static User login(String ID, String password) throws IOException {
        List<User> users = UserManager.loadUsers();

        for (User u : users) {
            // Check if username and password match
            if (u.getID().equals(ID) && u.getPassword().equals(password)) {
                return u; // Return the found user object
            }
        }
        return null; // Login failed
    }
}

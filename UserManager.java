/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fop_assignment_2025;
import java.io.*;
import java.util.*;
/**
 *
 * @author Asus
 */
public class UserManager {
    private static final String FILE_NAME = "employees.csv";

    // Load all users from the CSV file into a List
    public static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_NAME);

        // If file doesn't exist, return empty list (or create file)
        if (!file.exists()) {
            System.out.println("User data file not found. A new one will be created upon adding users.");
            return users;
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine(); // Skip header line if present
        String line;
        while ((line = br.readLine()) != null) {
            // CSV Format: ID,username,role,password
            String[] data = line.split(",");
            
            // Basic validation to ensure line is complete
            if (data.length >= 4) {
                users.add(new User(data[0], data[1], data[2], data[3]));
            }
        }
        br.close();
        return users;
    }

    // Add a new user to the CSV file
    public static void addUser(User user) throws IOException {
        // 1. Check if User ID already exists
        if (isUserExists(user.getID())) {
            throw new IOException("User ID " + user.getID() + " already exists.");
        }

        // 2. Append new user to file
        BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true));
        // Format: ID,username,role,password
        bw.write(user.getID() + "," + user.getUsername() + "," + user.getRole() + "," + user.getPassword());
        bw.newLine();
        bw.close();
    }

    // Helper to check for duplicates
    public static boolean isUserExists(String ID) throws IOException {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getID().equalsIgnoreCase(ID)) {
                return true;
            }
        }
        return false;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fop_assignment_2025;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        // Main Application Loop (Allows re-login after logout)
        while (true) {
            System.out.println("\n=== Operations System ===");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                handleLogin();
            } else if (choice.equals("2")) {
                System.out.println("System shutting down. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Handles the login process
    private static void handleLogin() {
        System.out.println("\n=== Employee Login ===");
        System.out.print("Enter User ID: ");
        String ID = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = LoginSystem.login(ID, password);

            if (currentUser != null) {
                System.out.println("Login Successful!");
                System.out.println("Welcome, " + currentUser.getID() + " (" + currentUser.getUsername() + ")");
                
                // Start the session for this user
                userSession();
                
            } else {
                System.out.println("Login Failed: Invalid User ID or Password.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    // The menu displayed AFTER logging in
    private static void userSession() {
        boolean loggedIn = true;
        
        while (loggedIn) {
            System.out.println("\n--- Main Menu (" + currentUser.getRole() + ") ---");
            System.out.println("1. View Profile");
            System.out.println("2. Clock in");
            // Feature: Only Managers can see/use Registration
            if (currentUser.getRole().equalsIgnoreCase("manager")) {
                System.out.println("3. Register New Employee");
            }
            
            
            System.out.println("0. Logout");
            System.out.print("Select option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- Profile ---");
                    System.out.println("Name: " + currentUser.getUsername());

                    System.out.println("ID:   " + currentUser.getID());
                    System.out.println("Role: " + currentUser.getRole());
                    break;
                case "2":
                    AttendanceTester.main(new String[0]);
                case "3":
                    if (currentUser.getRole().equalsIgnoreCase("manager")) {
                        handleRegistration();
                    } else {
                        System.out.println("Access Denied: Managers only.");
                    }
                    break;
                case "0":
                    loggedIn = false;
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Handles registering a new user
    private static void handleRegistration() {
        System.out.println("\n=== Register New Employee ===");
        try {

            System.out.print("Enter Employee ID: ");
            String id = scanner.nextLine();

            System.out.print("Enter Employee Username: ");
            String name = scanner.nextLine();

            // Validation: Check if ID exists immediately
            if (UserManager.isUserExists(id)) {
                System.out.println("Error: User ID '" + id + "' already exists! Try a different ID.");
                return;
            }

            System.out.print("Set Password: ");
            String pass = scanner.nextLine();

            System.out.print("Set Role (manager/employee): ");
            String role = scanner.nextLine();

            // Create and save (constructor is: User(ID, username, role, password))
            User newUser = new User(id, name, role.toLowerCase(), pass);
            UserManager.addUser(newUser);
            System.out.println("Success! Employee " + name + " registered.");

        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }
}

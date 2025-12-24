package fop_assignment_2025;

import java.util.Scanner;

public class AttendanceTester{
    public static void main(String[] args) {

        allAttendanceLog all = new allAttendanceLog();
        Scanner input = new Scanner(System.in);

        // 1. Load employees and attendance logs from file
        // NOTE: Ensure your Employee data is in a file named "Employee.csv"
        all.loadEmployees("employees.csv"); 
        
        // Load existing attendance logs (CRITICAL: Added this missing call)
        all.loadAttendanceLog("attendance.csv"); 

        while (true) {
            System.out.println("\n===== Attendance System =====");
            System.out.println("1. Clock In");
            System.out.println("2. Clock Out");
            System.out.println("3. View Attendance Log");
            System.out.println("4. Save Attendance Log");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            int choice = input.nextInt();
            input.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    System.out.print("Enter Employee ID: ");
                    String idIn = input.nextLine();
                    all.ClockIn(idIn);
                    break;

                case 2:
                    System.out.print("Enter Employee ID: ");
                    String idOut = input.nextLine();
                    all.ClockOut(idOut);
                    break;

                case 3:
                    all.viewAttendanceLog();
                    break;

                case 4:
                    // Saves the current state of logs (now uses consistent constant name)
                    all.SaveAttendanceLog("attendance.csv"); 
                    break;

                case 5:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
package goldenhourstoresystem;
//store employees' information
public class Employee {
    
    // 1. Attributes
    //Using private to protect data security
    // ID, Name, Role, Password [cite: 1100]
    private String employeeID;
    private String name;
    private String role;    
    private String password; //login password
    private String outletCode; //Which store they belong to

    // 2. Constructor
    // Create an Employee object reading from the CSV file.
    // Create new employee
    public Employee(String employeeID, String name, String role, String password, String outletCode) {
        this.employeeID = employeeID;
        this.name = name;
        this.role = role;
        this.password = password;
        this.outletCode = outletCode;
    }

    // 3. Getter Methods
    //access the private data from other classes 

    public String getEmployeeID() {
        return employeeID;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getOutletCode() {
        return outletCode;
    }

    // 4. toString 
    // Useful for checking data during testings
    public String toString() {
        return "Employee{" + "ID=" + employeeID + ", Name=" + name + ", Role=" + role + '}';
    }
}

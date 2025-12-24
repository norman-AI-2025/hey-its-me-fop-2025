package fop_assignment_2025;


public class Employee{
    private String employeeID;
    private String name;
    private String outlet;
    private String password;
    private String role;

    //Constructor
    public Employee(String employeeID, String name, String outlet, String password, String role) {
        this.employeeID = employeeID;
        this.name = name;
        this.outlet = outlet;
        this.password = password;
        this.role = role;
    }

    //Getter and Setter
    public String getEmployeeID() {
        return employeeID;
    }
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOutletCode() {
        return outlet;
    }
    public void setOutletCode(String outlet) {
        this.outlet = outlet;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
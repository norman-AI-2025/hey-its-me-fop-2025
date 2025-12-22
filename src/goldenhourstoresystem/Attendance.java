package goldenhourstoresystem;

//record the employees' clock-in times
public class Attendance {
    private String employeeID;
    private String date;      // Format: YYYY-MM-DD
    private String timeIn;    // Format: HH:mm
    private String timeOut;   

    public Attendance(String employeeID, String date, String timeIn, String timeOut) {
        this.employeeID = employeeID;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getEmployeeID() { 
       return employeeID; 
    }
    
    public String getDate() { 
       return date; 
    }
    
    public String getTimeIn() { 
       return timeIn; 
    }
    
    public String getTimeOut() { 
       return timeOut; 
    }

    // Needed for clocking out later
    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }
    
    // Helper to save to CSV easily
    public String toCSV() {
        return employeeID + "," + date + "," + timeIn + "," + timeOut;
    }
}
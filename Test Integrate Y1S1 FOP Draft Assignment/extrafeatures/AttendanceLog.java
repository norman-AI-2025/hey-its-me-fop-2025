package extrafeatures;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class AttendanceLog {
    private String employeeID;
    private LocalDate date;
    private LocalTime ClockIn;
    private LocalTime ClockOut;
    private double total_hours_worked;

    //Formatter to convert Strings to Objects
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
   
    //Constructor
   public AttendanceLog(String employeeID, String dateStr) {
    this.employeeID = employeeID;
    this.date = LocalDate.parse(dateStr);
   }
   
   //Clock-in - accept String and convert to LocalTime
   public void setClockIn(String timeStr) {
    String t = normalizeTime(timeStr);
    this.ClockIn = LocalTime.parse(t, timeFormatter);
   }

   //Clock-out- accept String, store and calculate Hours of worked
   public void setClockOut(String timeStr) {
    String t = normalizeTime(timeStr);
    this.ClockOut = LocalTime.parse(t, timeFormatter);
    calculateTotalHoursWorked();
   }

   private String normalizeTime(String timeStr) {
    if (timeStr == null) return "00:00 AM";
    String s = timeStr.trim();
    if (s.length() == 0) return "00:00 AM";

    s = s.replace("a.m.", "AM").replace("p.m.", "PM");
    s = s.replace("am", "AM").replace("pm", "PM");

    // ensure space before AM/PM
    if (!s.contains(" ")) {
        if (s.endsWith("AM") || s.endsWith("PM")) {
            s = s.substring(0, s.length() - 2) + " " + s.substring(s.length() - 2);
        }
    }
    return s.trim();
   }

   private void calculateTotalHoursWorked() {
    if (ClockIn != null && ClockOut != null) {
        Duration duration= Duration.between(ClockIn, ClockOut);
        total_hours_worked = duration.toMinutes() / 60.0; //convert minutes to hours
    } else {
        total_hours_worked = 0.0; //if either Clock-in or Clock-out is null
    }
   }

   //Getters
   public String getEmployeeID() {
    return employeeID;
   }
   public String getdate() {
    return date.toString();
   }
   public String getClockIn() {
     return (ClockIn != null) ? ClockIn.format(timeFormatter).toLowerCase() : "N/A";
   }
   public String getClockOut() {
    return (ClockOut != null) ? ClockOut.format(timeFormatter).toLowerCase() : "N/A";
   }
   public double gettotal_hours_worked() {
    return total_hours_worked;
   }

   //Helper to save to CSV
   public String toCSV() {
    return employeeID + "," + date + "," + getClockIn() + "," + getClockOut() + "," + total_hours_worked;
   }
}

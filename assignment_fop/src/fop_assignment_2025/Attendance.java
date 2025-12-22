package fop_assignment_2025;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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


package resort;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String resNo, name, address, contact, roomType;
    private LocalDate checkIn, checkOut;
    private long totalCost;
    private String manualStatus; // "Checked-Out", "Cancelled" - only set manually

    public Reservation(String resNo, String name, String address, String contact,
                String roomType, LocalDate checkIn, LocalDate checkOut) {
        this.resNo = resNo;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.manualStatus = null; // Auto-detect by default
        calculateCost();
    }

    void calculateCost() {
        long nights = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
        int rate = roomType.equals("Single") ? 8000 : roomType.equals("Double") ? 12000 : 20000;
        this.totalCost = nights * rate;
    }
    
    // Auto-detect status based on dates
    public String getStatus() {
        // If manually set (checked-out or cancelled), return that
        if(manualStatus != null) {
            return manualStatus;
        }
        
        // Auto-detect based on dates
        LocalDate today = LocalDate.now();
        
        if(today.isBefore(checkIn)) {
            return "Upcoming";
        } else if(today.isAfter(checkOut) || today.isEqual(checkOut)) {
            return "Completed";
        } else {
            return "Active";
        }
    }
    
    public void setStatusManual(String status) { 
        this.manualStatus = status; 
    }
    
    public boolean isManualStatus() {
        return manualStatus != null;
    }

    public String getResNo() { return resNo; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getContact() { return contact; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public long getTotalCost() { return totalCost; }
    
    public long getNights() {
        return Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
    }
}

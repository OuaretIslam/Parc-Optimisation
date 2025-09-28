package GestionMode;


public class Drivers {
    private int DriverId;  // Add this field
    private String DriverN;
    private String DriverStatus;

    // Constructor
    public Drivers(int DriverId, String DriverN, String DriverStatus) {
        this.DriverId = DriverId;
        this.DriverN = DriverN;
        this.DriverStatus = DriverStatus;
    }

    // Getters and Setters
    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int DriverId) {
        this.DriverId = DriverId;
    }

    public String getDriverN() {
        return DriverN;
    }

    public void setDriverN(String DriverN) {
        this.DriverN = DriverN;
    }

    public String getDriverStatus() {
        return DriverStatus;
    }

    public void setDriverStatus(String DriverStatus) {
        this.DriverStatus = DriverStatus;
    }
}

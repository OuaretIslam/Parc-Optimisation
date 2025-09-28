package GestionMode;

public class Truck {
    private int TruckId;  // Add this field
    private String TruckBrand;
    private String TruckType;
    private String TruckN;
    private String TrucksStat;
    private int RespDriverId;

    // Constructor
    public Truck(int TruckId, String TruckBrand, String TruckType, String TruckN, String TrucksStat, int RespDriverId) {
        this.TruckId = TruckId;
        this.TruckBrand = TruckBrand;
        this.TruckType = TruckType;
        this.TruckN = TruckN;
        this.TrucksStat = TrucksStat;
        this.RespDriverId = RespDriverId;
    }

    // Overloaded constructor for use when ID isn't immediately needed
    public Truck(String TruckBrand, String TruckType, String TruckN ,String TrucksStat, int RespDriverId) {
        this.TruckBrand = TruckBrand;
        this.TruckType = TruckType;
        this.TruckN = TruckN;
        this.TrucksStat = TrucksStat;
        this.RespDriverId = RespDriverId;
    }

    // Getters and Setters
    public int getTruckId() {
        return TruckId;
    }

    public void setTruckId(int TruckId) {
        this.TruckId = TruckId;
    }

    public String getTruckBrand() {
        return TruckBrand;
    }

    public void setTruckBrand(String TruckBrand) {
        this.TruckBrand = TruckBrand;
    }

    public String getTruckType() {
        return TruckType;
    }

    public void setTruckType(String TruckType) {
        this.TruckType = TruckType;
    }

    public String getTruckN() {
        return TruckN;
    }

    public void setTruckN(String TruckN) {
        this.TruckN = TruckN;
    }
    
    public String getTrucksStat() {
        return TrucksStat;
    }

    public void setTrucksStat(String TrucksStat) {
        this.TrucksStat = TrucksStat;
    }

    public int getRespDriverId() {
        return RespDriverId;
    }

    public void setRespDriverId(int RespDriverId) {
        this.RespDriverId = RespDriverId;
    }
}
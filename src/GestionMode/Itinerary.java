package GestionMode;

public class Itinerary {
    private int itineraryId;
    private int deliveryId;
    private String adresse;

    public Itinerary(int itineraryId, int deliveryId, String adresse) {
        this.itineraryId = itineraryId;
        this.deliveryId = deliveryId;
        this.adresse = adresse;
    }

    public int getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(int itineraryId) {
        this.itineraryId = itineraryId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}

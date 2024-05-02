package test.connect.geoexploreapp.model;

public class LocationProximity {
    public double latitude;
    public double longitude;
    public double range;

    public LocationProximity(double latitude, double longitude, double range) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }
}

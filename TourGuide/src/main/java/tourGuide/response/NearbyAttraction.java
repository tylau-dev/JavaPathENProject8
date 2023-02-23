package tourGuide.response;

public class NearbyAttraction {
    private final String name;
    private final Double longitude;
    private final Double latitude;
    private final Double distance;
    private final int rewardPoints;

    public NearbyAttraction(String name, double longitude, double latitude, double distance, int rewardPoints) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }

    public String getName() {
        return this.name;
    }


    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getDistance() {
        return this.distance;
    }

    public int getRewardPoints() {
        return this.rewardPoints;
    }

}
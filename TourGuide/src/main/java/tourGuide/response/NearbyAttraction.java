package tourGuide.response;

// Renommer en Attraction
public class NearbyAttraction extends Coordinate {
    private String name;
    private Double distance;
    private int rewardPoints;

    public NearbyAttraction(String name, double distance, int rewardPoints, Double latitude, Double longitude) {
        super(latitude, longitude);
        this.name = name;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }

    public String getName() {
        return this.name;
    }

    public Double getDistance() {
        return this.distance;
    }

    public int getRewardPoints() {
        return this.rewardPoints;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
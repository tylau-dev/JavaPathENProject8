package tourGuide.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NearbyAttractionResponse extends Location
{
    private List<Attraction> attractions;

    public NearbyAttractionResponse(double userLatitude, double userLongitude, List<Attraction> attractions) {
        super(userLatitude, userLongitude);
        this.attractions = attractions;
    }

    public void addNearbyAttraction(Attraction attraction) {
        this.attractions.add(attraction);
    }

    public List<Attraction> filterTopFiveAttraction() {
        return this.attractions.stream().sorted(Comparator.comparing(Attraction::getDistance)).limit(5).collect(Collectors.toList());
    }

    public List<Attraction> getNearbyAttractions() {
        return attractions;
    }

    public void setNearbyAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }
}

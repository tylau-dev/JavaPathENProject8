package tourGuide.response;

import gpsUtil.location.VisitedLocation;
import tourGuide.response.NearbyAttraction;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NearbyAttractionResponse
{
    private final Double userLatitude;
    private final Double userLongitude;
    private List<NearbyAttraction> nearbyAttractions;

    public NearbyAttractionResponse(double userLatitude, double userLongitude, List<NearbyAttraction> nearbyAttractions) {
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.nearbyAttractions = nearbyAttractions;
    }

    public void addNearbyAttraction(NearbyAttraction nearbyAttraction) {
        this.nearbyAttractions.add(nearbyAttraction);
    }

    public List<NearbyAttraction> filterTopFiveAttraction() {
        return this.nearbyAttractions.stream().sorted(Comparator.comparing(NearbyAttraction::getDistance)).limit(5).collect(Collectors.toList());
    }
}

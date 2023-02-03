package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;

public interface IRewardsService {
    void setProximityBuffer(int proximityBuffer);
    void setDefaultProximityBuffer();
    void calculateRewards(User user);
    boolean isLocationWithinAttractionProximity(Attraction attraction, Location location);
    int getRewardPoints(Attraction attraction, User user);
    double getDistance(Location loc1, Location loc2);
}

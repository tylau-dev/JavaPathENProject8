package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import tourGuide.model.User;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface IRewardsService {
    void setProximityBuffer(int proximityBuffer);
    void setDefaultProximityBuffer();
    void calculateRewards(User user) throws ExecutionException, InterruptedException;
    boolean isLocationWithinAttractionProximity(Attraction attraction, Location location);
    int getRewardPoints(Attraction attraction, UUID userId);
}

package tourGuide.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import static tourGuide.helper.DistanceHelper.getDistance;

@Service
public class RewardsService implements IRewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {

		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {

		proximityBuffer = defaultProximityBuffer;
	}

	// A bouger dans Rewards
	public void calculateRewards(User user) throws ExecutionException, InterruptedException {
		UUID uid = user.getUserId();
		Collection<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();
		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if (isVisitedLocationInAttractionProximity(visitedLocation, attraction)) {
					UserReward userReward = new UserReward(visitedLocation, attraction);
					CompletableFuture.supplyAsync(() -> {
						return getRewardPoints(attraction, uid);
					}).thenAccept((p) -> {
						userReward.setRewardPoints(p);
					});
					user.addUserReward(userReward);
				}
			}
		}
	}

	public int getRewardPoints(Attraction attraction, UUID userId) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userId);
	}

	// A bouger dans AttractionService ou Helper (static class)
	public boolean isLocationWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	private boolean isVisitedLocationInAttractionProximity(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	


}

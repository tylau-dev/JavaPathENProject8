package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService implements ITourGuideService{
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final IRewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	private final Map<String, User> internalUserMap = new HashMap<>();

	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private static final String tripPricerApiKey = "test-server-api-key";

	public TourGuideService(GpsUtil gpsUtil, IRewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
	}
	
	public Collection<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) throws ExecutionException, InterruptedException {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<Attraction> getNearbyAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isLocationWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		
		return nearbyAttractions;
	}

}

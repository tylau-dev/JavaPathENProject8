package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Location;
import tourGuide.model.Attraction;
import tourGuide.model.NearbyAttraction;
import tourGuide.model.UserCurrentLocation;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import static tourGuide.helper.DistanceHelper.getDistance;

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

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public Collection<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) throws ExecutionException, InterruptedException {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLatestVisitedLocation() :
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

	public List<VisitedLocation> trackUserListLocation(List<User> userList) {
		List<VisitedLocation> visitedLocations = new ArrayList<>();
		userList.parallelStream().forEach(user -> {
			try {
				visitedLocations.add(trackUserLocation(user));
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		return visitedLocations;
	}

	public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
		return CompletableFuture.supplyAsync(() -> {
			return gpsUtil.getUserLocation(user.getUserId());
		}).thenApply((p) -> {
			user.addToVisitedLocations(p);
			try {
				rewardsService.calculateRewards(user);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return p;
		}).get();
	}

	public List<gpsUtil.location.Attraction> getNearbyAttractions(VisitedLocation visitedLocation) {
		List<gpsUtil.location.Attraction> attractions = new ArrayList<>();
		for(gpsUtil.location.Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isLocationWithinAttractionProximity(attraction, visitedLocation.location)) {
				attractions.add(attraction);
			}
		}
		
		return attractions;
	}

	public NearbyAttraction getTopFiveNearbyAttractions(User user) throws ExecutionException, InterruptedException {
		VisitedLocation visitedLocation = getUserLocation(user);
		List<gpsUtil.location.Attraction> closeAttractions = getNearbyAttractions(visitedLocation);

		NearbyAttraction result = new NearbyAttraction(visitedLocation.location.latitude, visitedLocation.location.longitude, new ArrayList<Attraction>());

		for (gpsUtil.location.Attraction attraction : closeAttractions) {
			result.addNearbyAttraction(new Attraction(attraction.attractionName, getDistance(visitedLocation.location, attraction), rewardsService.getRewardPoints(attraction, user.getUserId()), attraction.longitude, attraction.latitude));
		}

		result.filterTopFiveAttraction();

		return result;
	}

	public List<UserCurrentLocation> getAllUserCurrentLocation() {
		List<User> allUser = getAllUsers();
		List<UserCurrentLocation> result = new ArrayList<UserCurrentLocation>();

		allUser.parallelStream().forEach(user -> {
			VisitedLocation latestVisitedLocation = user.getLatestVisitedLocation();
			result.add(new UserCurrentLocation(user.getUserId(),  new Location(latestVisitedLocation.location.latitude, latestVisitedLocation.location.longitude)));
		});

		return result;
	}


	/*
		Test Methods
	 */
	private final Tracker tracker;
	boolean testMode = true;

	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory

	public Tracker getTracker() {
		return tracker;
	}

	public void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}

	public void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	public void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new gpsUtil.location.Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	public double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	public double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	public Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
}

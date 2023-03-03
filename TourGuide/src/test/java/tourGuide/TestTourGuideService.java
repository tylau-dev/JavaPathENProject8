package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tourGuide.helper.DistanceHelper.getDistance;

import java.util.*;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.response.NearbyAttraction;
import tourGuide.response.NearbyAttractionResponse;
import tourGuide.response.UserCurrentLocation;
import tourGuide.service.*;
import tourGuide.service.ITourGuideService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

	@Before
	public void setUp()
	{
		Locale.setDefault(Locale.US);
	}
	@Test
	public void getUserLocation() throws ExecutionException, InterruptedException {
		GpsUtil gpsUtil = new GpsUtil();
		IRewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		VisitedLocation visitedLocation;
		visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.getTracker().stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrievedUser = tourGuideService.getUser(user.getUserName());
		User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.getTracker().stopTracking();
		
		assertEquals(user, retrievedUser);
		assertEquals(user2, retrievedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.getTracker().stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() throws ExecutionException, InterruptedException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.getTracker().stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}
	
	@Test
	public void getNearbyAttractions() throws ExecutionException, InterruptedException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		NearbyAttractionResponse attractions = tourGuideService.getFormatTopFiveNearbyAttractions(user);

		tourGuideService.getTracker().stopTracking();

		// Attractions list can be less than 5
		assertTrue(attractions.getNearbyAttractions().size() <= 5);
	}

	@Test
	public void getCurrentUserLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(2);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<UserCurrentLocation> userCurrentLocation = tourGuideService.getAllUserCurrentLocation();

		tourGuideService.getTracker().stopTracking();

		assertEquals(2, userCurrentLocation.size());
	}

	@Test
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.getTracker().stopTracking();
		
		assertEquals(5, providers.size());
	}


	
}

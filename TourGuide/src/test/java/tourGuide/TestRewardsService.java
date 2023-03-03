package tourGuide;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.ITourGuideService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestRewardsService {
	@Before
	public void setUp()
	{
		Locale.setDefault(new Locale("en", "US", "WIN"));
	}
	@Test
	public void userGetRewards() throws ExecutionException, InterruptedException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		Collection<UserReward> userRewards = user.getUserRewards();
		tourGuideService.getTracker().stopTracking();
		assertTrue(userRewards.size() == 1);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isLocationWithinAttractionProximity(attraction, attraction));
	}
	
	@Test
	public void nearAllAttractions() throws ExecutionException, InterruptedException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		ITourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		List<User> userList = tourGuideService.getAllUsers();
		User testUser = userList.get(0);

		rewardsService.calculateRewards(testUser);
		Collection<UserReward> userRewardsa = testUser.getUserRewards();
		Collection<UserReward> userRewards = tourGuideService.getUserRewards(testUser);
		tourGuideService.getTracker().stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}
	
}

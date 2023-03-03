package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.helper.InternalTestHelper;
import tourGuide.response.NearbyAttraction;
import tourGuide.response.NearbyAttractionResponse;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static tourGuide.helper.DistanceHelper.getDistance;

public class TourGuideTestService extends TourGuideService implements ITourGuideTestService {
    private Logger logger = LoggerFactory.getLogger(TourGuideTestService.class);
    private final Tracker tracker;
    boolean testMode = true;
    private final Map<String, User> internalUserMap = new HashMap<>();

    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    public static final String tripPricerApiKey = "test-server-api-key";

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public void addUser(User user) {
        if(!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }
    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    public TourGuideTestService(GpsUtil gpsUtil, IRewardsService rewardsService) {
        super(gpsUtil, rewardsService);
        if(testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

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
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
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

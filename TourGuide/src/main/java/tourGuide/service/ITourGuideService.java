package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.model.NearbyAttractionResponse;
import tourGuide.model.UserCurrentLocationResponse;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tripPricer.Provider;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ITourGuideService
{
    Collection<UserReward> getUserRewards(User user);
    VisitedLocation getUserLocation(User user) throws ExecutionException, InterruptedException;
    User getUser(String userName);
    List<User> getAllUsers();
    void addUser(User user);
    List<Provider> getTripDeals(User user);
    VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException;
    List<VisitedLocation> trackUserListLocation(List<User> userList);
    List<Attraction> getNearbyAttractions(VisitedLocation visitedLocation);
    NearbyAttractionResponse getTopFiveNearbyAttractions(User user) throws ExecutionException, InterruptedException;
    List<UserCurrentLocationResponse> getAllUserCurrentLocation();

    /*
        Test Methods
     */
    Tracker getTracker();
    void addShutDownHook();
    void initializeInternalUsers();
    void generateUserLocationHistory(User user);
    double generateRandomLongitude();
    double generateRandomLatitude();
    Date getRandomTime();
}

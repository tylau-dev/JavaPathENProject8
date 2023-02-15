package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.Collection;
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
    List<Attraction> getNearbyAttractions(VisitedLocation visitedLocation);
}

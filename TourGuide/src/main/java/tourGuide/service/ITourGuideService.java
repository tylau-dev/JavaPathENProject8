package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.List;

public interface ITourGuideService
{
    List<UserReward> getUserRewards(User user);
    VisitedLocation getUserLocation(User user);
    User getUser(String userName);
    List<User> getAllUsers();
    void addUser(User user);
    List<Provider> getTripDeals(User user);
    VisitedLocation trackUserLocation(User user);
    List<Attraction> getNearbyAttractions(VisitedLocation visitedLocation);
}

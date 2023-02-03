package tourGuide.service;

import tourGuide.tracker.Tracker;
import tourGuide.user.User;

import java.util.Date;

public interface ITourGuideTestService extends ITourGuideService {
    Tracker getTracker();
    void addShutDownHook();
    void initializeInternalUsers();
    void generateUserLocationHistory(User user);
    double generateRandomLongitude();
    double generateRandomLatitude();
    Date getRandomTime();

}

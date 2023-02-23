package tourGuide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import gpsUtil.location.Attraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.response.NearbyAttraction;
import tourGuide.response.NearbyAttractionResponse;
import tourGuide.service.ITourGuideService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import static tourGuide.helper.DistanceHelper.getDistance;

@RestController
public class TourGuideController {

    @Autowired
    ITourGuideService tourGuideService;

    @RequestMapping("/")
    public String index() {

        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    //  TODO: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        User currentUser = getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(currentUser);
        List<Attraction> closeAttractions = tourGuideService.getNearbyAttractions(visitedLocation);
        Collection<UserReward> userRewards = currentUser.getUserRewards();

        NearbyAttractionResponse result = new NearbyAttractionResponse(visitedLocation.location.latitude, visitedLocation.location.longitude, new ArrayList<NearbyAttraction>());

        for (Attraction attraction : closeAttractions) {
            // @todo filter rewardPoints
            result.addNearbyAttraction(new NearbyAttraction(attraction.attractionName, attraction.longitude, attraction.latitude, getDistance(visitedLocation.location, attraction), 0));
        }

        return JsonStream.serialize(result.filterTopFiveAttraction());    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        // TODO: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }

        return JsonStream.serialize("");
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }
}
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
import rewardCentral.RewardCentral;
import tourGuide.response.Coordinate;
import tourGuide.response.NearbyAttraction;
import tourGuide.response.NearbyAttractionResponse;
import tourGuide.response.UserCurrentLocation;
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

    @Autowired
    RewardCentral rewardCentral;

    @RequestMapping("/")
    public String index() {

        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        return JsonStream.serialize(tourGuideService.getFormatTopFiveNearbyAttractions(getUser(userName)));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        return JsonStream.serialize(tourGuideService.getAllUserCurrentLocation());
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
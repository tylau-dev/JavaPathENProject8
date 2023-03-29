package tourGuide.model;

import java.util.UUID;

// Renommer en UserLocation
public class UserCurrentLocation {
    private UUID userId;
    private Location userLocation;

    public UserCurrentLocation(UUID userId, Location userLocation) {
        this.userId = userId;
        this.userLocation = userLocation;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Location getUserCoordinate() {
        return userLocation;
    }

    public void setUserCoordinate(Location userLocation) {
        this.userLocation = userLocation;
    }

}

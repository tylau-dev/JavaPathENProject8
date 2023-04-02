package tourGuide.model;

import java.util.UUID;

public class UserCurrentLocationResponse {
    private UUID userId;
    private Location userLocation;

    public UserCurrentLocationResponse(UUID userId, Location userLocation) {
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

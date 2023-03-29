package tourGuide.response;

import java.util.List;
import java.util.UUID;

// Renommer en UserLocation
public class UserCurrentLocation {
    private UUID userId;
    private Coordinate userCoordinate;

    public UserCurrentLocation(UUID userId, Coordinate userCoordinate) {
        this.userId = userId;
        this.userCoordinate = userCoordinate;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Coordinate getUserCoordinate() {
        return userCoordinate;
    }

    public void setUserCoordinate(Coordinate userCoordinate) {
        this.userCoordinate = userCoordinate;
    }

}

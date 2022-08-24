package ttit.com.shuvo.terraintracker.timeline;

import android.location.Location;

public class WaypointList {
    private Location location;
    private String time;

    public WaypointList(Location location, String time) {
        this.location = location;
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

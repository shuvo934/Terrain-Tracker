package ttit.com.shuvo.terraintracker.timeline;

import com.google.android.gms.maps.model.LatLng;

public class StoppedLocationTime {

    private String locationName;
    private String locationTime;
    private String stoppedTime;
    private LatLng latLng;
    private String marker_id;
    private String markerType;
    private String positionFromMain;

    public StoppedLocationTime(String locationName, String locationTime, String stoppedTime, LatLng latLng, String marker_id, String markerType,String positionFromMain) {
        this.locationName = locationName;
        this.locationTime = locationTime;
        this.stoppedTime = stoppedTime;
        this.latLng = latLng;
        this.marker_id = marker_id;
        this.markerType = markerType;
        this.positionFromMain = positionFromMain;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public String getStoppedTime() {
        return stoppedTime;
    }

    public void setStoppedTime(String stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getMarker_id() {
        return marker_id;
    }

    public void setMarker_id(String marker_id) {
        this.marker_id = marker_id;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public String getPositionFromMain() {
        return positionFromMain;
    }

    public void setPositionFromMain(String positionFromMain) {
        this.positionFromMain = positionFromMain;
    }
}

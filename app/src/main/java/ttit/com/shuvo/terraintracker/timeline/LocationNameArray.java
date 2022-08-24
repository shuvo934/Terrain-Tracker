package ttit.com.shuvo.terraintracker.timeline;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class LocationNameArray {

    private String firstLocation;
    private String lastLocation;
    private Boolean isWay;
    private String firstTime;
    private String lastTime;
    private String distance;
    private String calcTime;
    private String polyId;
    private String marId;
    private ArrayList<StoppedLocationTime> stoppedLocationTimes;
    private LatLng firstLatLng;
    private LatLng secondLatLng;
    private String firstMarkerId;
    private String lastMarkerId;
    private String firstMarkerType;
    private String secondMarkerType;
    private String position;

    public LocationNameArray(String firstLocation, String lastLocation, Boolean isWay, String firstTime, String lastTime, String distance, String calcTime,String polyId,String marId,ArrayList<StoppedLocationTime> stoppedLocationTimes,LatLng firstLatLng, LatLng secondLatLng, String firstMarkerId, String lastMarkerId,String firstMarkerType, String secondMarkerType, String position) {
        this.firstLocation = firstLocation;
        this.lastLocation = lastLocation;
        this.isWay = isWay;
        this.firstTime = firstTime;
        this.lastTime = lastTime;
        this.distance = distance;
        this.calcTime = calcTime;
        this.polyId = polyId;
        this.marId = marId;
        this.stoppedLocationTimes = stoppedLocationTimes;
        this.firstLatLng = firstLatLng;
        this.secondLatLng = secondLatLng;
        this.firstMarkerId = firstMarkerId;
        this.lastMarkerId = lastMarkerId;
        this.firstMarkerType = firstMarkerType;
        this.secondMarkerType = secondMarkerType;
        this.position = position;
    }

    public String getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(String firstLocation) {
        this.firstLocation = firstLocation;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Boolean getWay() {
        return isWay;
    }

    public void setWay(Boolean way) {
        isWay = way;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalcTime() {
        return calcTime;
    }

    public void setCalcTime(String calcTime) {
        this.calcTime = calcTime;
    }

    public String getPolyId() {
        return polyId;
    }

    public void setPolyId(String polyId) {
        this.polyId = polyId;
    }

    public String getMarId() {
        return marId;
    }

    public void setMarId(String marId) {
        this.marId = marId;
    }

    public ArrayList<StoppedLocationTime> getStoppedLocationTimes() {
        return stoppedLocationTimes;
    }

    public void setStoppedLocationTimes(ArrayList<StoppedLocationTime> stoppedLocationTimes) {
        this.stoppedLocationTimes = stoppedLocationTimes;
    }

    public LatLng getFirstLatLng() {
        return firstLatLng;
    }

    public void setFirstLatLng(LatLng firstLatLng) {
        this.firstLatLng = firstLatLng;
    }

    public LatLng getSecondLatLng() {
        return secondLatLng;
    }

    public void setSecondLatLng(LatLng secondLatLng) {
        this.secondLatLng = secondLatLng;
    }

    public String getFirstMarkerId() {
        return firstMarkerId;
    }

    public void setFirstMarkerId(String firstMarkerId) {
        this.firstMarkerId = firstMarkerId;
    }

    public String getLastMarkerId() {
        return lastMarkerId;
    }

    public void setLastMarkerId(String lastMarkerId) {
        this.lastMarkerId = lastMarkerId;
    }

    public String getFirstMarkerType() {
        return firstMarkerType;
    }

    public void setFirstMarkerType(String firstMarkerType) {
        this.firstMarkerType = firstMarkerType;
    }

    public String getSecondMarkerType() {
        return secondMarkerType;
    }

    public void setSecondMarkerType(String secondMarkerType) {
        this.secondMarkerType = secondMarkerType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}

package ttit.com.shuvo.terraintracker.livelocation;

public class AllEmpLocationLists {

    private String lat;
    private String lng;
    private String bearing;
    private String accuracy;
    private String address;
    private String time;
    private String speed;
    private String empName;
    private String empId;

    public AllEmpLocationLists(String lat, String lng, String bearing, String accuracy, String address, String time, String speed, String empName, String empId) {
        this.lat = lat;
        this.lng = lng;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.address = address;
        this.time = time;
        this.speed = speed;
        this.empName = empName;
        this.empId = empId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }
}

package ttit.com.shuvo.terraintracker.timeline;

public class AreaList {
    private String latitude;
    private String longitude;
    private String coverage;
    private String coa_id;
    private String machine_code;
    private boolean canGive;
    private String coa_name;
    private String coa_address;

    public AreaList(String latitude, String longitude, String coverage, String coa_id, String machine_code, boolean canGive, String coa_name, String coa_address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.coverage = coverage;
        this.coa_id = coa_id;
        this.machine_code = machine_code;
        this.canGive = canGive;
        this.coa_name = coa_name;
        this.coa_address = coa_address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public String getCoa_id() {
        return coa_id;
    }

    public void setCoa_id(String coa_id) {
        this.coa_id = coa_id;
    }

    public String getMachine_code() {
        return machine_code;
    }

    public void setMachine_code(String machine_code) {
        this.machine_code = machine_code;
    }

    public boolean isCanGive() {
        return canGive;
    }

    public void setCanGive(boolean canGive) {
        this.canGive = canGive;
    }

    public String getCoa_name() {
        return coa_name;
    }

    public void setCoa_name(String coa_name) {
        this.coa_name = coa_name;
    }

    public String getCoa_address() {
        return coa_address;
    }

    public void setCoa_address(String coa_address) {
        this.coa_address = coa_address;
    }
}

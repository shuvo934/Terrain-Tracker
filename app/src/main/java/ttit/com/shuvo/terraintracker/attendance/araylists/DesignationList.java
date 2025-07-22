package ttit.com.shuvo.terraintracker.attendance.araylists;

public class DesignationList {
    private String desig_id;
    private String desig_name;

    public DesignationList(String desig_id, String desig_name) {
        this.desig_id = desig_id;
        this.desig_name = desig_name;
    }

    public String getDesig_id() {
        return desig_id;
    }

    public void setDesig_id(String desig_id) {
        this.desig_id = desig_id;
    }

    public String getDesig_name() {
        return desig_name;
    }

    public void setDesig_name(String desig_name) {
        this.desig_name = desig_name;
    }
}

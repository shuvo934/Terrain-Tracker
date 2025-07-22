package ttit.com.shuvo.terraintracker.attendance.araylists;

public class DivisionList {
    private String divm_id;
    private String divm_name;

    public DivisionList(String divm_id, String divm_name) {
        this.divm_id = divm_id;
        this.divm_name = divm_name;
    }

    public String getDivm_id() {
        return divm_id;
    }

    public void setDivm_id(String divm_id) {
        this.divm_id = divm_id;
    }

    public String getDivm_name() {
        return divm_name;
    }

    public void setDivm_name(String divm_name) {
        this.divm_name = divm_name;
    }
}

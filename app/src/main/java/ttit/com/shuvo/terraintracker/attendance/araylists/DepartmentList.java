package ttit.com.shuvo.terraintracker.attendance.araylists;

public class DepartmentList {
    private String dept_id;
    private String dept_name;
    private String divm_id;

    public DepartmentList(String dept_id, String dept_name, String divm_id) {
        this.dept_id = dept_id;
        this.dept_name = dept_name;
        this.divm_id = divm_id;
    }

    public String getDivm_id() {
        return divm_id;
    }

    public void setDivm_id(String divm_id) {
        this.divm_id = divm_id;
    }

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }
}

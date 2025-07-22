package ttit.com.shuvo.terraintracker.attendance.araylists;

public class AttendanceUpReqList {
    private String darm_app_code;
    private String emp_name;
    private String emp_code;
    private String darm_date;
    private String darm_update_date;
    private String darm_id;
    private String darm_emp_id;

    public AttendanceUpReqList(String darm_app_code, String emp_name, String emp_code, String darm_date, String darm_update_date, String darm_id, String darm_emp_id) {
        this.darm_app_code = darm_app_code;
        this.emp_name = emp_name;
        this.emp_code = emp_code;
        this.darm_date = darm_date;
        this.darm_update_date = darm_update_date;
        this.darm_id = darm_id;
        this.darm_emp_id = darm_emp_id;
    }

    public String getDarm_app_code() {
        return darm_app_code;
    }

    public void setDarm_app_code(String darm_app_code) {
        this.darm_app_code = darm_app_code;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public String getDarm_date() {
        return darm_date;
    }

    public void setDarm_date(String darm_date) {
        this.darm_date = darm_date;
    }

    public String getDarm_update_date() {
        return darm_update_date;
    }

    public void setDarm_update_date(String darm_update_date) {
        this.darm_update_date = darm_update_date;
    }

    public String getDarm_id() {
        return darm_id;
    }

    public void setDarm_id(String darm_id) {
        this.darm_id = darm_id;
    }

    public String getDarm_emp_id() {
        return darm_emp_id;
    }

    public void setDarm_emp_id(String darm_emp_id) {
        this.darm_emp_id = darm_emp_id;
    }
}

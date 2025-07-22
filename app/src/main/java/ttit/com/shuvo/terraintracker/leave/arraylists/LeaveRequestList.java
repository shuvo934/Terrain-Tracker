package ttit.com.shuvo.terraintracker.leave.arraylists;

public class LeaveRequestList {
    private String la_app_code;
    private String la_emp_id;
    private String emp_code;
    private String emp_name;
    private String la_date;
    private String la_leave_days;
    private String la_id;

    public LeaveRequestList(String la_app_code, String la_emp_id, String emp_code, String emp_name, String la_date, String la_leave_days, String la_id) {
        this.la_app_code = la_app_code;
        this.la_emp_id = la_emp_id;
        this.emp_code = emp_code;
        this.emp_name = emp_name;
        this.la_date = la_date;
        this.la_leave_days = la_leave_days;
        this.la_id = la_id;
    }

    public String getLa_app_code() {
        return la_app_code;
    }

    public void setLa_app_code(String la_app_code) {
        this.la_app_code = la_app_code;
    }

    public String getLa_emp_id() {
        return la_emp_id;
    }

    public void setLa_emp_id(String la_emp_id) {
        this.la_emp_id = la_emp_id;
    }

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getLa_date() {
        return la_date;
    }

    public void setLa_date(String la_date) {
        this.la_date = la_date;
    }

    public String getLa_leave_days() {
        return la_leave_days;
    }

    public void setLa_leave_days(String la_leave_days) {
        this.la_leave_days = la_leave_days;
    }

    public String getLa_id() {
        return la_id;
    }

    public void setLa_id(String la_id) {
        this.la_id = la_id;
    }
}

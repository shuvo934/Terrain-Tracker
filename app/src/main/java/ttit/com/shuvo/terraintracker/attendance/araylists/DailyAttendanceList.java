package ttit.com.shuvo.terraintracker.attendance.araylists;

public class DailyAttendanceList {
    private String emp_id;
    private String emp_code;
    private String emp_name;
    private String job_calling_title;
    private String jsm_divm_id;
    private String jsm_dept_id;
    private String jsm_desig_id;
    private String osm_name;
    private String in_time;
    private String out_time;
    private String status;
    private String late_status;
    private String early_status;
    private String coa_name;

    public DailyAttendanceList(String emp_id, String emp_code, String emp_name, String job_calling_title, String jsm_divm_id, String jsm_dept_id, String jsm_desig_id, String osm_name, String in_time, String out_time, String status, String late_status, String early_status, String coa_name) {
        this.emp_id = emp_id;
        this.emp_code = emp_code;
        this.emp_name = emp_name;
        this.job_calling_title = job_calling_title;
        this.jsm_divm_id = jsm_divm_id;
        this.jsm_dept_id = jsm_dept_id;
        this.jsm_desig_id = jsm_desig_id;
        this.osm_name = osm_name;
        this.in_time = in_time;
        this.out_time = out_time;
        this.status = status;
        this.late_status = late_status;
        this.early_status = early_status;
        this.coa_name = coa_name;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
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

    public String getJob_calling_title() {
        return job_calling_title;
    }

    public void setJob_calling_title(String job_calling_title) {
        this.job_calling_title = job_calling_title;
    }

    public String getJsm_divm_id() {
        return jsm_divm_id;
    }

    public void setJsm_divm_id(String jsm_divm_id) {
        this.jsm_divm_id = jsm_divm_id;
    }

    public String getJsm_dept_id() {
        return jsm_dept_id;
    }

    public void setJsm_dept_id(String jsm_dept_id) {
        this.jsm_dept_id = jsm_dept_id;
    }

    public String getJsm_desig_id() {
        return jsm_desig_id;
    }

    public void setJsm_desig_id(String jsm_desig_id) {
        this.jsm_desig_id = jsm_desig_id;
    }

    public String getOsm_name() {
        return osm_name;
    }

    public void setOsm_name(String osm_name) {
        this.osm_name = osm_name;
    }

    public String getIn_time() {
        return in_time;
    }

    public void setIn_time(String in_time) {
        this.in_time = in_time;
    }

    public String getOut_time() {
        return out_time;
    }

    public void setOut_time(String out_time) {
        this.out_time = out_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLate_status() {
        return late_status;
    }

    public void setLate_status(String late_status) {
        this.late_status = late_status;
    }

    public String getEarly_status() {
        return early_status;
    }

    public void setEarly_status(String early_status) {
        this.early_status = early_status;
    }

    public String getCoa_name() {
        return coa_name;
    }

    public void setCoa_name(String coa_name) {
        this.coa_name = coa_name;
    }
}

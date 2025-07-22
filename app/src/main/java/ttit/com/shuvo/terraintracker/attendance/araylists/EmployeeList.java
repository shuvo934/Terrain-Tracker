package ttit.com.shuvo.terraintracker.attendance.araylists;

public class EmployeeList {
    private String emp_id;
    private String emp_code;
    private String emp_name;
    private String jsm_divm_id;
    private String jsm_dept_id;
    private String jsm_desig_id;
    private String job_calling_title;
    private String coa_name;

    public EmployeeList(String emp_id, String emp_code, String emp_name, String jsm_divm_id, String jsm_dept_id, String jsm_desig_id, String job_calling_title, String coa_name) {
        this.emp_id = emp_id;
        this.emp_code = emp_code;
        this.emp_name = emp_name;
        this.jsm_divm_id = jsm_divm_id;
        this.jsm_dept_id = jsm_dept_id;
        this.jsm_desig_id = jsm_desig_id;
        this.job_calling_title = job_calling_title;
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

    public String getJob_calling_title() {
        return job_calling_title;
    }

    public void setJob_calling_title(String job_calling_title) {
        this.job_calling_title = job_calling_title;
    }

    public String getCoa_name() {
        return coa_name;
    }

    public void setCoa_name(String coa_name) {
        this.coa_name = coa_name;
    }
}

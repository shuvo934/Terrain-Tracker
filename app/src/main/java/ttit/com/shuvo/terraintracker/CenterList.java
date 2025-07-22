package ttit.com.shuvo.terraintracker;

public class CenterList {
    private String center_api;
    private String center_name;
    private String user_emp_code;
    private String user_emp_id;

    public CenterList(String center_api, String center_name, String user_emp_code, String user_emp_id) {
        this.center_api = center_api;
        this.center_name = center_name;
        this.user_emp_code = user_emp_code;
        this.user_emp_id = user_emp_id;
    }

    public String getCenter_api() {
        return center_api;
    }

    public void setCenter_api(String center_api) {
        this.center_api = center_api;
    }

    public String getCenter_name() {
        return center_name;
    }

    public void setCenter_name(String center_name) {
        this.center_name = center_name;
    }

    public String getUser_emp_code() {
        return user_emp_code;
    }

    public void setUser_emp_code(String user_emp_code) {
        this.user_emp_code = user_emp_code;
    }

    public String getUser_emp_id() {
        return user_emp_id;
    }

    public void setUser_emp_id(String user_emp_id) {
        this.user_emp_id = user_emp_id;
    }
}

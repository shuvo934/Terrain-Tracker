package ttit.com.shuvo.terraintracker.loginFile;

public class UserInfoList {

    private String emp_code;
    private String user_name;
    private String user_nick_name;
    private String email;
    private String contact;
    private String emp_id;

    public UserInfoList(String emp_code, String user_name, String user_nick_name, String email, String contact, String emp_id) {
        this.emp_code = emp_code;
        this.user_name = user_name;
        this.user_nick_name = user_nick_name;
        this.email = email;
        this.contact = contact;
        this.emp_id = emp_id;
    }

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_nick_name() {
        return user_nick_name;
    }

    public void setUser_nick_name(String user_nick_name) {
        this.user_nick_name = user_nick_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }
}

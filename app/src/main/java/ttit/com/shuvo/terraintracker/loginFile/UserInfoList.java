package ttit.com.shuvo.terraintracker.loginFile;

public class UserInfoList {

    private String userName;
    private String user_fname;
    private String user_lname;
    private String email;
    private String contact;
    private String emp_id;

    public UserInfoList(String userName, String user_fname, String user_lname, String email, String contact, String emp_id) {
        this.userName = userName;
        this.user_fname = user_fname;
        this.user_lname = user_lname;
        this.email = email;
        this.contact = contact;
        this.emp_id = emp_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
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

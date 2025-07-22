package ttit.com.shuvo.terraintracker.dash_board.model;

public class DateWiseLeaveList {
    private final String d_date;
    private final String date_day;
    private final String emp_id;
    private final String emp_name;
    private final String leave;
    private final String h_flag;

    public DateWiseLeaveList(String d_date, String date_day, String emp_id, String emp_name, String leave, String h_flag) {
        this.d_date = d_date;
        this.date_day = date_day;
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.leave = leave;
        this.h_flag = h_flag;
    }

    public String getD_date() {
        return d_date;
    }

    public String getDate_day() {
        return date_day;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public String getLeave() {
        return leave;
    }

    public String getH_flag() {
        return h_flag;
    }
}

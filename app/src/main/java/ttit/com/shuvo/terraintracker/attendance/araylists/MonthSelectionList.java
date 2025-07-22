package ttit.com.shuvo.terraintracker.attendance.araylists;

public class MonthSelectionList {
    private String monthNo;
    private String monthName;

    public MonthSelectionList(String monthNo, String monthName) {
        this.monthNo = monthNo;
        this.monthName = monthName;
    }

    public String getMonthNo() {
        return monthNo;
    }

    public void setMonthNo(String monthNo) {
        this.monthNo = monthNo;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
}

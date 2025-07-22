package ttit.com.shuvo.terraintracker;

public class AllUrlList {
    private String urls;
    private boolean checked;

    public AllUrlList(String urls, boolean checked) {
        this.urls = urls;
        this.checked = checked;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

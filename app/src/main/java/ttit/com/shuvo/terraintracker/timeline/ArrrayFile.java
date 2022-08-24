package ttit.com.shuvo.terraintracker.timeline;

import android.location.Location;

import java.util.ArrayList;

public class ArrrayFile {

    private ArrayList<Location> myLatlng;
    private String listName;
    private String descc;
    private ArrayList<String> myTime;

    public ArrrayFile(ArrayList<Location> latLngs, String lm, String descc, ArrayList<String> myTime) {
        this.myLatlng = latLngs;
        this.listName = lm;
        this.descc = descc;
        this.myTime = myTime;
    }

    public String getDescc() {
        return descc;
    }

    public void setDescc(String descc) {
        this.descc = descc;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public ArrayList<Location> getMyLatlng() {
        return myLatlng;
    }

    public void setMyLatlng(ArrayList<Location> myLatlng) {
        this.myLatlng = myLatlng;
    }

    public ArrayList<String> getMyTime() {
        return myTime;
    }

    public void setMyTime(ArrayList<String> myTime) {
        this.myTime = myTime;
    }
}

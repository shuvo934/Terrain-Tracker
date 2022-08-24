package ttit.com.shuvo.terraintracker.timeline;

import com.google.android.gms.maps.model.Polyline;

public class PolyLindata {

    private Polyline polyline;
    private String id;

    public PolyLindata(Polyline polyline, String id) {
        this.polyline = polyline;
        this.id = id;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

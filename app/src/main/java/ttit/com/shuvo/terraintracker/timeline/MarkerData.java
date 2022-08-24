package ttit.com.shuvo.terraintracker.timeline;

import com.google.android.gms.maps.model.Marker;

public class MarkerData {
    private Marker marker;
    private String id;

    public MarkerData(Marker marker, String id) {
        this.marker = marker;
        this.id = id;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

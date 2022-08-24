package ttit.com.shuvo.terraintracker.timeline;

import com.google.android.gms.maps.model.Marker;

public class AllMarkerLists {
    private Marker marker;
    private String markerId;
    private String markerType;

    public AllMarkerLists(Marker marker, String markerId, String markerType) {
        this.marker = marker;
        this.markerId = markerId;
        this.markerType = markerType;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }
}

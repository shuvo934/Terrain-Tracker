package ttit.com.shuvo.terraintracker.timeline;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.R;

import static ttit.com.shuvo.terraintracker.timeline.LocationAdapter.firstSelected;
import static ttit.com.shuvo.terraintracker.timeline.LocationAdapter.lastSelected;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.markerClicked;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.positionFromAdapter;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.scrollView;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.selectedAdapterPosition;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.selectedChildAdapterPosition;

public class StoppedLocationAdapter extends RecyclerView.Adapter<StoppedLocationAdapter.SLAHolder> {

    public ArrayList<StoppedLocationTime> stoppedLocationTimes;

    public Context myContext;

    public static boolean isit = false;

    public StoppedLocationAdapter(ArrayList<StoppedLocationTime> stoppedLocationTimes, Context myContext) {
        this.stoppedLocationTimes = stoppedLocationTimes;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public SLAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.stopped_location_details, parent, false);
        SLAHolder ammvh = new SLAHolder(v);
        return ammvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SLAHolder holder, int position) {

        StoppedLocationTime locNA = stoppedLocationTimes.get(position);

        String text = "Stopped for "+ locNA.getStoppedTime()+ "\n" + "at "+ locNA.getLocationName();
        String time = locNA.getLocationTime();

        holder.time.setText(time);
        holder.place.setText(text);
        System.out.println("2nd Adapter: "+ isit);
        if (isit) {
            if (selectedAdapterPosition == positionFromAdapter) {
                if(selectedChildAdapterPosition == position) {
                    if(!firstSelected && !lastSelected) {
                        if (markerClicked) {
                            holder.place.setBackgroundColor(Color.parseColor("#dfe6e9"));
                            TimeLineActivity.scrollToViewChild(scrollView, holder.place.getRootView());
                            isit = false;
                            markerClicked = false;
                        } else {
                            holder.place.setBackgroundColor(Color.parseColor("#dfe6e9"));
                            isit = false;
                        }

                    } else {
                        holder.place.setBackgroundColor(Color.WHITE);
                    }
                } else {
                    holder.place.setBackgroundColor(Color.WHITE);
                }
            } else {
                holder.place.setBackgroundColor(Color.WHITE);
            }

        } else {
            holder.place.setBackgroundColor(Color.WHITE);
        }



    }
    @Override
    public int getItemCount() {
        return stoppedLocationTimes.size();
    }

    public class SLAHolder extends RecyclerView.ViewHolder {

        public TextView place;
        TextView time;

        public SLAHolder(@NonNull View itemView) {
            super(itemView);
            place = itemView.findViewById(R.id.stopped_place_text);
            time = itemView.findViewById(R.id.stopped_when_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isit = true;
                    firstSelected = false;
                    lastSelected = false;
                    markerClicked = false;
                    LatLng latLng = stoppedLocationTimes.get(getAdapterPosition()).getLatLng();
                    String id = stoppedLocationTimes.get(getAdapterPosition()).getMarker_id();
                    String type = stoppedLocationTimes.get(getAdapterPosition()).getMarkerType();
                    selectedAdapterPosition = Integer.parseInt(stoppedLocationTimes.get(getAdapterPosition()).getPositionFromMain());

                    if (selectedChildAdapterPosition != getAdapterPosition()) {
                        selectedChildAdapterPosition = getAdapterPosition();
                    }
                    TimeLineActivity.LocationCalled(latLng,id,type);
                    //notifyDataSetChanged();
                }
            });

        }


    }
}

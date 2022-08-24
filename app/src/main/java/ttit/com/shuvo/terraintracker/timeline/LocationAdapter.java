package ttit.com.shuvo.terraintracker.timeline;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.R;

import static ttit.com.shuvo.terraintracker.timeline.StoppedLocationAdapter.isit;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.markerClicked;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.positionFromAdapter;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.scrollView;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.selectedAdapterPosition;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.viewBottom;
import static ttit.com.shuvo.terraintracker.timeline.TimeLineActivity.viewTop;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocHolder>{

    public ArrayList<LocationNameArray> locationNameArrays;

    public Context myContext;

    private final ClickedItem myClickedItem;

    public static StoppedLocationAdapter stoppedLocationAdapter;

    public static boolean firstSelected = false;
    public static boolean lastSelected = false;

    public LocationAdapter(ArrayList<LocationNameArray> locationNameArrays, Context myContext, ClickedItem cli) {
        this.locationNameArrays = locationNameArrays;
        this.myContext = myContext;
        this.myClickedItem = cli;
    }

    @NonNull
    @Override
    public LocHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.location_details, parent, false);
        LocHolder ammvh = new LocHolder(v,myClickedItem);
        return ammvh;
    }

    @Override
    public void onBindViewHolder(@NonNull LocHolder holder, int position) {

        positionFromAdapter = position;

        if(selectedAdapterPosition == position) {
            viewTop = holder.place.getRootView().getTop();
            viewBottom = holder.place.getRootView().getBottom();
            System.out.println(viewTop);
            System.out.println(viewBottom);
        }

        LocationNameArray locNA = locationNameArrays.get(position);

        boolean isWay = locNA.getWay();

        if (isWay) {
            holder.lastLay.setVisibility(View.GONE);
            holder.midDes.setVisibility(View.GONE);
            holder.wayTrack.setText("You were in:");
            holder.place.setText(locNA.getFirstLocation());
            //holder.place.setClickable(false);
            holder.endTime.setVisibility(View.GONE);
            holder.distancelay.setVisibility(View.GONE);
            holder.timeClay.setVisibility(View.GONE);
            if (locNA.getFirstTime().isEmpty()) {
                holder.startLay.setVisibility(View.GONE);
                holder.startTime.setVisibility(View.GONE);
            } else {
                holder.startLay.setVisibility(View.GONE);
                holder.startTime.setVisibility(View.VISIBLE);
                holder.startTime.setText(locNA.getFirstTime());
            }

            ArrayList<StoppedLocationTime> stoppedLocationTimes = locNA.getStoppedLocationTimes();

            holder.stoppedView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(myContext);
            holder.stoppedView.setLayoutManager(layoutManager);
            stoppedLocationAdapter = new StoppedLocationAdapter(stoppedLocationTimes,myContext);
            holder.stoppedView.setAdapter(stoppedLocationAdapter);

        } else {
            holder.lastLay.setVisibility(View.VISIBLE);
            holder.midDes.setVisibility(View.VISIBLE);
            holder.wayTrack.setText("You went from ");
            //holder.place.setClickable(true);
            holder.place.setText(locNA.getFirstLocation());
            holder.lastPlace.setText(locNA.getLastLocation());
            if (locNA.getFirstTime().isEmpty()) {
                holder.startLay.setVisibility(View.GONE);
                holder.startTime.setVisibility(View.GONE);
            } else {
                holder.startLay.setVisibility(View.VISIBLE);
                holder.startTime.setVisibility(View.VISIBLE);
                holder.startTime.setText(locNA.getFirstTime());
            }

            if (locNA.getLastTime().isEmpty()) {
                holder.endTime.setVisibility(View.GONE);
            } else {
                holder.endTime.setVisibility(View.VISIBLE);
                holder.endTime.setText(locNA.getLastTime());
            }

            if (locNA.getDistance().isEmpty()) {
                holder.distancelay.setVisibility(View.GONE);
            }else {
                holder.distancelay.setVisibility(View.VISIBLE);
                holder.distance.setText(locNA.getDistance());
            }

            if (locNA.getCalcTime().isEmpty()) {
                holder.timeClay.setVisibility(View.GONE);
            } else {
                holder.timeClay.setVisibility(View.VISIBLE);
                holder.calculateTime.setText(locNA.getCalcTime());
            }

            ArrayList<StoppedLocationTime> stoppedLocationTimes = locNA.getStoppedLocationTimes();

            holder.stoppedView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(myContext);
            holder.stoppedView.setLayoutManager(layoutManager);
            stoppedLocationAdapter = new StoppedLocationAdapter(stoppedLocationTimes,myContext);
            holder.stoppedView.setAdapter(stoppedLocationAdapter);




        }
        System.out.println(firstSelected);
        System.out.println(lastSelected);
        System.out.println(selectedAdapterPosition);

        //positionFromAdapter = position;
        System.out.println(positionFromAdapter);



        if(selectedAdapterPosition == position) {
            if (firstSelected && !lastSelected) {
                if (markerClicked) {
                    holder.place.setBackgroundColor(Color.parseColor("#dfe6e9"));
                    System.out.println(holder.place.getBottom());
                    System.out.println(holder.place.getTop());
//                viewTop = holder.place.getTop();
//                viewBottom = holder.place.getBottom();
                    TimeLineActivity.scrollToView(scrollView, holder.place.getRootView());
                    holder.lastPlace.setBackgroundColor(Color.WHITE);
                    firstSelected = false;
                    markerClicked = false;
                } else {
                    holder.place.setBackgroundColor(Color.parseColor("#dfe6e9"));
                    holder.lastPlace.setBackgroundColor(Color.WHITE);
                    firstSelected = false;
                }

            }
            else if (lastSelected && !firstSelected) {
                if (markerClicked) {
                    holder.lastPlace.setBackgroundColor(Color.parseColor("#dfe6e9"));
                    System.out.println(holder.lastPlace.getBottom());
                    System.out.println(holder.lastPlace.getTop());
                    TimeLineActivity.scrollToView(scrollView, holder.lastPlace.getRootView());
                    holder.place.setBackgroundColor(Color.WHITE);
                    lastSelected = false;
                    markerClicked = false;
                } else {
                    holder.lastPlace.setBackgroundColor(Color.parseColor("#dfe6e9"));
//                    System.out.println(holder.lastPlace.getBottom());
//                    System.out.println(holder.lastPlace.getTop());
//                    TimeLineActivity.scrollToView(scrollView, holder.lastPlace.getRootView());
                    holder.place.setBackgroundColor(Color.WHITE);
                    lastSelected = false;
                }

            }
            else {
                holder.place.setBackgroundColor(Color.WHITE);
                holder.lastPlace.setBackgroundColor(Color.WHITE);
            }
        }
        else {
            holder.place.setBackgroundColor(Color.WHITE);
            holder.lastPlace.setBackgroundColor(Color.WHITE);
        }



    }

    @Override
    public int getItemCount() {
        return locationNameArrays.size();
    }

    public class LocHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView place;
        TextView wayTrack;
        LinearLayout midDes;
        LinearLayout lastLay;
        TextView lastPlace;
        LinearLayout startLay;
        TextView startTime;
        TextView endTime;
        LinearLayout distancelay;
        TextView distance;
        LinearLayout timeClay;
        TextView calculateTime;
        RecyclerView stoppedView;

        ClickedItem clickedItem;


        public LocHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);

            place = itemView.findViewById(R.id.place_text);
            wayTrack = itemView.findViewById(R.id.way_or_track);
            midDes = itemView.findViewById(R.id.mid_to_des);
            lastLay = itemView.findViewById(R.id.last_place_lay);
            lastPlace = itemView.findViewById(R.id.last_place_text);
            startLay = itemView.findViewById(R.id.start_time_layout);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
            distancelay =itemView.findViewById(R.id.distance_layout);
            distance = itemView.findViewById(R.id.calculate_distance);
            timeClay = itemView.findViewById(R.id.time_calculate_layout);
            calculateTime = itemView.findViewById(R.id.calculate_time);
            stoppedView = itemView.findViewById(R.id.stopped_location_view);

            this.clickedItem = ci;

            itemView.setOnClickListener(this::onClick);


            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    boolean isWay = locationNameArrays.get(getAdapterPosition()).getWay();
//                    if (!isWay) {
                        lastSelected = false;
                        firstSelected = true;
                        isit = false;
                        markerClicked = false;
                        LatLng first = locationNameArrays.get(getAdapterPosition()).getFirstLatLng();
                        String id = locationNameArrays.get(getAdapterPosition()).getFirstMarkerId();
                        String type = locationNameArrays.get(getAdapterPosition()).getFirstMarkerType();
                        if (selectedAdapterPosition != getAdapterPosition()) {
                            selectedAdapterPosition = getAdapterPosition();
                        }
                        TimeLineActivity.LocationCalled(first,id,type);
                        //notifyDataSetChanged();
//                    }
                }
            });

            lastPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isWay = locationNameArrays.get(getAdapterPosition()).getWay();

                    if (!isWay) {
                        firstSelected = false;
                        lastSelected = true;
                        isit = false;
                        markerClicked = false;
                        LatLng second = locationNameArrays.get(getAdapterPosition()).getSecondLatLng();
                        String id = locationNameArrays.get(getAdapterPosition()).getLastMarkerId();
                        String type = locationNameArrays.get(getAdapterPosition()).getSecondMarkerType();
                        if (selectedAdapterPosition != getAdapterPosition()) {
                            selectedAdapterPosition = getAdapterPosition();
                        }
                        TimeLineActivity.LocationCalled(second,id,type);
                        //notifyDataSetChanged();
                    }

                }
            });

        }

        @Override
        public void onClick(View v) {
            clickedItem.onCategoryClicked(getAdapterPosition());
            Log.i("Distance: ", locationNameArrays.get(getAdapterPosition()).getDistance());
        }
    }
    public interface ClickedItem {
        void onCategoryClicked(int CategoryPosition);
    }


}

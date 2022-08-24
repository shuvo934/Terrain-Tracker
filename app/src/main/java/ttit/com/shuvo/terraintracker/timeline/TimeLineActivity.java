package ttit.com.shuvo.terraintracker.timeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.progressBar.WaitProgress;

import static ttit.com.shuvo.terraintracker.MainPage.HomePage.testDataBlob;
import static ttit.com.shuvo.terraintracker.OracleConnection.createConnection;
import static ttit.com.shuvo.terraintracker.timeline.LocationAdapter.firstSelected;
import static ttit.com.shuvo.terraintracker.timeline.LocationAdapter.lastSelected;
import static ttit.com.shuvo.terraintracker.timeline.StoppedLocationAdapter.isit;

public class TimeLineActivity extends FragmentActivity implements OnMapReadyCallback,LocationAdapter.ClickedItem {

    public static GoogleMap mMap;

    TextView toDate;
    TextView noRecordMsg;

    RecyclerView locationView;
    public static LocationAdapter locationAdapter;
    RecyclerView.LayoutManager layoutManager;

    public static NestedScrollView scrollView;

    ArrayList<LocationNameArray> locationNameArrays;
    ArrayList<PolyLindata> polyLindata;
    ArrayList<MarkerData> markerData;
    public static ArrayList<AllMarkerLists> allMarkerLists;
    HorizontalCalendar horizontalCalendar;

    WaitProgress waitProgress = new WaitProgress();
    private String message = null;
    private Boolean conn = false;
    private Boolean blobNotNull = false;
    private Boolean connected = false;
    private Connection connection;

    String selectedDate = "";

    String downloadFile = "Downloaded_GPX.gpx";
    String todayDate = "";
    String todayFile = "";

    String address = "";
    public static ArrayList<WaypointList> wptList;

    public static ArrayList<ArrrayFile> multiGpxList;

    String emp_id = "";
    public static int selectedAdapterPosition = -1;
    public static int selectedChildAdapterPosition = -1;
    public static int positionFromAdapter = -1;

    public static int viewTop = 0;
    public static int viewBottom = 0;
    public static boolean markerClicked = false;

    // testing
    String totodayDate = "";
    String seselectDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        emp_id = intent.getStringExtra("EMP_ID");
        System.out.println("EMP ID =" + emp_id);

        downloadFile = emp_id+"_Downloaded_GPX.gpx";

        toDate = findViewById(R.id.date_text);
        noRecordMsg = findViewById(R.id.no_record_msg);
        noRecordMsg.setVisibility(View.GONE);
        locationView = findViewById(R.id.location_details_review);
        scrollView = findViewById(R.id.scrollview_data);


        wptList = new ArrayList<>();
        multiGpxList = new ArrayList<>();
        locationNameArrays = new ArrayList<>();
        polyLindata = new ArrayList<>();
        markerData = new ArrayList<>();
        allMarkerLists = new ArrayList<>();

        locationView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        locationView.setLayoutManager(layoutManager);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setZoomControlsEnabled(true);

        Calendar endDates = Calendar.getInstance();
        endDates.add(Calendar.DATE, 0);


        Calendar startDates = Calendar.getInstance();
        startDates.add(Calendar.MONTH, -2);

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMMM, yyyy", Locale.getDefault());
        todayDate = simpleDateFormat.format(today);

        totodayDate = simpleDateFormat.format(today);

        todayDate = todayDate.toUpperCase();
        todayFile = emp_id+"_"+todayDate+"_track.gpx";
        String nowDate = sdf.format(today);
        toDate.setText(nowDate);
        GpxInMapToday();


        System.out.println(Calendar.getInstance().getTime());
        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView).range(startDates,endDates).defaultSelectedDate(Calendar.getInstance()).build();
        horizontalCalendar.selectDate(Calendar.getInstance(),false);
        horizontalCalendar.goToday(false);
        Calendar calendar = horizontalCalendar.getSelectedDate();
        horizontalCalendar.setElevation(2);
        horizontalCalendar.show();

        System.out.println(calendar.getTime());

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {


                mMap.clear();
                wptList = new ArrayList<>();
                multiGpxList = new ArrayList<>();
                locationNameArrays = new ArrayList<>();
                polyLindata = new ArrayList<>();
                markerData = new ArrayList<>();
                allMarkerLists = new ArrayList<>();
                selectedAdapterPosition = -1;
                selectedChildAdapterPosition = -1;
                positionFromAdapter = -1;

                viewTop = 0;
                viewBottom = 0;
                markerClicked = false;


                System.out.println("Position: " + position);
                Date c = date.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd, MMMM, yyyy", Locale.getDefault());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.getDefault());

                String fileName = simpleDateFormat.format(c);
                seselectDate = simpleDateFormat.format(c);
                fileName = fileName.toUpperCase();
                selectedDate = fileName;
                //fileName = "2008_"+fileName+"_track";

                String nowDate = sdf.format(c);

                toDate.setText(nowDate);

                if (selectedDate.equals(todayDate)) {
                    todayFile = emp_id+"_"+fileName+"_track.gpx";
                    noRecordMsg.setVisibility(View.GONE);
                    GpxInMapToday();

                } else {
                    noRecordMsg.setVisibility(View.GONE);
                    if (testDataBlob.isEmpty()) {
                        new Check().execute();
                    } else {
                        int dayNo = 0;
                        try {
                            Date date1 = simpleDateFormat.parse(totodayDate);
                            Date date2 = simpleDateFormat.parse(seselectDate);
                            long diff = date1.getTime() - date2.getTime();
                            System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                            dayNo = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            float days = (diff / (1000*60*60*24));
                            System.out.println((int)days);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (dayNo > 0 && dayNo < 7) {
                            String file = testDataBlob.get(dayNo - 1);
                            GpxInMapTestData(file);

                        } else {
                            new Check().execute();
                        }

                    }

                }


            }

        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                for (int i = 0 ; i < polyLindata.size(); i++) {
                    Polyline polyline = polyLindata.get(i).getPolyline();
                    polyline.setColor(Color.parseColor("#74b9ff"));
                    polyline.setWidth(17);
                }
                for (int i = 0; i < markerData.size(); i++) {
                    Marker marker = markerData.get(i).getMarker();
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
                    marker.hideInfoWindow();
                }
                for (int i = 0 ; i < allMarkerLists.size(); i++) {
                    Marker marker = allMarkerLists.get(i).getMarker();
                    marker.hideInfoWindow();
                    String type = allMarkerLists.get(i).getMarkerType();
                    switch (type) {
                        case "1":
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
                            break;
                        case "2":
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stopped_location_icon));
                            break;
                        case "3":
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_new));
                            break;
                        case "4":
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_new));
                            break;
                    }
                }

                isit = false;
                firstSelected = false;
                lastSelected = false;
                locationAdapter.notifyDataSetChanged();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                markerClicked = true;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                for (int i = 0 ; i < allMarkerLists.size(); i++) {
                    Marker marker1 = allMarkerLists.get(i).getMarker();
                    //marker1.hideInfoWindow();
                    String type = allMarkerLists.get(i).getMarkerType();
                    switch (type) {
                        case "1":
                            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));

                            break;
                        case "2":
                            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stopped_location_icon));

                            break;
                        case "3":
                            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_new));

                            break;
                        case "4":
                            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_new));

                            break;
                    }

                    String id = marker.getId();
                    String allId = marker1.getId();
                    String markerId = allMarkerLists.get(i).getMarkerId();

                    if (id.equals(allId)) {

                        switch (type) {
                            case "1":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon_active));
                                lastSelected = false;
                                firstSelected = true;
                                isit = false;
                                for (int j = 0 ; j< locationNameArrays.size(); j++) {
                                    String marId = locationNameArrays.get(j).getFirstMarkerId();
                                    if (markerId.equals(marId)) {
                                        selectedAdapterPosition = j;
                                    }
                                }
                                break;
                            case "2":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_selected));
                                isit = true;
                                firstSelected = false;
                                lastSelected = false;
                                for (int j = 0 ; j< locationNameArrays.size(); j++) {

                                    ArrayList<StoppedLocationTime> sss = locationNameArrays.get(j).getStoppedLocationTimes();
                                    for (int k = 0; k < sss.size(); k++) {
                                        String marId = sss.get(k).getMarker_id();
                                        if (markerId.equals(marId)) {

                                            selectedAdapterPosition = Integer.parseInt(sss.get(k).getPositionFromMain());
                                            selectedChildAdapterPosition = k;
                                        }
                                    }

                                }
                                break;
                            case "3":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_selected));
                                lastSelected = false;
                                firstSelected = true;
                                isit = false;
                                for (int j = 0 ; j< locationNameArrays.size(); j++) {
                                    String marId = locationNameArrays.get(j).getFirstMarkerId();
                                    if (markerId.equals(marId)) {
                                        selectedAdapterPosition = j;
                                    }
                                }
                                break;
                            case "4":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.finish_loc_icon_selected));
                                firstSelected = false;
                                lastSelected = true;
                                isit = false;
                                for (int j = 0 ; j< locationNameArrays.size(); j++) {
                                    String marId = locationNameArrays.get(j).getLastMarkerId();
                                    if (markerId.equals(marId)) {
                                        selectedAdapterPosition = j;
                                    }
                                }
                                break;
                        }
                    }
                }

                locationAdapter.notifyDataSetChanged();

                return false;
            }
        });
    }

    public static void LocationCalled(LatLng latLng, String markerId, String markerType) {
        locationAdapter.notifyDataSetChanged();
        if (latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            for (int i = 0; i < allMarkerLists.size(); i++) {
                if (!markerId.isEmpty()) {
                    if (markerId.equals(allMarkerLists.get(i).getMarkerId())) {
                        Marker marker = allMarkerLists.get(i).getMarker();
                        marker.showInfoWindow();
                        String type = allMarkerLists.get(i).getMarkerType();
                        switch (type) {
                            case "1":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon_active));
                                break;
                            case "2":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_selected));
                                break;
                            case "3":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_selected));
                                break;
                            case "4":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.finish_loc_icon_selected));
                                break;
                        }

                    } else {
                        Marker marker = allMarkerLists.get(i).getMarker();
                        marker.hideInfoWindow();

                        String type = allMarkerLists.get(i).getMarkerType();
                        switch (type) {
                            case "1":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
                                break;
                            case "2":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stopped_location_icon));
                                break;
                            case "3":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_new));
                                break;
                            case "4":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_new));
                                break;
                        }
                    }
                }
            }
        }
    }

    public void GpxInMap() {

        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        int marker_Id = 0;
        int position = -1;
        String stringFIle = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +  downloadFile;

        File file = new File(stringFIle);

        if (!file.exists()) {
//            Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
            noRecordMsg.setVisibility(View.VISIBLE);
        }
        else {
            wptList = GPXFileDecoder.decodeWPT(file);
            multiGpxList = GPXFileDecoder.multiLine(file);

            if (multiGpxList.isEmpty() && wptList.isEmpty()) {
                Toast.makeText(getApplicationContext(),"File Data Not Found",Toast.LENGTH_SHORT).show();
            }
            else {
                if (wptList.size() == 1) {
                    Log.i("Ekhane", "1 ta");
                    for (int i = 0; i< wptList.size(); i++) {
                        marker_Id++;
                        position++;
                        System.out.println(position);
                        String addss = getAddress(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        LatLng wpt = new LatLng(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        locationNameArrays.add(new LocationNameArray(addss,"",true, wptList.get(i).getTime(),"","","",null,String.valueOf(i),new ArrayList<>(),wpt,null,String.valueOf(marker_Id),"","1","",String.valueOf(position)));

                        Marker marker = mMap.addMarker(new MarkerOptions().position(wpt).title(addss).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)).snippet(wptList.get(i).getTime()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 18));
                        markerData.add(new MarkerData(marker,String.valueOf(i)));

                        allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"1"));

                    }
                } else {
                    for (int i = 0; i< wptList.size(); i++) {
                        marker_Id++;
                        position++;
                        System.out.println(position);
                        String addss = getAddress(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        LatLng wpt = new LatLng(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        locationNameArrays.add(new LocationNameArray(addss,"",true,wptList.get(i).getTime(),"","","",null,String.valueOf(i), new ArrayList<>(),wpt,null,String.valueOf(marker_Id),"","1","",String.valueOf(position)));

                        Marker marker = mMap.addMarker(new MarkerOptions().position(wpt).title(addss).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)).snippet(wptList.get(i).getTime()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 14));
                        markerData.add(new MarkerData(marker,String.valueOf(i)));

                        allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"1"));
                    }
                }

//                        String total = GPXFileDecoder.decoder(file);
//                        if (!total.isEmpty()) {
//                            Toast.makeText(getApplicationContext(), "Total: " + total , Toast.LENGTH_LONG).show();
//                        }

                for (int a = 0; a < multiGpxList.size(); a++) {

                    position++;
                    System.out.println(position);
                    ArrayList<Location> gpxList = multiGpxList.get(a).getMyLatlng();
                    ArrayList<String> timelist = multiGpxList.get(a).getMyTime();
                    String lengthh = multiGpxList.get(a).getDescc();
                    System.out.println(lengthh);
                    String firstLoc = "";
                    String lastLoc = "";
                    String firstTime = "";
                    String lastTime = "";
                    String distance = "";
                    String calculateTime = "";
                    String middleTime = "";
                    ArrayList<StoppedLocationTime> stoppedLocationTimes = new ArrayList<>();

//                    if (gpxList.size() == timelist.size()) {
//                        System.out.println("SOMAN PAISE");
//                    }

                    MarkerOptions opp = new MarkerOptions();
                    for (int z = 0 ; z < timelist.size(); z++) {
                        if (z != timelist.size() - 1) {
                            String oneTime = timelist.get(z);
                            String twoTime = timelist.get(z+1);

                            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());

                            Date first = null;
                            Date last = null;

                            try {
                                first = sdfTime.parse(oneTime);
                                last = sdfTime.parse(twoTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (first != null && last != null) {
                                long millis =  last.getTime() - first.getTime();
                                int hours = (int) (millis / (1000 * 60 * 60));
                                int mins = (int) ((millis / (1000 * 60)) % 60);

                                if (hours == 0) {
                                    middleTime = mins + " Minutes";
                                }else {
                                    if (hours > 1) {
                                        middleTime = hours + " hours " + mins + " Minutes";
                                    } else {
                                        middleTime = hours + " hour " + mins + " Minutes";
                                    }

                                }
                                System.out.println("Calculate Middle Time: "+middleTime);

                                if (hours != 0 || mins >= 5) {
                                    System.out.println("5 min er beshi");
                                    String address = getAddress(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                                    LatLng latLng = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());

                                    opp.position(latLng);
                                    opp.icon(BitmapDescriptorFactory.fromResource(R.drawable.stopped_location_icon));
                                    opp.anchor((float) 0.5,(float) 0.5);
                                    opp.snippet(middleTime);
                                    opp.flat(true);
                                    opp.title(address);
                                    Marker marker = mMap.addMarker(opp);
                                    marker_Id++;
                                    allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"2"));
                                    stoppedLocationTimes.add(new StoppedLocationTime(address, timelist.get(z),middleTime,latLng,String.valueOf(marker_Id),"2",String.valueOf(position)));
                                }
                            }
                        }

                    }

                    if (timelist.size() != 0) {
                        firstTime = timelist.get(0);
                        lastTime = timelist.get(timelist.size()-1);

                        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());

                        Date first = null;
                        Date last = null;

                        try {
                            first = sdfTime.parse(firstTime);
                            last = sdfTime.parse(lastTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (first != null && last != null) {
                            long millis =  last.getTime() - first.getTime();
                            System.out.println("Mili second: "+millis);
                            int hours = (int) (millis / (1000 * 60 * 60));
                            int mins = (int) ((millis / (1000 * 60)) % 60);

                            if (hours == 0) {
                                calculateTime = mins + " Minutes";
                            }else {
                                if (hours > 1) {
                                    calculateTime = hours + " hours " + mins + " Minutes";
                                } else {
                                    calculateTime = hours + " hour " + mins + " Minutes";
                                }

                            }
                            System.out.println("Calculate Time: "+calculateTime);
                        }
                    }


                    int index = lengthh.indexOf(" ");
                    int index2 = lengthh.indexOf(" KM");
                    String substr = "";
                    if (index < 0 && index2 < 0) {
                        substr = "0";
                    } else {
                        Log.i("Index of 1st:", String.valueOf(index));
                        Log.i("Index of 2nd:", String.valueOf(index2));
                        substr=lengthh.substring(index + 1, index2);
                        System.out.println(substr);
                    }

                    distance = substr + " KM";

                    MarkerOptions options = new MarkerOptions();

                    PolylineOptions option = new PolylineOptions().width(17).color(Color.parseColor("#74b9ff")).geodesic(true).clickable(true).zIndex(a);
                    for (int z = 0; z < gpxList.size(); z++) {
                        LatLng point = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                        option.add(point);
                    }


                    Polyline polyline = mMap.addPolyline(option);
                    polyLindata.add(new PolyLindata(polyline,String.valueOf(a)));



                    Double j = 0.0;
                    LatLng firstLatLng = null;
                    LatLng secondLatLng = null;
                    String firstMarkerId = "";
                    String lastMarkerId = "";

                    for (int i = 0; i< gpxList.size(); i++) {

                        LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                        //markerPoints.add(gpx);
                        options.position(gpx);

                        if (i == 0 ) {
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_new));
                            options.anchor((float) 0.5,(float) 0.5);
                            options.snippet("Start Time: "+firstTime+"\nDistance: 0 KM");
                            options.flat(true);

                            firstLoc = getAddress(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                            firstLatLng = gpx;
                            options.title(firstLoc);

                            Marker marker = mMap.addMarker(options);
                            marker_Id++;

                            firstMarkerId = String.valueOf(marker_Id);
                            allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"3"));


                        }else if (i == gpxList.size()-1){
                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_new));
                            options.anchor((float) 0.5,(float) 0.5);
//                            Double diss = CalculationByDistance(preGpx, gpx);
//                            j  = j + diss;
//                            options.snippet(String.format("%.3f", j) + " KM");
                            options.snippet("End Time: "+lastTime+"\nDistance: "+substr + " KM");
                            options.flat(true);

                            lastLoc = getAddress(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                            secondLatLng = gpx;

                            options.title(lastLoc);

                            Marker marker = mMap.addMarker(options);
                            marker_Id++;
                            lastMarkerId = String.valueOf(marker_Id);
                            allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"4"));

                        } else {
//                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());
//
//                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent_circle));
//                            options.anchor((float) 0.5,(float) 0.5);
//                            Double diss = CalculationByDistance(preGpx, gpx);
//                            j  = j + diss;
//                            options.snippet(String.format("%.3f", j) + " KM");
//                            mMap.addMarker(options).setTitle("On Going Road");
                        }

                    }
                    locationNameArrays.add(new LocationNameArray(firstLoc,lastLoc,false,firstTime,lastTime,distance,calculateTime,String.valueOf(a),null,stoppedLocationTimes,firstLatLng,secondLatLng,firstMarkerId,lastMarkerId,"3","4",String.valueOf(position)));
                    int i = (gpxList.size() - 1) / 2;
                    LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 13));

                }
            }

            waitProgress.dismiss();
        }
//                try {
//                    BufferedReader bufferedReader = new BufferedReader(new FileReader(stringFIle));
//                    String line;String input = "";
//
//                    while ((line = bufferedReader.readLine()) != null) {
//                        input += line + '\n';
//                    }
//
//                    bufferedReader.close();
//
//                    System.out.println(input);
//                    if (input.contains("</gpx>")){
//                        System.out.println("Got It");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


        locationAdapter = new LocationAdapter(locationNameArrays, TimeLineActivity.this,TimeLineActivity.this);
        locationView.setAdapter(locationAdapter);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                scrollView.arrowScroll(View.FOCUS_UP);
                scrollView.pageScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0,0);
            }
        });

    }

    public void GpxInMapToday() {

        int marker_Id = 0;
        int position = -1;
        String stringFIle = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +  todayFile;

        File file = new File(stringFIle);

        if (!file.exists()) {
            //Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
            noRecordMsg.setVisibility(View.VISIBLE);
        } else {
            wptList = GPXFileDecoder.decodeWPT(file);
            multiGpxList = GPXFileDecoder.multiLine(file);

            if (multiGpxList.isEmpty() && wptList.isEmpty()) {
                Toast.makeText(getApplicationContext(),"File Data Not Found",Toast.LENGTH_SHORT).show();
            }
            else {
                if (wptList.size() == 1) {
                    Log.i("Ekhane", "1 ta");
                    for (int i = 0; i< wptList.size(); i++) {
                        marker_Id++;
                        position++;
                        System.out.println(position);
                        String addss = getAddress(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        LatLng wpt = new LatLng(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        locationNameArrays.add(new LocationNameArray(addss,"",true, wptList.get(i).getTime(),"","","",null,String.valueOf(i),new ArrayList<>(),wpt,null,String.valueOf(marker_Id),"","1","",String.valueOf(position)));

                        Marker marker = mMap.addMarker(new MarkerOptions().position(wpt).title(addss).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)).snippet(wptList.get(i).getTime()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 18));
                        markerData.add(new MarkerData(marker,String.valueOf(i)));
                        allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"1"));
                    }
                } else {
                    for (int i = 0; i< wptList.size(); i++) {
                        marker_Id++;
                        position++;
                        System.out.println(position);
                        String addss = getAddress(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        LatLng wpt = new LatLng(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        locationNameArrays.add(new LocationNameArray(addss,"",true,wptList.get(i).getTime(),"","","",null,String.valueOf(i), new ArrayList<>(),wpt,null,String.valueOf(marker_Id),"","1","",String.valueOf(position)));

                        Marker marker = mMap.addMarker(new MarkerOptions().position(wpt).title(addss).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)).snippet(wptList.get(i).getTime()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 14));
                        markerData.add(new MarkerData(marker,String.valueOf(i)));
                        allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"1"));
                    }
                }

//                        String total = GPXFileDecoder.decoder(file);
//                        if (!total.isEmpty()) {
//                            Toast.makeText(getApplicationContext(), "Total: " + total , Toast.LENGTH_LONG).show();
//                        }

                for (int a = 0; a < multiGpxList.size(); a++) {

                    position++;
                    System.out.println(position);

                    ArrayList<Location> gpxList = multiGpxList.get(a).getMyLatlng();
                    ArrayList<String> timelist = multiGpxList.get(a).getMyTime();
                    String lengthh = multiGpxList.get(a).getDescc();
                    System.out.println(lengthh);
                    String firstLoc = "";
                    String lastLoc = "";
                    String firstTime = "";
                    String lastTime = "";
                    String distance = "";
                    String calculateTime = "";
                    String middleTime = "";

                    ArrayList<StoppedLocationTime> stoppedLocationTimes = new ArrayList<>();

                    MarkerOptions opp = new MarkerOptions();
                    for (int z = 0 ; z < timelist.size(); z++) {
                        if (z != timelist.size() - 1) {
                            String oneTime = timelist.get(z);
                            String twoTime = timelist.get(z+1);

                            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());

                            Date first = null;
                            Date last = null;

                            try {
                                first = sdfTime.parse(oneTime);
                                last = sdfTime.parse(twoTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (first != null && last != null) {
                                long millis =  last.getTime() - first.getTime();
                                int hours = (int) (millis / (1000 * 60 * 60));
                                int mins = (int) ((millis / (1000 * 60)) % 60);

                                if (hours == 0) {
                                    middleTime = mins + " Minutes";
                                }else {
                                    if (hours > 1) {
                                        middleTime = hours + " hours " + mins + " Minutes";
                                    } else {
                                        middleTime = hours + " hour " + mins + " Minutes";
                                    }

                                }
                                System.out.println("Calculate Middle Time: "+middleTime);

                                if (hours != 0 || mins >= 5) {
                                    System.out.println("5 min er beshi");
                                    String address = getAddress(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                                    LatLng latLng = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());

                                    opp.position(latLng);
                                    opp.snippet(middleTime);
                                    opp.icon(BitmapDescriptorFactory.fromResource(R.drawable.stopped_location_icon));
                                    opp.anchor((float) 0.5,(float) 0.5);
                                    opp.flat(true);
                                    opp.title(address);
                                    Marker marker = mMap.addMarker(opp);
                                    marker_Id++;
                                    allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"2"));
                                    stoppedLocationTimes.add(new StoppedLocationTime(address, timelist.get(z),middleTime,latLng,String.valueOf(marker_Id),"2",String.valueOf(position)));
                                }
                            }
                        }

                    }


                    if (timelist.size() != 0) {
                        firstTime = timelist.get(0);
                        lastTime = timelist.get(timelist.size()-1);

                        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());

                        Date first = null;
                        Date last = null;

                        try {
                            first = sdfTime.parse(firstTime);
                            last = sdfTime.parse(lastTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (first != null && last != null) {
                            long millis =  last.getTime() - first.getTime();
                            int hours = (int) (millis / (1000 * 60 * 60));
                            int mins = (int) ((millis / (1000 * 60)) % 60);

                            if (hours == 0) {
                                calculateTime = mins + " Minutes";
                            }else {
                                if (hours > 1) {
                                    calculateTime = hours + " hours " + mins + " Minutes";
                                } else {
                                    calculateTime = hours + " hour " + mins + " Minutes";
                                }

                            }
                            System.out.println("Calculate Time: "+calculateTime);
                        }
                    }


                    int index = lengthh.indexOf(" ");
                    int index2 = lengthh.indexOf(" KM");
                    String substr = "";
                    if (index < 0 && index2 < 0) {
                        substr = "0";
                    } else {
                        Log.i("Index of 1st:", String.valueOf(index));
                        Log.i("Index of 2nd:", String.valueOf(index2));
                        substr=lengthh.substring(index + 1, index2);
                        System.out.println(substr);
                    }

                    distance = substr + " KM";

                    MarkerOptions options = new MarkerOptions();

                    PolylineOptions option = new PolylineOptions().width(17).color(Color.parseColor("#74b9ff")).geodesic(true).clickable(true).zIndex(a);
                    for (int z = 0; z < gpxList.size(); z++) {
                        LatLng point = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                        option.add(point);
                    }


                    Polyline polyline = mMap.addPolyline(option);
                    polyLindata.add(new PolyLindata(polyline,String.valueOf(a)));



                    Double j = 0.0;
                    LatLng firstLatLng = null;
                    LatLng secondLatLng = null;
                    String firstMarkerId = "";
                    String lastMarkerId = "";

                    for (int i = 0; i< gpxList.size(); i++) {

                        LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                        //markerPoints.add(gpx);
                        options.position(gpx);

                        if (i == 0 ) {
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_new));
                            options.anchor((float) 0.5,(float) 0.5);
                            options.snippet("Start Time: "+firstTime+"\nDistance: 0 KM");
                            options.flat(true);

                            firstLoc = getAddress(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                            firstLatLng = gpx;
                            options.title(firstLoc);

                            Marker marker = mMap.addMarker(options);
                            marker_Id++;

                            firstMarkerId = String.valueOf(marker_Id);
                            allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"3"));

                        }else if (i == gpxList.size()-1){
                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_new));
                            options.anchor((float) 0.5,(float) 0.5);
//                            Double diss = CalculationByDistance(preGpx, gpx);
//                            j  = j + diss;
//                            options.snippet(String.format("%.3f", j) + " KM");
                            options.snippet("End Time: "+lastTime+"\nDistance: "+substr + " KM");
                            options.flat(true);

                            lastLoc = getAddress(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                            secondLatLng = gpx;
                            options.title(lastLoc);

                            Marker marker = mMap.addMarker(options);
                            marker_Id++;
                            lastMarkerId = String.valueOf(marker_Id);
                            allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"4"));
                        } else {
//                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());
//
//                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent_circle));
//                            options.anchor((float) 0.5,(float) 0.5);
//                            Double diss = CalculationByDistance(preGpx, gpx);
//                            j  = j + diss;
//                            options.snippet(String.format("%.3f", j) + " KM");
//                            mMap.addMarker(options).setTitle("On Going Road");
                        }

                    }
                    locationNameArrays.add(new LocationNameArray(firstLoc,lastLoc,false,firstTime,lastTime,distance,calculateTime,String.valueOf(a),null,stoppedLocationTimes,firstLatLng,secondLatLng,firstMarkerId,lastMarkerId,"3","4",String.valueOf(position)));
                    int i = (gpxList.size() - 1) / 2;
                    LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 13));


                }
            }


        }
//                try {
//                    BufferedReader bufferedReader = new BufferedReader(new FileReader(stringFIle));
//                    String line;String input = "";
//
//                    while ((line = bufferedReader.readLine()) != null) {
//                        input += line + '\n';
//                    }
//
//                    bufferedReader.close();
//
//                    System.out.println(input);
//                    if (input.contains("</gpx>")){
//                        System.out.println("Got It");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


        locationAdapter = new LocationAdapter(locationNameArrays, TimeLineActivity.this,TimeLineActivity.this);
        locationView.setAdapter(locationAdapter);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                scrollView.arrowScroll(View.FOCUS_UP);
                scrollView.pageScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0,0);

            }
        });

    }

    public void GpxInMapTestData(String testFile) {

        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        int marker_Id = 0;
        int position = -1;
        String stringFIle = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +  testFile;

        File file = new File(stringFIle);

        if (!file.exists()) {
//            Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
            noRecordMsg.setVisibility(View.VISIBLE);
        }
        else {
            wptList = GPXFileDecoder.decodeWPT(file);
            multiGpxList = GPXFileDecoder.multiLine(file);

            if (multiGpxList.isEmpty() && wptList.isEmpty()) {
                Toast.makeText(getApplicationContext(),"File Data Not Found",Toast.LENGTH_SHORT).show();
            }
            else {
                if (wptList.size() == 1) {
                    Log.i("Ekhane", "1 ta");
                    for (int i = 0; i< wptList.size(); i++) {
                        marker_Id++;
                        position++;
                        System.out.println(position);
                        String addss = getAddress(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        LatLng wpt = new LatLng(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        locationNameArrays.add(new LocationNameArray(addss,"",true, wptList.get(i).getTime(),"","","",null,String.valueOf(i),new ArrayList<>(),wpt,null,String.valueOf(marker_Id),"","1","",String.valueOf(position)));

                        Marker marker = mMap.addMarker(new MarkerOptions().position(wpt).title(addss).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)).snippet(wptList.get(i).getTime()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 18));
                        markerData.add(new MarkerData(marker,String.valueOf(i)));

                        allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"1"));

                    }
                } else {
                    for (int i = 0; i< wptList.size(); i++) {
                        marker_Id++;
                        position++;
                        System.out.println(position);
                        String addss = getAddress(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        LatLng wpt = new LatLng(wptList.get(i).getLocation().getLatitude(), wptList.get(i).getLocation().getLongitude());
                        locationNameArrays.add(new LocationNameArray(addss,"",true,wptList.get(i).getTime(),"","","",null,String.valueOf(i), new ArrayList<>(),wpt,null,String.valueOf(marker_Id),"","1","",String.valueOf(position)));

                        Marker marker = mMap.addMarker(new MarkerOptions().position(wpt).title(addss).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)).snippet(wptList.get(i).getTime()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wpt, 14));
                        markerData.add(new MarkerData(marker,String.valueOf(i)));

                        allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"1"));
                    }
                }

//                        String total = GPXFileDecoder.decoder(file);
//                        if (!total.isEmpty()) {
//                            Toast.makeText(getApplicationContext(), "Total: " + total , Toast.LENGTH_LONG).show();
//                        }

                for (int a = 0; a < multiGpxList.size(); a++) {

                    position++;
                    System.out.println(position);
                    ArrayList<Location> gpxList = multiGpxList.get(a).getMyLatlng();
                    ArrayList<String> timelist = multiGpxList.get(a).getMyTime();
                    String lengthh = multiGpxList.get(a).getDescc();
                    System.out.println(lengthh);
                    String firstLoc = "";
                    String lastLoc = "";
                    String firstTime = "";
                    String lastTime = "";
                    String distance = "";
                    String calculateTime = "";
                    String middleTime = "";
                    ArrayList<StoppedLocationTime> stoppedLocationTimes = new ArrayList<>();

//                    if (gpxList.size() == timelist.size()) {
//                        System.out.println("SOMAN PAISE");
//                    }

                    MarkerOptions opp = new MarkerOptions();
                    for (int z = 0 ; z < timelist.size(); z++) {
                        if (z != timelist.size() - 1) {
                            String oneTime = timelist.get(z);
                            String twoTime = timelist.get(z+1);

                            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());

                            Date first = null;
                            Date last = null;

                            try {
                                first = sdfTime.parse(oneTime);
                                last = sdfTime.parse(twoTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (first != null && last != null) {
                                long millis =  last.getTime() - first.getTime();
                                int hours = (int) (millis / (1000 * 60 * 60));
                                int mins = (int) ((millis / (1000 * 60)) % 60);

                                if (hours == 0) {
                                    middleTime = mins + " Minutes";
                                }else {
                                    if (hours > 1) {
                                        middleTime = hours + " hours " + mins + " Minutes";
                                    } else {
                                        middleTime = hours + " hour " + mins + " Minutes";
                                    }

                                }
                                System.out.println("Calculate Middle Time: "+middleTime);

                                if (hours != 0 || mins >= 5) {
                                    System.out.println("5 min er beshi");
                                    String address = getAddress(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                                    LatLng latLng = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());

                                    opp.position(latLng);
                                    opp.icon(BitmapDescriptorFactory.fromResource(R.drawable.stopped_location_icon));
                                    opp.anchor((float) 0.5,(float) 0.5);
                                    opp.snippet(middleTime);
                                    opp.flat(true);
                                    opp.title(address);
                                    Marker marker = mMap.addMarker(opp);
                                    marker_Id++;
                                    allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"2"));
                                    stoppedLocationTimes.add(new StoppedLocationTime(address, timelist.get(z),middleTime,latLng,String.valueOf(marker_Id),"2",String.valueOf(position)));
                                }
                            }
                        }

                    }

                    if (timelist.size() != 0) {
                        firstTime = timelist.get(0);
                        lastTime = timelist.get(timelist.size()-1);

                        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());

                        Date first = null;
                        Date last = null;

                        try {
                            first = sdfTime.parse(firstTime);
                            last = sdfTime.parse(lastTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (first != null && last != null) {
                            long millis =  last.getTime() - first.getTime();
                            System.out.println("Mili second: "+millis);
                            int hours = (int) (millis / (1000 * 60 * 60));
                            int mins = (int) ((millis / (1000 * 60)) % 60);

                            if (hours == 0) {
                                calculateTime = mins + " Minutes";
                            }else {
                                if (hours > 1) {
                                    calculateTime = hours + " hours " + mins + " Minutes";
                                } else {
                                    calculateTime = hours + " hour " + mins + " Minutes";
                                }

                            }
                            System.out.println("Calculate Time: "+calculateTime);
                        }
                    }


                    int index = lengthh.indexOf(" ");
                    int index2 = lengthh.indexOf(" KM");
                    String substr = "";
                    if (index < 0 && index2 < 0) {
                        substr = "0";
                    } else {
                        Log.i("Index of 1st:", String.valueOf(index));
                        Log.i("Index of 2nd:", String.valueOf(index2));
                        substr=lengthh.substring(index + 1, index2);
                        System.out.println(substr);
                    }

                    distance = substr + " KM";

                    MarkerOptions options = new MarkerOptions();

                    PolylineOptions option = new PolylineOptions().width(17).color(Color.parseColor("#74b9ff")).geodesic(true).clickable(true).zIndex(a);
                    for (int z = 0; z < gpxList.size(); z++) {
                        LatLng point = new LatLng(gpxList.get(z).getLatitude(), gpxList.get(z).getLongitude());
                        option.add(point);
                    }


                    Polyline polyline = mMap.addPolyline(option);
                    polyLindata.add(new PolyLindata(polyline,String.valueOf(a)));



                    Double j = 0.0;
                    LatLng firstLatLng = null;
                    LatLng secondLatLng = null;
                    String firstMarkerId = "";
                    String lastMarkerId = "";

                    for (int i = 0; i< gpxList.size(); i++) {

                        LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                        //markerPoints.add(gpx);
                        options.position(gpx);

                        if (i == 0 ) {
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.star_loc_icon_new));
                            options.anchor((float) 0.5,(float) 0.5);
                            options.snippet("Start Time: "+firstTime+"\nDistance: 0 KM");
                            options.flat(true);

                            firstLoc = getAddress(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                            firstLatLng = gpx;
                            options.title(firstLoc);

                            Marker marker = mMap.addMarker(options);
                            marker_Id++;

                            firstMarkerId = String.valueOf(marker_Id);
                            allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"3"));


                        }else if (i == gpxList.size()-1){
                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());

                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_loc_icon_new));
                            options.anchor((float) 0.5,(float) 0.5);
//                            Double diss = CalculationByDistance(preGpx, gpx);
//                            j  = j + diss;
//                            options.snippet(String.format("%.3f", j) + " KM");
                            options.snippet("End Time: "+lastTime+"\nDistance: "+substr + " KM");
                            options.flat(true);

                            lastLoc = getAddress(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());

                            secondLatLng = gpx;

                            options.title(lastLoc);

                            Marker marker = mMap.addMarker(options);
                            marker_Id++;
                            lastMarkerId = String.valueOf(marker_Id);
                            allMarkerLists.add(new AllMarkerLists(marker,String.valueOf(marker_Id),"4"));

                        } else {
//                            LatLng preGpx = new LatLng(gpxList.get(i-1).getLatitude(), gpxList.get(i-1).getLongitude());
//
//                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent_circle));
//                            options.anchor((float) 0.5,(float) 0.5);
//                            Double diss = CalculationByDistance(preGpx, gpx);
//                            j  = j + diss;
//                            options.snippet(String.format("%.3f", j) + " KM");
//                            mMap.addMarker(options).setTitle("On Going Road");
                        }

                    }
                    locationNameArrays.add(new LocationNameArray(firstLoc,lastLoc,false,firstTime,lastTime,distance,calculateTime,String.valueOf(a),null,stoppedLocationTimes,firstLatLng,secondLatLng,firstMarkerId,lastMarkerId,"3","4",String.valueOf(position)));
                    int i = (gpxList.size() - 1) / 2;
                    LatLng gpx = new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 13));

                }
            }


        }
//                try {
//                    BufferedReader bufferedReader = new BufferedReader(new FileReader(stringFIle));
//                    String line;String input = "";
//
//                    while ((line = bufferedReader.readLine()) != null) {
//                        input += line + '\n';
//                    }
//
//                    bufferedReader.close();
//
//                    System.out.println(input);
//                    if (input.contains("</gpx>")){
//                        System.out.println("Got It");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


        locationAdapter = new LocationAdapter(locationNameArrays, TimeLineActivity.this,TimeLineActivity.this);
        locationView.setAdapter(locationAdapter);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                scrollView.fullScroll(NestedScrollView.FOCUS_UP);
                scrollView.arrowScroll(View.FOCUS_UP);
                scrollView.pageScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0,0);
            }
        });

        waitProgress.dismiss();
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(TimeLineActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (Geocoder.isPresent()) {
                Address obj = addresses.get(0);
                String adds = obj.getAddressLine(0);
                String add = "Address from GeoCODE: ";
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();
                add = add + "\n" + obj.getFeatureName();
                add = add + "\n" + obj.getPhone();
                add = add + "\n" + obj.getPremises();
                add = add + "\n" + obj.getSubLocality();
                add = add + "\n" + obj.getThoroughfare();
                add = add + "\n" + obj.getUrl();

                Log.v("IGA", "Address: " + add);
                Log.v("NEW ADD", "Address: " + adds);
                address = adds;
                // Toast.makeText(this, "Address=>" + add,
                // Toast.LENGTH_SHORT).show();

                //place.setText(address);

            }

            return address;

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onCategoryClicked(int CategoryPosition)  {
        String  polyId = locationNameArrays.get(CategoryPosition).getPolyId();
        String distance = locationNameArrays.get(CategoryPosition).getDistance();
        String markerId = locationNameArrays.get(CategoryPosition).getMarId();
        System.out.println(polyId);
        if (polyId != null) {
            for (int i = 0; i < polyLindata.size(); i++) {
                Polyline polyline = polyLindata.get(i).getPolyline();
                String poid = polyLindata.get(i).getId();

                if (polyId.equals(poid)) {
                    polyline.setColor(Color.parseColor("#1e3799"));
                    polyline.setWidth(30);
                    int size = polyline.getPoints().size();
                    size = size / 2;
                    Double latitude = polyline.getPoints().get(size).latitude;
                    Double longitude = polyline.getPoints().get(size).longitude;
                    LatLng gpx = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpx, 14));
                    Toast.makeText(getApplicationContext(),distance,Toast.LENGTH_SHORT).show();
                } else {
                    polyline.setColor(Color.parseColor("#74b9ff"));
                    polyline.setWidth(17);
                    int size = polyline.getPoints().size();
                }
            }
        } else {
            for (int i = 0 ; i < polyLindata.size(); i++) {
                Polyline polyline = polyLindata.get(i).getPolyline();
                polyline.setColor(Color.parseColor("#74b9ff"));
                polyline.setWidth(17);
            }
        }

        if (markerId != null) {
            for (int i = 0 ; i < markerData.size(); i++) {
                Marker marker = markerData.get(i).getMarker();
                LatLng latLng = marker.getPosition();
                if (markerId.equals(markerData.get(i).getId())) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon_active));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    marker.showInfoWindow();
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
                    marker.hideInfoWindow();
                }
            }
        } else {
            for (int i = 0; i < markerData.size(); i++) {
                Marker marker = markerData.get(i).getMarker();
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
                marker.hideInfoWindow();
            }
        }


        //mMap.setOnPolylineClickListener(this);
    }

    public boolean isConnected () {
        boolean connected = false;
        boolean isMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public boolean isOnline () {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public class Check extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(), "WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                GetData();
                if (connected) {
                    conn = true;
                    message = "Internet Connected";
                }


            } else {
                conn = false;
                message = "Not Connected";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if (conn) {

                waitProgress.dismiss();
                conn = false;
                connected = false;

                System.out.println("GPX File Created");

                if (blobNotNull) {
                    GpxInMap();
                } else {
                    locationAdapter = new LocationAdapter(locationNameArrays, TimeLineActivity.this,TimeLineActivity.this);
                    locationView.setAdapter(locationAdapter);
//                    Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
                    noRecordMsg.setVisibility(View.VISIBLE);
                }

                blobNotNull = false;




            } else {
                waitProgress.dismiss();
                wptList = new ArrayList<>();
                multiGpxList = new ArrayList<>();
                locationNameArrays = new ArrayList<>();
                polyLindata = new ArrayList<>();
                markerData = new ArrayList<>();

                locationAdapter = new LocationAdapter(locationNameArrays, TimeLineActivity.this,TimeLineActivity.this);
                locationView.setAdapter(locationAdapter);

                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(TimeLineActivity.this)
                        .setMessage("Internet not Connected")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel",null)
                        .show();


                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Check().execute();
                        dialog.dismiss();
                    }
                });

                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    public void GetData() {

        try {
            this.connection = createConnection();

            PreparedStatement ps = connection.prepareStatement("Select ELR_FILE_NAME, ELR_FILETYPE , ELR_LOCATION_FILE from EMP_LOCATION_RECORD where ELR_EMP_ID = "+emp_id+" AND ELR_DATE = TO_DATE('"+selectedDate+"','DD-MON-YY')");

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Blob b = resultSet.getBlob(3);
                String fileName = resultSet.getString(1)+resultSet.getString(2);

                if (b != null && b.length() != 0) {
                    System.out.println("BLOB paise");
                    File myExternalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),downloadFile);

                    InputStream r = b.getBinaryStream();
                    FileWriter fw=new FileWriter(myExternalFile);
                    int i;
                    while((i=r.read())!=-1)
                        fw.write((char)i);
                    fw.close();
                    blobNotNull = true;
                } else {
                    System.out.println("BLOB pai nai");
                    blobNotNull = false;
                }

            }
            connected = true;
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void scrollToView (final NestedScrollView scrollView, final View view) {
        view.requestFocus();
        final Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);

        //if (!view.getLocalVisibleRect(scrollBounds)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                   // scrollView.smoothScrollTo(0, view.getBottom());
                    int vTop = view.getTop();
                    int vBottom = view.getBottom();
                    int sHeight = scrollView.getBottom();
                    System.out.println("View Top: "+vTop);
                    System.out.println("View Bottom: " + vBottom);
                    System.out.println("Scroll Bottom: "+ sHeight);
                    //scrollView.smoothScrollTo(0,((vTop + vBottom) / 2) - (sHeight / 2) );
                    scrollView.smoothScrollTo(0,((vTop + vBottom - sHeight) / 2));
                }
            });
        //}

//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                int vTop = view.getTop();
//                int vBottom = view.getBottom();
//                int sHeight = scrollView.getBottom();
//                scrollView.smoothScrollTo(((vTop + vBottom - sHeight) / 2), 0);
//                scrollView.smoothScrollTo(((vTop + vBottom) / 2) - (sHeight / 2), 0);
//            }
//        });
    }

    public static void scrollToViewChild (final NestedScrollView scrollView, final View view) {
        view.requestFocus();
        final Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);

        //if (!view.getLocalVisibleRect(scrollBounds)) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // scrollView.smoothScrollTo(0, view.getBottom());
                int vTop = view.getTop()+viewTop;
                int vBottom = view.getBottom()+viewBottom;
                int sHeight = scrollView.getBottom();
                System.out.println("View Top: "+vTop);
                System.out.println("View Bottom: " + vBottom);
                System.out.println("Scroll Bottom: "+ sHeight);
                //scrollView.smoothScrollTo(0,((vTop + vBottom) / 2) - (sHeight / 2) );
                scrollView.smoothScrollTo(0,((vTop + vBottom - sHeight) / 2));
            }
        });
        //}

//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                int vTop = view.getTop();
//                int vBottom = view.getBottom();
//                int sHeight = scrollView.getBottom();
//                scrollView.smoothScrollTo(((vTop + vBottom - sHeight) / 2), 0);
//                scrollView.smoothScrollTo(((vTop + vBottom) / 2) - (sHeight / 2), 0);
//            }
//        });
    }
}
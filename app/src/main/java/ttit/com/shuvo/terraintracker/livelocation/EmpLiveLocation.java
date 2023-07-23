package ttit.com.shuvo.terraintracker.livelocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ttit.com.shuvo.terraintracker.R;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static ttit.com.shuvo.terraintracker.OracleConnection.DEFAULT_USERNAME;

public class EmpLiveLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private GoogleApiClient googleApiClient;
    String emp_id = "";
    String emp_name = "";
    String url = "http://103.56.208.123:8001/apex/tracker/rest-v4/getloctracker/?empno=";
    boolean allEmp = false;
    String div_id = "";
    String dep_id = "";
    String des_id = "";

    Spinner layer;

    ArrayList<AllEmpLocationLists> allEmpLocationLists;

    Marker empLocationMarker;
    Marker detailsMarker;
    Circle locationAccuracyCircle;

    List<Marker> allMarkers;
    List<Marker> allDetailsMarker;
    List<Circle> allLocationAccCircle;

    TextView speedText;
    TextView textView;
    IconGenerator iconGenerator;
    View inflatedView;

    CardView speedCard;
    CardView moveEmpLoc;
    LatLng empLatLng;
    Boolean emplatlngChange = false;

    Handler timerHandler = new Handler();
    long startTime = 0;
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            getUpdatedLocation();
            //Toast.makeText(getApplicationContext(), String.format("%d:%02d", minutes, seconds),Toast.LENGTH_SHORT).show();
            //timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_live_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.live_map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        speedText = findViewById(R.id.speed_text);
        moveEmpLoc = findViewById(R.id.move_card);
        speedCard = findViewById(R.id.speed_card);
        layer = findViewById(R.id.spinner_layer);


        iconGenerator = new IconGenerator(EmpLiveLocation.this);
        iconGenerator.setBackground(EmpLiveLocation.this.getDrawable(R.drawable.bg_custom_marker));
        inflatedView = View.inflate(EmpLiveLocation.this, R.layout.marker_custom, null);
        textView = (TextView) inflatedView.findViewById(R.id.test_text);

        moveEmpLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empLatLng != null) {
                    emplatlngChange = false;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(empLatLng,15));
                    moveEmpLoc.setVisibility(View.GONE);
                }
            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("NORMAL");
        categories.add("TRAFFIC");
        categories.add("SATELLITE");
        categories.add("TERRAIN");
        categories.add("HYBRID");
        categories.add("NO LANDMARK");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        layer.setAdapter(spinnerAdapter);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableGPS();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //getUpdatedLocation();

//        startTime = System.currentTimeMillis();
//        timerHandler.postDelayed(timerRunnable, 0);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                if (emplatlngChange && !allEmp) {
                    moveEmpLoc.setVisibility(View.VISIBLE);
                }


            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                emplatlngChange = true;
            }
        });

        layer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                switch (name) {
                    case "NORMAL":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            EmpLiveLocation.this, R.raw.normal));

                            if (!success) {
                                Log.i("Failed ", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("Style ", "Can't find style. Error: ", e);
                        }
                        mMap.setTrafficEnabled(false);
                        break;
                    case "SATELLITE":
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mMap.setTrafficEnabled(false);
                        break;
                    case "TERRAIN":
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mMap.setTrafficEnabled(false);
                        break;
                    case "HYBRID":
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mMap.setTrafficEnabled(false);
                        break;
                    case "TRAFFIC":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            EmpLiveLocation.this, R.raw.normal));

                            if (!success) {
                                Log.i("Failed ", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("Style ", "Can't find style. Error: ", e);
                        }
                        mMap.setTrafficEnabled(true);
                        break;
                    case "NO LANDMARK":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            EmpLiveLocation.this, R.raw.no_landmark));

                            if (!success) {
                                Log.i("Failed ", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("Style ", "Can't find style. Error: ", e);
                        }
                        mMap.setTrafficEnabled(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setLocationMarkerForAll() {
        if (allEmpLocationLists != null) {

            for (int i = 0; i < allEmpLocationLists.size(); i++) {
                LatLng latLng = new LatLng(Double.parseDouble(allEmpLocationLists.get(i).getLat()),Double.parseDouble(allEmpLocationLists.get(i).getLng()));
                float bear = Float.parseFloat(allEmpLocationLists.get(i).getBearing());
                float acc = Float.parseFloat(allEmpLocationLists.get(i).getAccuracy());
                if (allMarkers != null) {
                    Marker marker = allMarkers.get(i);
                    if (marker == null) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_icon));
                        markerOptions.rotation(bear);
                        markerOptions.title(allEmpLocationLists.get(i).getAddress());
                        markerOptions.snippet("Speed: "+allEmpLocationLists.get(i).getSpeed());
                        markerOptions.anchor((float) 0.5, (float) 0.5);
                        markerOptions.flat(true);
                        marker = mMap.addMarker(markerOptions);
                        allMarkers.set(i,marker);
                        System.out.println("NULL MARKER");
                    }
                    else{
                        marker.setPosition(latLng);
                        marker.setRotation(bear);
                        marker.setTitle(allEmpLocationLists.get(i).getAddress());
                        marker.setSnippet("Speed: "+allEmpLocationLists.get(i).getSpeed());
                        allMarkers.set(i,marker);
                        System.out.println("NOT NULL MARKER");
                    }
                }

                if (allLocationAccCircle != null) {
                    Circle circle = allLocationAccCircle.get(i);
                    if (circle == null) {
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(latLng);
                        circleOptions.strokeWidth(4);
                        circleOptions.strokeColor(Color.parseColor("#d95206"));
                        circleOptions.fillColor(Color.argb(30,242,165,21));
                        circleOptions.radius(acc);
                        circle = mMap.addCircle(circleOptions);
                        allLocationAccCircle.set(i,circle);
                    }
                    else {
                        circle.setCenter(latLng);
                        circle.setRadius(acc);
                        allLocationAccCircle.set(i,circle);
                    }
                }

                if (allDetailsMarker != null) {
                    Marker marker = allDetailsMarker.get(i);
                    if (marker == null) {
                        textView.setText(allEmpLocationLists.get(i).getEmpName()+"\n"+allEmpLocationLists.get(i).getTime());
                        iconGenerator.setContentView(inflatedView);
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(latLng).anchor(0,0)
                                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())));
                        allDetailsMarker.set(i,marker);
                    }
                    else {
                        textView.setText(allEmpLocationLists.get(i).getEmpName()+"\n"+allEmpLocationLists.get(i).getTime());
                        iconGenerator.setContentView(inflatedView);
                        marker.setPosition(latLng);
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
                        allDetailsMarker.set(i,marker);
                    }
                }

            }
        }
    }

    public void setLocationMarker(String lat,String lng,String bearing,String accuracy,String address, String time, String speed) {
        LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
        emplatlngChange = true;
        empLatLng = latLng;
        float bear = Float.parseFloat(bearing);
        float acc = Float.parseFloat(accuracy);
        if (empLocationMarker == null) {
           MarkerOptions markerOptions = new MarkerOptions();
           markerOptions.position(latLng);
           markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_icon));
           markerOptions.rotation(bear);
           markerOptions.flat(true);
           markerOptions.title(address);
           markerOptions.anchor((float) 0.5, (float) 0.5);
           empLocationMarker = mMap.addMarker(markerOptions);
           //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
           speedText.setText(speed);
           System.out.println("NULL MARKER");


        }
        else {
            empLocationMarker.setPosition(latLng);
            empLocationMarker.setRotation(bear);
            empLocationMarker.setTitle(address);
            speedText.setText(speed);
            System.out.println("NOT NULL MARKER");
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        if (locationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.parseColor("#d95206"));
            circleOptions.fillColor(Color.argb(30,242,165,21));
            circleOptions.radius(acc);
            locationAccuracyCircle = mMap.addCircle(circleOptions);
        } else {
            locationAccuracyCircle.setCenter(latLng);
            locationAccuracyCircle.setRadius(acc);
        }

        if (detailsMarker == null) {
            textView.setText(emp_name+"\n"+time);
            iconGenerator.setContentView(inflatedView);
            detailsMarker = mMap.addMarker(new MarkerOptions()
                                .position(latLng).anchor(0,0)
                                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())));
        } else {
            textView.setText(emp_name+"\n"+time);
            iconGenerator.setContentView(inflatedView);
            detailsMarker.setPosition(latLng);
            detailsMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
        }
    }



    @Override
    public void onBackPressed() {
//        timerHandler.removeCallbacks(timerRunnable);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        allEmp = intent.getBooleanExtra("ALL", false);
        if (!allEmp) {
            emp_id = intent.getStringExtra("EMP_ID");
            emp_name = intent.getStringExtra("EMP_NAME");
            System.out.println("EMP ID = " + emp_id + ", NAME: "+ emp_name);
        } else {
            div_id = intent.getStringExtra("DIV_ID");
            dep_id = intent.getStringExtra("DEP_ID");
            des_id = intent.getStringExtra("DES_ID");
            System.out.println("DIV_ID = " + div_id + ", DEP_ID: "+ dep_id + ", DES_ID: "+ des_id);
            moveEmpLoc.setVisibility(View.GONE);
            speedCard.setVisibility(View.GONE);
        }

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void getUpdatedLocation() {

        if (DEFAULT_USERNAME.equals("IKGL")) {
            if (!allEmp) {
                url = "http://103.56.208.123:8001/apex/tracker/rest-v4/getloctracker/?empno="+ emp_id;
            } else {
                url = "http://103.56.208.123:8001/apex/tracker/rest-v4/getempv10/?div_id="+div_id+"&dept_id="+dep_id+"&desig_id="+des_id;

            }
        }
        else if (DEFAULT_USERNAME.equals("TTRAMS")){
            if (!allEmp) {
                url = "http://103.56.208.123:8001/apex/ttrams/tracker/get_live_loc?emp_id="+ emp_id;
            }
            else {
                url = "http://103.56.208.123:8001/apex/ttrams/tracker/get_all_emp_location?div_id="+div_id+"&dept_id="+dep_id+"&desig_id="+des_id;
            }
        }


        System.out.println(url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    System.out.println("Count: "+ count);

                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        if (!allEmp) {

                            for (int i = 0 ; i < array.length(); i++) {
                                JSONObject location = array.getJSONObject(i);
                                String lat = location.getString("ell_lat");
                                String lng = location.getString("ell_long");
                                String time = location.getString("ell_time");
                                String speed = location.getString("ell_speed");
                                String adds = location.getString("ell_address");
                                String acc = location.getString("ell_accuracy");
                                String bear = location.getString("ell_bearing");

                                byte[] ptext = location.getString("ell_address").getBytes(ISO_8859_1);
                                adds = new String(ptext, UTF_8);

                                System.out.println("CHECKING ADDS: "+adds);

                                System.out.println("LAT: "+ lat);
                                System.out.println("LNG: "+ lng);
                                System.out.println("Time: "+ time);
                                System.out.println("Speed: "+ speed);
                                System.out.println("Address: "+ adds);
                                System.out.println("Accuracy: "+ acc);
                                System.out.println("Bearing: "+ bear);

                                //Toast.makeText(getApplicationContext(), adds,Toast.LENGTH_SHORT).show();


                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                Date date = dateFormat.parse(time);//You will get date object relative to server/client timezone wherever it is parsed
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.getDefault()); //If you need time just put specific format for time like 'HH:mm:ss'
                                String dateStr = formatter.format(date);
                                System.out.println("Converted Date: "+dateStr);

                                Calendar calendar = Calendar.getInstance();
                                Date nowDate = calendar.getTime();

                                // adding 6 hours
                                long serverTime = date.getTime() + (1000*60*60*6);

                                long diff = nowDate.getTime() - serverTime;

                                long secondsInMilli = 1000;
                                long minutesInMilli = secondsInMilli * 60;
                                long hoursInMilli = minutesInMilli * 60;
                                long daysInMilli = hoursInMilli * 24;

                                long elapsedDays = diff / daysInMilli;
                                diff = diff % daysInMilli;

                                long elapsedHours = diff / hoursInMilli;
                                diff = diff % hoursInMilli;

                                long elapsedMinutes = diff / minutesInMilli;
                                diff = diff % minutesInMilli;

                                long elapsedSeconds = diff / secondsInMilli;

                                System.out.println( elapsedDays+ " Days, "+ elapsedHours+" hours, "+ elapsedMinutes+" mins, "+ elapsedSeconds+ " seconds ");

                                if (elapsedDays == 0) {
                                    if (elapsedHours == 0) {
                                        if (elapsedMinutes == 0) {
                                            time = "Just now";
                                        } else {
                                            time =  elapsedMinutes+" mins ago";
                                        }
                                    } else {
                                        time = elapsedHours+" hours "+ elapsedMinutes+" mins ago";
                                    }
                                } else {
                                    time = elapsedDays +" Days " + elapsedHours+" hours "+ elapsedMinutes+" mins ago";
                                }


                                setLocationMarker(lat,lng,bear,acc,adds,time,speed);


                            }
                        }
                        else {
                            allEmpLocationLists = new ArrayList<>();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject location = array.getJSONObject(i);
                                String lat = location.getString("ell_lat");
                                String lng = location.getString("ell_long");
                                String time = location.getString("ell_time");
                                String speed = location.getString("ell_speed");
                                String adds = location.getString("ell_address");
                                String acc = location.getString("ell_accuracy");
                                String bear = location.getString("ell_bearing");
                                String emp__ID = location.getString("ell_emp_id");
                                String name = location.getString("emp_name");
                                String emp__code = location.getString("emp_code");

                                name = name+" ("+emp__code+")";

                                byte[] ptext = location.getString("ell_address").getBytes(ISO_8859_1);
                                adds = new String(ptext, UTF_8);

                                System.out.println("CHECKING ADDS: "+adds);

                                System.out.println("LAT: "+ lat);
                                System.out.println("LNG: "+ lng);
                                System.out.println("Time: "+ time);
                                System.out.println("Speed: "+ speed);
                                System.out.println("Address: "+ adds);
                                System.out.println("Accuracy: "+ acc);
                                System.out.println("Bearing: "+ bear);
                                System.out.println("EMP ID: " + emp__ID);
                                System.out.println("EMP NAME: "+ name);

                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                Date date = dateFormat.parse(time);//You will get date object relative to server/client timezone wherever it is parsed
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.getDefault()); //If you need time just put specific format for time like 'HH:mm:ss'
                                String dateStr = formatter.format(date);
                                System.out.println("Converted Date: "+dateStr);

                                Calendar calendar = Calendar.getInstance();
                                Date nowDate = calendar.getTime();

                                // adding 6 hours
                                long serverTime = date.getTime() + (1000*60*60*6);

                                long diff = nowDate.getTime() - serverTime;

                                long secondsInMilli = 1000;
                                long minutesInMilli = secondsInMilli * 60;
                                long hoursInMilli = minutesInMilli * 60;
                                long daysInMilli = hoursInMilli * 24;

                                long elapsedDays = diff / daysInMilli;
                                diff = diff % daysInMilli;

                                long elapsedHours = diff / hoursInMilli;
                                diff = diff % hoursInMilli;

                                long elapsedMinutes = diff / minutesInMilli;
                                diff = diff % minutesInMilli;

                                long elapsedSeconds = diff / secondsInMilli;

                                System.out.println( elapsedDays+ " Days, "+ elapsedHours+" hours, "+ elapsedMinutes+" mins, "+ elapsedSeconds+ " seconds ");

                                if (elapsedDays == 0) {
                                    if (elapsedHours == 0) {
                                        if (elapsedMinutes == 0) {
                                            time = "Just now";
                                        } else {
                                            time =  elapsedMinutes+" mins ago";
                                        }
                                    } else {
                                        time = elapsedHours+" hours "+ elapsedMinutes+" mins ago";
                                    }
                                } else {
                                    time = elapsedDays +" Days " + elapsedHours+" hours "+ elapsedMinutes+" mins ago";
                                }

                                allEmpLocationLists.add(new AllEmpLocationLists(lat,lng,bear,acc,adds,time,speed,name,emp__ID));
                            }

                            if (allMarkers == null) {
                                System.out.println("NULL MARKER LIST");
                                allMarkers = new ArrayList<Marker>();
                                for (int i = 0; i < array.length(); i++) {
                                    allMarkers.add(null);
                                }
                            } else {
                                System.out.println("NOT NULL MARKER LIST");
                            }
                            if (allDetailsMarker == null) {
                                allDetailsMarker = new ArrayList<Marker>();
                                for (int i = 0; i < array.length(); i++) {
                                    allDetailsMarker.add(null);
                                }
                            }
                            if (allLocationAccCircle == null) {

                                allLocationAccCircle = new ArrayList<Circle>();
                                for (int i = 0; i < array.length(); i++) {
                                    allLocationAccCircle.add(null);
                                }
                            }
                            setLocationMarkerForAll();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Employee Location Not Found",Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", error.toString());
            }
        });

        requestQueue.add(stringRequest);
    }

    public void zoomToUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.i("Ekhane", "1");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
//                Log.i("lattt", location.toString());
                LatLng latLng = new LatLng(23.6850, 90.3563);


                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    System.out.println(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } else {
                    latLng = new LatLng(23.6850, 90.3563);
                    System.out.println(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
                }

            }
        });
    }

    private void enableGPS() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(EmpLiveLocation.this)
                    .addOnConnectionFailedListener(EmpLiveLocation.this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            Log.i("Exit", "3");
                            //info.setText("Done");
                            zoomToUserLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            Log.i("Exit", "4");
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(EmpLiveLocation.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            Log.i("Exit", "5");
                            break;
                    }
                }

            });


        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                //info.setText("Done");
                zoomToUserLocation();
                Log.i("Hoise ", "1");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Log.i("Hoise ", "2");
                LatLng latLng = new LatLng(23.6850, 90.3563);
                System.out.println(latLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
//                info.setText("Please Set the GPS ON for Using This App");
//                quit.setVisibility(View.VISIBLE);
//                System.exit(0);
            }
        }
    }
}
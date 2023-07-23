package ttit.com.shuvo.terraintracker.MainPage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.rosemaryapp.amazingspinner.AmazingSpinner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ttit.com.shuvo.terraintracker.MainPage.adapters.AttenReportAdapter;
import ttit.com.shuvo.terraintracker.MainPage.attendanceData.EmpAttendance;
import ttit.com.shuvo.terraintracker.MainPage.lists.AttenReportList;
import ttit.com.shuvo.terraintracker.MainPage.lists.ThreeItemLists;
import ttit.com.shuvo.terraintracker.MainPage.lists.TwoItemLists;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.livelocation.EmpLiveLocation;
import ttit.com.shuvo.terraintracker.loginFile.Login;
import ttit.com.shuvo.terraintracker.progressBar.WaitProgress;
import ttit.com.shuvo.terraintracker.timeline.TimeLineActivity;

import static ttit.com.shuvo.terraintracker.OracleConnection.DEFAULT_USERNAME;
import static ttit.com.shuvo.terraintracker.OracleConnection.createConnection;
import static ttit.com.shuvo.terraintracker.loginFile.Login.userDesignations;
import static ttit.com.shuvo.terraintracker.loginFile.Login.userInfoLists;

public class HomePage extends AppCompatActivity {

    TextView userName;

    SharedPreferences sharedpreferences;

    ImageView logOut;

    public static final String LOGIN_FILE_NAME = "LOGIN_INFO";
    public static final String EMP_NAME = "EMP_NAME";
    public static final String EMP_CODE = "EMP_CODE";
    public static final String EMP_ID = "EMP_ID";
    public static final String IS_LOGIN = "TRUE_FALSE";
    public static final String DATABASE_NAME = "DATABASE_NAME";

    String getUserName = "";
    String getEmpCode = "";
    String getEmpId = "";
    boolean getLogin = false;

    WaitProgress waitProgress = new WaitProgress();
    private String message = null;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean allLive = false;
    private Connection connection;

    LinearLayout allEmployeeLay;
    LinearLayout singleEmployeeLay;

    AmazingSpinner divSpinner;
    AmazingSpinner depSpinner;
    AmazingSpinner empSpinner;
    AmazingSpinner desSpinner;

    String divName = "";
    String depName = "";
    String desName = "";
    String div_id = "";
    String dep_id = "";
    String emp_id = "";
    String emp_code = "";
    String des_id = "";
    String empName = "";
    String email = "";
    String title = "";
    String companyName = "";

    int trackingFlag = 0;
    int liveFlag = 0;

    ArrayList<TwoItemLists> divLists;
    ArrayList<TwoItemLists> depLists;
    ArrayList<ThreeItemLists> empLists;
    ArrayList<TwoItemLists> desLists;

    CardView empCard;
    ImageView userImage;
    TextView emp_name_text;
    TextView title_text;
    TextView department_text;
    TextView division_text;
    TextView company_name_text;
    TextView email_text;

    TextView msgTimeline;
    TextView msgTimelineAll;

    Button timelineButton;
    Button liveLocationButton;
    Button liveLocationButtonAll;

    String android_id = "";
    String model = "";
    String brand = "";
    String ipAddress = "";
    String hostUserName = "";
    String sessionId = "";
    String osName = "";
    Bitmap selectedImage;
    boolean imageFound = false;
    Button attend;

    Button setData;
    public static ArrayList<String> testDataBlob;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        userName = findViewById(R.id.user_name_homepage);
        logOut = findViewById(R.id.log_out_icon);

        depSpinner = findViewById(R.id.department_type_spinner);
        divSpinner = findViewById(R.id.division_type_spinner);
        empSpinner = findViewById(R.id.employee_type_spinner);
        desSpinner = findViewById(R.id.designation_type_spinner);
        userImage = findViewById(R.id.user_pic);
        attend = findViewById(R.id.attendance_details_button);

        empCard = findViewById(R.id.emp_card);
        empCard.setVisibility(View.GONE);

        emp_name_text = findViewById(R.id.emp_name);
        title_text = findViewById(R.id.emp_title);
        department_text = findViewById(R.id.department_name);
        division_text = findViewById(R.id.division_name);
        company_name_text = findViewById(R.id.company_name);
        email_text = findViewById(R.id.email_name);

        msgTimeline = findViewById(R.id.timeline_not_msg);
        msgTimeline.setVisibility(View.GONE);

        msgTimelineAll = findViewById(R.id.timeline_all_not_msg);
        msgTimelineAll.setVisibility(View.GONE);

        timelineButton = findViewById(R.id.button_time_line);
        liveLocationButton = findViewById(R.id.button_live_location);
        liveLocationButtonAll = findViewById(R.id.button_live_location_all_employee);


        allEmployeeLay = findViewById(R.id.all_employee_layout);
        singleEmployeeLay = findViewById(R.id.single_employee_layout);

        allEmployeeLay.setVisibility(View.GONE);
        singleEmployeeLay.setVisibility(View.GONE);

        setData = findViewById(R.id.button_set_data);

        depLists = new ArrayList<>();
        divLists = new ArrayList<>();
        empLists = new ArrayList<>();
        desLists = new ArrayList<>();

        testDataBlob = new ArrayList<>();

        if (testDataBlob.isEmpty()) {
            setData.setVisibility(View.VISIBLE);
        } else {
            setData.setVisibility(View.GONE);
        }

        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TestDataCheck().execute();
            }
        });


        sharedpreferences = getSharedPreferences(LOGIN_FILE_NAME,MODE_PRIVATE);
        getUserName = sharedpreferences.getString(EMP_NAME,null);
        getEmpCode = sharedpreferences.getString(EMP_CODE,null);
        getEmpId = sharedpreferences.getString(EMP_ID,null);
        getLogin = sharedpreferences.getBoolean(IS_LOGIN, false);
        DEFAULT_USERNAME = sharedpreferences.getString(DATABASE_NAME,DEFAULT_USERNAME);

        System.out.println(getUserName);
        System.out.println(getEmpCode);
        System.out.println(getEmpId);
        System.out.println(getLogin);

        userName.setText(getUserName);

//        System.out.println("USER Info LIST: \n\n");
//        System.out.println(userInfoLists.get(0).getUserName());
//        System.out.println(userInfoLists.get(0).getUser_fname());
//        System.out.println(userInfoLists.get(0).getUser_lname());
//        System.out.println(userInfoLists.get(0).getEmp_id());
//        System.out.println(userInfoLists.get(0).getEmail());
//        System.out.println(userInfoLists.get(0).getContact());
//
//
//        System.out.println("USER DESIGNATIONS LIST: \n\n");
//        System.out.println(userDesignations.get(0).getJsm_name());
//        System.out.println(userDesignations.get(0).getDept_name());
//        System.out.println(userDesignations.get(0).getDesg_name());
//        System.out.println(userDesignations.get(0).getDesg_priority());
//        System.out.println(userDesignations.get(0).getDiv_id());
//        System.out.println(userDesignations.get(0).getDiv_name());
//        System.out.println(userDesignations.get(0).getJoining_date());
//        System.out.println(userDesignations.get(0).getJsd_id());
//        System.out.println(userDesignations.get(0).getJsd_objective());
//        System.out.println(userDesignations.get(0).getJsm_code());

        model = android.os.Build.MODEL;

        brand = Build.BRAND;

        ipAddress = getIPAddress(true);

        hostUserName = getHostName("localhost");

        StringBuilder builder = new StringBuilder();
        builder.append("ANDROID: ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(": ").append(fieldName);
                //builder.append(" : ").append(fieldName).append(" : ");
                //builder.append("sdk=").append(fieldValue);
            }
        }

        System.out.println("OS: " + builder.toString());
        //Log.d(LOG_TAG, "OS: " + builder.toString());

        //System.out.println("HOSTTTTT: " + getHostName());

        osName = builder.toString();

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Selecting Division
        divSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                System.out.println(position+": "+name);
                empCard.setVisibility(View.GONE);
                allEmployeeLay.setVisibility(View.GONE);
                singleEmployeeLay.setVisibility(View.GONE);
                depSpinner.setText("");
                empSpinner.setText("");
                desSpinner.setText("");
                depName = "";
                empName = "";
                desName = "";
                for (int i = 0; i <divLists.size(); i++) {
                    if (name.equals(divLists.get(i).getName())) {
                        div_id = divLists.get(i).getId();
                        if (div_id.isEmpty()) {
                            divName = "";
                        } else {
                            divName = divLists.get(i).getName();
                        }

                    }
                }

                dep_id = "";
                emp_id = "";
                des_id = "";

                new DepartmentCheck().execute();

            }
        });


        // Selecting Department
        depSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                System.out.println(position+": "+name);
                empCard.setVisibility(View.GONE);
                allEmployeeLay.setVisibility(View.GONE);
                singleEmployeeLay.setVisibility(View.GONE);
                desSpinner.setText("");
                empSpinner.setText("");
                desName = "";
                empName = "";
                for (int i = 0; i <depLists.size(); i++) {
                    if (name.equals(depLists.get(i).getName())) {
                        dep_id = depLists.get(i).getId();
                        if (dep_id.isEmpty()) {
                            depName = "";
                        } else {
                            depName = depLists.get(i).getName();
                        }
                    }
                }

                des_id = "";
                emp_id = "";

                new DesignationCheck().execute();

            }
        });

        // Selecting Designation
        desSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                System.out.println(position+": "+name);
                empCard.setVisibility(View.GONE);
                allEmployeeLay.setVisibility(View.GONE);
                singleEmployeeLay.setVisibility(View.GONE);
                empSpinner.setText("");
                empName = "";

                for (int i = 0; i <desLists.size(); i++) {
                    if (name.equals(desLists.get(i).getName())) {
                        des_id = desLists.get(i).getId();
                        if (des_id.isEmpty()) {
                            desName = "";
                        } else {
                            desName = desLists.get(i).getName();
                        }
                    }
                }

                emp_id = "";

                new EmployeeCheck().execute();

            }
        });

        // Selecting Employee
        empSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                System.out.println(position+": "+name);
                empCard.setVisibility(View.GONE);
                allEmployeeLay.setVisibility(View.GONE);
                singleEmployeeLay.setVisibility(View.GONE);
                for (int i = 0; i <empLists.size(); i++) {
                    if (name.equals(empLists.get(i).getName())) {
                        emp_id = empLists.get(i).getId();
                        emp_code = empLists.get(i).getCode();
                        if (emp_id.isEmpty()) {
                            empName = "";
                        } else {
                            empName = empLists.get(i).getName();
                        }
                    }
                }

                new EmployeeDetailsCheck().execute();

            }
        });

        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, EmpAttendance.class);
                intent.putExtra("EMP_NAME",empName);
                intent.putExtra("TITLE",title);
                intent.putExtra("EMP_ID",emp_id);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
                builder.setTitle("LOG OUT")
                        .setMessage("Do you want to Log Out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.remove(EMP_NAME);
                                editor1.remove(EMP_CODE);
                                editor1.remove(EMP_ID);
                                editor1.remove(IS_LOGIN);
                                editor1.apply();
                                editor1.commit();

                                Intent intent = new Intent(HomePage.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        timelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePage.this, TimeLineActivity.class);
                intent.putExtra("EMP_ID", emp_id);
                startActivity(intent);
            }
        });

        liveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, EmpLiveLocation.class);
                intent.putExtra("EMP_ID", emp_id);
                intent.putExtra("EMP_NAME", empName);
                intent.putExtra("ALL", false);
                startActivity(intent);
            }
        });

        liveLocationButtonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, EmpLiveLocation.class);
                intent.putExtra("DIV_ID", div_id);
                intent.putExtra("DEP_ID", dep_id);
                intent.putExtra("DES_ID", des_id);
                intent.putExtra("ALL", true);
                startActivity(intent);
            }
        });

        new DivisionCheck().execute();

    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(HomePage.this)
                .setTitle("EXIT!")
                .setMessage("Do You Want to Exit?")
                .setIcon(R.drawable.tracker_logo)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    public static String getHostName(String defValue) {
        try {
            @SuppressLint("DiscouragedPrivateApi") Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        boolean isMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;
    }

    public class DivisionCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(),"WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                DivisionData();
                if (connected) {
                    conn = true;
                }

            } else {
                conn = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (conn) {

                conn = false;
                connected = false;

                //String[] type = new String[] {"Approved", "Pending","Both"};
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divLists.size(); i++) {
                    type.add(divLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

                divSpinner.setAdapter(arrayAdapter);

                //new ItemCheck().execute();

//                new ReOrderFragment.ReOrderLevelCheck().execute();

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new DivisionCheck().execute();
                        dialog.dismiss();
                    }
                });

                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        }
    }

    public class DepartmentCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(),"WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                DepartmentData();
                if (connected) {
                    conn = true;
                }

            } else {
                conn = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (conn) {

                conn = false;
                connected = false;

                //String[] type = new String[] {"Approved", "Pending","Both"};
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < depLists.size(); i++) {
                    type.add(depLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

                depSpinner.setAdapter(arrayAdapter);


                desLists = new ArrayList<>();
                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < desLists.size(); i++) {
                    type1.add(desLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);

                desSpinner.setAdapter(arrayAdapter1);


                empLists = new ArrayList<>();
                ArrayList<String> type2 = new ArrayList<>();
                for(int i = 0; i < empLists.size(); i++) {
                    type2.add(empLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);

                empSpinner.setAdapter(arrayAdapter2);

                //new ReOrderLevelCheck().execute();

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new DepartmentCheck().execute();
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

    public class DesignationCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(),"WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                DesignationData();
                if (connected) {
                    conn = true;
                }

            } else {
                conn = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (conn) {

                conn = false;
                connected = false;


                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < desLists.size(); i++) {
                    type1.add(desLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);

                desSpinner.setAdapter(arrayAdapter1);


                empLists = new ArrayList<>();
                ArrayList<String> type2 = new ArrayList<>();
                for(int i = 0; i < empLists.size(); i++) {
                    type2.add(empLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);

                empSpinner.setAdapter(arrayAdapter2);

                //new ReOrderLevelCheck().execute();

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new DesignationCheck().execute();
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

    public class EmployeeCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(),"WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                EmployeeData();
                if (connected) {
                    conn = true;
                }

            } else {
                conn = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (conn) {

                conn = false;
                connected = false;

                //String[] type = new String[] {"Approved", "Pending","Both"};
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < empLists.size(); i++) {
                    type.add(empLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

                empSpinner.setAdapter(arrayAdapter);

                empCard.setVisibility(View.VISIBLE);
                allEmployeeLay.setVisibility(View.VISIBLE);
                singleEmployeeLay.setVisibility(View.GONE);

                if (allLive) {
                    liveLocationButtonAll.setVisibility(View.VISIBLE);
                    msgTimelineAll.setVisibility(View.GONE);
                }
                else {
                    liveLocationButtonAll.setVisibility(View.GONE);
                    msgTimelineAll.setVisibility(View.VISIBLE);
                }

                allLive = false;


                //new ReOrderLevelCheck().execute();

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new EmployeeCheck().execute();
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

    public class EmployeeDetailsCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(),"WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                EmployeeDetailsData();
                if (connected) {
                    conn = true;
                }

            } else {
                conn = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (conn) {

                conn = false;
                connected = false;


                emp_name_text.setText(empName);
                title_text.setText(title);
                department_text.setText(depName);
                division_text.setText(divName);
                company_name_text.setText(companyName);
                if (email != null) {
                    email_text.setText(email);
                } else {
                    email_text.setText("Email Not Found");
                }

                empCard.setVisibility(View.VISIBLE);
                singleEmployeeLay.setVisibility(View.VISIBLE);
                allEmployeeLay.setVisibility(View.GONE);

                if (trackingFlag == 1) {
                    timelineButton.setVisibility(View.VISIBLE);
                    setData.setVisibility(View.VISIBLE);

                    if (liveFlag == 1) {
                        liveLocationButton.setVisibility(View.VISIBLE);
                    } else {
                        liveLocationButton.setVisibility(View.GONE);
                    }
                    msgTimeline.setVisibility(View.GONE);

                } else {
                    liveLocationButton.setVisibility(View.GONE);
                    timelineButton.setVisibility(View.GONE);
                    msgTimeline.setVisibility(View.VISIBLE);
                    setData.setVisibility(View.GONE);
                }
                if (imageFound) {
                    Glide.with(HomePage.this)
                            .load(selectedImage)
                            .fitCenter()
                            .into(userImage);
                }
                else {
                    userImage.setImageResource(R.drawable.profile);
                }


                //new ReOrderLevelCheck().execute();

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new EmployeeDetailsCheck().execute();
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

    public class TestDataCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(),"WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                testDataGetting();
                if (connected) {
                    conn = true;
                }

            } else {
                conn = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (conn) {

                conn = false;
                connected = false;


                setData.setVisibility(View.GONE);

                //new ReOrderLevelCheck().execute();

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new TestDataCheck().execute();
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

    public void DivisionData() {
        try {
            this.connection = createConnection();
            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();

            Statement stmt = connection.createStatement();

            divLists = new ArrayList<>();



//            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
//                    "    division_mst.divm_id,\n" +
//                    "    division_mst.divm_name\n" +
//                    "FROM\n" +
//                    "    division_mst\n" +
//                    "order by divm_id");
            ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT\n" +
                    "    division_mst.divm_id,\n" +
                    "    division_mst.divm_name\n" +
                    "FROM\n" +
                    "         division_mst\n" +
                    "    INNER JOIN job_setup_mst ON job_setup_mst.jsm_divm_id = division_mst.divm_id\n" +
                    "    INNER JOIN job_setup_dtl ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
                    "    INNER JOIN emp_mst ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
                    "WHERE emp_mst.emp_quit = 0\n" +
                    "ORDER BY\n" +
                    "    division_mst.divm_id");

            while (resultSet1.next()) {
                divLists.add(new TwoItemLists(resultSet1.getString(1),resultSet1.getString(2)));
            }
//            categoryLists.add(new ReceiveTypeList("","All Categories"));

            ResultSet rs = stmt.executeQuery("SELECT CIM_NAME--,CIM_LOGO_APPS\n" +
                    " FROM COMPANY_INFO_MST\n");

            while (rs.next()) {
                companyName = rs.getString(1);
            }


            String userName = getEmpCode;
            String userId = getEmpId;

            if (userId != null) {
                if (!userId.isEmpty()) {
                    System.out.println(userId);
                } else {
                    userId = "0";
                }
            } else {
                userId = "0";
            }

            sessionId = "";

            ResultSet resultSet3 = stmt.executeQuery("SELECT SYS_CONTEXT ('USERENV', 'SESSIONID') --INTO P_IULL_SESSION_ID\n" +
                    "   FROM DUAL\n");

            while (resultSet3.next()) {
                System.out.println("SESSION ID: "+ resultSet3.getString(1));
                sessionId = resultSet3.getString(1);
            }

            resultSet3.close();

            CallableStatement callableStatement1 = connection.prepareCall("{call USERLOGINDTL(?,?,?,?,?,?,?,?,?)}");
            callableStatement1.setString(1,userName);
            callableStatement1.setString(2, brand+" "+model);
            callableStatement1.setString(3,ipAddress);
            callableStatement1.setString(4,hostUserName);
            callableStatement1.setInt(5,Integer.parseInt(userId));
            callableStatement1.setInt(6,Integer.parseInt(sessionId));
            callableStatement1.setString(7,"1");
            callableStatement1.setString(8,osName);
            callableStatement1.setInt(9,4);
            callableStatement1.execute();

            callableStatement1.close();



            connected = true;

            connection.close();

        }
        catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void DepartmentData() {
        try {
            this.connection = createConnection();
            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();

            Statement stmt = connection.createStatement();

            depLists = new ArrayList<>();



            if (div_id.isEmpty()) {
                div_id = null;
            }

//            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
//                    "    dept_mst.dept_id,\n" +
//                    "    dept_mst.dept_name,\n" +
//                    "    dept_mst.dept_divm_id\n" +
//                    "FROM\n" +
//                    "    dept_mst\n" +
//                    "where dept_divm_id = "+div_id+"\n" +
//                    "order by dept_id");
            ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT\n" +
                    "    dept_mst.dept_id,\n" +
                    "    dept_mst.dept_name,\n" +
                    "    dept_mst.dept_divm_id\n" +
                    "FROM\n" +
                    "         dept_mst\n" +
                    "    INNER JOIN job_setup_mst ON job_setup_mst.jsm_dept_id = dept_mst.dept_id\n" +
                    "    INNER JOIN job_setup_dtl ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
                    "    INNER JOIN emp_mst ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
                    "WHERE\n" +
                    "        dept_mst.dept_divm_id = "+div_id+"\n" +
                    "    AND emp_mst.emp_quit = 0\n" +
                    "ORDER BY\n" +
                    "    dept_mst.dept_id");

            while (resultSet1.next()) {
                depLists.add(new TwoItemLists(resultSet1.getString(1),resultSet1.getString(2)));
            }
//            categoryLists.add(new ReceiveTypeList("","All Categories"));

            if (div_id == null) {
                div_id = "";
            }
            connected = true;

            connection.close();

        }
        catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void DesignationData() {
        try {
            this.connection = createConnection();
            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();

            Statement stmt = connection.createStatement();

            desLists = new ArrayList<>();


            if (div_id.isEmpty()) {
                div_id = null;
            }
            if (dep_id.isEmpty()) {
                dep_id = null;
            }

//            ResultSet resultSet1 = stmt.executeQuery("select distinct desig_id, desig_name from emp_details_v where desig_name is not NULL and dept_id = "+dep_id+" and divm_id = "+div_id+"\n");
            ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT\n" +
                    "    desig_mst.desig_id,\n" +
                    "    desig_mst.desig_name\n" +
                    "FROM\n" +
                    "    desig_mst,\n" +
                    "    job_setup_mst,\n" +
                    "    dept_mst,\n" +
                    "    division_mst,\n" +
                    "    job_setup_dtl,\n" +
                    "    emp_mst\n" +
                    "WHERE\n" +
                    "        desig_mst.desig_id = job_setup_mst.jsm_desig_id\n" +
                    "    AND job_setup_mst.jsm_divm_id = division_mst.divm_id\n" +
                    "    AND job_setup_mst.jsm_dept_id = dept_mst.dept_id\n" +
                    "    AND emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
                    "    AND job_setup_mst.jsm_id = job_setup_dtl.jsd_jsm_id\n" +
                    "    AND desig_mst.desig_name IS NOT NULL\n" +
                    "    AND division_mst.divm_id = "+div_id+"\n" +
                    "    AND dept_mst.dept_id = "+dep_id+"\n" +
                    "    AND emp_mst.emp_quit = 0\n" +
                    "ORDER BY\n" +
                    "    desig_mst.desig_id");

            while (resultSet1.next()) {
                desLists.add(new TwoItemLists(resultSet1.getString(1),resultSet1.getString(2)));
            }
//            categoryLists.add(new ReceiveTypeList("","All Categories"));

            if (div_id == null) {
                div_id = "";
            }
            if (dep_id == null) {
                dep_id = "";
            }
            connected = true;

            connection.close();

        }
        catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void EmployeeData() {
        try {
            this.connection = createConnection();
            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();

            Statement stmt = connection.createStatement();

            empLists = new ArrayList<>();

            allLive = false;


            if (div_id.isEmpty()) {
                div_id = null;
            }
            if (dep_id.isEmpty()) {
                dep_id = null;
            }
            if (des_id.isEmpty()) {
                des_id = null;
            }

            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
                    "    emp_mst.emp_id,\n" +
                    "    emp_mst.emp_name,\n" +
                    "    emp_mst.emp_code,\n" +
                    "    job_setup_mst.jsm_divm_id,\n" +
                    "    job_setup_mst.jsm_dept_id,\n" +
                    "    job_setup_mst.jsm_desig_id\n" +
                    "FROM\n" +
                    "         emp_mst\n" +
                    "    INNER JOIN job_setup_dtl ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
                    "    INNER JOIN job_setup_mst ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
                    "WHERE\n" +
                    "        job_setup_mst.jsm_divm_id = "+div_id+"\n" +
                    "    AND job_setup_mst.jsm_dept_id = "+dep_id+"\n" +
                    "    AND job_setup_mst.jsm_desig_id = "+des_id+"\n" +
                    "ORDER BY\n" +
                    "    emp_mst.emp_id");

            while (resultSet1.next()) {
                empLists.add(new ThreeItemLists(resultSet1.getString(1),resultSet1.getString(2)+" ("+resultSet1.getString(3)+")",resultSet1.getString(3)));
            }

            ResultSet resultSet = stmt.executeQuery("SELECT\n" +
                    "    emp_mst.emp_timeline_tracker_flag,\n" +
                    "    emp_mst.emp_live_loc_tracker_flag\n" +
                    "FROM\n" +
                    "         emp_mst\n" +
                    "    INNER JOIN job_setup_dtl ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
                    "    INNER JOIN job_setup_mst ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
                    "WHERE\n" +
                    "        job_setup_mst.jsm_divm_id = "+div_id+"\n" +
                    "    AND job_setup_mst.jsm_dept_id = "+dep_id+"\n" +
                    "    AND job_setup_mst.jsm_desig_id = "+des_id+"");

            while (resultSet.next()) {
                int track = resultSet.getInt(1);
                int live = resultSet.getInt(2);

                if (track == 1 && live == 1) {
                    allLive = true;
                }
            }
            if (div_id == null) {
                div_id = "";
            }
            if (dep_id == null) {
                dep_id = "";
            }
            if (des_id == null) {
                des_id = "";
            }

            connected = true;

            connection.close();

        }
        catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void EmployeeDetailsData() {
        try {
            this.connection = createConnection();
            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();

            Statement stmt = connection.createStatement();
            imageFound = false;

            if (emp_id.isEmpty()) {
                emp_id = null;
            }


            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
                    "    emp_job_history.job_calling_title,\n" +
                    "    emp_job_history.job_email,\n" +
                    "    emp_job_history.job_emp_id\n" +
                    "FROM\n" +
                    "    emp_job_history\n" +
                    "    where emp_job_history.job_emp_id = "+emp_id+"");

            while (resultSet1.next()) {
                title = resultSet1.getString(1);
                email = resultSet1.getString(2);
            }
//            categoryLists.add(new ReceiveTypeList("","All Categories"));

            ResultSet resultSet = stmt.executeQuery("SELECT emp_mst.emp_timeline_tracker_flag, emp_mst.EMP_LIVE_LOC_TRACKER_FLAG from emp_mst where emp_mst.emp_id = "+emp_id+"");

            while (resultSet.next()) {
                trackingFlag = resultSet.getInt(1);
                liveFlag = resultSet.getInt(2);
            }
            System.out.println(emp_code);
//            ResultSet imageResult = stmt.executeQuery("SELECT EMP_IMAGE FROM EMP_MST WHERE EMP_ID = "+emp_id+"");
            ResultSet imageResult = stmt.executeQuery("Select loadblobfromfile('"+emp_code+"'||'.jpg') EMP_IMAGE from dual");

            while (imageResult.next()) {
                Blob b = imageResult.getBlob(1);
                if (b == null) {
                    imageFound = false;
                }
                else {
                    imageFound = true;
                    byte[] barr =b.getBytes(1,(int)b.length());
                    selectedImage = BitmapFactory.decodeByteArray(barr,0,barr.length);
                }
                System.out.println(imageFound);
            }
            imageResult.close();

            stmt.close();

            connected = true;

            connection.close();

        }
        catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void testDataGetting () {

        try {

            this.connection = createConnection();

            testDataBlob = new ArrayList<>();

            PreparedStatement ps = connection.prepareStatement("SELECT ELR_FILE_NAME, ELR_FILETYPE, ELR_LOCATION_FILE\n" +
                    "FROM EMP_LOCATION_RECORD\n" +
                    "WHERE ELR_EMP_ID = 2021\n" +
                    "AND ELR_FILE_NAME = ANY('2021_01-FEB-22_track','2021_10-FEB-22_track','2021_07-FEB-22_track','2021_20-JAN-22_track','2021_30-JAN-22_track','2021_24-JAN-22_track')");

            ResultSet set = ps.executeQuery();
            int ii  = 0;
            while (set.next()) {
                ii++;
                Blob b = set.getBlob(3);
                String fileName = set.getString(1) + "_" + ii + set.getString(2);

                if (b != null && b.length() != 0) {
                    System.out.println("BLOB paise");
                    File myExternalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);

                    InputStream r = b.getBinaryStream();
                    FileWriter fw=new FileWriter(myExternalFile);
                    int i;
                    while((i=r.read())!=-1)
                        fw.write((char)i);
                    fw.close();
                    testDataBlob.add(fileName);
                    //blobNotNull = true;
                } else {
                    System.out.println("BLOB pai nai");
                    //blobNotNull = false;
                }

            }

            connected = true;

            connection.close();

        } catch (Exception e) {
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
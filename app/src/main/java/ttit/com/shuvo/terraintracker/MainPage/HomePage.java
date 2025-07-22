package ttit.com.shuvo.terraintracker.MainPage;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ttit.com.shuvo.terraintracker.Constants.CompanyName;
import static ttit.com.shuvo.terraintracker.Constants.api_url_front;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.terraintracker.MainPage.lists.ThreeItemLists;
import ttit.com.shuvo.terraintracker.MainPage.lists.TwoItemLists;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.araylists.BranchList;
import ttit.com.shuvo.terraintracker.livelocation.EmpLiveLocation;
import ttit.com.shuvo.terraintracker.timeline.TimeLineActivity;
import static ttit.com.shuvo.terraintracker.dash_board.Dashboard.testDataBlob;


public class HomePage extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    LinearLayout branchSelect;
    TextView branchName;
    ArrayList<BranchList> branchLists;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean allLive = false;
    private Boolean loading = false;
    String parsing_message = "";

    LinearLayout allEmployeeLay;
    LinearLayout singleEmployeeLay;

    AppCompatAutoCompleteTextView divSpinner;

    TextInputLayout departmentSelectLay;
    AppCompatAutoCompleteTextView depSpinner;

    TextInputLayout designationSelectLay;
    AppCompatAutoCompleteTextView desSpinner;

    TextInputLayout employeeSelectLay;
    AppCompatAutoCompleteTextView empSpinner;

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

    MaterialButton timelineButton;
    MaterialButton liveLocationButton;
    MaterialButton liveLocationButtonAll;

    Bitmap selectedImage;
    boolean imageFound = false;

    MaterialButton setData;

    Logger logger = Logger.getLogger(HomePage.class.getName());
    PopupMenu popupMenu;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        fullLayout = findViewById(R.id.emp_track_record_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_emp_track_record);
        circularProgressIndicator.setVisibility(GONE);

        backButton = findViewById(R.id.back_logo_of_emp_track_record);

        branchSelect = findViewById(R.id.branch_selection_for_track_record);
        branchName = findViewById(R.id.selected_branch_name_emp_track_record);
        branchLists = new ArrayList<>();

        popupMenu = new PopupMenu(HomePage.this, branchSelect);

        divSpinner = findViewById(R.id.division_type_spinner);

        departmentSelectLay = findViewById(R.id.spinner_layout_department);
        departmentSelectLay.setEnabled(false);
        depSpinner = findViewById(R.id.department_type_spinner);

        designationSelectLay = findViewById(R.id.spinner_layout_designation);
        designationSelectLay.setEnabled(false);
        desSpinner = findViewById(R.id.designation_type_spinner);

        employeeSelectLay = findViewById(R.id.spinner_layout_employee);
        employeeSelectLay.setEnabled(false);
        empSpinner = findViewById(R.id.employee_type_spinner);

        userImage = findViewById(R.id.user_pic);

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

        if (CompanyName.equals("Terrain Company")) {
            setData.setVisibility(VISIBLE);
        }
        else {
            setData.setVisibility(View.GONE);
        }

        setData.setOnClickListener(v -> getTestData());

//        popupMenu.setOnMenuItemClickListener(menuItem -> {
//            String branch_name = "";
//            for (int i = 0; i <branchLists.size(); i++) {
//                if (String.valueOf(menuItem.getItemId()).equals(branchLists.get(i).getId())) {
//
//                    selected_coa_id = branchLists.get(i).getId();
//                    branch_name = branchLists.get(i).getName();
//                }
//            }
//
//            branchName.setText(branch_name);
//
//            if (selected_coa_id.equals("30")) {
//                getDivisions();
//            }
//            else {
//                getDivisionsCoa();
//            }
//            return false;
//        });

        // Selecting Division
        divSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            empCard.setVisibility(View.GONE);
            allEmployeeLay.setVisibility(View.GONE);
            singleEmployeeLay.setVisibility(View.GONE);

            depSpinner.setText("");
            departmentSelectLay.setEnabled(false);
            empSpinner.setText("");
            employeeSelectLay.setEnabled(false);
            desSpinner.setText("");
            designationSelectLay.setEnabled(false);

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

            getDivisionWiseDepData();

        });

        // Selecting Department
        depSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);

            empCard.setVisibility(View.GONE);
            allEmployeeLay.setVisibility(View.GONE);
            singleEmployeeLay.setVisibility(View.GONE);

            desSpinner.setText("");
            designationSelectLay.setEnabled(false);

            empSpinner.setText("");
            employeeSelectLay.setEnabled(false);

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

            getDesignations();

        });

        // Selecting Designation
        desSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);

            empCard.setVisibility(View.GONE);
            allEmployeeLay.setVisibility(View.GONE);
            singleEmployeeLay.setVisibility(View.GONE);

            empSpinner.setText("");
            employeeSelectLay.setEnabled(false);

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

           getEmployeeData();

        });

        // Selecting Employee
        empSpinner.setOnItemClickListener((parent, view, position, id) -> {
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

            getEmployeeDetailsData();

        });

        timelineButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, TimeLineActivity.class);
            intent.putExtra("EMP_ID", emp_id);
            startActivity(intent);
        });

        liveLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, EmpLiveLocation.class);
            intent.putExtra("EMP_ID", emp_id);
            intent.putExtra("EMP_NAME", empName);
            intent.putExtra("ALL", false);
            startActivity(intent);
        });

        liveLocationButtonAll.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, EmpLiveLocation.class);
            intent.putExtra("DIV_ID", div_id);
            intent.putExtra("DEP_ID", dep_id);
            intent.putExtra("DES_ID", des_id);
            intent.putExtra("ALL", true);
            startActivity(intent);
        });

        getFirstData();

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(getApplicationContext(), "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(getApplicationContext(), "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });
    }

    public void getFirstData() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        divLists = new ArrayList<>();

        String divUrl = api_url_front + "directory/getDivisions";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject divInfo = array.getJSONObject(i);
                        String divm_id_new = divInfo.getString("divm_id");
                        String divm_name_new = divInfo.getString("divm_name");

                        divLists.add(new TwoItemLists(divm_id_new,divm_name_new));
                    }
                }
                connected = true;
                updateLay();
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                connected = false;
                parsing_message = e.getLocalizedMessage();
                updateLay();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            conn = false;
            connected = false;
            parsing_message = error.getLocalizedMessage();
            updateLay();
        });

        requestQueue.add(divReq);
    }

    private void updateLay() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);

                div_id = "";
                divSpinner.setText("");

                dep_id = "";
                depSpinner.setText("");
                departmentSelectLay.setEnabled(false);

                des_id = "";
                desSpinner.setText("");
                designationSelectLay.setEnabled(false);

                emp_id = "";
                emp_code = "";
                empSpinner.setText("");
                employeeSelectLay.setEnabled(false);

                empCard.setVisibility(View.GONE);
                allEmployeeLay.setVisibility(View.GONE);
                singleEmployeeLay.setVisibility(View.GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divLists.size(); i++) {
                    type.add(divLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

                divSpinner.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                alertMessageDiv();
            }
        }
        else {
            alertMessageDiv();
        }
    }

    public void alertMessageDiv() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getFirstData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getDivisionWiseDepData() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        depLists = new ArrayList<>();

        String depUrl = api_url_front + "directory/getDepartments/"+div_id;

        RequestQueue requestQueue = Volley.newRequestQueue(HomePage.this);

        StringRequest depReq = new StringRequest(Request.Method.GET, depUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject depInfo = array.getJSONObject(i);
                        String dept_id_new = depInfo.getString("dept_id");
                        String dept_name_new = depInfo.getString("dept_name");

                        depLists.add(new TwoItemLists(dept_id_new,dept_name_new));
                    }
                }
                connected = true;
                updateAfterSelectingDiv();
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                connected = false;
                parsing_message = e.getLocalizedMessage();
                updateAfterSelectingDiv();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            conn = false;
            connected = false;
            parsing_message = error.getLocalizedMessage();
            updateAfterSelectingDiv();
        });

        requestQueue.add(depReq);
    }

    private void updateAfterSelectingDiv() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);

                empCard.setVisibility(View.GONE);
                allEmployeeLay.setVisibility(View.GONE);
                singleEmployeeLay.setVisibility(View.GONE);

                dep_id = "";
                depSpinner.setText("");
                departmentSelectLay.setEnabled(true);

                des_id = "";
                desSpinner.setText("");
                designationSelectLay.setEnabled(false);

                emp_id = "";
                emp_code = "";
                empSpinner.setText("");
                employeeSelectLay.setEnabled(false);

                System.out.println("DEP: "+depLists.size());
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < depLists.size(); i++) {
                    type.add(depLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

                depSpinner.setAdapter(arrayAdapter);

                desLists = new ArrayList<>();
                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < desLists.size(); i++) {
                    type1.add(desLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);

                desSpinner.setAdapter(arrayAdapter1);


                empLists = new ArrayList<>();
                ArrayList<String> type2 = new ArrayList<>();
                for(int i = 0; i < empLists.size(); i++) {
                    type2.add(empLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);

                empSpinner.setAdapter(arrayAdapter2);

                loading = false;

            }
            else {
                alertMessageDept();
            }
        }
        else {
            alertMessageDept();
        }
    }

    public void alertMessageDept() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDivisionWiseDepData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getDesignations() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        desLists = new ArrayList<>();

        String url = api_url_front + "directory/getDesignations/"+div_id+"/"+dep_id;

        RequestQueue requestQueue = Volley.newRequestQueue(HomePage.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject desInfo = array.getJSONObject(i);

                        String desig_id_new = desInfo.getString("desig_id");
                        String desig_name_new = desInfo.getString("desig_name");

                        desLists.add(new TwoItemLists(desig_id_new,desig_name_new));
                    }
                }
                connected = true;
                updateAfterSelectingDEP();
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                connected = false;
                parsing_message = e.getLocalizedMessage();
                updateAfterSelectingDEP();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            conn = false;
            connected = false;
            parsing_message = error.getLocalizedMessage();
            updateAfterSelectingDEP();
        });

        requestQueue.add(stringRequest);
    }

    private void updateAfterSelectingDEP() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);

                des_id = "";
                desSpinner.setText("");
                designationSelectLay.setEnabled(true);

                emp_id = "";
                emp_code = "";
                empSpinner.setText("");
                employeeSelectLay.setEnabled(false);

                empCard.setVisibility(View.GONE);
                allEmployeeLay.setVisibility(View.GONE);
                singleEmployeeLay.setVisibility(View.GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < desLists.size(); i++) {
                    type.add(desLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

                desSpinner.setAdapter(arrayAdapter);

                empLists = new ArrayList<>();
                ArrayList<String> type2 = new ArrayList<>();
                for(int i = 0; i < empLists.size(); i++) {
                    type2.add(empLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);

                empSpinner.setAdapter(arrayAdapter2);

                loading = false;
            }
            else {
                alertMessageDesig();
            }
        }
        else {
            alertMessageDesig();
        }
    }

    public void alertMessageDesig() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDesignations();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getEmployeeData() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        allLive = true;
        empLists = new ArrayList<>();

        String url = api_url_front + "directory/getEmpData?div_id="+div_id+"&dep_id="+dep_id+"&des_id="+des_id;

        RequestQueue requestQueue = Volley.newRequestQueue(HomePage.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject desInfo = array.getJSONObject(i);

                        String emp_id_new = desInfo.getString("emp_id");
                        String emp_name_new = desInfo.getString("emp_name");
                        String emp_code_new = desInfo.getString("emp_code");

                        empLists.add(new ThreeItemLists(emp_id_new,emp_name_new+" ("+emp_code_new+")",emp_code_new));

                        int track = desInfo.getInt("emp_timeline_tracker_flag");
                        int live = desInfo.getInt("emp_live_loc_tracker_flag");

                        if (allLive) {
                            allLive = track == 1 && live == 1;
                        }
                    }
                }
                connected = true;
                updateAfterSelectingEMP();
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                connected = false;
                parsing_message = e.getLocalizedMessage();
                updateAfterSelectingEMP();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            conn = false;
            connected = false;
            parsing_message = error.getLocalizedMessage();
            updateAfterSelectingEMP();
        });

        requestQueue.add(stringRequest);
    }

    private void updateAfterSelectingEMP() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);

                emp_id = "";
                emp_code = "";
                empSpinner.setText("");
                employeeSelectLay.setEnabled(true);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < empLists.size(); i++) {
                    type.add(empLists.get(i).getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

                empSpinner.setAdapter(arrayAdapter);

                if (allLive) {
                    empCard.setVisibility(VISIBLE);
                    allEmployeeLay.setVisibility(VISIBLE);
                    singleEmployeeLay.setVisibility(View.GONE);

                    liveLocationButtonAll.setVisibility(VISIBLE);
                    msgTimelineAll.setVisibility(View.GONE);
                }
                else {
                    empCard.setVisibility(GONE);
                    allEmployeeLay.setVisibility(GONE);
                    singleEmployeeLay.setVisibility(View.GONE);

                    liveLocationButtonAll.setVisibility(View.GONE);
                    msgTimelineAll.setVisibility(VISIBLE);
                }

                allLive = false;
                loading = false;
            }
            else {
                alertMessageEmp();
            }
        }
        else {
            alertMessageEmp();
        }
    }

    public void alertMessageEmp() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getEmployeeData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getEmployeeDetailsData() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        imageFound = false;
        loading = true;

        String url = api_url_front + "directory/getEmpDetails?emp_id="+emp_id;
        String userImageUrl = api_url_front + "utility/getUserImage/" + emp_code;

        RequestQueue requestQueue = Volley.newRequestQueue(HomePage.this);

        StringRequest imageReq = new StringRequest(Request.Method.GET, userImageUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userImageInfo = array.getJSONObject(i);
                        String emp_image = userImageInfo.getString("emp_image");
                        if (emp_image.equals("null") || emp_image.isEmpty()) {
                            System.out.println("NULL IMAGE");
                            imageFound = false;
                        } else {
                            byte[] decodedString = Base64.decode(emp_image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            if (bitmap != null) {
                                imageFound = true;
                                selectedImage = bitmap;
                            } else {
                                imageFound = false;
                            }
                        }
                    }
                }
                connected = true;
                updateAfterSelectingEMPDTL();
            } catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING, e.getMessage(), e);
                parsing_message = e.getLocalizedMessage();
                updateAfterSelectingEMPDTL();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING, error.getMessage(), error);
            parsing_message = error.getLocalizedMessage();
            updateAfterSelectingEMPDTL();
        });

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject desInfo = array.getJSONObject(i);

                        title = desInfo.getString("job_calling_title");
                        email = desInfo.getString("job_email");
                        trackingFlag = desInfo.getInt("emp_timeline_tracker_flag");
                        liveFlag = desInfo.getInt("emp_live_loc_tracker_flag");
                    }
                }
                requestQueue.add(imageReq);
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                connected = false;
                parsing_message = e.getLocalizedMessage();
                updateAfterSelectingEMPDTL();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            conn = false;
            connected = false;
            parsing_message = error.getLocalizedMessage();
            updateAfterSelectingEMPDTL();
        });

        requestQueue.add(stringRequest);
    }

    private void updateAfterSelectingEMPDTL() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);

                emp_name_text.setText(empName);
                title_text.setText(title);
                department_text.setText(depName);
                division_text.setText(divName);
                company_name_text.setText(CompanyName);

                email_text.setText(Objects.requireNonNullElse(email, "Email Not Found"));

                empCard.setVisibility(VISIBLE);
                singleEmployeeLay.setVisibility(VISIBLE);
                allEmployeeLay.setVisibility(View.GONE);

                if (trackingFlag == 1) {
                    timelineButton.setVisibility(VISIBLE);

                    if (liveFlag == 1) {
                        liveLocationButton.setVisibility(VISIBLE);
                    } else {
                        liveLocationButton.setVisibility(View.GONE);
                    }
                    msgTimeline.setVisibility(View.GONE);

                } else {
                    liveLocationButton.setVisibility(View.GONE);
                    timelineButton.setVisibility(View.GONE);
                    msgTimeline.setVisibility(VISIBLE);
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
                loading = false;
            }
            else {
                alertMessageEmpDetails();
            }
        }
        else {
            alertMessageEmpDetails();
        }
    }

    public void alertMessageEmpDetails() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getEmployeeDetailsData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getTestData() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String testUrl = api_url_front + "utility/testTrackerData";

        RequestQueue requestQueue = Volley.newRequestQueue(HomePage.this);

        StringRequest imageReq = new StringRequest(Request.Method.GET, testUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userImageInfo = array.getJSONObject(i);
                        String elr_file_name = userImageInfo.getString("elr_file_name");
                        String elr_filetype = userImageInfo.getString("elr_filetype");
                        String elr_location_file = userImageInfo.getString("elr_location_file");

                        String fileName = elr_file_name + "_" + i+1 + elr_filetype;

                        if (elr_location_file.equals("null") || elr_location_file.isEmpty()) {
                            System.out.println("NULL DATA");
                        } else {
                            File myExternalFile = new File(getExternalFilesDir(null),fileName);
                            byte[] decodedString = Base64.decode(elr_location_file, Base64.DEFAULT);

                            try {
                                FileOutputStream fos = new FileOutputStream(myExternalFile);
                                fos.write(decodedString);
                                fos.close();
                            }
                            catch (Exception e) {
                                logger.log(Level.WARNING, e.getMessage(), e);
                            }

                            testDataBlob.add(fileName);

                        }
                    }
                }
                connected = true;
                updateAfterGettingTestData();
            } catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING, e.getMessage(), e);
                parsing_message = e.getLocalizedMessage();
                updateAfterGettingTestData();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING, error.getMessage(), error);
            parsing_message = error.getLocalizedMessage();
            updateAfterGettingTestData();
        });

        requestQueue.add(imageReq);
    }

    private void updateAfterGettingTestData() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                setData.setVisibility(View.GONE);
                loading = false;
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                        .setMessage("There is a network issue in the server. Please Try later")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(v -> {

                    getTestData();
                    loading = false;
                    dialog.dismiss();
                });

                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(v -> dialog.dismiss());
            }
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .setNegativeButton("Cancel", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                getTestData();
                loading = false;
                dialog.dismiss();
            });

            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(v -> dialog.dismiss());
        }
    }

//    public boolean isConnected() {
//        boolean connected = false;
//        boolean isMobile = false;
//        try {
//            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo nInfo = cm.getActiveNetworkInfo();
//            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
//            return connected;
//        } catch (Exception e) {
//            Log.e("Connectivity Exception", e.getMessage());
//        }
//        return connected;
//    }
//
//    public boolean isOnline() {
//
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        }
//        catch (IOException | InterruptedException e)          { e.printStackTrace(); }
//
//        return false;
//    }

//    public class DivisionCheck extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                DivisionData();
//                if (connected) {
//                    conn = true;
//                }
//
//            } else {
//                conn = false;
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                conn = false;
//                connected = false;
//
//                //String[] type = new String[] {"Approved", "Pending","Both"};
//                ArrayList<String> type = new ArrayList<>();
//                for(int i = 0; i < divLists.size(); i++) {
//                    type.add(divLists.get(i).getName());
//                }
//
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
//
//                divSpinner.setAdapter(arrayAdapter);
//
//                //new ItemCheck().execute();
//
////                new ReOrderFragment.ReOrderLevelCheck().execute();
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .setNegativeButton("Cancel", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new DivisionCheck().execute();
//                        dialog.dismiss();
//                    }
//                });
//
//                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negative.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
//            }
//        }
//    }

//    public class DepartmentCheck extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                DepartmentData();
//                if (connected) {
//                    conn = true;
//                }
//
//            } else {
//                conn = false;
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                conn = false;
//                connected = false;
//
//                //String[] type = new String[] {"Approved", "Pending","Both"};
//                ArrayList<String> type = new ArrayList<>();
//                for(int i = 0; i < depLists.size(); i++) {
//                    type.add(depLists.get(i).getName());
//                }
//
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
//
//                depSpinner.setAdapter(arrayAdapter);
//
//
//                desLists = new ArrayList<>();
//                ArrayList<String> type1 = new ArrayList<>();
//                for(int i = 0; i < desLists.size(); i++) {
//                    type1.add(desLists.get(i).getName());
//                }
//
//                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
//
//                desSpinner.setAdapter(arrayAdapter1);
//
//
//                empLists = new ArrayList<>();
//                ArrayList<String> type2 = new ArrayList<>();
//                for(int i = 0; i < empLists.size(); i++) {
//                    type2.add(empLists.get(i).getName());
//                }
//
//                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);
//
//                empSpinner.setAdapter(arrayAdapter2);
//
//                //new ReOrderLevelCheck().execute();
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .setNegativeButton("Cancel", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new DepartmentCheck().execute();
//                        dialog.dismiss();
//                    }
//                });
//
//                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negative.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }

//    public class DesignationCheck extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                DesignationData();
//                if (connected) {
//                    conn = true;
//                }
//
//            } else {
//                conn = false;
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                conn = false;
//                connected = false;
//
//
//                ArrayList<String> type1 = new ArrayList<>();
//                for(int i = 0; i < desLists.size(); i++) {
//                    type1.add(desLists.get(i).getName());
//                }
//
//                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
//
//                desSpinner.setAdapter(arrayAdapter1);
//
//
//                empLists = new ArrayList<>();
//                ArrayList<String> type2 = new ArrayList<>();
//                for(int i = 0; i < empLists.size(); i++) {
//                    type2.add(empLists.get(i).getName());
//                }
//
//                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);
//
//                empSpinner.setAdapter(arrayAdapter2);
//
//                //new ReOrderLevelCheck().execute();
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .setNegativeButton("Cancel", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new DesignationCheck().execute();
//                        dialog.dismiss();
//                    }
//                });
//
//                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negative.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }

//    public class EmployeeCheck extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                EmployeeData();
//                if (connected) {
//                    conn = true;
//                }
//
//            } else {
//                conn = false;
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                conn = false;
//                connected = false;
//
//                //String[] type = new String[] {"Approved", "Pending","Both"};
//                ArrayList<String> type = new ArrayList<>();
//                for(int i = 0; i < empLists.size(); i++) {
//                    type.add(empLists.get(i).getName());
//                }
//
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
//
//                empSpinner.setAdapter(arrayAdapter);
//
//                empCard.setVisibility(View.VISIBLE);
//                allEmployeeLay.setVisibility(View.VISIBLE);
//                singleEmployeeLay.setVisibility(View.GONE);
//
//                if (allLive) {
//                    liveLocationButtonAll.setVisibility(View.VISIBLE);
//                    msgTimelineAll.setVisibility(View.GONE);
//                }
//                else {
//                    liveLocationButtonAll.setVisibility(View.GONE);
//                    msgTimelineAll.setVisibility(View.VISIBLE);
//                }
//
//                allLive = false;
//
//
//                //new ReOrderLevelCheck().execute();
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .setNegativeButton("Cancel", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new EmployeeCheck().execute();
//                        dialog.dismiss();
//                    }
//                });
//
//                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negative.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }

//    public class EmployeeDetailsCheck extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                EmployeeDetailsData();
//                if (connected) {
//                    conn = true;
//                }
//
//            } else {
//                conn = false;
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                conn = false;
//                connected = false;
//
//
//                emp_name_text.setText(empName);
//                title_text.setText(title);
//                department_text.setText(depName);
//                division_text.setText(divName);
//                company_name_text.setText(CompanyName);
//                if (email != null) {
//                    email_text.setText(email);
//                } else {
//                    email_text.setText("Email Not Found");
//                }
//
//                empCard.setVisibility(View.VISIBLE);
//                singleEmployeeLay.setVisibility(View.VISIBLE);
//                allEmployeeLay.setVisibility(View.GONE);
//
//                if (trackingFlag == 1) {
//                    timelineButton.setVisibility(View.VISIBLE);
//                    setData.setVisibility(View.VISIBLE);
//
//                    if (liveFlag == 1) {
//                        liveLocationButton.setVisibility(View.VISIBLE);
//                    } else {
//                        liveLocationButton.setVisibility(View.GONE);
//                    }
//                    msgTimeline.setVisibility(View.GONE);
//
//                } else {
//                    liveLocationButton.setVisibility(View.GONE);
//                    timelineButton.setVisibility(View.GONE);
//                    msgTimeline.setVisibility(View.VISIBLE);
//                    setData.setVisibility(View.GONE);
//                }
//                if (imageFound) {
//                    Glide.with(HomePage.this)
//                            .load(selectedImage)
//                            .fitCenter()
//                            .into(userImage);
//                }
//                else {
//                    userImage.setImageResource(R.drawable.profile);
//                }
//
//
//                //new ReOrderLevelCheck().execute();
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .setNegativeButton("Cancel", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new EmployeeDetailsCheck().execute();
//                        dialog.dismiss();
//                    }
//                });
//
//                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negative.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }

//    public class TestDataCheck extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                testDataGetting();
//                if (connected) {
//                    conn = true;
//                }
//
//            } else {
//                conn = false;
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                conn = false;
//                connected = false;
//
//
//                setData.setVisibility(View.GONE);
//
//                //new ReOrderLevelCheck().execute();
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .setNegativeButton("Cancel", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new TestDataCheck().execute();
//                        dialog.dismiss();
//                    }
//                });
//
//                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negative.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }

//    public void DivisionData() {
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//
//            divLists = new ArrayList<>();
//
//
//
////            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
////                    "    division_mst.divm_id,\n" +
////                    "    division_mst.divm_name\n" +
////                    "FROM\n" +
////                    "    division_mst\n" +
////                    "order by divm_id");
//            ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT\n" +
//                    "    division_mst.divm_id,\n" +
//                    "    division_mst.divm_name\n" +
//                    "FROM\n" +
//                    "         division_mst\n" +
//                    "    INNER JOIN job_setup_mst ON job_setup_mst.jsm_divm_id = division_mst.divm_id\n" +
//                    "    INNER JOIN job_setup_dtl ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
//                    "    INNER JOIN emp_mst ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
//                    "WHERE emp_mst.emp_quit = 0\n" +
//                    "ORDER BY\n" +
//                    "    division_mst.divm_id");
//
//            while (resultSet1.next()) {
//                divLists.add(new TwoItemLists(resultSet1.getString(1),resultSet1.getString(2)));
//            }
////            categoryLists.add(new ReceiveTypeList("","All Categories"));
//
//            ResultSet rs = stmt.executeQuery("SELECT CIM_NAME--,CIM_LOGO_APPS\n" +
//                    " FROM COMPANY_INFO_MST\n");
//
//            while (rs.next()) {
//                companyName = rs.getString(1);
//            }
//
//
//            String userName = getEmpCode;
//            String userId = getEmpId;
//
//            if (userId != null) {
//                if (!userId.isEmpty()) {
//                    System.out.println(userId);
//                } else {
//                    userId = "0";
//                }
//            } else {
//                userId = "0";
//            }
//
//            sessionId = "";
//
//            ResultSet resultSet3 = stmt.executeQuery("SELECT SYS_CONTEXT ('USERENV', 'SESSIONID') --INTO P_IULL_SESSION_ID\n" +
//                    "   FROM DUAL\n");
//
//            while (resultSet3.next()) {
//                System.out.println("SESSION ID: "+ resultSet3.getString(1));
//                sessionId = resultSet3.getString(1);
//            }
//
//            resultSet3.close();
//
//            CallableStatement callableStatement1 = connection.prepareCall("{call USERLOGINDTL(?,?,?,?,?,?,?,?,?)}");
//            callableStatement1.setString(1,userName);
//            callableStatement1.setString(2, brand+" "+model);
//            callableStatement1.setString(3,ipAddress);
//            callableStatement1.setString(4,hostUserName);
//            callableStatement1.setInt(5,Integer.parseInt(userId));
//            callableStatement1.setInt(6,Integer.parseInt(sessionId));
//            callableStatement1.setString(7,"1");
//            callableStatement1.setString(8,osName);
//            callableStatement1.setInt(9,4);
//            callableStatement1.execute();
//
//            callableStatement1.close();
//
//
//
//            connected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }

//    public void DepartmentData() {
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//
//            depLists = new ArrayList<>();
//
//
//
//            if (div_id.isEmpty()) {
//                div_id = null;
//            }
//
////            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
////                    "    dept_mst.dept_id,\n" +
////                    "    dept_mst.dept_name,\n" +
////                    "    dept_mst.dept_divm_id\n" +
////                    "FROM\n" +
////                    "    dept_mst\n" +
////                    "where dept_divm_id = "+div_id+"\n" +
////                    "order by dept_id");
//            ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT\n" +
//                    "    dept_mst.dept_id,\n" +
//                    "    dept_mst.dept_name,\n" +
//                    "    dept_mst.dept_divm_id\n" +
//                    "FROM\n" +
//                    "         dept_mst\n" +
//                    "    INNER JOIN job_setup_mst ON job_setup_mst.jsm_dept_id = dept_mst.dept_id\n" +
//                    "    INNER JOIN job_setup_dtl ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
//                    "    INNER JOIN emp_mst ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
//                    "WHERE\n" +
//                    "        dept_mst.dept_divm_id = "+div_id+"\n" +
//                    "    AND emp_mst.emp_quit = 0\n" +
//                    "ORDER BY\n" +
//                    "    dept_mst.dept_id");
//
//            while (resultSet1.next()) {
//                depLists.add(new TwoItemLists(resultSet1.getString(1),resultSet1.getString(2)));
//            }
////            categoryLists.add(new ReceiveTypeList("","All Categories"));
//
//            if (div_id == null) {
//                div_id = "";
//            }
//            connected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }

//    public void DesignationData() {
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//
//            desLists = new ArrayList<>();
//
//
//            if (div_id.isEmpty()) {
//                div_id = null;
//            }
//            if (dep_id.isEmpty()) {
//                dep_id = null;
//            }
//
////            ResultSet resultSet1 = stmt.executeQuery("select distinct desig_id, desig_name from emp_details_v where desig_name is not NULL and dept_id = "+dep_id+" and divm_id = "+div_id+"\n");
//            ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT\n" +
//                    "    desig_mst.desig_id,\n" +
//                    "    desig_mst.desig_name\n" +
//                    "FROM\n" +
//                    "    desig_mst,\n" +
//                    "    job_setup_mst,\n" +
//                    "    dept_mst,\n" +
//                    "    division_mst,\n" +
//                    "    job_setup_dtl,\n" +
//                    "    emp_mst\n" +
//                    "WHERE\n" +
//                    "        desig_mst.desig_id = job_setup_mst.jsm_desig_id\n" +
//                    "    AND job_setup_mst.jsm_divm_id = division_mst.divm_id\n" +
//                    "    AND job_setup_mst.jsm_dept_id = dept_mst.dept_id\n" +
//                    "    AND emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
//                    "    AND job_setup_mst.jsm_id = job_setup_dtl.jsd_jsm_id\n" +
//                    "    AND desig_mst.desig_name IS NOT NULL\n" +
//                    "    AND division_mst.divm_id = "+div_id+"\n" +
//                    "    AND dept_mst.dept_id = "+dep_id+"\n" +
//                    "    AND emp_mst.emp_quit = 0\n" +
//                    "ORDER BY\n" +
//                    "    desig_mst.desig_id");
//
//            while (resultSet1.next()) {
//                desLists.add(new TwoItemLists(resultSet1.getString(1),resultSet1.getString(2)));
//            }
////            categoryLists.add(new ReceiveTypeList("","All Categories"));
//
//            if (div_id == null) {
//                div_id = "";
//            }
//            if (dep_id == null) {
//                dep_id = "";
//            }
//            connected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }

//    public void EmployeeData() {
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//
//            empLists = new ArrayList<>();
//
//            allLive = false;
//
//
//            if (div_id.isEmpty()) {
//                div_id = null;
//            }
//            if (dep_id.isEmpty()) {
//                dep_id = null;
//            }
//            if (des_id.isEmpty()) {
//                des_id = null;
//            }
//
//            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
//                    "    emp_mst.emp_id,\n" +
//                    "    emp_mst.emp_name,\n" +
//                    "    emp_mst.emp_code,\n" +
//                    "    job_setup_mst.jsm_divm_id,\n" +
//                    "    job_setup_mst.jsm_dept_id,\n" +
//                    "    job_setup_mst.jsm_desig_id\n" +
//                    "FROM\n" +
//                    "         emp_mst\n" +
//                    "    INNER JOIN job_setup_dtl ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
//                    "    INNER JOIN job_setup_mst ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
//                    "WHERE\n" +
//                    "        job_setup_mst.jsm_divm_id = "+div_id+"\n" +
//                    "    AND job_setup_mst.jsm_dept_id = "+dep_id+"\n" +
//                    "    AND job_setup_mst.jsm_desig_id = "+des_id+"\n" +
//                    "ORDER BY\n" +
//                    "    emp_mst.emp_id");
//
//            while (resultSet1.next()) {
//                empLists.add(new ThreeItemLists(resultSet1.getString(1),resultSet1.getString(2)+" ("+resultSet1.getString(3)+")",resultSet1.getString(3)));
//            }
//
//            ResultSet resultSet = stmt.executeQuery("SELECT\n" +
//                    "    emp_mst.emp_timeline_tracker_flag,\n" +
//                    "    emp_mst.emp_live_loc_tracker_flag\n" +
//                    "FROM\n" +
//                    "         emp_mst\n" +
//                    "    INNER JOIN job_setup_dtl ON emp_mst.emp_jsd_id = job_setup_dtl.jsd_id\n" +
//                    "    INNER JOIN job_setup_mst ON job_setup_dtl.jsd_jsm_id = job_setup_mst.jsm_id\n" +
//                    "WHERE\n" +
//                    "        job_setup_mst.jsm_divm_id = "+div_id+"\n" +
//                    "    AND job_setup_mst.jsm_dept_id = "+dep_id+"\n" +
//                    "    AND job_setup_mst.jsm_desig_id = "+des_id+"");
//
//            while (resultSet.next()) {
//                int track = resultSet.getInt(1);
//                int live = resultSet.getInt(2);
//
//                if (track == 1 && live == 1) {
//                    allLive = true;
//                }
//            }
//            if (div_id == null) {
//                div_id = "";
//            }
//            if (dep_id == null) {
//                dep_id = "";
//            }
//            if (des_id == null) {
//                des_id = "";
//            }
//
//            connected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }

//    public void EmployeeDetailsData() {
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//            imageFound = false;
//
//            if (emp_id.isEmpty()) {
//                emp_id = null;
//            }
//
//
//            ResultSet resultSet1 = stmt.executeQuery("SELECT\n" +
//                    "    emp_job_history.job_calling_title,\n" +
//                    "    emp_job_history.job_email,\n" +
//                    "    emp_job_history.job_emp_id\n" +
//                    "FROM\n" +
//                    "    emp_job_history\n" +
//                    "    where emp_job_history.job_emp_id = "+emp_id+"");
//
//            while (resultSet1.next()) {
//                title = resultSet1.getString(1);
//                email = resultSet1.getString(2);
//            }
////            categoryLists.add(new ReceiveTypeList("","All Categories"));
//
//            ResultSet resultSet = stmt.executeQuery("SELECT emp_mst.emp_timeline_tracker_flag, emp_mst.EMP_LIVE_LOC_TRACKER_FLAG from emp_mst where emp_mst.emp_id = "+emp_id+"");
//
//            while (resultSet.next()) {
//                trackingFlag = resultSet.getInt(1);
//                liveFlag = resultSet.getInt(2);
//            }
//            System.out.println(emp_code);
////            ResultSet imageResult = stmt.executeQuery("SELECT EMP_IMAGE FROM EMP_MST WHERE EMP_ID = "+emp_id+"");
//
//            ResultSet imageResult;
//            if (DEFAULT_USERNAME.equals("ELITE")) {
//                imageResult = stmt.executeQuery("Select loadBlobFrom_DOCARC_File('" + emp_code + "'||'.jpg','ELITE_EMP_IMG_DIR') EMP_IMAGE from dual");
//            }
//            else {
//                imageResult = stmt.executeQuery("Select loadblobfromfile('" + emp_code + "'||'.jpg') EMP_IMAGE from dual");
//            }
//
//            while (imageResult.next()) {
//                Blob b = imageResult.getBlob(1);
//                if (b == null) {
//                    imageFound = false;
//                }
//                else {
//                    imageFound = true;
//                    byte[] barr =b.getBytes(1,(int)b.length());
//                    selectedImage = BitmapFactory.decodeByteArray(barr,0,barr.length);
//                }
//                System.out.println(imageFound);
//            }
//            imageResult.close();
//
//            stmt.close();
//
//            connected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }

//    public void testDataGetting () {
//
//        try {
//
//            this.connection = createConnection();
//
//            testDataBlob = new ArrayList<>();
//
//            PreparedStatement ps = connection.prepareStatement("SELECT ELR_FILE_NAME, ELR_FILETYPE, ELR_LOCATION_FILE\n" +
//                    "FROM EMP_LOCATION_RECORD\n" +
//                    "WHERE ELR_EMP_ID = 2021\n" +
//                    "AND ELR_FILE_NAME = ANY('2021_01-FEB-22_track','2021_10-FEB-22_track','2021_07-FEB-22_track','2021_20-JAN-22_track','2021_30-JAN-22_track','2021_24-JAN-22_track')");
//
//            ResultSet set = ps.executeQuery();
//            int ii  = 0;
//            while (set.next()) {
//                ii++;
//                Blob b = set.getBlob(3);
//                String fileName = set.getString(1) + "_" + ii + set.getString(2);
//
//                if (b != null && b.length() != 0) {
//                    System.out.println("BLOB paise");
//                    File myExternalFile = new File(getExternalFilesDir(null),fileName);
//
//                    InputStream r = b.getBinaryStream();
//                    FileWriter fw=new FileWriter(myExternalFile);
//                    int i;
//                    while((i=r.read())!=-1)
//                        fw.write((char)i);
//                    fw.close();
//                    testDataBlob.add(fileName);
//                    //blobNotNull = true;
//                } else {
//                    System.out.println("BLOB pai nai");
//                    //blobNotNull = false;
//                }
//
//            }
//
//            connected = true;
//
//            connection.close();
//
//        } catch (Exception e) {
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }
}
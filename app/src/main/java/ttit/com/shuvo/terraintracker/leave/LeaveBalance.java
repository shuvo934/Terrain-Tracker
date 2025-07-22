package ttit.com.shuvo.terraintracker.leave;

import static ttit.com.shuvo.terraintracker.Constants.api_url_front;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.araylists.BranchList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DepartmentList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DesignationList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DivisionList;
import ttit.com.shuvo.terraintracker.attendance.araylists.EmployeeList;
import ttit.com.shuvo.terraintracker.leave.adapters.LeaveBalanceAdapter;
import ttit.com.shuvo.terraintracker.leave.arraylists.LeaveBalanceList;

public class LeaveBalance extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    LinearLayout branchSelect;
    TextView branchName;
    ArrayList<BranchList> branchLists;

    AppCompatAutoCompleteTextView divisionSelect;
    ArrayList<DivisionList> divisionLists;

    TextInputLayout departmentSelectLay;
    AppCompatAutoCompleteTextView departmentSelect;
    ArrayList<DepartmentList> departmentLists;

    TextInputLayout designationSelectLay;
    AppCompatAutoCompleteTextView designationSelect;
    ArrayList<DesignationList> designationLists;

    TextInputLayout employeeSelectLay;
    AppCompatAutoCompleteTextView employeeSelect;
    ArrayList<EmployeeList> employeeLists;

    ScrollView empDataLay;

    TextView empName;
    TextView empCode;
    TextView empDesignation;
    TextView empCenter;

    String emp_name = "";
    String emp_code = "";
    String emp_designation = "";
    String emp_center = "";

    TextView yearSelection;
    AlertDialog yearDialog;
    String last_date = "";

    BarChart leaveBalChart;
    ArrayList<BarEntry> totLeave;
    ArrayList<BarEntry> consumed;
    ArrayList<BarEntry> balance;
    ArrayList<String> shortCode;

    String selected_coa_id = "";
    String selected_div_id = "";
    String selected_dept_id = "";
    String selected_desig_id = "";
    String selected_emp_id = "";
    String la_to_date = "";

    LinearLayout leaveData;
    TextView noLeaveDataMsg;

    RecyclerView reportView;
    RecyclerView.LayoutManager layoutManager;
    LeaveBalanceAdapter leaveBalanceAdapter;

    ArrayList<LeaveBalanceList> leaveBalanceLists;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    Logger logger = Logger.getLogger(LeaveBalance.class.getName());

    PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_balance);

        fullLayout = findViewById(R.id.leave_balance_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_leave_balance);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_leave_Balance);

        branchSelect = findViewById(R.id.branch_selection_leave_Balance);
        branchName = findViewById(R.id.selected_branch_name_leave_Balance);
        branchLists = new ArrayList<>();

        popupMenu = new PopupMenu(LeaveBalance.this, branchSelect);

        divisionSelect = findViewById(R.id.division_select_leave_balance);
        divisionLists = new ArrayList<>();

        departmentSelectLay = findViewById(R.id.spinner_layout_department_select_leave_balance);
        departmentSelectLay.setEnabled(false);
        departmentSelect = findViewById(R.id.department_select_leave_balance);
        departmentLists = new ArrayList<>();

        designationSelectLay = findViewById(R.id.spinner_layout_designation_select_leave_balance);
        designationSelectLay.setEnabled(false);
        designationSelect = findViewById(R.id.designation_select_leave_balance);
        designationLists = new ArrayList<>();

        employeeSelectLay = findViewById(R.id.spinner_layout_emp_select_leave_balance);
        employeeSelectLay.setEnabled(false);
        employeeSelect = findViewById(R.id.emp_select_leave_balance);
        employeeLists = new ArrayList<>();

        empDataLay = findViewById(R.id.leave_balance_data_layout);
        empDataLay.setVisibility(View.GONE);

        empName = findViewById(R.id.emp_name_in_leave_balance);
        empCode = findViewById(R.id.emp_code_in_leave_balance);
        empDesignation = findViewById(R.id.emp_designation_in_leave_balance);
        empCenter = findViewById(R.id.emp_pri_loc_in_leave_balance);

        yearSelection = findViewById(R.id.select_year_for_lv_bal_graph_emp_wise);

        leaveBalChart = findViewById(R.id.emp_leave_balance_bar_chart);
        totLeave = new ArrayList<>();
        consumed = new ArrayList<>();
        balance = new ArrayList<>();
        shortCode = new ArrayList<>();

        leaveData = findViewById(R.id.leave_bal_list_header_layout);
        noLeaveDataMsg = findViewById(R.id.no_leave_bal_msg_in_lv_bal);
        noLeaveDataMsg.setVisibility(View.GONE);

        reportView = findViewById(R.id.leave_list_view);
        leaveBalanceLists = new ArrayList<>();

        reportView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        reportView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(reportView.getContext(),DividerItemDecoration.VERTICAL);
        reportView.addItemDecoration(dividerItemDecoration);

        Intent intent = getIntent();
        selected_coa_id = intent.getStringExtra("COA_ID") != null ? intent.getStringExtra("COA_ID") : "";
        selected_div_id = intent.getStringExtra("DIV_ID") != null ? intent.getStringExtra("DIV_ID") : "";
        selected_dept_id = intent.getStringExtra("DEPT_ID") != null ? intent.getStringExtra("DEPT_ID") : "";
        selected_desig_id = intent.getStringExtra("DESIG_ID") != null ? intent.getStringExtra("DESIG_ID") : "";
        selected_emp_id = intent.getStringExtra("EMP_ID") != null ? intent.getStringExtra("EMP_ID") : "";
        la_to_date = intent.getStringExtra("LAST_DATE") != null ? intent.getStringExtra("LAST_DATE") : "";

        barChartInit();

        getCurrentDateMonthYear();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String branch_name = "";
            for (int i = 0; i <branchLists.size(); i++) {
                if (String.valueOf(menuItem.getItemId()).equals(branchLists.get(i).getId())) {

                    selected_coa_id = branchLists.get(i).getId();
                    branch_name = branchLists.get(i).getName();
                }
            }

            branchName.setText(branch_name);

            if (selected_coa_id.equals("30")) {
                selected_coa_id = "";
                getDivisions();
            }
            else {
                getDivisionsCoa();
            }
            return false;
        });

        branchSelect.setOnClickListener(view -> popupMenu.show());

        divisionSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_dept_id = "";
            departmentSelect.setText("");
            departmentSelectLay.setEnabled(false);

            selected_desig_id = "";
            designationSelect.setText("");
            designationSelectLay.setEnabled(false);

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);

            empDataLay.setVisibility(View.GONE);

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <divisionLists.size(); i++) {
                if (name.equals(divisionLists.get(i).getDivm_name())) {

                    selected_div_id = divisionLists.get(i).getDivm_id();
                }
            }

            if (selected_div_id.isEmpty()) {
                divisionSelect.setText("");
            }

            getDepartment();

        });

        departmentSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_desig_id = "";
            designationSelect.setText("");
            designationSelectLay.setEnabled(false);

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);

            empDataLay.setVisibility(View.GONE);

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <departmentLists.size(); i++) {
                if (name.equals(departmentLists.get(i).getDept_name())) {

                    selected_dept_id = departmentLists.get(i).getDept_id();
                }
            }

            if (selected_dept_id.isEmpty()) {
                departmentSelect.setText("");
            }

            getDesignations();

        });

        designationSelect.setOnItemClickListener((parent, view, position, id) -> {

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);

            empDataLay.setVisibility(View.GONE);

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <designationLists.size(); i++) {
                if (name.equals(designationLists.get(i).getDesig_name())) {

                    selected_desig_id = designationLists.get(i).getDesig_id();
                }
            }

            if (selected_desig_id.isEmpty()) {
                designationSelect.setText("");
            }

            getEmployees();

        });

        employeeSelect.setOnItemClickListener((parent, view, position, id) -> {

            empDataLay.setVisibility(View.GONE);
            emp_name = "";
            emp_code = "";
            emp_designation = "";
            emp_center = "";

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <employeeLists.size(); i++) {
                if (name.equals(employeeLists.get(i).getEmp_name())) {

                    selected_emp_id = employeeLists.get(i).getEmp_id();
                    emp_name = employeeLists.get(i).getEmp_name();
                    emp_code = employeeLists.get(i).getEmp_code();
                    emp_designation = employeeLists.get(i).getJob_calling_title();
                    emp_center = employeeLists.get(i).getCoa_name();
                }
            }

            if (selected_emp_id.isEmpty()) {
                employeeSelect.setText("");
            }

            getLeaveData();

        });

        yearSelection.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            Date c = Calendar.getInstance().getTime();
            Date d = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
            if (!last_date.isEmpty()) {
                Date date = null;
                try {
                    date = simpleDateFormat.parse(last_date);
                }
                catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }
                if (date != null) {
                    d = date;
                }
            }

            MonthPickerDialog.Builder appBuilder = new MonthPickerDialog.Builder(LeaveBalance.this, (selectedMonth, selectedYear) -> {
                System.out.println("Selected Year: "+selectedYear);
                String ms = "YEAR: " + selectedYear;
                yearSelection.setText(ms);

                String short_year = String.valueOf(selectedYear).substring(String.valueOf(selectedYear).length()-2);
                last_date = "31-DEC-"+short_year;

                getLeaveData();

            },today.get(Calendar.YEAR),today.get(Calendar.MONTH));

            appBuilder.setActivatedYear(Integer.parseInt(df.format(d)))
                    .setMinYear(Integer.parseInt(df.format(c))-10)
                    .setMaxYear(Integer.parseInt(df.format(c)))
                    .showYearOnly()
                    .setTitle("Selected Year")
                    .setOnYearChangedListener(year1 -> {
                    });

            yearDialog = appBuilder.build();
            yearDialog.show();
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(LeaveBalance.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(LeaveBalance.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        if (!selected_div_id.isEmpty() && !selected_dept_id.isEmpty() && !selected_desig_id.isEmpty() && !selected_emp_id.isEmpty()) {
            if (!la_to_date.isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
                Date today = null;
                try {
                    today = simpleDateFormat.parse(la_to_date);
                } catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }
                if (today!= null) {
                    String mo_name = yearFormat.format(today);
                    mo_name = mo_name.toUpperCase(Locale.ENGLISH);
                    String ms = "YEAR: " + mo_name;
                    yearSelection.setText(ms);

                    String short_year = mo_name.substring(mo_name.length()-2);
                    last_date = "31-DEC-"+short_year;
                }
            }
            if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
                getAllData();
            }
            else {
                getAllDataCoa();
            }
        }
        else {
            getDivisions();
        }
    }

    public void barChartInit() {
        leaveBalChart.getDescription().setEnabled(false);
        leaveBalChart.setPinchZoom(false);
        leaveBalChart.getAxisLeft().setDrawGridLines(true);
        leaveBalChart.getAxisLeft().setAxisMinimum(0);

        leaveBalChart.getLegend().setStackSpace(20);
        leaveBalChart.getLegend().setYOffset(10);
        leaveBalChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        leaveBalChart.setExtraOffsets(0,0,0,20);

        leaveBalChart.setScaleEnabled(false);
        leaveBalChart.setTouchEnabled(false);
        leaveBalChart.setDoubleTapToZoomEnabled(false);

        leaveBalChart.getAxisRight().setEnabled(false);
    }

    private void getCurrentDateMonthYear() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
        Date dd = Calendar.getInstance().getTime();
        String mo_name = simpleDateFormat.format(dd);
        mo_name = mo_name.toUpperCase(Locale.ENGLISH);
        String ms = "YEAR: " + mo_name;
        yearSelection.setText(ms);

        String short_year = mo_name.substring(mo_name.length()-2);
        last_date = "31-DEC-"+short_year;
    }

    // -- Divisions
    public void getDivisions() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        empDataLay.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;
        divisionLists = new ArrayList<>();
        branchLists = new ArrayList<>();

        String brnUrl = api_url_front+"hrm_dashboard/getBranches";
        String divUrl = api_url_front+"hrm_dashboard/getDivision";

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
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                connected = true;
                updateDivisionLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDivisionLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDivisionLay();
        });

        StringRequest brnReq = new StringRequest(Request.Method.GET, brnUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    branchLists.add(new BranchList("30","None"));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        branchLists.add(new BranchList(coa_id,coa_name));
                    }
                }
                requestQueue.add(divReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDivisionLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDivisionLay();
        });

        requestQueue.add(brnReq);
    }

    public void updateDivisionLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                selected_div_id = "";
                divisionSelect.setText("");

                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(false);

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(false);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(View.GONE);

                popupMenu.getMenuInflater().inflate(R.menu.branch_menu, popupMenu.getMenu());

                Menu menu = popupMenu.getMenu();
                menu.clear();
                for(int i = 0; i < branchLists.size(); i++) {
                    menu.add(0,Integer.parseInt(branchLists.get(i).getId()),Menu.NONE,branchLists.get(i).getName());
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

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
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    getDivisions();
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

    // -- Divisions with COA
    public void getDivisionsCoa() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        empDataLay.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;
        divisionLists = new ArrayList<>();

        String divUrl = api_url_front+"hrm_dashboard/getDivisionWithCoa?p_coa_id="+selected_coa_id;

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
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                connected = true;
                updateDivisionLayCoa();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDivisionLayCoa();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDivisionLayCoa();
        });

        requestQueue.add(divReq);
    }

    public void updateDivisionLayCoa() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                selected_div_id = "";
                divisionSelect.setText("");

                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(false);

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(false);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(View.GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                alertMessageDivCoa();
            }
        }
        else {
            alertMessageDivCoa();
        }
    }

    public void alertMessageDivCoa() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    getDivisionsCoa();
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

    // --- Departments
    public void getDepartment() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        departmentLists = new ArrayList<>();

        String deptUrl;
        if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
            deptUrl = api_url_front + "hrm_dashboard/getDepartmentByDiv?p_div_id=" + selected_div_id;
        }
        else {
            deptUrl = api_url_front+"hrm_dashboard/getDepartmentByDivCoa?p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest depReq = new StringRequest(Request.Method.GET, deptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dept_id = info.getString("dept_id")
                                .equals("null") ? "" :info.getString("dept_id");
                        String dept_name = info.getString("dept_name")
                                .equals("null") ? "" :info.getString("dept_name");
                        String dept_divm_id = info.getString("dept_divm_id")
                                .equals("null") ? "" :info.getString("dept_divm_id");

                        departmentLists.add(new DepartmentList(dept_id,dept_name,dept_divm_id));
                    }
                }
                connected = true;
                updateDeptLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDeptLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDeptLay();
        });

        requestQueue.add(depReq);
    }

    public void updateDeptLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(true);

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(false);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(View.GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < departmentLists.size(); i++) {
                    type.add(departmentLists.get(i).getDept_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                departmentSelect.setAdapter(arrayAdapter);

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
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    getDepartment();
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

    // --- Designations
    public void getDesignations() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        designationLists = new ArrayList<>();

        String desigUrl;
        if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
            desigUrl = api_url_front + "hrm_dashboard/getDesignationsByDept?p_dep_Id=" + selected_dept_id + "&p_div_id=" + selected_div_id;
        }
        else {
            desigUrl = api_url_front+"hrm_dashboard/getDesignationsByDeptCoa?p_dep_Id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest desigReq = new StringRequest(Request.Method.GET, desigUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" :info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" :info.getString("desig_name");

                        designationLists.add(new DesignationList(desig_id,desig_name));
                    }
                }
                connected = true;
                updateDesigLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDesigLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDesigLay();
        });

        requestQueue.add(desigReq);
    }

    public void updateDesigLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(true);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(View.GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < designationLists.size(); i++) {
                    type.add(designationLists.get(i).getDesig_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                designationSelect.setAdapter(arrayAdapter);

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
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

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

    // --- Employees
    public void getEmployees() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        employeeLists = new ArrayList<>();

        String empUrl;
        if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
            empUrl = api_url_front + "hrm_dashboard/getEmployeeByDesig?p_dep_id=" + selected_dept_id + "&p_div_id=" + selected_div_id + "&p_desig_id=" + selected_desig_id;
        }
        else {
            empUrl = api_url_front+"hrm_dashboard/getEmployeeByDesigCoa?p_dep_id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_desig_id="+selected_desig_id+"&p_coa_id="+selected_coa_id;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest empReq = new StringRequest(Request.Method.GET, empUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,coa_name));
                    }
                }
                connected = true;
                updateEmpLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateEmpLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateEmpLay();
        });

        requestQueue.add(empReq);
    }

    public void updateEmpLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(true);

                empDataLay.setVisibility(View.GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < employeeLists.size(); i++) {
                    type.add(employeeLists.get(i).getEmp_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                employeeSelect.setAdapter(arrayAdapter);

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
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    getEmployees();
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

    // --- Leave Data
    public void getLeaveData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        totLeave = new ArrayList<>();
        consumed = new ArrayList<>();
        balance = new ArrayList<>();
        shortCode = new ArrayList<>();

        leaveBalanceLists = new ArrayList<>();

        String leaveBalUrl = api_url_front+"hrm_dashboard/getLeaveBalance?now_date="+last_date+"&p_emp_id="+selected_emp_id;
        String leaveChartUrl = api_url_front+"hrm_dashboard/getLeaveChartData?now_date="+last_date+"&p_emp_id="+selected_emp_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest leaveChartReq = new StringRequest(Request.Method.GET, leaveChartUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject leaveInfo = array.getJSONObject(i);

                        String lc_short_code = leaveInfo.getString("lc_short_code")
                                .equals("null") ? "" : leaveInfo.getString("lc_short_code");
                        String quantity = leaveInfo.getString("quantity")
                                .equals("null") ? "0" : leaveInfo.getString("quantity");
                        String taken = leaveInfo.getString("consumed")
                                .equals("null") ? "0" : leaveInfo.getString("consumed");
                        String balance_all = leaveInfo.getString("balance")
                                .equals("null") ? "0" : leaveInfo.getString("balance");

                        if (!quantity.equals("0")) {
                            totLeave.add(new BarEntry(i, Float.parseFloat(quantity),i));
                            consumed.add(new BarEntry(i, Float.parseFloat(taken)));
                            balance.add(new BarEntry(i, Float.parseFloat(balance_all),i));
                            shortCode.add(lc_short_code);
                        }
                    }
                }

                connected = true;
                updateLeaveLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLeaveLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLeaveLay();
        });

        StringRequest leaveBalReq = new StringRequest(Request.Method.GET, leaveBalUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject leaveBalanceInfo = array.getJSONObject(i);

                        String lc_name = leaveBalanceInfo.getString("lc_name")
                                .equals("null") ? "" : leaveBalanceInfo.getString("lc_name");
                        String lc_short_code = leaveBalanceInfo.getString("lc_short_code")
                                .equals("null") ? "" : leaveBalanceInfo.getString("lc_short_code");
                        String lbd_opening_qty = leaveBalanceInfo.getString("lbd_opening_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_opening_qty");
                        String lbd_current_qty = leaveBalanceInfo.getString("lbd_current_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_current_qty");
                        String lbd_taken_qty = leaveBalanceInfo.getString("lbd_taken_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_taken_qty");
                        String lbd_cash_taken_qty = leaveBalanceInfo.getString("lbd_cash_taken_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_cash_taken_qty");
                        String lbd_transfer_qty = leaveBalanceInfo.getString("lbd_transfer_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_transfer_qty");
                        String lbd_balance_qty = leaveBalanceInfo.getString("lbd_balance_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_balance_qty");

                        leaveBalanceLists.add(new LeaveBalanceList(lc_name,lc_short_code,lbd_opening_qty,
                                lbd_current_qty,lbd_taken_qty,lbd_transfer_qty,lbd_cash_taken_qty,lbd_balance_qty));

                    }
                }

                requestQueue.add(leaveChartReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLeaveLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLeaveLay();
        });

        requestQueue.add(leaveBalReq);
    }

    public void updateLeaveLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                empDataLay.setVisibility(View.VISIBLE);

                empName.setText(emp_name);
                String tt = "("+emp_code+")";
                empCode.setText(tt);
                empDesignation.setText(emp_designation);
                empCenter.setText(emp_center);

                // --- Bar chart Initialization
                if (!balance.isEmpty() && !totLeave.isEmpty() && !shortCode.isEmpty() && !consumed.isEmpty()) {
                    XAxis xAxis = leaveBalChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setAxisMinimum(0);
                    xAxis.setAxisMaximum(totLeave.size());
                    xAxis.setGranularity(1);

                    BarDataSet set1 = new BarDataSet(totLeave, "Total Leave");
                    set1.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
                    set1.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (String.valueOf(value).contains(".0")) {
                                return String.valueOf((int) Math.floor(value));
                            }
                            else {
                                return String.valueOf(value);
                            }
                        }
                    });

                    BarDataSet set2 = new BarDataSet(consumed, "Consumed");
                    set2.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
                    set2.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (String.valueOf(value).contains(".0")) {
                                return String.valueOf((int) Math.floor(value));
                            }
                            else {
                                return String.valueOf(value);
                            }
                        }
                    });

                    BarDataSet set3 = new BarDataSet(balance, "Balance");
                    set3.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
                    set3.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (String.valueOf(value).contains(".0")) {
                                return String.valueOf((int) Math.floor(value));
                            }
                            else {
                                return String.valueOf(value);
                            }
                        }
                    });

                    float groupSpace = 0.04f;
                    float barSpace = 0.03f; // x3 dataset
                    float barWidth = 0.29f;

                    BarData data = new BarData(set1, set2, set3);
                    data.setValueTextSize(12);
                    data.setBarWidth(barWidth); // set the width of each bar
                    leaveBalChart.animateY(1000);
                    leaveBalChart.setData(data);
                    leaveBalChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
                    leaveBalChart.invalidate();

                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            if (value < 0 || value >= shortCode.size()) {
                                return null;
                            } else {
                                return (shortCode.get((int) value));
                            }

                        }
                    });
                }
                else {
                    BarData data = new BarData();
                    leaveBalChart.setData(data);
                    leaveBalChart.getData().clearValues();
                    leaveBalChart.notifyDataSetChanged();
                    leaveBalChart.clear();
                    leaveBalChart.invalidate();
                    leaveBalChart.fitScreen();
                }

                if (leaveBalanceLists.isEmpty()) {
                    noLeaveDataMsg.setVisibility(View.VISIBLE);
                    leaveData.setVisibility(View.GONE);
                }
                else {
                    leaveData.setVisibility(View.VISIBLE);
                    noLeaveDataMsg.setVisibility(View.GONE);
                }

                leaveBalanceAdapter = new LeaveBalanceAdapter(leaveBalanceLists, LeaveBalance.this);
                reportView.setAdapter(leaveBalanceAdapter);

                loading = false;

            }
            else {
                alertMessageAttendance();
            }
        }
        else {
            alertMessageAttendance();
        }
    }

    public void alertMessageAttendance() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    getLeaveData();
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

    // - getting All Data
    public void getAllData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        empDataLay.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        branchLists = new ArrayList<>();
        divisionLists = new ArrayList<>();
        departmentLists = new ArrayList<>();
        designationLists = new ArrayList<>();
        employeeLists = new ArrayList<>();

        totLeave = new ArrayList<>();
        consumed = new ArrayList<>();
        balance = new ArrayList<>();
        shortCode = new ArrayList<>();

        leaveBalanceLists = new ArrayList<>();

        String brnUrl = api_url_front+"hrm_dashboard/getBranches";
        String divUrl = api_url_front+"hrm_dashboard/getDivision";
        String deptUrl = api_url_front+"hrm_dashboard/getDepartmentByDiv?p_div_id="+selected_div_id;
        String desigUrl = api_url_front+"hrm_dashboard/getDesignationsByDept?p_dep_Id="+selected_dept_id+"&p_div_id="+selected_div_id;
        String empUrl = api_url_front+"hrm_dashboard/getEmployeeByDesig?p_dep_id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_desig_id="+selected_desig_id;
        String leaveBalUrl = api_url_front+"hrm_dashboard/getLeaveBalance?now_date="+last_date+"&p_emp_id="+selected_emp_id;
        String leaveChartUrl = api_url_front+"hrm_dashboard/getLeaveChartData?now_date="+last_date+"&p_emp_id="+selected_emp_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest leaveChartReq = new StringRequest(Request.Method.GET, leaveChartUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject leaveInfo = array.getJSONObject(i);

                        String lc_short_code = leaveInfo.getString("lc_short_code")
                                .equals("null") ? "" : leaveInfo.getString("lc_short_code");
                        String quantity = leaveInfo.getString("quantity")
                                .equals("null") ? "0" : leaveInfo.getString("quantity");
                        String taken = leaveInfo.getString("consumed")
                                .equals("null") ? "0" : leaveInfo.getString("consumed");
                        String balance_all = leaveInfo.getString("balance")
                                .equals("null") ? "0" : leaveInfo.getString("balance");

                        if (!quantity.equals("0")) {
                            totLeave.add(new BarEntry(i, Float.parseFloat(quantity),i));
                            consumed.add(new BarEntry(i, Float.parseFloat(taken)));
                            balance.add(new BarEntry(i, Float.parseFloat(balance_all),i));
                            shortCode.add(lc_short_code);
                        }
                    }
                }

                connected = true;
                updateInterface();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest leaveBalReq = new StringRequest(Request.Method.GET, leaveBalUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject leaveBalanceInfo = array.getJSONObject(i);

                        String lc_name = leaveBalanceInfo.getString("lc_name")
                                .equals("null") ? "" : leaveBalanceInfo.getString("lc_name");
                        String lc_short_code = leaveBalanceInfo.getString("lc_short_code")
                                .equals("null") ? "" : leaveBalanceInfo.getString("lc_short_code");
                        String lbd_opening_qty = leaveBalanceInfo.getString("lbd_opening_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_opening_qty");
                        String lbd_current_qty = leaveBalanceInfo.getString("lbd_current_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_current_qty");
                        String lbd_taken_qty = leaveBalanceInfo.getString("lbd_taken_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_taken_qty");
                        String lbd_cash_taken_qty = leaveBalanceInfo.getString("lbd_cash_taken_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_cash_taken_qty");
                        String lbd_transfer_qty = leaveBalanceInfo.getString("lbd_transfer_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_transfer_qty");
                        String lbd_balance_qty = leaveBalanceInfo.getString("lbd_balance_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_balance_qty");

                        leaveBalanceLists.add(new LeaveBalanceList(lc_name,lc_short_code,lbd_opening_qty,
                                lbd_current_qty,lbd_taken_qty,lbd_transfer_qty,lbd_cash_taken_qty,lbd_balance_qty));

                    }
                }

                requestQueue.add(leaveChartReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest empReq = new StringRequest(Request.Method.GET, empUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,coa_name));
                    }
                }
                requestQueue.add(leaveBalReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest desigReq = new StringRequest(Request.Method.GET, desigUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" :info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" :info.getString("desig_name");

                        designationLists.add(new DesignationList(desig_id,desig_name));
                    }
                }
                requestQueue.add(empReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest depReq = new StringRequest(Request.Method.GET, deptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dept_id = info.getString("dept_id")
                                .equals("null") ? "" :info.getString("dept_id");
                        String dept_name = info.getString("dept_name")
                                .equals("null") ? "" :info.getString("dept_name");
                        String dept_divm_id = info.getString("dept_divm_id")
                                .equals("null") ? "" :info.getString("dept_divm_id");

                        departmentLists.add(new DepartmentList(dept_id,dept_name,dept_divm_id));
                    }
                }
                requestQueue.add(desigReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                requestQueue.add(depReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest brnReq = new StringRequest(Request.Method.GET, brnUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    branchLists.add(new BranchList("30","None"));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        branchLists.add(new BranchList(coa_id,coa_name));
                    }
                }
                requestQueue.add(divReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(brnReq);
    }

    public void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                popupMenu.getMenuInflater().inflate(R.menu.branch_menu, popupMenu.getMenu());
                Menu menu = popupMenu.getMenu();
                menu.clear();
                for(int i = 0; i < branchLists.size(); i++) {
                    menu.add(0,Integer.parseInt(branchLists.get(i).getId()),Menu.NONE,branchLists.get(i).getName());
                }

                if (selected_coa_id.isEmpty()) {
                    String bb = "None";
                    branchName.setText(bb);
                }
                else {
                    String brn = "";
                    for (int i = 0; i < branchLists.size(); i++) {
                        if (selected_coa_id.equals(branchLists.get(i).getId())) {
                            brn = branchLists.get(i).getName();
                        }
                    }
                    branchName.setText(brn);
                }
                // --- Division
                String div_name = "";
                for (int i = 0; i < divisionLists.size(); i++) {
                    if (selected_div_id.equals(divisionLists.get(i).getDivm_id())) {
                        div_name = divisionLists.get(i).getDivm_name();
                    }
                }
                divisionSelect.setText(div_name);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                // --- Department
                String dept_name = "";
                for (int i = 0; i < departmentLists.size(); i++) {
                    if (selected_dept_id.equals(departmentLists.get(i).getDept_id())) {
                        dept_name = departmentLists.get(i).getDept_name();
                    }
                }
                departmentSelect.setText(dept_name);
                departmentSelectLay.setEnabled(true);

                ArrayList<String> dept_type = new ArrayList<>();
                for(int i = 0; i < departmentLists.size(); i++) {
                    dept_type.add(departmentLists.get(i).getDept_name());
                }

                ArrayAdapter<String> dept_arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,dept_type);
                departmentSelect.setAdapter(dept_arrayAdapter);

                // --- Designation
                String desig_name = "";
                for (int i = 0; i < designationLists.size(); i++) {
                    if (selected_desig_id.equals(designationLists.get(i).getDesig_id())) {
                        desig_name = designationLists.get(i).getDesig_name();
                    }
                }
                designationSelect.setText(desig_name);
                designationSelectLay.setEnabled(true);

                ArrayList<String> desig_type = new ArrayList<>();
                for(int i = 0; i < designationLists.size(); i++) {
                    desig_type.add(designationLists.get(i).getDesig_name());
                }

                ArrayAdapter<String> desig_arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,desig_type);
                designationSelect.setAdapter(desig_arrayAdapter);

                // --- Employees
                emp_name = "";
                emp_code = "";
                emp_designation = "";
                emp_center = "";
                for (int i = 0; i < employeeLists.size(); i++) {
                    if (selected_emp_id.equals(employeeLists.get(i).getEmp_id())) {
                        emp_name = employeeLists.get(i).getEmp_name();
                        emp_code = employeeLists.get(i).getEmp_code();
                        emp_designation = employeeLists.get(i).getJob_calling_title();
                        emp_center = employeeLists.get(i).getCoa_name();
                    }
                }
                employeeSelect.setText(emp_name);
                employeeSelectLay.setEnabled(true);

                ArrayList<String> emp_type = new ArrayList<>();
                for(int i = 0; i < employeeLists.size(); i++) {
                    emp_type.add(employeeLists.get(i).getEmp_name());
                }

                ArrayAdapter<String> emp_arrayAdapter = new ArrayAdapter<>(LeaveBalance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,emp_type);
                employeeSelect.setAdapter(emp_arrayAdapter);

                empDataLay.setVisibility(View.VISIBLE);
                empName.setText(emp_name);
                String tt = "("+emp_code+")";
                empCode.setText(tt);
                empDesignation.setText(emp_designation);
                empCenter.setText(emp_center);

                // --- Bar chart Initialization
                if (!balance.isEmpty() && !totLeave.isEmpty() && !shortCode.isEmpty() && !consumed.isEmpty()) {
                    XAxis xAxis = leaveBalChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setAxisMinimum(0);
                    xAxis.setAxisMaximum(totLeave.size());
                    xAxis.setGranularity(1);

                    BarDataSet set1 = new BarDataSet(totLeave, "Total Leave");
                    set1.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
                    set1.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (String.valueOf(value).contains(".0")) {
                                return String.valueOf((int) Math.floor(value));
                            }
                            else {
                                return String.valueOf(value);
                            }
                        }
                    });

                    BarDataSet set2 = new BarDataSet(consumed, "Consumed");
                    set2.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
                    set2.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (String.valueOf(value).contains(".0")) {
                                return String.valueOf((int) Math.floor(value));
                            }
                            else {
                                return String.valueOf(value);
                            }
                        }
                    });

                    BarDataSet set3 = new BarDataSet(balance, "Balance");
                    set3.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
                    set3.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (String.valueOf(value).contains(".0")) {
                                return String.valueOf((int) Math.floor(value));
                            }
                            else {
                                return String.valueOf(value);
                            }
                        }
                    });

                    float groupSpace = 0.04f;
                    float barSpace = 0.03f; // x3 dataset
                    float barWidth = 0.29f;

                    BarData data = new BarData(set1, set2, set3);
                    data.setValueTextSize(12);
                    data.setBarWidth(barWidth); // set the width of each bar
                    leaveBalChart.animateY(1000);
                    leaveBalChart.setData(data);
                    leaveBalChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
                    leaveBalChart.invalidate();

                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            if (value < 0 || value >= shortCode.size()) {
                                return null;
                            } else {
                                return (shortCode.get((int) value));
                            }

                        }
                    });
                }
                else {
                    BarData data = new BarData();
                    leaveBalChart.setData(data);
                    leaveBalChart.getData().clearValues();
                    leaveBalChart.notifyDataSetChanged();
                    leaveBalChart.clear();
                    leaveBalChart.invalidate();
                    leaveBalChart.fitScreen();
                }

                if (leaveBalanceLists.isEmpty()) {
                    noLeaveDataMsg.setVisibility(View.VISIBLE);
                    leaveData.setVisibility(View.GONE);
                }
                else {
                    leaveData.setVisibility(View.VISIBLE);
                    noLeaveDataMsg.setVisibility(View.GONE);
                }

                leaveBalanceAdapter = new LeaveBalanceAdapter(leaveBalanceLists, LeaveBalance.this);
                reportView.setAdapter(leaveBalanceAdapter);

                loading = false;

            }
            else {
                alertMessage();
            }
        }
        else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
                        getAllData();
                    }
                    else {
                        getAllDataCoa();
                    }
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

    public void getAllDataCoa() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        empDataLay.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        branchLists = new ArrayList<>();
        divisionLists = new ArrayList<>();
        departmentLists = new ArrayList<>();
        designationLists = new ArrayList<>();
        employeeLists = new ArrayList<>();

        totLeave = new ArrayList<>();
        consumed = new ArrayList<>();
        balance = new ArrayList<>();
        shortCode = new ArrayList<>();

        leaveBalanceLists = new ArrayList<>();

        String brnUrl = api_url_front+"hrm_dashboard/getBranches";
        String divUrl = api_url_front+"hrm_dashboard/getDivisionWithCoa?p_coa_id="+selected_coa_id;
        String deptUrl = api_url_front+"hrm_dashboard/getDepartmentByDivCoa?p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        String desigUrl = api_url_front+"hrm_dashboard/getDesignationsByDeptCoa?p_dep_Id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        String empUrl = api_url_front+"hrm_dashboard/getEmployeeByDesigCoa?p_dep_id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_desig_id="+selected_desig_id+"&p_coa_id="+selected_coa_id;
        String leaveBalUrl = api_url_front+"hrm_dashboard/getLeaveBalance?now_date="+last_date+"&p_emp_id="+selected_emp_id;
        String leaveChartUrl = api_url_front+"hrm_dashboard/getLeaveChartData?now_date="+last_date+"&p_emp_id="+selected_emp_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest leaveChartReq = new StringRequest(Request.Method.GET, leaveChartUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject leaveInfo = array.getJSONObject(i);

                        String lc_short_code = leaveInfo.getString("lc_short_code")
                                .equals("null") ? "" : leaveInfo.getString("lc_short_code");
                        String quantity = leaveInfo.getString("quantity")
                                .equals("null") ? "0" : leaveInfo.getString("quantity");
                        String taken = leaveInfo.getString("consumed")
                                .equals("null") ? "0" : leaveInfo.getString("consumed");
                        String balance_all = leaveInfo.getString("balance")
                                .equals("null") ? "0" : leaveInfo.getString("balance");

                        if (!quantity.equals("0")) {
                            totLeave.add(new BarEntry(i, Float.parseFloat(quantity),i));
                            consumed.add(new BarEntry(i, Float.parseFloat(taken)));
                            balance.add(new BarEntry(i, Float.parseFloat(balance_all),i));
                            shortCode.add(lc_short_code);
                        }
                    }
                }

                connected = true;
                updateInterface();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest leaveBalReq = new StringRequest(Request.Method.GET, leaveBalUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject leaveBalanceInfo = array.getJSONObject(i);

                        String lc_name = leaveBalanceInfo.getString("lc_name")
                                .equals("null") ? "" : leaveBalanceInfo.getString("lc_name");
                        String lc_short_code = leaveBalanceInfo.getString("lc_short_code")
                                .equals("null") ? "" : leaveBalanceInfo.getString("lc_short_code");
                        String lbd_opening_qty = leaveBalanceInfo.getString("lbd_opening_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_opening_qty");
                        String lbd_current_qty = leaveBalanceInfo.getString("lbd_current_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_current_qty");
                        String lbd_taken_qty = leaveBalanceInfo.getString("lbd_taken_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_taken_qty");
                        String lbd_cash_taken_qty = leaveBalanceInfo.getString("lbd_cash_taken_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_cash_taken_qty");
                        String lbd_transfer_qty = leaveBalanceInfo.getString("lbd_transfer_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_transfer_qty");
                        String lbd_balance_qty = leaveBalanceInfo.getString("lbd_balance_qty")
                                .equals("null") ? "0" : leaveBalanceInfo.getString("lbd_balance_qty");

                        leaveBalanceLists.add(new LeaveBalanceList(lc_name,lc_short_code,lbd_opening_qty,
                                lbd_current_qty,lbd_taken_qty,lbd_transfer_qty,lbd_cash_taken_qty,lbd_balance_qty));

                    }
                }

                requestQueue.add(leaveChartReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest empReq = new StringRequest(Request.Method.GET, empUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,coa_name));
                    }
                }
                requestQueue.add(leaveBalReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest desigReq = new StringRequest(Request.Method.GET, desigUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" :info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" :info.getString("desig_name");

                        designationLists.add(new DesignationList(desig_id,desig_name));
                    }
                }
                requestQueue.add(empReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest depReq = new StringRequest(Request.Method.GET, deptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dept_id = info.getString("dept_id")
                                .equals("null") ? "" :info.getString("dept_id");
                        String dept_name = info.getString("dept_name")
                                .equals("null") ? "" :info.getString("dept_name");
                        String dept_divm_id = info.getString("dept_divm_id")
                                .equals("null") ? "" :info.getString("dept_divm_id");

                        departmentLists.add(new DepartmentList(dept_id,dept_name,dept_divm_id));
                    }
                }
                requestQueue.add(desigReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                requestQueue.add(depReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest brnReq = new StringRequest(Request.Method.GET, brnUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    branchLists.add(new BranchList("30","None"));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        branchLists.add(new BranchList(coa_id,coa_name));
                    }
                }
                requestQueue.add(divReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(brnReq);

    }
}